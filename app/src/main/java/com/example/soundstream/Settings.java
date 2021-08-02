package com.example.soundstream;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText editText = findViewById(R.id.editText);
        editText.setText(((MyApplication) this.getApplication()).getApiPath());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((MyApplication) getApplication()).setApiPath(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
