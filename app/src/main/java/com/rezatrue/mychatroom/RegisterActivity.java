package com.rezatrue.mychatroom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegister;
    EditText etName, etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btn_login);
        etName = findViewById(R.id.reg_name);
        etEmail = findViewById(R.id.reg_email);
        etPassword = findViewById(R.id.reg_password);


        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        String name = etEmail.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();



    }
}
