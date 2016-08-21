package com.example.franciscojavier.tfgproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.franciscojavier.tfgproject.datamodel.MainUser;
import com.example.franciscojavier.tfgproject.webapiclient.RestService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity {
    RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        restService = new RestService();

        Button saveB = (Button) findViewById(R.id.saveB);
        final Context context = this;
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText setUsername = (EditText) findViewById(R.id.setUsername);
                EditText setPassword = (EditText) findViewById(R.id.setPassword);
                EditText setCityAndCountry = (EditText) findViewById(R.id.setCityAndCountry);
                EditText setJob = (EditText) findViewById(R.id.setJob);
                EditText setHobbies = (EditText) findViewById(R.id.setHobbies);
                EditText setMusicTastes= (EditText) findViewById(R.id.setMusicTastes);
                EditText setFilmTastes = (EditText) findViewById(R.id.setFilmTastes);
                EditText setReadingTastes = (EditText) findViewById(R.id.setReadingTastes);

                String username = setUsername.getText().toString();
                String password = setPassword.getText().toString();
                String cityAndCountry = setCityAndCountry.getText().toString();
                String job = setJob.getText().toString();
                String hobbies = setHobbies.getText().toString();
                String musicTastes = setMusicTastes.getText().toString();
                String filmTastes = setFilmTastes.getText().toString();
                String readingTastes = setReadingTastes.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                if(!username.equals("") && !password.equals("")){
                    MainUser user = new MainUser(username,password,cityAndCountry,job,hobbies,musicTastes,filmTastes,readingTastes);
                    user.setBluetoothMac(android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address"));
                    SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME, 0);
                    final SharedPreferences.Editor editor = settings.edit();

                    restService.getApiTFGService().createUser(user, new Callback<MainUser>() {
                        @Override
                        public void success(MainUser mainUser, Response response) {
                            editor.putInt("Id", mainUser.getId());
                            editor.putString("Username", mainUser.getName());
                            editor.putString("CityAndCountry", mainUser.getCityAndCountry());
                            editor.putString("Job", mainUser.getJob());
                            editor.putString("Hobbies", mainUser.getHobbies());
                            editor.putString("MusicTastes", mainUser.getMusicTastes());
                            editor.putString("FilmTastes", mainUser.getFilmsTastes());
                            editor.putString("ReadingTastes", mainUser.getReadingTastes());

                            editor.commit();

                            Toast.makeText(context, "Profile saved succesfully", Toast.LENGTH_SHORT).show();
                            if(progressDialog.isShowing())progressDialog.dismiss();
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if(progressDialog.isShowing())progressDialog.dismiss();

                            if(error.getMessage().equals("400 Bad Request")){
                                Toast.makeText(context, "User alredy exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if(username.equals("")){
                    Toast.makeText(context, "Introduce username", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Introduce password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
