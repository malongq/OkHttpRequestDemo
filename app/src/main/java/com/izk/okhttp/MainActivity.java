package com.izk.okhttp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tv_get;
    private TextView tv_post;

    private OkHttpClient client;
    private OkHttpClient client2;
    private Request request;
    private Request request2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_get = findViewById(R.id.tv_get);
        tv_post = findViewById(R.id.tv_post);


        //未加拦截器之前
        //client = new OkHttpClient();
        client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .readTimeout(500, TimeUnit.MILLISECONDS)//请求超时限制
                .build();
        request = new Request.Builder()
                .url("http://apis.juhe.cn/idcard/index?key=e6de57fb8b8ba61b6af71d8124aa68a5&cardno=130434198905176977")
                .build();


        //这种方式与前面的区别就是在构造Request对象时，需要多构造一个RequestBody对象
        // 用它来携带我们要提交的数据。在构造 RequestBody 需要指定MediaType，用于描述请求/响应 body 的内容类型
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        //未加拦截器之前
        //client2 = new OkHttpClient();
        client2 = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .readTimeout(500, TimeUnit.MILLISECONDS)//请求超时限制
                .build();
        RequestBody body = RequestBody.create(mediaType,"");
        request2 = new Request.Builder()
                .url("http://v.juhe.cn/toutiao/index?type=top&key=897af1512910c56d9302e601612a2f05")
                .post(body)
                .build();

    }

    //布局文件中 GET 请求 按钮点击事件函数
    public void getMethod(View view) {

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                //更新UI放到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_get.setText(string);
                    }
                });

            }
        });

    }

    //布局文件中 POST 请求 按钮点击事件函数
    public void postMethod(View view) {

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                //更新UI放到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_post.setText(string);
                    }
                });
            }
        });

    }

    //进入下一个页面
    public void goNextPage(View view) {
        startActivity(new Intent(MainActivity.this,UserActivity.class));
    }
}
