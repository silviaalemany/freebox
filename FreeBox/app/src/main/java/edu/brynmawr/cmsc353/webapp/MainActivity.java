package edu.brynmawr.cmsc353.webapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_USER_ACTIVITY_ID = 1;

    public static final int LOGIN_ACTIVITY_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddNewUserButtonClick(View v){
        Intent i = new Intent(this, addUser.class);
        startActivityForResult(i, ADD_USER_ACTIVITY_ID);
    }

    public void onLoginClick(View v){
        Intent i = new Intent(this, login.class);
        startActivityForResult(i, LOGIN_ACTIVITY_ID);
    }
}