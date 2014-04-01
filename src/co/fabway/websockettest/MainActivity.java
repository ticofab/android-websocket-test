/*
 * 
 *  adding some stuff
 * 
 * 
 * 
 */

package co.fabway.websockettest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;

public class MainActivity extends Activity {

    // patching stuff

    private static final String TAG = "WebsocketTest";
    private static final String IP = "192.168.1.67";
    private final String mLocalhostUri = "ws://" + IP + ":9000/ws2";
    private final String mLocalhostUriTime = "ws://" + IP + ":9000/time";
    private final String mLocalhostUriUpload = "ws://" + IP + ":9000/wsupload";
    private final List<BasicNameValuePair> mExtraHeaders = null;

    private WebSocketClient mWsClient;
    private WebSocketClient mWsTimeClient;
    private WebSocketClient mWsUploadClient;
    private WebSocketClient mWsPhotoClient;
    private TextView mTextView;
    private TextView mSentTV;
    private EditText mEditText;
    private TextView mTimeTV;
    private int mCounter = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSentTV = (TextView) findViewById(R.id.sent_string_text_view);
        mTextView = (TextView) findViewById(R.id.my_text_view);
        mTimeTV = (TextView) findViewById(R.id.time_text_view);
        mEditText = (EditText) findViewById(R.id.my_edit_view);

        mWsUploadClient = new WebSocketClient(URI.create(mLocalhostUriUpload), new Listener() {

            @Override
            public void onMessage(final byte[] data) {
                String message = "couldn't read message";
                try {
                    message = new String(data, "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d(TAG, "Got binary message! " + message);
            }

            @Override
            public void onMessage(final String message) {
                Log.d(TAG, "got: " + message);

            }

            @Override
            public void onError(final Exception error) {
                Log.e(TAG, "error: " + error.getMessage());
            }

            @Override
            public void onDisconnect(final int code, final String reason) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onConnect() {
                final String data = "{\"content\":\"myName\"}";
                try {
                    final String header = "json000000";

                    final byte[] headerBytes = header.getBytes(HTTP.UTF_8);
                    final byte[] dataBytes = data.getBytes(HTTP.UTF_8);

                    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(headerBytes);
                    outputStream.write(dataBytes);
                    final byte[] stuffToSend = outputStream.toByteArray();

                    mWsUploadClient.send(stuffToSend);

                } catch (final UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, mExtraHeaders);

        mWsUploadClient.connect();

        mWsTimeClient = new WebSocketClient(URI.create(mLocalhostUriTime), new Listener() {

            @Override
            public void onMessage(final byte[] data) {
                String message = "couldn't read message";
                try {
                    message = new String(data, "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d(TAG, "Got binary message! " + message);
            }

            @Override
            public void onMessage(final String message) {
                // Log.d(TAG, String.format("Got string message! %s", message));
                setTimeText(message);
            }

            @Override
            public void onError(final Exception error) {
                Log.e(TAG, "Error!", error);
            }

            @Override
            public void onDisconnect(final int code, final String reason) {
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onConnect() {
                Log.d(TAG, "websocket time onConnect");
            }
        }, mExtraHeaders);

        mWsTimeClient.connect();
    }

    // click from xml
    public void sendMessage(final View v) {
        final String mex = mEditText.getText().toString() + " " + mCounter;
        mCounter++;
        mWsClient.send(mex);
        setSentText(mex);
    }

    // click from xml
    public void disconnect(final View v) {
        mWsClient.disconnect();
    }

    // click from xml
    public void connectAndSendPic(final View v) {

    }

    private void setTimeText(final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTimeTV.setText(text);
            }
        });
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWsClient.disconnect();
        mWsTimeClient.disconnect();
    }

    public static byte[] encodeTobase64(final Bitmap image) {
        final Bitmap immagex = image;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] b = baos.toByteArray();
        final byte[] imagebytes = Base64.encode(b, Base64.DEFAULT);
        return imagebytes;
    }

    public static String encodeTobase64String(final Bitmap image) {
        final Bitmap immagex = image;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] b = baos.toByteArray();
        final String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(final String input) {
        final byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private static class JsonPayload {
        public String mHeader;
        public String mContent;

        public static JSONObject toJson(final JsonPayload jsonPayload) throws JSONException {
            final JSONObject json = new JSONObject();
            if (!TextUtils.isEmpty(jsonPayload.mHeader)) {
                json.put("header", jsonPayload.mHeader);
            }
            if (!TextUtils.isEmpty(jsonPayload.mContent)) {
                json.put("content", jsonPayload.mContent);
            }
            return json;
        }

        public static JsonPayload fromJson(final JSONObject json) throws JSONException {
            final JsonPayload pl = new JsonPayload();
            if (json.has("header")) {
                pl.mHeader = json.getString("header");
            }
            if (json.has("content")) {
                pl.mContent = json.getString("content");
            }
            return pl;
        }
    }

}
