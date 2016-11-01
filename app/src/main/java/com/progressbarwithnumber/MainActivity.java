package com.progressbarwithnumber;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    HorizontalProgressBarWithNum progressBarWithNum;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarWithNum= (HorizontalProgressBarWithNum) findViewById(R.id.horizontalProgressBar);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      if(count>=100){
                          count=100;
                      }
                      progressBarWithNum.setProgress(count);
                      count+=10;
                  }
              });
            }
        },1000,1000);
    }
}
