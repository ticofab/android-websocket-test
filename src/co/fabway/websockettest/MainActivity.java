package co.fabway.websockettest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;

public class MainActivity extends Activity {

    private final String                   TAG           = "WebsocketTest";
    private final String                   mLocalhostUri = "ws://192.178.10.38:9000/ws2";
    private final List<BasicNameValuePair> mExtraHeaders = null;

    private WebSocketClient                mWsClient;
    private TextView                       mTextView;
    private TextView                       mSentTV;
    private EditText                       mEditText;
    private int                            mCounter      = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSentTV = (TextView) findViewById(R.id.sent_string_text_view);
        mTextView = (TextView) findViewById(R.id.my_text_view);
        mEditText = (EditText) findViewById(R.id.my_edit_view);

        mWsClient = new WebSocketClient(URI.create(mLocalhostUri), new Listener() {

            @Override
            public void onMessage(byte[] data) {
                String message = "couldn't read message";
                try {
                    message = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d(TAG, "Got binary message! " + message);
                setText(message);
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));
                setText(message);
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error!", error);
            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onConnect() {
                Log.d(TAG, "websocket onConnect");
                sendMessage(null);
            }
        }, mExtraHeaders);

        mWsClient.connect();
    }

    // click from xml
    public void sendMessage(View v) {
        String mex = mEditText.getText().toString() + " " + mCounter;
        mCounter++;
        mWsClient.send(mex);
        setSentText(mex);
    }

    // click from xml
    public void disconnect(View v) {
        mWsClient.disconnect();
    }
    
    private void setSentText(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mSentTV.setText(text);
            }
        });  
    }

    private void setText(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
