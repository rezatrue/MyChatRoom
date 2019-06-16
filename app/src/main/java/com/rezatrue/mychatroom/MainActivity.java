package com.rezatrue.mychatroom;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rezatrue.mychatroom.adapters.MessageAdapter;
import com.rezatrue.mychatroom.pojo.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AbsListView.OnScrollListener {

    EditText etSms;
    Button btnSend;
    ListView listView;
    DatabaseReference root;
    String userName, userImage;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etSms = findViewById(R.id.et_sms);
        btnSend = findViewById(R.id.btn_send);
        listView = findViewById(R.id.smslist);

        btnSend.setOnClickListener(this);
        listView.setOnScrollListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            Log.d(": success : ", "user.getDisplayName()"+ userName);
            String email = user.getEmail();
            Log.d(": success : ", "user.getEmail()"+ email);
            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl!=null)userImage = photoUrl.toString();
            Log.d(": success : ", "user.getPhotoUrl()"+ photoUrl);
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            Log.d(": success : ", "user.isEmailVerified()"+ emailVerified);
            String uid = user.getUid();
            Log.d(": success : ", "user.getUid()"+ uid);
        }

        root = FirebaseDatabase.getInstance().getReference().child("message");
        // display first few message if room is empty
        root.limitToFirst(loadLimit).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(messages!=null && messages.size()>=loadLimit) return;
                messages = new ArrayList<>();
                Iterator it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot) it.next();
                    if ((snapshot!=null) || (lastPostId!=null) || !lastPostId.equals(snapshot.getKey())){
                        lastPostId = snapshot.getKey();
                        Message message = snapshot.getValue(Message.class);
                        messages.add(message);
                    }
                }
                messageAdapter = new MessageAdapter(MainActivity.this, messages);
                Log.d(":success: ", "List Size : "+ messages.size());
                listView.setAdapter(messageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(":success: ", "error msg : "+ databaseError.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String timeStamp = df.format(cal.getTime());

        String sms = etSms.getText().toString();
        etSms.setText("");
        Toast.makeText(this, sms, Toast.LENGTH_LONG).show();

        Map<String, Object> map = new HashMap<>();
        String temp_key = root.push().getKey();
        root.updateChildren(map);

        DatabaseReference msg_root = root.child(temp_key);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", userName);
        map2.put("image", userImage);
        map2.put("msg", sms);
        map2.put("time", timeStamp);
        map2.put("status", "unseen");
        msg_root.updateChildren(map2);
    }


    ArrayList<Message> messages;
    MessageAdapter messageAdapter;
    private int currentVisibleItemCount;
    private int currentScrollState;
    private int currentFirstVisibleItem;
    private int totalItem;
    private int loadLimit = 5;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        this.totalItem = totalItemCount;
    }

    // for checking post is already dispalyed
    private String lastPostId;

    private void isScrollCompleted() {
        if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                && this.currentScrollState == SCROLL_STATE_IDLE) {
            root.orderByKey().startAt(lastPostId).limitToFirst(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator it = dataSnapshot.getChildren().iterator();
                    it.next(); // skip the last time add in list
                    while (it.hasNext()) {
                        DataSnapshot snapshot = (DataSnapshot) it.next();
                        if(lastPostId.equals(snapshot.getKey())){
                            return;
                        }else {
                            lastPostId = (snapshot.getKey());
                            Message message = snapshot.getValue(Message.class);
                            messages.add(message);
                            Log.d(":success: ", "messages added : ");
                        }

                    }
                    messageAdapter = new MessageAdapter(MainActivity.this, messages);
                    Log.d(":success: ", "List Size : "+ messages.size());
                    listView.setAdapter(messageAdapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
    }



}






