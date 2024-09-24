package com.nouroeddinne.mp3player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {

    ImageView play,next,previous,loop,back;
    TextView name,curectTime,completTime;
    SeekBar seekBar;

    List<String> list = new ArrayList<>();;
    String uriFile;
    int position;

    Runnable runnable;
    Handler handler;
    int totalTime;

    boolean mode1=true,mode2=false,mode3=false;

    Random rand;
    MediaPlayer mediaPlayer;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        play = findViewById(R.id.imageView6);
        next = findViewById(R.id.imageView7);
        previous = findViewById(R.id.imageView5);
        loop = findViewById(R.id.imageView4);
        back = findViewById(R.id.imageView8);

        name = findViewById(R.id.textView2);
        curectTime = findViewById(R.id.textView3);
        completTime = findViewById(R.id.textView4);

        seekBar = findViewById(R.id.seekBar);

        rand = new Random();
        mediaPlayer = new MediaPlayer();
        animation = AnimationUtils.loadAnimation(PlayerActivity.this,R.anim.tanim);
        name.startAnimation(animation);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            name.setText(extras.getString("name"));
            uriFile = extras.getString("file");
            position = extras.getInt("position");
            list = extras.getStringArrayList("list");

            Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();

        }


        try {
            mediaPlayer.setDataSource(uriFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.baseline_play_circle_24);
                }else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.baseline_pause_circle_24);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                play(false,true);

                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.baseline_pause_circle_24);
                }else {
                    play.setImageResource(R.drawable.baseline_play_circle_24);
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                play(true,false);

                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.baseline_pause_circle_24);
                }else {
                    play.setImageResource(R.drawable.baseline_play_circle_24);
                }

            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                modePlay();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PlayerActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer!=null){
                    totalTime=mediaPlayer.getDuration();
                    seekBar.setMax(totalTime);

                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    handler.postDelayed(runnable, 500);

                    curectTime.setText(time(currentPosition));
                    completTime.setText(time(totalTime));

                    if (curectTime.getText().toString().equals(completTime.getText().toString())){
                        mediaPlayer.reset();

                        play(false,true);

                    }

                }
            }
        };

        handler.post(runnable);
















    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                play.setImageResource(R.drawable.baseline_play_circle_24);
            }

            Intent intent = new Intent(PlayerActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public String time(int currentPosition){

        String currentTime;
        int minute = currentPosition / 1000 / 60;
        int second = currentPosition / 1000 % 60;

        if (second<10){
            currentTime = minute+":0"+second;
        }else {
            currentTime = minute+" : "+second;
        }
        return  currentTime;

    }

    public void modePlay(){

        if (mode1){
            mode1 = false;
            mode2 = true;
            mode3 = false;
            loop.setImageResource(R.drawable.random);
        }else if (mode2){
            mode1 = false;
            mode2 = false;
            mode3 = true;
            loop.setImageResource(R.drawable.repeatonetime);
        }else {
            mode1 = true;
            mode2 = false;
            mode3 = false;
            loop.setImageResource(R.drawable.repeat);
        }

    }

    public void play(boolean previous,boolean next){

        // play random or repet or normal

        if (mode1){

            if (next){
                if (position<list.size()-1){
                    position++;
                }else {
                    position=0;
                }
            }else if (previous){
                if (position>0){
                    position--;
                }else {
                    position=list.size()-1;
                }
            }

        }else if (mode2){
            position = rand.nextInt(list.size()-1);
        }else {
            //Repet the position
        }

        animation.reset();
        name.startAnimation(animation);

        String filename = list.get(position);
        name.setText(filename.substring(filename.lastIndexOf("/") + 1));
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(list.get(position));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




















}