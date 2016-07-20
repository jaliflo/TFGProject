package com.example.franciscojavier.tfgproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button saveB = (Button) findViewById(R.id.saveB);
        final Context context = this;
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText setUsername = (EditText) findViewById(R.id.setUsername);
                EditText setPassword = (EditText) findViewById(R.id.setPassword);

                String username = setUsername.getText().toString();
                String password = setPassword.getText().toString();

                if(!username.equals("") && !password.equals("")){
                    SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString("Username", username);
                    editor.putString("Password", password);

                    editor.commit();

                    Toast.makeText(context, "Profile saved succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }else if(username.equals("")){
                    Toast.makeText(context, "Introduce username", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Introduce password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
