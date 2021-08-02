package com.example.soundstream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Song {
    public String id;
    public String name;
    public String album;
    public String interpret;
    public String genre;
    public String songFile;
    public String coverFile;

    public Song(JSONObject object){
        try {
            this.name = object.getString("Name");
            this.album = object.getString("Album");
            this.interpret = object.getString("Interpret");
            this.genre = object.getString("Genre");
            this.songFile = object.getString("Dateipfad");
            this.coverFile = object.getString("Coverpfad");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Song> fromJson(JSONArray jsonObjects) {
        ArrayList<Song> users = new ArrayList<Song>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                users.add(new Song(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

}
