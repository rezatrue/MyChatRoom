package com.rezatrue.mychatroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvCreate;
    Button btnLogin;
    EditText etEmail, etPassord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvCreate = findViewById(R.id.tv_create);
        btnLogin = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.login_email);
        etPassord = findViewById(R.id.login_pass);


        tvCreate.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.tv_create:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                String email = etEmail.getText().toString();
                String password = etPassord.getText().toString();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }

    }
}
