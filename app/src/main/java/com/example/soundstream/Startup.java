package com.example.soundstream;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
public class Startup extends AppCompatActivity {

    TextView status;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        status = findViewById(R.id.textView2);
        status.setText(getString(R.string.status_notConnected));

        queue = Volley.newRequestQueue(this);

        //Get API status
        ((MyApplication) this.getApplication()).setApiPath(getString(R.string.api_url));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    handler.sendMessage(new Message());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.settings:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            connect();
        }
    };

    private void connect(){
        String url = ((MyApplication) this.getApplication()).getApiPath();
        Button btn = findViewById(R.id.button);
        btn.setEnabled(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        status.setText(getString(R.string.status_connected));
                        btn.setEnabled(true);
                        queue.cancelAll("a");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        status.setText(getString(R.string.status_connectionFailed));
                        queue.cancelAll("a");
                    }

                });

        queue.add(jsonObjectRequest);
        status.setText(getString(R.string.status_connecting));
    }

}
