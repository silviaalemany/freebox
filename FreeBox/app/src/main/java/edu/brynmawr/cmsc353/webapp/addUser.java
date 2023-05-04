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

public class addUser extends AppCompatActivity{
    EditText inputName;
    EditText inputUserName;
    EditText inputEmail;
    EditText inputBio;
    EditText inputPassword;

    protected Toast toast;
    int duration = Toast.LENGTH_SHORT;
    protected CharSequence toastText;
    boolean success = false;
    public static final int LOGGED_IN_MAIN_ACTIVITY_ID = 1;
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        //inputName.getText()

        setContentView(R.layout.activity_adduser);


    }

    public void onConnectUserButtonClick(View v) {
        inputName = (EditText) findViewById(R.id.inputName);
        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputBio = (EditText) findViewById(R.id.inputBio);
        inputPassword = (EditText) findViewById(R.id.inputPassword);

        String name = inputName.getText().toString();
        String username = inputUserName.getText().toString();
        String email = inputEmail.getText().toString();
        String bio = inputBio.getText().toString();
        String password = inputPassword.getText().toString();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/createUserApp?name=" + name
                            + "&id=" + username + "&password=" + password + "&email=" + email + "&bio=" + bio);




                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
                            success = jo.getBoolean("success");



                            if (success) {
                                toastText = "Account creation successful!";
                            } else {
                                toastText = (CharSequence) jo.getString("status");
                            }
                            Log.d("test",jo.toString());


                        }
                        catch (Exception e) {
                            toastText = "There was an issue creating your new account.";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
            toastText = e.toString();
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
