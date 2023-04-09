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
    public static final int COUNTER_ACTIVITY_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddNewUserButtonClick(View v){
        Intent i = new Intent(this, addUser.class);

        i.putExtra("MESSAGE", "Hey,buddy!");

        startActivityForResult(i, COUNTER_ACTIVITY_ID);
    }

    public void onEditUserButtonClick(View v){
        Intent i = new Intent(this, editUser.class);

        i.putExtra("MESSAGE", "Hey,buddy!");

        startActivityForResult(i, COUNTER_ACTIVITY_ID);

    }
}