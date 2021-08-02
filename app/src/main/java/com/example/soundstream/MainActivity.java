package com.example.soundstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    static MainActivity instance;

    SongAdapter adapter;
    ArrayList<Song> songs = new ArrayList<Song>();
    RequestQueue queue;

    MediaPlayer mediaPlayer = new MediaPlayer();

    ListView listView;
    ConstraintLayout topBar;
    ImageView play;
    ImageView cover;
    TextView songPlaying;
    TextView artist;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    SeekBar bar;
    Boolean mpIsPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_view);

        instance=this;

        listView = findViewById(R.id.list_view);
        topBar =findViewById(R.id.top_bar);
        play = findViewById(R.id.music_play);
        cover = findViewById(R.id.cover_image);
        songPlaying = findViewById(R.id.songname);
        artist = findViewById(R.id.artist);
        elapsedTimeLabel = findViewById(R.id.elapsedTime);
        remainingTimeLabel = findViewById(R.id.remainingTime);
        bar = findViewById(R.id.seekBar);

        adapter = new SongAdapter(this, songs, mediaPlayer, mpIsPrepared);
        listView.setAdapter(adapter);


        //get Song infos from API
        String url = ((MyApplication) this.getApplication()).getApiPath() + "&function=getSong";
        queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        songs = Song.fromJson(response);
                        adapter.addAll(songs);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                });

        queue.add(jsonObjectRequest);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    onPauseSong();
                }else{
                    mediaPlayer.start();
                    onResumeSong();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onStopSong();
                mediaPlayer.reset();
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void onPlaySong(Song song) {
        topBar.setVisibility(View.VISIBLE);
        play.setImageResource(R.drawable.ic_pause);
        songPlaying.setText(song.name);
        artist.setText(song.interpret);

        //setup seekbar for song progress and set handler for changes
        bar.setMax(mediaPlayer.getDuration());
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    bar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Thread to update seekbar
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mediaPlayer!=null){
                    try{
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //start download of cover image asynchrounously
        new DownloadImageTask(cover).execute( ((MyApplication) this.getApplication()).getApiPath() +"&function=getCover&fname=" + song.coverFile);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            int currentPosition = msg.what;

            //update bar
            bar.setProgress(currentPosition);

            //update time labels
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);
            String remainingTime = createTimeLabel(mediaPlayer.getDuration()-currentPosition);
            remainingTimeLabel.setText("-" + remainingTime);
        }
    };

    private String createTimeLabel(int currentPosition) {
        String timelabel = "";
        int min= currentPosition/1000/60;
        int sec= currentPosition/1000%60;
        timelabel = min + ":";
        if(sec<10) timelabel +="0";
        timelabel += sec;

        return timelabel;
    }

    public void onResumeSong(){
        play.setImageResource(R.drawable.ic_pause);
    }

    public void onPauseSong(){
        play.setImageResource(R.drawable.ic_play);
    }

    public void onStopSong(){
        play.setImageResource(R.drawable.ic_play);
        topBar.setVisibility(View.GONE);
    }

}
