package com.ran.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.service.KillProcessService;
import com.ran.mobilesafe.utils.ServiceStatusUtils;
import com.ran.mobilesafe.utils.SharedPreferencesUtils;

public class TaskManagerSettingActivity extends AppCompatActivity {

    @ViewInject(R.id.cb_checked)
    private CheckBox cb_checked;
    @ViewInject(R.id.cb_kill_process)
    private CheckBox cb_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        initData();
    }

    private void initData() {

    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager_setting);
        ViewUtils.inject(this);

        // 默认不显示系统进程
        boolean isShowed = SharedPreferencesUtils.getBoolean(TaskManagerSettingActivity.this, ParameterUtils.IS_SHOW_SYSTEM, false);

        cb_checked.setChecked(isShowed);

        cb_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.putObject(TaskManagerSettingActivity.this, ParameterUtils.IS_SHOW_SYSTEM, isChecked);
            }
        });

        // 定时清理进程
        final Intent intent = new Intent(this, KillProcessService.class);

        cb_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ServiceStatusUtils.isServiceRunning(TaskManagerSettingActivity.this, KillProcessService.class.getName())){
            cb_kill_process.setChecked(true);
        }else{
            cb_kill_process.setChecked(false);
        }
    }
}
