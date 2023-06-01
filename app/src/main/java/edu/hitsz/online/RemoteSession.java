package edu.hitsz.online;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.util.LinkedList;
import java.util.Optional;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.rank.Record;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteSession {
    protected User user = null;
    protected String url = "https://air.terase.cn";

    private static final RemoteSession INSTANCE = new RemoteSession();
    OkHttpClient client = new OkHttpClient();
    private RemoteSession() {}
    public static RemoteSession getInstance() {
        return INSTANCE;
    }

    public boolean login(String username, String password) {
        System.out.printf("trying to login: %s %s\n", username, password);
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(url + "/login")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.body() == null) {
                System.out.println("login failed: response body is null");
                return false;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            if(jsonObject.getString("status").equals("success")) {
                user = new User(username, password);
                System.out.println("login success");
                return true;
            } else {
                System.out.println("login failed " + jsonObject.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean enQueue() {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();
        Request request = new Request.Builder()
                .url(url + "/en_queue")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.body() == null) {
                System.out.println("enqueue failed: response body is null");
                return false;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            if(jsonObject.getString("status").equals("success")) {
                System.out.println("en_queue success");
                return true;
            } else {
                System.out.println("en_queue failed "  + jsonObject.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkBegin() {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();
        Request request = new Request.Builder()
                .url(url + "/check_fighting")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.body() == null) {
                System.out.println("check failed: response body is null");
                return false;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            if(jsonObject.getString("status").equals("success")) {
                System.out.println("check_begin success");
                return true;
            } else {
                System.out.println("check_begin failed " + jsonObject.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void finishGame() {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();
        Request request = new Request.Builder()
                .url(url + "/finish")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.body() == null) {
                System.out.println("finish failed: response body is null");
                return;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            if(jsonObject.getString("status").equals("success")) {
                System.out.println("finish_game success");
            } else {
                System.out.println("finish_game failed " + jsonObject.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public Optional<Integer> syncScore(int myScore) {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .add("score", String.valueOf(myScore))
                .build();
        Request request = new Request.Builder()
                .url(url + "/sync_score")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.body() == null) {
                System.out.println("sync failed: response body is null");
                return Optional.empty();
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            if(jsonObject.getString("status").equals("success")) {
                int opponentScore = jsonObject.getInt("opponent_score");
                System.out.println("sync_score success");
                return Optional.of(opponentScore);
            } else {
                System.out.println("sync_score failed " + jsonObject.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String getUsername() {
        assert user != null;
        return user.getUsername();
    }

    public LinkedList<Record> getRecords() {
        RequestBody requestBody = new FormBody.Builder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();
        Request request = new Request.Builder()
                .url(url + "/get_records")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.body() == null) {
                System.out.println("get records failed: response body is null");
                return null;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            if(jsonObject.getString("status").equals("success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("records");
                LinkedList<Record> records = new LinkedList<>();
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject record = jsonArray.getJSONObject(i);
                    records.add(new Record(record.getString("name"), record.getInt("score"), record.getInt("record_id"), new Date(Long.parseLong(record.getString("date")))));
                }
                System.out.println("get_records success");
                return records;
            } else {
                System.out.println("get_records failed " + jsonObject.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
