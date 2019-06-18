package com.rezatrue.mychatroom;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AbsListView.OnScrollListener, ChildEventListener {

    EditText etSms;
    Button btnSend;
    ListView listView;
    DatabaseReference root;
    String userName, userImage;
    public static String uid; // msg seen

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
            uid = user.getUid();
            Log.d(": success : ", "user.getUid()"+ uid);
        }

        root = FirebaseDatabase.getInstance().getReference().child("message");
        // display last few messages for the first time if any


        root.orderByKey().limitToLast(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(messages!=null && messages.size()>=loadLimit) return;
                if(messages == null) messages = new ArrayList<>();
                Iterator it = dataSnapshot.getChildren().iterator();
                DataSnapshot snapshot1 = (DataSnapshot) it.next();
                Message message1 = snapshot1.getValue(Message.class);
                firstViewedPostId = snapshot1.getKey();
                if(message1.getStatus().equals("unseen") && !message1.getUid().equals(uid)) {
                    makeMessageSeen(firstViewedPostId); // msg seen
                    message1.setStatus("seen");
                }
                messages.add(message1);

                while (it.hasNext()) {
                    DataSnapshot snapshot = (DataSnapshot) it.next();
                    Message message = snapshot.getValue(Message.class);
                    String postId = snapshot.getKey();
                    if(message.getStatus().equals("unseen") && !message.getUid().equals(uid)) {
                        makeMessageSeen(postId); // msg seen
                        message.setStatus("seen");
                    }
                    messages.add(message);
                }
                messageAdapter = new MessageAdapter(MainActivity.this, messages);
                Log.d(":success: ", "List Size : "+ messages.size());
                listView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        root.orderByKey().limitToLast(1).addChildEventListener(this);

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
        map2.put("uid", uid); // msg seen
        msg_root.updateChildren(map2);
    }

    public void makeMessageSeen(String msgKey){  // msg seen
        DatabaseReference msg_root = root.child(msgKey);
        Map<String, Object> map = new HashMap<>();
        map.put("status", "seen");
        msg_root.updateChildren(map);
    }

    String firstViewedPostId;
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
        this.isScrollUpCompleted(firstViewedPostId);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        this.totalItem = totalItemCount;
    }


    private void isScrollUpCompleted(final String previousKey) {
//        Log.d(":success: ", "scrolling up : currentFirstVisibleItem - "
//                + currentFirstVisibleItem + " currentVisibleItemCount - " + currentVisibleItemCount + " totalItem - " + totalItem);
        if (currentFirstVisibleItem == 0
                && this.currentScrollState == SCROLL_STATE_IDLE) {
            Log.d(":success: ", "previousKey  " + previousKey );
            root.orderByKey().endAt(previousKey).limitToFirst(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Iterator it = dataSnapshot.getChildren().iterator();

                    int i = 0;
                    DataSnapshot snapshot1 = (DataSnapshot) it.next();
                    String currentFirstKey = snapshot1.getKey();
                    if (!currentFirstKey.equals(previousKey)) {
                        Message message1 = snapshot1.getValue(Message.class);
                        messages.add(i, message1);
                        i++;
                        while (it.hasNext()) {
                            DataSnapshot snapshot = (DataSnapshot) it.next();
                            String currentKey = snapshot.getKey();
                            Message message = snapshot.getValue(Message.class);
                            if (currentKey.equals(previousKey)) break;
                            Log.d(":success: ", "Data  " + message.getMsg() + "  : " + currentKey);
                            messages.add(i, message);
                            i++;
                        }
                        firstViewedPostId = currentFirstKey;
                        messageAdapter = new MessageAdapter(MainActivity.this, messages);
                        Log.d(":success: ", "List Size : " + messages.size());
                        listView.setAdapter(messageAdapter);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
    }

    // for last/current messages
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Log.d(":success: ", "new msg ");
        // not to load data for the first time / allow addListenerForSingleValueEvent to load first
        if(messages == null) return;
        Log.d(":success: ", " : added : ");
        Message message = dataSnapshot.getValue(Message.class);
        String postId = dataSnapshot.getKey();
        if(message.getStatus().equals("unseen")  && !message.getUid().equals(uid) ) {
            makeMessageSeen(postId); // msg seen
            message.setStatus("seen");
        }
        messages.add(message);
        messageAdapter = new MessageAdapter(MainActivity.this, messages);
        Log.d(":success: ", "List Size : "+ messages.size() + " viewPostID : " + firstViewedPostId);
        listView.setAdapter(messageAdapter);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}






