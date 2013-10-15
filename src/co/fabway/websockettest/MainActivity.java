package co.fabway.websockettest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;

public class MainActivity extends Activity {

    final String                   TAG           = "WebsocketTest";
    final String                   mLocalhostUri = "ws://192.178.10.38:9000/ws";
    final List<BasicNameValuePair> mExtraHeaders = null;
    WebSocketClient mWsClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                ((TextView) findViewById(R.id.my_text_view)).setText(message); 
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));
                ((TextView) findViewById(R.id.my_text_view)).setText(message); 

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
                sendMessage();
            }
        }, mExtraHeaders);

        mWsClient.connect();

        //        wsClient.disconnect();
    }
    
    private void sendMessage() {
        mWsClient.send("hello from android!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}