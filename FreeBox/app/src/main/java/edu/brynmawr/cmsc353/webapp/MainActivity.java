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
    public static final int EDIT_USER_ACTIVITY_ID = 2;
    public static final int CREATE_POST_ACTIVITY_ID = 3;
    public static final int USER_LOOKUP_ACTIVITY_ID = 4;
    public static final int ALL_POSTS_ACTIVITY_ID = 5;

    public static final int EDIT_DESC_POST_ACTIVITY_ID = 6;

    public static final int REMOVE_POST_ACTIVITY_ID = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddNewUserButtonClick(View v){
        Intent i = new Intent(this, addUser.class);
        startActivityForResult(i, ADD_USER_ACTIVITY_ID);
    }

    public void onEditUserButtonClick(View v){
        Intent i = new Intent(this, editUser.class);
        startActivityForResult(i, EDIT_USER_ACTIVITY_ID);

    }

    public void onCreatePostButtonClick(View v){
        Intent i = new Intent(this, createPost.class);
        startActivityForResult(i, CREATE_POST_ACTIVITY_ID);

    }

    public void onUserLookupButtonClick(View v){
        Intent i = new Intent(this, userLookup.class);
        startActivityForResult(i, REMOVE_POST_ACTIVITY_ID);

    }

    public void onViewAllPostsButtonClick(View v) {
        Intent i = new Intent(this, ViewAllPosts.class);
        startActivityForResult(i, ALL_POSTS_ACTIVITY_ID);
    }

    public void onEditPostClick(View v) {
        Intent i = new Intent(this, editPost.class);
        startActivityForResult(i, EDIT_DESC_POST_ACTIVITY_ID);
    }

    public void onRemovePostClick(View v) {
        Intent i = new Intent(this, removePost.class);
        startActivityForResult(i, EDIT_DESC_POST_ACTIVITY_ID);
    }
}