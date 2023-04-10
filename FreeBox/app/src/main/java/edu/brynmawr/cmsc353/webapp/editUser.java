package edu.brynmawr.cmsc353.webapp;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class editUser extends AppCompatActivity{
    EditText inputUserName;
    EditText inputProperty;
    EditText inputEdit;


    protected String message;

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_edituser);

    }

    public void onConnectUserEditButtonClick(View v) {

        inputUserName = (EditText) findViewById(R.id.inputUserName);
        inputProperty = (EditText) findViewById(R.id.inputProperty);
        inputEdit = (EditText) findViewById(R.id.inputEdit);


        TextView tv = findViewById(R.id.output);

        String id = inputUserName.getText().toString();
        String property = inputProperty.getText().toString();
        String edit = inputEdit.getText().toString();

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host on port 3000
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3000/editUserApp?id=" + id
                                    +"&property=" + property + "&newValue=" + edit);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);

                            // need to set the instance variable in the Activity object
                            // because we cannot directly access the TextView from here
                            message = jo.toString();

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            message = e.toString();
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
            e.printStackTrace();
            tv.setText(e.toString());
        }
    }
}