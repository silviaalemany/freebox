package edu.brynmawr.cmsc353.webapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class makePurchase extends AppCompatActivity {
    String status;

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_makepurchase);
        String postInfo = getIntent().getStringExtra("postInfo");
        String status = changePostStatus(postInfo);
        updateUI(postInfo);
    }

    public void updateUI(String postInfo) {
        TextView tv = findViewById(R.id.statusField);
        StringBuilder sb = new StringBuilder();
        sb.append("Success! You have purchased the following item:\n");
        String[] postData = postInfo.split("\n");
        sb.append(postData[1] + "\n");
        sb.append(postData[2] + "\n");
        sb.append(postData[3] + "\n");
        sb.append(status);
        tv.setText(sb.toString());
    }

    public String changePostStatus(String postInfo) {
        String[] postData = postInfo.split("\n");
        String postID = postData[1].split(" ")[2];
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    URL url = new URL("http://10.0.2.2:3000/editPostStatusApp?id=" + postID.trim() + "&status=false");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    Scanner in = new Scanner(url.openStream());
                    String response = in.nextLine();

                    //JSONObject jo = new JSONObject(response);

                    // need to set the instance variable in the Activity object
                    // because we cannot directly access the TextView from here
                    status = "";

                } catch (Exception e) {
                    e.printStackTrace();
                    status = e.toString();
                }
            });

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            // uh oh
            e.printStackTrace();
            status = e.toString();
        }
        return status;
    }

}
