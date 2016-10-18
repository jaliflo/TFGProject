package com.example.franciscojavier.tfgproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franciscojavier.tfgproject.datamodel.MainUser;
import com.example.franciscojavier.tfgproject.webapiclient.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity{
    private RestService restService;
    private TextView state;
    private int stateInt;

    private final String[] OPTIONS_HOBBIES = {"sports", "play music"};
    private final String[] OPTIONS_MUSIC = {"rock"};
    private final String[] OPTIONS_FILM = {"action"};
    private final String[] OPTIONS_READ = {"fantasy"};

    private List<String> music;
    private List<String> film;
    private List<String> hobbies;
    private List<String> read;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditText setCity = (EditText) findViewById(R.id.cityED);
        EditText setCountry = (EditText) findViewById(R.id.countryED);
        TextView text2 = (TextView) findViewById(R.id.textView2);
        ListView optionsList = (ListView) findViewById(R.id.optionsList);
        setCity.setVisibility(View.GONE);
        setCountry.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        optionsList.setVisibility(View.GONE);

        final Context context = this;
        restService = new RestService();
        state = (TextView) findViewById(R.id.stateTx);
        stateInt = 1;

        hobbies = new ArrayList<>();
        read = new ArrayList<>();
        film = new ArrayList<>();
        music = new ArrayList<>();

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String element = (String) parent.getItemAtPosition(position);
                if(view.isActivated()){
                    view.setActivated(false);
                    view.setBackgroundColor(Color.WHITE);
                    if(stateInt == 3){
                        hobbies.remove(element);
                    }else if(stateInt == 4){
                        music.remove(element);
                    }else if(stateInt == 5){
                        film.remove(element);
                    }else if(stateInt == 6){
                        read.remove(element);
                    }
                }else {
                    view.setActivated(true);
                    view.setBackgroundColor(Color.GRAY);
                    if(stateInt == 3){
                        hobbies.add(element);
                    }else if(stateInt == 4){
                        music.add(element);
                    }else if(stateInt == 5){
                        film.add(element);
                    }else if(stateInt == 6){
                        read.add(element);
                    }
                }
            }
        });

        final Button next_or_save_b = (Button) findViewById(R.id.next_save_b);
        next_or_save_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text1 = (TextView) findViewById(R.id.textView1);
                TextView text2 = (TextView) findViewById(R.id.textView3);
                TextView text3 = (TextView) findViewById(R.id.textView2);
                EditText setCity = (EditText) findViewById(R.id.cityED);
                EditText setCountry = (EditText) findViewById(R.id.countryED);
                EditText setUsername = (EditText) findViewById(R.id.usernameED);
                EditText setPassword = (EditText) findViewById(R.id.passwordED);
                ListView optionsList = (ListView) findViewById(R.id.optionsList);

                String username = "";
                String password = "";
                String cityAndCountry = "";

                if(stateInt == 1){
                    if(!setUsername.getText().toString().equals("") && !setPassword.getText().toString().equals("")){
                        Toast.makeText(context, "Good, username and password are correct!", Toast.LENGTH_SHORT).show();
                        state.setText("2 of 6");
                        text1.setText("City");
                        text2.setText("Country");
                        stateInt++;
                        setUsername.setVisibility(View.GONE);
                        setPassword.setVisibility(View.GONE);
                        setCity.setVisibility(View.VISIBLE);
                        setCountry.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(context, "Something wrong...", Toast.LENGTH_SHORT).show();
                    }
                }else if(stateInt == 2){
                    if(!setCity.getText().equals("") && !setCountry.getText().equals("")){
                        Toast.makeText(context, "Great, city and country set correct!", Toast.LENGTH_SHORT).show();
                        state.setText("3 of 6");
                        stateInt++;
                        text1.setVisibility(View.GONE);
                        text2.setVisibility(View.GONE);
                        setCity.setVisibility(View.GONE);
                        setCountry.setVisibility(View.GONE);
                        text3.setVisibility(View.VISIBLE);
                        hobbies.clear();
                        optionsList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, OPTIONS_HOBBIES));
                        optionsList.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(context, "Something wrong...", Toast.LENGTH_SHORT).show();
                    }
                }else if(stateInt == 3){
                    Toast.makeText(context, "Great, hobbies selected correctly!", Toast.LENGTH_SHORT).show();
                    state.setText("4 of 6");
                    stateInt++;
                    text3.setText("Music Tastes");
                    music.clear();
                    optionsList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, OPTIONS_MUSIC));
                }else if(stateInt == 4){
                    Toast.makeText(context, "Great, Music selected correctly!", Toast.LENGTH_SHORT).show();
                    state.setText("5 of 6");
                    stateInt++;
                    text3.setText("Film Tastes");
                    film.clear();
                    optionsList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, OPTIONS_FILM));
                    next_or_save_b.setText("Save");
                }else if(stateInt == 5){
                    Toast.makeText(context, "Great, Film selected correctly!", Toast.LENGTH_SHORT).show();
                    state.setText("6 of 6");
                    stateInt++;
                    text3.setText("Read Tastes");
                    read.clear();
                    optionsList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, OPTIONS_READ));
                }else if(stateInt == 6){
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Sending Data");
                    progressDialog.show();

                    username = setUsername.getText().toString();
                    password = setPassword.getText().toString();
                    cityAndCountry = setCity.getText().toString()+"-"+setCountry.getText().toString();

                    String hobbiesS = "";
                    for(String hobbie: hobbies){
                        if(hobbiesS.equals("")){
                            hobbiesS = hobbiesS + hobbie;
                        }else {
                            hobbiesS = hobbiesS + ", "+hobbie;
                        }
                    }
                    String musicS = "";
                    for(String m:music){
                        if(musicS.equals("")){
                            musicS = musicS + m;
                        }else {
                            musicS = musicS + ", "+ m;
                        }
                    }
                    String filmS = "";
                    for(String f:film){
                        if(filmS.equals("")){
                            filmS = filmS + f;
                        }else {
                            filmS = filmS + ", " + f;
                        }
                    }
                    String readS = "";
                    for(String r:read){
                        if(readS.equals("")){
                            readS = readS + r;
                        }else {
                            readS = readS + ", " + r;
                        }
                    }

                    MainUser user = new MainUser(username, password, cityAndCountry, "",hobbiesS,musicS,filmS,readS);
                    user.setBluetoothMac(android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address"));
                    SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME, 0);
                    final SharedPreferences.Editor editor = settings.edit();

                    restService.getApiTFGService().createUser(user, new Callback<MainUser>() {
                        @Override
                        public void success(MainUser mainUser, Response response) {
                            editor.putInt("Id", mainUser.getId());
                            editor.putString("Username", mainUser.getName());
                            editor.putString("Password", mainUser.getPassword());
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
                }
            }
        });
        //Button saveB = (Button) findViewById(R.id.saveB);

        /*saveB.setOnClickListener(new View.OnClickListener() {
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
                            editor.putString("Password", mainUser.getPassword());
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
        });*/
    }

    private void backToCredentials(){
        TextView text1 = (TextView) findViewById(R.id.textView1);
        TextView text2 = (TextView) findViewById(R.id.textView3);
        EditText setUsername = (EditText) findViewById(R.id.usernameED);
        EditText setPassword = (EditText) findViewById(R.id.passwordED);
        setUsername.setVisibility(View.VISIBLE);
        setPassword.setVisibility(View.VISIBLE);
        EditText setCity = (EditText) findViewById(R.id.cityED);
        EditText setCountry = (EditText) findViewById(R.id.countryED);
        setCity.setVisibility(View.GONE);
        setCountry.setVisibility(View.GONE);
        state.setText("1 of 6");
        text1.setText("Username");
        text2.setText("Password");
        stateInt--;
    }

    private void backToCityAndCountry(){
        TextView text1 = (TextView) findViewById(R.id.textView1);
        TextView text2 = (TextView) findViewById(R.id.textView3);
        EditText setUsername = (EditText) findViewById(R.id.usernameED);
        EditText setPassword = (EditText) findViewById(R.id.passwordED);
        EditText setCity = (EditText) findViewById(R.id.cityED);
        EditText setCountry = (EditText) findViewById(R.id.countryED);
        TextView text3 = (TextView) findViewById(R.id.textView2);
        ListView optionsList = (ListView) findViewById(R.id.optionsList);
        setUsername.setVisibility(View.GONE);
        setPassword.setVisibility(View.GONE);
        text3.setVisibility(View.GONE);
        optionsList.setVisibility(View.GONE);
        setCity.setVisibility(View.VISIBLE);
        setCountry.setVisibility(View.VISIBLE);
        state.setText("2 of 6");
        text1.setVisibility(View.VISIBLE);
        text2.setVisibility(View.VISIBLE);
        text1.setText("City");
        text2.setText("Country");
        stateInt--;
    }

    private void backToHobbies(){
        TextView text3 = (TextView) findViewById(R.id.textView2);
        ListView optionsList = (ListView) findViewById(R.id.optionsList);
        text3.setText("Hobbies");
        hobbies.clear();
        optionsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, OPTIONS_HOBBIES));
        state.setText("3 of 6");
        stateInt--;
    }

    private void backToMusic(){
        TextView text3 = (TextView) findViewById(R.id.textView2);
        ListView optionsList = (ListView) findViewById(R.id.optionsList);
        text3.setText("Music Tastes");
        music.clear();
        optionsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, OPTIONS_MUSIC));
        state.setText("4 of 6");
        stateInt--;
    }

    private void backToFilm(){
        TextView text3 = (TextView) findViewById(R.id.textView2);
        ListView optionsList = (ListView) findViewById(R.id.optionsList);
        text3.setText("Film Tastes");
        film.clear();
        optionsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, OPTIONS_FILM));
        state.setText("5 of 6");
        stateInt--;
    }

    @Override
    public void onBackPressed(){
        if(stateInt == 2){
            backToCredentials();
        }else if(stateInt == 3){
            backToCityAndCountry();
        }else if(stateInt == 4){
            backToHobbies();
        }else if(stateInt == 5){
            backToMusic();
        }else if(stateInt == 6){
            backToFilm();
        }else {
            super.onBackPressed();
        }
    }
}
