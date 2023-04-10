package edu.brynmawr.cmsc353.webapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class userLookup extends AppCompatActivity {
    EditText inputUserName;

    protected String finalDisplay;
    protected String message;

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_userlookup);

    }

    public void onConnectUserButtonClick(View v) {

        inputUserName = (EditText) findViewById(R.id.inputUserName);


        TextView tv = findViewById(R.id.output);

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
                                finalDisplay = message;
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append(jo.getString("user") + "\n");
                                JSONArray posts = jo.getJSONArray("store");
                                sb.append(String.format("%d posts total.\n\n", posts.length()));
                                for (int i = 0; i < posts.length(); i++){
                                    JSONObject post = posts.getJSONObject(i);
                                    String price = post.getString("price");
                                    String desc = post.getString("desc");
                                    boolean status = post.getBoolean("status");
                                    sb.append(String.format("Post %d:\n Price: %s\n Description: %s\n",
                                            i, price, desc));
                                    if (status) {
                                        sb.append("This is still available!\n\n");
                                    } else {
                                        sb.append("No longer available :(\n\n");
                                    }
                                }

                                finalDisplay = sb.toString();

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            finalDisplay = e.toString();
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // now we can set the status in the TextView
            tv.setText(finalDisplay);
        } catch (Exception e) {
            // uh oh
            e.printStackTrace();
            tv.setText(e.toString());
        }
    }
}
