package edu.hitsz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static int screenWidth;
    public static int screenHeight;

    private int gameType=0;
    private boolean withMusic = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button medium_btn = findViewById(R.id.medium_btn);
        Button easy_btn = findViewById(R.id.easy_btn);
        Button hard_btn = findViewById(R.id.hard_btn);
        Switch music_switch = findViewById(R.id.withMusic);

        getScreenHW();

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        medium_btn.setOnClickListener(view -> {
            gameType=1;
            intent.putExtra("gameType",gameType);
            intent.putExtra("withMusic", withMusic);
            startActivity(intent);
        });

        easy_btn.setOnClickListener(view -> {
            gameType =2;
            intent.putExtra("gameType",gameType);
            intent.putExtra("withMusic", withMusic);
            startActivity(intent);
        });

        hard_btn.setOnClickListener(view -> {
            gameType =3;
            intent.putExtra("gameType",gameType);
            intent.putExtra("withMusic", withMusic);
            startActivity(intent);
        });

        music_switch.setOnCheckedChangeListener((compoundButton, b) -> withMusic = b);
    }
    public void getScreenHW(){
        //定义DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //窗口的宽度
        screenWidth= dm.widthPixels;
        //窗口高度
        screenHeight = dm.heightPixels;

        Log.i(TAG, "screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}