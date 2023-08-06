package com.example.musictn;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicView extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView songName,startTime,EndTime;
  ImageView btnPlay, btnPrevious, btnNext;
  ArrayList songs;
  MediaPlayer mediaPlayer;
  String textContent;
  int position;
  SeekBar seekBar;
  thread updateSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_view);
        songName = findViewById(R.id.songName);
        btnPlay =findViewById(R.id.play);
        btnPrevious = findViewById(R.id.previous);
        btnNext = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        startTime = findViewById(R.id.startTime);
        EndTime = findViewById(R.id.EndTime);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs =(ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        songName.setText(textContent);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer= MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        //jb user change kre seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if(mediaPlayer!=null && fromUser){
                  mediaPlayer.seekTo(progress);
              }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new thread(){
            @Override
            public void run(){
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition=0;
                     while(currentPosition <totalDuration){
                         try {
                             sleep(500);
                             currentPosition = mediaPlayer.getCurrentPosition();
                             seekBar.setProgress(currentPosition);
                         }catch (InterruptedException | IllegalStateException e){
                             e.printStackTrace();
                         }
                     }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeek.start();
        String endTime = createTime(mediaPlayer.getDuration());
        EndTime.setText(endTime);

        // updating the time When seekbar is update by touching
        final Handler handler = new Handler();
        final int delay  = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                startTime.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);
        //play button
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    btnPlay.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        //when songs end auto jump to next
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });
        //previous button
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                        position = position-1;
                }
                else{
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
                textContent = songs.get(position).toString();
                songName.setText(textContent);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());


            }
        });
        //next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position+1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
                textContent = songs.get(position).toString();
                songName.setText(textContent);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });

    }
    //creating time here
    public String createTime(int duration){
        String Time  = "";
        int min = duration/1000/60;
        int second = duration/1000%60;
        Time = Time+min+":";
        if(second <10){
            Time +="0";
        }
        Time +=second;
        return Time;
    }
}