package com.example.soundstream;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> {

    MediaPlayer mediaPlayer;
    Boolean mpIsPrepared;
    Context context;
    public SongAdapter(Context context, ArrayList<Song> songs, MediaPlayer mediaPlayer, Boolean mpIsPrepared) {
        super(context, 0, songs);
        this.context = context;
        this.mediaPlayer = mediaPlayer;
        this.mpIsPrepared = mpIsPrepared;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        Song song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song, parent, false);
        }
        // Lookup view for data population
        TextView songName = (TextView) convertView.findViewById(R.id.song);
        TextView songAlbum = (TextView) convertView.findViewById(R.id.album);
        // Populate the data into the template view using the data object
        songName.setText(song.name);
        songAlbum.setText(song.album);

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        // Cache row position inside the button using `setTag`
        icon.setTag(position);
        // Attach the click event handler
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                Song song = getItem(position);
                onStartSong(song,parent);
            }
        });

        // Return the completed view to render on screen
        return convertView;

    }

    private void onStartSong(Song song,ViewGroup parent){
        if(!mediaPlayer.isPlaying() && mpIsPrepared==false) {
            try {
                String url =  ((MyApplication) MainActivity.getInstance().getApplication()).getApiPath() + "&function=getSongFile&fname=" + song.songFile; // your URL here
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();
                MainActivity.getInstance().onPlaySong(song);
                mpIsPrepared=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            mediaPlayer.stop();
            mediaPlayer.reset();
            MainActivity.getInstance().onStopSong();
            mpIsPrepared=false;
        }
    }
}
