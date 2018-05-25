package com.example.mariamaddai.bremote_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button login, register;
    private EditText etName, etPass;
    private DB_remote db;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db=new DB_remote(this);
        session=new Session(this);
        login=(Button)findViewById(R.id.btnLogin);
        register=(Button)findViewById(R.id.btnReg);
        etName=(EditText)findViewById(R.id.etName);
        etPass=(EditText)findViewById(R.id.etPass);
        login.setOnClickListener(this);
        register.setOnClickListener(this);

        if(session.loggedIn()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnReg:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            default:
        }
    }
    private void login(){
        String name=etName.getText().toString();
        String pass=etPass.getText().toString();
        if(db.getUser(name,pass)){
            session.setLoggedin(true);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong name/password",Toast.LENGTH_SHORT).show();
        }
    }
}
