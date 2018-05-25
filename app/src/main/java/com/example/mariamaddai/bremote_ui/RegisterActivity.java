package com.example.mariamaddai.bremote_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private Button reg;
    private TextView tvLogin;
    private EditText etName, etPass;
    private DB_remote db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db=new DB_remote(this);
        reg=(Button)findViewById(R.id.btnReg);
        tvLogin=(TextView)findViewById(R.id.tvLogin);
        etName=(EditText)findViewById(R.id.etName);
        etPass=(EditText)findViewById(R.id.etPass);
        reg.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnReg:
                register(v);
                break;
            case R.id.tvLogin:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
                break;
            default:
        }
    }
    private void register(View view){
        String name=etName.getText().toString();
        String pass=etPass.getText().toString();
        if(name.isEmpty()&&pass.isEmpty()){
            displayToast("Username/Password field empty");
        }else{
            db.addUser(name,pass);
            displayToast("User registered");
            finish();
        }
    }
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


}
