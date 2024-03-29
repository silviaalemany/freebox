package edu.brynmawr.cmsc353.webapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class removePost extends AppCompatActivity {
    EditText inputPostID;
    protected String name;
    protected String user;
    protected String message;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        name = getIntent().getStringExtra("name");
        user = getIntent().getStringExtra("user");

        setContentView(R.layout.activity_deleteposts);

    }

    public void onConnectDeletePostButtonClick(View v) {

        inputPostID = (EditText) findViewById(R.id.inputPostID);

        TextView tv = findViewById(R.id.output);

        String id = inputPostID.getText().toString();


        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/deletePostApp?id=" + id +"&user=" + user);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);

                            // need to set the instance variable in the Activity object
                            // because we cannot directly access the TextView from here
                            message = jo.getString("status");

                        }
                        catch (Exception e) {
                            message = "There was an issue with editing your post. Try again?";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // now we can set the status in the TextView
            tv.setText(message);
        }
        catch (Exception e) {
            // uh oh
            tv.setText("There was an issue with editing your post. Try again?");
        }
    }

}
