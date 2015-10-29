package com.ran.rocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 启动火箭
     *
     * @param view
     */
    public void startRocket(View view) {
        startService(new Intent(this, RocketService.class));
        finish();
    }

    /**
     * 停止火箭
     *
     * @param view
     */
    public void stopRocket(View view) {
        stopService(new Intent(this, RocketService.class));
        finish();
    }
}
