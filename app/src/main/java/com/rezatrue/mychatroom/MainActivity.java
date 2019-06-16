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
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            userName = user.getDisplayName();
            Log.d(": success : ", "user.getDisplayName()"+ userName);
            String email = user.getEmail();
            Log.d(": success : ", "user.getEmail()"+ email);
            Uri photoUrl = user.getPhotoUrl();
            userImage = photoUrl.toString();
            //Uri myUri = Uri.parse(mystring);
            Log.d(": success : ", "user.getPhotoUrl()"+ photoUrl);
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            Log.d(": success : ", "user.isEmailVerified()"+ emailVerified);
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            Log.d(": success : ", "user.getUid()"+ uid);

        }



        root = FirebaseDatabase.getInstance().getReference().child("message");

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> messages = new ArrayList<>();
                //ArrayList<String> chat_conversation = new ArrayList<>();
                //String chat_msg, user_name;
                Iterator it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    Message message = ((DataSnapshot) it.next()).getValue(Message.class);
                    messages.add(message);
                    //SingleObject singleObject = ((DataSnapshot) it.next()).getValue(SingleObject.class);
                    //user_name = singleObject.getName();
                    //chat_msg = singleObject.getMsg();
                    //chat_conversation.add(user_name + " : " + chat_msg);
                }
                //ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, chat_conversation);
                MessageAdapter messageAdapter = new MessageAdapter(MainActivity.this, messages);
                listView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


}
