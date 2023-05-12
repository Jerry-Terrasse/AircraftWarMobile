package edu.hitsz.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private int gameType=0;
    private boolean withMusic = false;
    private BaseGame basGameView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "GameActivity address: " + GameActivity.this);
                int score = msg.arg1; // store first
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                if (msg.what == 1) {
                    Toast.makeText(GameActivity.this,"GameOver",Toast.LENGTH_SHORT).show();

                    // input user name
                    EditText editText = new EditText(GameActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setTitle("游戏结束");
                    builder.setMessage("请输入你的昵称：");
                    builder.setCancelable(false);
                    builder.setView(editText);
                    builder.setPositiveButton("确认", (dialogInterface, i) -> {
                        String name = editText.getText().toString();
                        if(name.length() == 0){
                            Toast.makeText(GameActivity.this,"昵称为空，不进行记录",Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(GameActivity.this, RankActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("score", score);
                        intent.putExtra("gameType", gameType);
                        Log.i(TAG, String.format("name: %s, score: %d, gameType: %d", name, score, gameType));
                        startActivity(intent);
                        finish();
                    });

                    builder.show();
                }
            }
        };
        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
            withMusic = getIntent().getBooleanExtra("withMusic",false);
        }
        if(gameType == 1){
            basGameView = new MediumGame(this, handler, withMusic);

        }else if(gameType == 3){
            basGameView = new HardGame(this, handler, withMusic);
        }else{
            basGameView = new EasyGame(this, handler, withMusic);
        }
        setContentView(basGameView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(basGameView != null){
            basGameView.gameOver();
        }
    }
}