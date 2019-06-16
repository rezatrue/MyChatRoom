package com.rezatrue.mychatroom;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

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
        imageView.setOnClickListener(this);

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
        switch(v.getId()){
            case R.id.btn_update:
                updateProfile();
                break;
            case R.id.user_pic:
                setPhotoToImageView();
                break;
        }

    }


    private void updateProfile() {

        if(name!=nameET.getText().toString() || selectedImageUri!=null) {
            UserProfileChangeRequest profileUpdates = null;
            if(name!=nameET.getText().toString() && selectedImageUri!=null) {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameET.getText().toString())
                        .setPhotoUri(selectedImageUri)
                        .build();
            }else if(name!=nameET.getText().toString()) {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameET.getText().toString())
                        .build();
            }else if(selectedImageUri!=null) {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(selectedImageUri)
                        .build();
            }
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User profile updated.");
                                finish();
                            }
                        }
                    });
        }else
            finish();
    }

    private void setPhotoToImageView() {
        dispatchSelectPictureIntent();
    }

    static final int RESULT_LOAD_IMAGE = 2;
    Uri selectedImageUri;
    private void dispatchSelectPictureIntent(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
        selectedImageUri = data.getData();
        imageView.setImageURI(selectedImageUri);
        }
    }

}
