package com.izk.okhttp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * get、post 请求
 */
public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_click;
    private Button btn_post;
    private Button btn_post2;
    private Button btn_post3;
    private ImageView iv_img;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btn_click = findViewById(R.id.btn_click);
        btn_post = findViewById(R.id.btn_post);
        btn_post2 = findViewById(R.id.btn_post2);
        btn_post3 = findViewById(R.id.btn_post3);
        iv_img = findViewById(R.id.iv_img);
        tv_content = findViewById(R.id.tv_content);

        btn_click.setOnClickListener(this);
        btn_post.setOnClickListener(this);
        btn_post2.setOnClickListener(this);
        btn_post3.setOnClickListener(this);

    }

    //点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_click:
                getRequest();
                break;
            case R.id.btn_post:
                postRequest();
                break;
            case R.id.btn_post2:
                postJsonRequest();
                break;
            case R.id.btn_post3:
                startActivity(new Intent(UserActivity.this,DownloadActivity.class));
                break;
        }
    }

    //get请求
    private void getRequest() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .get()
                .url("http://apis.juhe.cn/mobile/get?phone=15311413124&key=53571dcc6966b339045157ea379754e7")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    final String string = body.string();
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        final String city = (String) jsonObject.opt("reason");
                        final String company = (String) jsonObject.opt("resultcode");


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String url = "http://03.imgmini.eastday.com/mobile/20170105/20170105110355_806f4ed3fe71d04fa452783d6736a02b_1_mwpm_03200403.jpeg";
                                Picasso.get().load(url).resize(200, 200).centerCrop().into(iv_img);
                                tv_content.setText(city + "--" + company);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    //post请求 (Form表单形式进行POST请求)--> 用户登录
    private void postRequest() {
        //创建OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();

        //Form表单形式进行POST请求
        RequestBody body = new FormBody.Builder()
                .add("type", "top")
                .add("key", "897af1512910c56d9302e601612a2f05")
                .build();

        //创建Request
        Request request = new Request.Builder()
                .url(Config.API.BASE_URL + "index")
                .post(body)
                .build();

        //将OkHttpClient配合Request进行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String s = response.body().string();
                    Log.d("onResponse", s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        final String reason = (String) jsonObject.opt("reason");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserActivity.this, "Form表单形式进行POST请求: " + reason, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    //post请求 (JSON形式进行POST请求)--> 用户登录
    private void postJsonRequest() {
        //创建OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .build();

        //RequestBody所需参数
        MediaType type = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("type", "top");
            json.put("key", "897af1512910c56d9302e601612a2f05");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String s = json.toString();
        Log.d("打印json", s);
        //Json形式进行POST请求
        RequestBody body = RequestBody.create(type, s);

        //创建Request
        Request request = new Request.Builder()
                .url(Config.API.BASE_URL + "index")
                .post(body)
                .build();

        //将OkHttpClient配合Request进行请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String s = response.body().string();
                    Log.d("onResponse", s);
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        final String reason = (String) jsonObject.opt("reason");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserActivity.this, "JSON形式进行POST请求: " + reason, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
