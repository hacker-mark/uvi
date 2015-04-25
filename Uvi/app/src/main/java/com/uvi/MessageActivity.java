package com.uvi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.uvi.synch.MessageService;
import com.uvi.synch.User;

/**
 * Created by Mark on 18/04/2015.
 */
public class MessageActivity extends Activity {

    private String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);
        bindService(new Intent(this, MessageService.class), serviceConnection, BIND_AUTO_CREATE);
        //get recipientId from the intent
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
        currentUserId = User.username;
        messageBodyField = (EditText) findViewById(R.id.messageBodyField);
        //listen for a click on the send button
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageBody = messageBodyField.getText().toString();
                if (messageBody.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                messageService.sendMessage(currentUserId, messageBody);
                messageBodyField.setText("");
            }
        });
    }
    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = (MessageService.MessageServiceInterface) iBinder;
            Log.d("ASDSDASD", "econnted");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

}
