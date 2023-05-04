package edu.brynmawr.cmsc353.webapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class userLookup extends AppCompatActivity {
    EditText inputUserName;

    protected String userDisplay;
    protected String[] postDisplay;
    protected boolean[] availability;
    protected String message;
    protected String user;
    protected String name;
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        name = getIntent().getStringExtra("name");
        user = getIntent().getStringExtra("user");
        setContentView(R.layout.activity_userlookup);

    }

    public void onConnectUserButtonClick(View v) {

        inputUserName = (EditText) findViewById(R.id.inputUserName);


        TextView userTV = findViewById(R.id.userOutput);
        //TextView postTV = findViewById(R.id.postOutput);
        LinearLayout postLayouts = (LinearLayout) findViewById(R.id.postLayouts);

        String user = inputUserName.getText().toString();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/singleUserApp?user=" + user);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);

                            // need to set the instance variable in the Activity object
                            // because we cannot directly access the TextView from here
                            message = jo.getString("message");
                            if (!message.equals("success")) {
                                userDisplay = message;
                            } else {
                                StringBuilder user_sb = new StringBuilder();
                                user_sb.append(jo.getString("user") + "\n");
                                JSONArray posts = jo.getJSONArray("store");
                                user_sb.append(String.format("%d posts total.\n\n", posts.length()));
                                String[] post_strings = new String[posts.length()];
                                availability = new boolean[posts.length()];
                                for (int i = 0; i < posts.length(); i++){
                                    JSONObject post = posts.getJSONObject(i);
                                    String price = post.getString("price");
                                    String desc = post.getString("desc");
                                    String id = post.getString("id");
                                    boolean status = post.getBoolean("status");
                                    String postString = String.format("Post %d:\nPost ID: %s\nPrice: %s\nDescription: %s\n",
                                            i, id, price, desc);
                                    if (status) {
                                        availability[i] = true;
                                        postString = String.format("%sThis is still available!\n\n", postString);
                                    } else {
                                        availability[i] = false;
                                        postString = String.format("%sNo longer available :(\n\n", postString);
                                    }
                                    post_strings[i] = postString;

                                }

                                userDisplay = user_sb.toString();
                                postDisplay = post_strings;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            userDisplay = "There was an issue with searching for the user you specified. Try again?";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // now we can set the status in the TextView
            userTV.setText(userDisplay);
            for (int i = 0; i < postDisplay.length; i++) {
                TextView post = new TextView(this);
                post.setText(postDisplay[i]);
                post.setTextSize(30);
                post.setGravity(Gravity.CENTER);
                postLayouts.addView(post);

                if (availability[i]) {
                    Button purchaseButton = new Button(this);
                    purchaseButton.setText("Purchase");
                    int purchase_activity_id = i;
                    purchaseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(userLookup.this, makePurchase.class);
                            intent.putExtra("postInfo", postDisplay[purchase_activity_id]);
                            startActivityForResult(intent, purchase_activity_id);
                        }
                    });
                    postLayouts.addView(purchaseButton);
                }

            }
        } catch (Exception e) {
            // uh oh
            e.printStackTrace();
            userTV.setText(e.toString());
        }
    }

}
