package com.wx1998.test;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.wx1998.perissions.PermissionUtil;
import com.wx1998.perissions.RequestPermissionsResultCallBack;

public class MainActivity extends Activity {

    public String TAG = "MainActivity";


    TextView permissionListAllow ;
    TextView permissionListDenied ;
    TextView permissionListDontShow;
    TextView onAllowAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        GetPermission();
    }

    private void initView(){
        permissionListAllow = findViewById(R.id.permissionListAllow);
        permissionListDenied = findViewById(R.id.permissionListDenied);
        permissionListDontShow = findViewById(R.id.permissionListDontShow);
        onAllowAll = findViewById(R.id.onAllowAll);

    }


    private void GetPermission(){
        //用来控制Log的开关
        PermissionUtil.LogSwitch(true);
        /**
         * 用来接收获取权限状态的回调
         */
        RequestPermissionsResultCallBack CallBack = new RequestPermissionsResultCallBack() {
            @Override
            public void onAllowAll() {
                onAllowAll.setText("已经获取全部权限");
            }

            @Override
            public void onAllowList(String... permissions) {
                String s = "";
                for (String p:permissions){p= p+"\n";s += p;}
                permissionListAllow.setText(s);
            }

            @Override
            public void onShowAgainList(String... permissions) {
                String s = "";
                for (String p:permissions){p= p+"\n"; s += p;}
                permissionListDenied.setText(s);
            }

            @Override
            public void onDontShowList(String... permissions) {
                String s = "";
                for (String p:permissions){p= p+"\n"; s += p;}
                permissionListDontShow.setText(s);
            }
        };

        PermissionUtil.getInstance(this)
                //设置回调
                .setCallback(CallBack)
                //设置需求权限
                .setPermissionList(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                //获取权限
                .getPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //必须在此处调用此方法
        PermissionUtil.getInstance(this).onRequestPermissionResult(requestCode,permissions,grantResults);
    }


}
