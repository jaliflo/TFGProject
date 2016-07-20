package com.example.franciscojavier.tfgproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME, 0);
        final String username = settings.getString("Username", "");
        final String password = settings.getString("Password", "-1");
        final Context context = this;

        TextView register = (TextView) findViewById(R.id.regB);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.equals("") && password.equals("-1")) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(context, "You are alredy registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button loginB = (Button) findViewById(R.id.loginB);
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameET = (EditText) findViewById(R.id.usernameET);
                EditText passwordET = (EditText) findViewById(R.id.passwordET);

                if(!username.equals(usernameET.getText().toString()) || !password.equals(passwordET.getText().toString())){
                    Toast.makeText(context, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Correct login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
