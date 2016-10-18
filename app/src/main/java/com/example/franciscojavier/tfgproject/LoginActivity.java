package com.example.franciscojavier.tfgproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franciscojavier.tfgproject.datamodel.MainUser;
import com.example.franciscojavier.tfgproject.datamodel.User;
import com.example.franciscojavier.tfgproject.webapiclient.RestService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {
    RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        restService = new RestService();

        SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME,0);
        MainUser user = new MainUser();
        user.setName(settings.getString("Username", ""));
        user.setPassword(settings.getString("Password", ""));

        System.out.println("Name: "+user.getName()+" Password: "+user.getPassword());

        if(!user.getName().equals("") && !user.getPassword().equals("")){
            login(user);
        }

        final Context context = this;

        TextView register = (TextView) findViewById(R.id.regB);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button loginB = (Button) findViewById(R.id.loginB);
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameET = (EditText) findViewById(R.id.usernameET);
                EditText passwordET = (EditText) findViewById(R.id.passwordET);

                MainUser user = new MainUser();
                user.setName(usernameET.getText().toString());
                user.setPassword(passwordET.getText().toString());

                login(user);
            }
        });
    }

    private void login(MainUser user){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        restService.getApiTFGService().login(user, new Callback<MainUser>() {
            @Override
            public void success(MainUser user, Response response) {
                Toast.makeText(LoginActivity.this, "Correct login: Welcome "+user.getName(), Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing())progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing())progressDialog.dismiss();
            }
        });
    }
}
