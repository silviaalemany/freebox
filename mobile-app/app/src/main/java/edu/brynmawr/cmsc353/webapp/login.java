package edu.brynmawr.cmsc353.webapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class login extends AppCompatActivity {
    protected EditText inputUserName;
    protected EditText inputPassword;
    protected Toast toast;
    int duration = Toast.LENGTH_SHORT;
    protected CharSequence toastText;
    boolean success = false;
    public static final int LOGGED_IN_MAIN_ACTIVITY_ID = 1;
    protected String name;

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);


        setContentView(R.layout.activity_login);

    }

    public void onConnectUserButtonClick(View v) {
        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputPassword = (EditText) findViewById(R.id.password);

        String username = inputUserName.getText().toString();
        String password = inputPassword.getText().toString();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/loginApp?id=" + username + "&password=" + password);




                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
                            success = jo.getBoolean("success");



                            if (success) {
                                name = jo.getString("name");
                                toastText = "Login successful!";
                                Log.d("test",jo.toString());
                            } else {
                                name = "";
                                toastText = (CharSequence) jo.getString("status");
                            }
                        }
                        catch (Exception e) {
                            toastText = "There was an issue logging in. Try again?";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // need to set the instance variable in the Activity object
            // because we cannot directly access the TextView from here

        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
            toastText = e.toString();
            name = "";

        } finally {
            Context context = getApplicationContext();
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            if (success) {
                Intent i = new Intent(this, loggedInMainActivity.class);
                i.putExtra("user", username);
                i.putExtra("name", name);

                startActivityForResult(i, LOGGED_IN_MAIN_ACTIVITY_ID);
            }
        }
    }
}
