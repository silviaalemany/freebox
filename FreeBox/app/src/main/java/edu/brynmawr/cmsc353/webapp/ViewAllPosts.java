package edu.brynmawr.cmsc353.webapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ViewAllPosts extends AppCompatActivity {
    protected String message;
    protected String headerDisplay;
    protected String[] postDisplay;
    protected String user;
    protected String name;
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_allposts);
        name = getIntent().getStringExtra("name");
        user = getIntent().getStringExtra("user");
        updateUI();
    }

    public void updateUI() {
        LinearLayout postLayouts = (LinearLayout) findViewById(R.id.postLayouts);

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // assumes that there is a server running on the AVD's host on port 3000
                    // and that it has a /test endpoint that returns a JSON object with
                    // a field called "message"
                    URL url = new URL("http://10.0.2.2:3000/allPosts");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    Scanner in = new Scanner(url.openStream());
                    String response = in.nextLine();

                    JSONObject jo = new JSONObject(response);

                    // need to set the instance variable in the Activity object
                    // because we cannot directly access the TextView from here
                    message = jo.getString("status");
                    if (!message.equals("successful")) {
                        headerDisplay = message;
                    } else {
                        StringBuilder user_sb = new StringBuilder();
                        JSONArray posts = jo.getJSONArray("entries");
                        headerDisplay = String.format("%d posts total.\n\n", posts.length());
                        String[] post_strings = new String[posts.length()];
                        for (int i = 0; i < posts.length(); i++){
                            JSONObject post = posts.getJSONObject(i);
                            String price = post.getString("price");
                            String owner = post.getString("user");
                            String desc = post.getString("desc");
                            String id = post.getString("id");
                            boolean status = post.getBoolean("available");
                            String postString = String.format("Post %d:\nPost ID: %s\nOwner: %s\nPrice: %s\nDescription: %s\n",
                                    i, id, owner, price, desc);
                            if (status) {
                                postString = String.format("%sThis is still available!\n\n", postString);
                            } else {
                                postString = String.format("%sNo longer available :(\n\n", postString);
                            }
                            post_strings[i] = postString;

                        }

                        headerDisplay = user_sb.toString();
                        postDisplay = post_strings;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    message = e.toString();
                }
            });

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            // uh oh
            e.printStackTrace();
            message = e.toString();
        }

        for (int i = 0; i < postDisplay.length; i++) {
            TextView post = new TextView(this);
            post.setText(postDisplay[i]);
            post.setTextSize(30);
            post.setGravity(Gravity.CENTER);
            postLayouts.addView(post);
        }

    }
}
