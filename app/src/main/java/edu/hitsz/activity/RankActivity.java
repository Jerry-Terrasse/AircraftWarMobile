package edu.hitsz.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import edu.hitsz.R;
import edu.hitsz.rank.Record;
import edu.hitsz.rank.SQLiteHelper;

public class RankActivity extends AppCompatActivity {
    private static final String TAG = "RankActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        SQLiteHelper sqLiteHelper = new SQLiteHelper(this);

        int score = getIntent().getIntExtra("score", 0);
        int gameType = getIntent().getIntExtra("gameType", 0);
        String name = getIntent().getStringExtra("name");

        String titleStr = "排行榜";
        switch (gameType) {
            case 1:
                titleStr += " - 普通模式";
                break;
            case 2:
                titleStr += " - 简单模式";
                break;
            case 3:
                titleStr += " - 困难模式";
                break;
            default:
                break;
        }

        setTitle(titleStr);

        if(!Objects.equals(name, "")) {
            sqLiteHelper.insertRecord(new Record(name, score, -1, null), gameType);
        }

        ArrayAdapter<Record> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sqLiteHelper.getRecords(gameType));
        adapter.sort((record, t1) -> t1.getScore() - record.getScore());
        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getItem(i).setRank(i + 1);
        }

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RankActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否删除该记录？");
                builder.setPositiveButton("确定", (dialogInterface, i) -> {
                    sqLiteHelper.deleteRecord(adapter.getItem(pos).getRecord_id());
                    adapter.remove(adapter.getItem(pos));
                    adapter.notifyDataSetChanged();
                });
                builder.setNegativeButton("取消", (dialogInterface, i) -> {
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
