package edu.brynmawr.cmsc353.webapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class createPost extends AppCompatActivity{
    EditText inputPrice;
    EditText inputDesc;
    protected Toast toast;
    int duration = Toast.LENGTH_SHORT;
    protected CharSequence toastText;

    protected String name;
    protected String user;
    boolean success = false;
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        name = getIntent().getStringExtra("name");
        user = getIntent().getStringExtra("user");

        setContentView(R.layout.activity_createpost);


    }

    public void onConnectUserButtonClick(View v) {
        inputPrice = (EditText) findViewById(R.id.inputPrice);
        inputDesc = (EditText) findViewById(R.id.inputDesc);

        TextView tv = findViewById(R.id.output);

        String price = inputPrice.getText().toString();
        String desc = inputDesc.getText().toString();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/createPostApp?user=" + user
                                    +"&price=" + price + "&desc=" + desc);

                            Log.d("createPost",url.toString());


                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
                            success = jo.getBoolean("success");



                            if (success) {
                                toastText = "Successfully added " + name + "\'s post to the database.";
                            } else {
                                toastText = (CharSequence) jo.getString("status");
                            }

                        }
                        catch (Exception e) {
                            toastText = "There was an issue creating your post. Try again?";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            toastText = "There was an issue creating your post. Try again?";
        } finally {
            Context context = getApplicationContext();
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }
    }
}

