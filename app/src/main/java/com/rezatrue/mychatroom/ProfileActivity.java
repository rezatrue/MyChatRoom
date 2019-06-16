package com.rezatrue.mychatroom;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    EditText nameET;
    EditText emailET;
    Button updateBtn;

    FirebaseUser user;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.user_pic);
        nameET = findViewById(R.id.user_name);
        emailET = findViewById(R.id.user_email);
        updateBtn = findViewById(R.id.btn_update);
        updateBtn.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            if(name!=null) nameET.setText(name);
            String email = user.getEmail();
            if(email!=null) emailET.setText(email);
            Uri photoUrl = user.getPhotoUrl();
            if(photoUrl!=null) imageView.setImageURI(photoUrl);
            String uid = user.getUid();
        }


    }

    @Override
    public void onClick(View v) {
        if(name!=nameET.getText().toString()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameET.getText().toString())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User profile updated.");
                            }
                        }
                    });
        }
    }
}
