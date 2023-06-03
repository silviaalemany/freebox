package edu.brynmawr.cmsc353.webapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class loggedInMainActivity extends AppCompatActivity {
    protected TextView tv;
    public static final int EDIT_USER_ACTIVITY_ID = 1;
    public static final int CREATE_POST_ACTIVITY_ID = 2;
    public static final int USER_LOOKUP_ACTIVITY_ID = 3;
    public static final int ALL_POSTS_ACTIVITY_ID = 4;

    public static final int EDIT_DESC_POST_ACTIVITY_ID = 5;

    public static final int REMOVE_POST_ACTIVITY_ID = 6;
    public static final int MAIN_ACTIVITY_ID = 7;
    protected String name;
    protected String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getIntent().getStringExtra("name");
        user = getIntent().getStringExtra("user");
        setContentView(R.layout.activity_loggedinmain);
        tv = (TextView) findViewById(R.id.statusField);
        CharSequence displayText = "Logged in as @" + user + ".";
        tv.setText(displayText);

    }


    public void onEditUserButtonClick(View v){
        Intent i = new Intent(this, editUser.class);
        i.putExtra("user", user);
        i.putExtra("name", name);
        startActivityForResult(i, EDIT_USER_ACTIVITY_ID);


    }

    public void onCreatePostButtonClick(View v){
        Intent i = new Intent(this, createPost.class);
        i.putExtra("user", user);
        i.putExtra("name", name);
        startActivityForResult(i, CREATE_POST_ACTIVITY_ID);

    }

    public void onUserLookupButtonClick(View v){
        Intent i = new Intent(this, userLookup.class);
        i.putExtra("user", user);
        i.putExtra("name", name);
        startActivityForResult(i, USER_LOOKUP_ACTIVITY_ID);

    }

    public void onViewAllPostsButtonClick(View v) {
        Intent i = new Intent(this, ViewAllPosts.class);
        i.putExtra("user", user);
        i.putExtra("name", name);
        startActivityForResult(i, ALL_POSTS_ACTIVITY_ID);
    }

    public void onEditPostClick(View v) {
        Intent i = new Intent(this, editPost.class);
        i.putExtra("user", user);
        i.putExtra("name", name);
        startActivityForResult(i, EDIT_DESC_POST_ACTIVITY_ID);
    }

    public void onRemovePostClick(View v) {
        Intent i = new Intent(this, removePost.class);
        i.putExtra("user", user);
        i.putExtra("name", name);
        startActivityForResult(i, REMOVE_POST_ACTIVITY_ID);
    }


    public void onLogoutClick(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivityForResult(i, MAIN_ACTIVITY_ID);
    }

}
