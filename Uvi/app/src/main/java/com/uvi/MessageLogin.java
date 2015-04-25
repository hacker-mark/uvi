package com.uvi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uvi.synch.MessageService;
import com.uvi.synch.User;

/**
 * Created by Mark on 18/04/2015.
 */
public class MessageLogin extends Activity {

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button signUpButton = (Button) findViewById(R.id.signupButton);
        final EditText usernameField = (EditText) findViewById(R.id.loginUsername);
        final EditText passwordField = (EditText) findViewById(R.id.loginPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();
                doUser();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();
                doUser();
            }
        });
    }

    private void doUser() {
        User.username = username;
        User.password = password;
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

}
