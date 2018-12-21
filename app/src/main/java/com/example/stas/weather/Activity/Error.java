package com.example.stas.weather.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.stas.weather.Objects.Data;
import com.example.stas.weather.R;

public class Error extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_error);
        Intent intent = getIntent();
        Data input = intent.getParcelableExtra("err");

        ((TextView) findViewById(R.id.tvError)).setText(input.err);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
