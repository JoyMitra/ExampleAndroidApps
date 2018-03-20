package edu.example.part1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/*
Displays an image in fullscreen using Picasso
 */

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView iv = findViewById(R.id.fullimage);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Picasso.get().load(url).into(iv);
    }
}
