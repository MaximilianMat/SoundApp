package com.example.soundstream;

import android.app.Application;

public class MyApplication extends Application {

    private String apiPath;

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
