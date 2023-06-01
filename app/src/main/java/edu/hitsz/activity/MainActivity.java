package edu.hitsz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import edu.hitsz.R;
import edu.hitsz.online.RemoteSession;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static int screenWidth;
    public static int screenHeight;

    private int gameType=0;
    private boolean withMusic = false;
    private boolean loggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button medium_btn = findViewById(R.id.medium_btn);
        Button easy_btn = findViewById(R.id.easy_btn);
        Button hard_btn = findViewById(R.id.hard_btn);
        Switch music_switch = findViewById(R.id.withMusic);

        Button online_btn = findViewById(R.id.online_btn);

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

        online_btn.setOnClickListener(view -> {
            System.out.println("online_btn!!!! loggedIn: "+loggedIn);
            if(loggedIn){
                gameType = 4;
                intent.putExtra("gameType",gameType);
                intent.putExtra("withMusic", withMusic);
                startActivity(intent);
            } else {
                EditText editUsername = new EditText(MainActivity.this);
                EditText editPassword = new EditText(MainActivity.this);
                editUsername.setHint("请输入用户名");
                editPassword.setHint("请输入密码");

                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(editUsername);
                layout.addView(editPassword);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("登录");
                builder.setView(layout);
                builder.setPositiveButton("登录", (dialogInterface, i) -> {
                    String username = editUsername.getText().toString();
                    String password = editPassword.getText().toString();
                    if (username.length() == 0 || password.length() == 0) {
                        Toast.makeText(MainActivity.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                    }

                    new Thread(() -> {
                        boolean res = RemoteSession.getInstance().login(username, password);

                        if (res) {
                            System.out.println("登录成功");
                            loggedIn = true;
                            online_btn.setText("联机模式");

                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else {
                            System.out.println("登录失败");
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }).start();
                });
                builder.show();
            }
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