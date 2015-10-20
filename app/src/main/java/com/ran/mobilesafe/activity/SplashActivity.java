package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    private final String HTTP_URL = "http://10.10.11.126:8080/update.do";    // 服务器最新版本信息

    private final int CODE_UPDATE_APP = 0;      // 更新app状态码
    private final int CODE_URL_ERROR = 1;       // url错误状态码
    private final int CODE_NET_ERROR = 2;       // 网络错误状态码
    private final int CODE_JSON_ERROR = 3;      // json解析错误状态码
    private final int CODE_ENTER_HOME = 4;      // 进入主页状态码

    private TextView tv_version;
    private TextView tv_down;
    private RelativeLayout rl_root;

    private String versionName;     // 服务器新版本名
    private int versionCode;        // 服务器新版本号
    private String description;       // 服务器新版本描述
    private String downUrl;         // 服务器新版下载地址

    private SharedPreferences preferences;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_APP:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url解析出错", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "json解析出错", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_down = (TextView) findViewById(R.id.tv_down);

        tv_version.setText("版本名:" + getVersionName());

        rl_root = (RelativeLayout) findViewById(R.id.rl_root);

        // 判断是否需要自动更新
        preferences = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);
        if (preferences.getBoolean(ParameterUtils.AUTO_UPDATE, true)) {
            checkVersion();
        } else {
            myHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, ParameterUtils.SPLASH_TIME); // 延迟2秒发送
        }

        // 渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1);
        anim.setDuration(ParameterUtils.SPLASH_TIME);
        rl_root.startAnimation(anim);
    }

    /**
     * 校验服务器版本
     */
    private void checkVersion() {
        final long startTime = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message msg = myHandler.obtainMessage();
                try {
                    // 本机地址用localhost, 但是如果用模拟器加载本机的地址时,可以用ip(10.0.2.2)来替换
                    URL url = new URL(HTTP_URL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000); // 连接超时
                    conn.setReadTimeout(5000);  // 响应超时
                    conn.connect(); // 连接服务器

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        String result = StreamUtils.readFromStream(conn.getInputStream());
                        JSONObject json = new JSONObject(result);
                        versionName = json.getString("versionName");
                        versionCode = json.getInt("versionCode");
                        description = json.getString("description");
                        downUrl = json.getString("downloadUrl");

                        if (versionCode > getVersionCode()) {
                            msg.what = CODE_UPDATE_APP;
                        } else {
                            // 没有版本更新
                            msg.what = CODE_ENTER_HOME;
                        }
                    } else {
                        // 服务器响应码错误
                        msg.what = CODE_NET_ERROR;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = CODE_NET_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = CODE_URL_ERROR; // url解析出现问题
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = CODE_JSON_ERROR; // json数据解析出现问题
                } finally {
                    if (conn != null) {
                        conn.disconnect(); // 释放网络连接
                    }
                    long endTime = System.currentTimeMillis();
                    long usedTime = endTime - startTime;
                    if (usedTime < ParameterUtils.SPLASH_TIME) {
                        try {
                            Thread.sleep(ParameterUtils.SPLASH_TIME - usedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    myHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + versionName);
        builder.setMessage(description);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whic) {
                Log.i(TAG, "立即升级");
                download();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        // 设置取消的监听,用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });

        builder.show();
    }

    /**
     * 进入主页
     */
    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // 销毁当前页面,无法返回
    }

    /**
     * 下载新版app
     */
    private void download() {
        // 校验sd卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tv_down.setVisibility(View.VISIBLE);    // 显示进度

            String path = Environment.getExternalStorageDirectory() + "/update.apk";

            HttpUtils utils = new HttpUtils();
            utils.download(downUrl, path, new RequestCallBack<File>() {

                @Override
                public void onStart() {
                    tv_down.setText("开始下载...");
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    tv_down.setText("下载进度:" + current * 100 / total + "%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    tv_down.setText("下载完成:" + responseInfo.result.getPath());
                    // 跳转到系统安装页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    // startActivity(intent);
                    startActivityForResult(intent, 0);// 如果用户取消安装的话,会返回结果,回调方法onActivityResult
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    tv_down.setText("下载失败:" + s);
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "未发现sd卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 如果用户取消安装,调此方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取当前app版本名
     *
     * @return string
     */
    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前app版本号
     *
     * @return int
     */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
