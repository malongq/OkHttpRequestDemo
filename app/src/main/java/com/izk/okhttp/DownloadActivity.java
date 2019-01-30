package com.izk.okhttp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载
 */
public class DownloadActivity extends AppCompatActivity {

    private Button btn_down;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        btn_down = findViewById(R.id.btn_down);
        progress_bar = findViewById(R.id.progress_bar);

        //请求读写权限
        requestPermission();

    }

    //请求读写权限
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;

    private void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "如要下载文件，请允许该权限，谢谢！", Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                //如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功
                    Toast.makeText(this, "已成功获取权限，谢谢！", Toast.LENGTH_SHORT).show();
                } else {
                    //申请失败
                    Toast.makeText(this, "请求权限时出现未知错误，请重新授权！", Toast.LENGTH_SHORT).show();
                    //继续请求读写权限
                    requestPermission();
                }
            }
        }
    }

    //创建handler用来异步更新ui
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                int progress = msg.arg1;
                progress_bar.setProgress(progress);
            }

        }
    };

    //下载按钮
    public void btn_onclick(View view) {
        //请求网络下载文件
        requestNet();
    }

    //请求网络下载文件
    private void requestNet() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).readTimeout(10000, TimeUnit.MILLISECONDS).build();

        String url = "http://izhikang.vod.weclassroom.com/app-release_394_9_2019-01-14_sign.apk";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                writeFile(response);
            }
        });
    }

    //写文件并更新UI
    private void writeFile(Response response) {
        InputStream is = null;
        FileOutputStream fos = null;

        is = response.body().byteStream();//这是后台返回的要下载的数据，是以流的形式返回的


        String path = Environment.getExternalStorageDirectory().getAbsolutePath();//这个是因为创建File需要参数文件路径

        Log.i("path","path:     "+path);

        File file = new File(path, "aaaaa.apk");//创建这个是因为创建FileOutputStream需要参数  第二个参数是文件名称，也可以是截取出来的，这里只是随便定义
        try {
            fos = new FileOutputStream(file);//创建这个是因为要讲下载的文件写道本地文件

            //流读写
            int len = 0;
            long totalSize = response.body().contentLength();//文件总大小
            Log.d("totalSize",totalSize+"");
            long sum = 0;

            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {//读

                fos.write(bytes);//写

                sum += len;
                int p = (int) ((sum * 1.0f / totalSize) * 100);

                Log.d("progress",p+"");

                Message message = handler.obtainMessage(1);
                message.arg1 = p;
                handler.sendMessage(message);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
