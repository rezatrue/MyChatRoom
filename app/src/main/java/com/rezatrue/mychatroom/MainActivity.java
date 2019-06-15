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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rezatrue.mychatroom.pojo.SingleObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etSms;
    Button btnSend;
    ListView listView;


    DatabaseReference root;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }



        root = FirebaseDatabase.getInstance().getReference().child("message");

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> chat_conversation = new ArrayList<>();
                String chat_msg, user_name;
                Iterator it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    SingleObject singleObject = ((DataSnapshot) it.next()).getValue(SingleObject.class);
                    user_name = singleObject.getName();
                    chat_msg = singleObject.getMsg();
                    chat_conversation.add(user_name + " : " + chat_msg);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, chat_conversation);
                //ArrayAdapter<String>adapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,chat_conversation);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        String sms = etSms.getText().toString();
        etSms.setText("");
        Toast.makeText(this, sms, Toast.LENGTH_LONG).show();

        Map<String, Object> map = new HashMap<>();
        String temp_key = root.push().getKey();
        root.updateChildren(map);

        DatabaseReference msg_root = root.child(temp_key);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "Ali");
        map2.put("msg", sms);
        msg_root.updateChildren(map2);
    }


}
