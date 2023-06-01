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

import edu.hitsz.R;
import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;
import edu.hitsz.game.OnlineGame;
import edu.hitsz.online.RemoteSession;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private int gameType=0;
    private boolean withMusic = false;
    private BaseGame basGameView = null;

    Handler handler = null;

    protected void startOnlineGame() {
        assert handler != null;
        basGameView = new OnlineGame(GameActivity.this, handler, withMusic);
        setContentView(basGameView);
    }

    protected void waitingForFinish() {
        assert handler != null;
        setContentView(R.layout.waiting_for_finish);
        new Thread(() -> {
             while (true) {
                 boolean is_game_running = RemoteSession.getInstance().checkBegin();
                 if (!is_game_running) {
                     handler.sendEmptyMessage(5); // show online ranking table
                     break;
                 }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
             }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "GameActivity address: " + GameActivity.this);
                int score = msg.arg1; // store first
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                if (msg.what == 1) {

                    if(gameType == 4) {
//                        Toast.makeText(GameActivity.this, "联机游戏结束", Toast.LENGTH_SHORT).show();
                        waitingForFinish();
//                        String name = RemoteSession.getInstance().getUsername();
                    } else {
                        Toast.makeText(GameActivity.this, "GameOver", Toast.LENGTH_SHORT).show();

                        // input user name
                        EditText editText = new EditText(GameActivity.this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                        builder.setTitle("游戏结束");
                        builder.setMessage("请输入你的昵称：");
                        builder.setCancelable(false);
                        builder.setView(editText);
                        builder.setPositiveButton("确认", (dialogInterface, i) -> {
                            String name = editText.getText().toString();
                            if (name.length() == 0) {
                                Toast.makeText(GameActivity.this, "昵称为空，不进行记录", Toast.LENGTH_SHORT).show();
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
                } else if(msg.what == 4) {
                    startOnlineGame();
                } else if(msg.what == 5) {
                    Intent intent = new Intent(GameActivity.this, RankActivity.class);
                    intent.putExtra("gameType", gameType);
                    startActivity(intent);
                    finish();
                }
            }
        };
        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
            withMusic = getIntent().getBooleanExtra("withMusic",false);
        }

        if(gameType == 4) {
            setContentView(R.layout.waiting);
            new Thread(() -> {
                try {
                    boolean en_queue_success = RemoteSession.getInstance().enQueue();
                    if(!en_queue_success){
                        Looper.prepare();
                        Toast.makeText(GameActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return;
                    }
                    while (true) {
                        boolean is_game_start = RemoteSession.getInstance().checkBegin();
                        if (is_game_start) {
//                            Toast.makeText(GameActivity.this,"游戏开始",Toast.LENGTH_SHORT).show();
//                            basGameView = new OnlineGame(this, handler, withMusic);
//                            setContentView(basGameView);
                            Message message = Message.obtain();
                            message.what = 4; // start online game
                            handler.sendMessage(message);
                            break;
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            return;
        }

        if(gameType == 1){
            basGameView = new MediumGame(this, handler, withMusic);
        }else if(gameType == 3) {
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