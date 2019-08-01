package com.wx1998.perissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请工具类
 *
 */
public class PermissionUtil extends Activity {
    /**
     *  权限需求列表
     */
    static private List<String> permissionList = new ArrayList<>();
    /**
     *  需要再次申请权限的列表
     */
    static private List<String> permissionListShowAgain = new ArrayList<>();
    /**
     *  已经获取权限列表
     */
    static private List<String> permissionListAllow = new ArrayList<>();
    /**
     *  不可再次申请权限的权限
     */
    static private List<String> permissionListDontShow = new ArrayList<>();
    /**
     *  单次获取权限被拒绝权限
     */
    static private List<String> permissionListDenied = new ArrayList<>();

    private static Activity mActivity;
    private static Fragment mFragment;
    private static int mRequestCode;

    private static boolean isAllGranted = true;

    private volatile static RequestPermissionsResultCallBack ResultcallBack = null;


    /**
     * 不允许空参实例化本类
     */
    private void PermissionUtil(){}

    /**
     * 设置回调
     * @param callBack
     */
    public PermissionUtil setCallback(RequestPermissionsResultCallBack callBack){
        this.ResultcallBack = callBack;
        LogUtil.d("-------------------设置回调-----------------------");
        return permissionUtil;
    }

    /**
     * 实例化必须带有上下文
     * @param activity
     * @return
     */
    public PermissionUtil PermissionUtil(Activity activity){
        mActivity = activity;
        return permissionUtil;
    }

    public PermissionUtil PermissionUtil(Fragment mFragment){
        mActivity = mFragment.getActivity();
        return permissionUtil;
    }

    /**
     * 单例模式获取本类实例
     */
    private volatile static PermissionUtil permissionUtil;
    public static PermissionUtil getInstance(Activity activity) {
        mActivity = activity;
        if (permissionUtil == null) {
            synchronized (PermissionUtil.class) {
                if (permissionUtil == null) {
                    permissionUtil = new PermissionUtil();
                }
            }
        }
        return permissionUtil;
    }

    /**
     * 设置所需权限列表
     * @param PermissionList
     * @return  本类实例
     */
    public PermissionUtil setPermissionList(String... PermissionList){
        LogUtil.e("===============根据参数设置权限表=====================");
        //循环将参数列表的内容添加置权限表
        for(String Permission:PermissionList) {
            permissionList.add(Permission);
        }

        //返回此类实例
        return permissionUtil;
    }

    /**
     * 检查是否需要申请权限
     * 此处对
     *     'allowList'
     *     'permissionListDontShow'
     *     'permissionListShowAgain'
     * 进行第一次操作
     * 如果不申请权限，则将这3个List回调给调用者
     * @return
     */
    private boolean needToRequest(){
        permissionListShowAgain.clear();
        permissionListDontShow.clear();
        permissionListAllow.clear();
        //此处开始权限检测
        LogUtil.e("=================开始权限检测================================");
        for (String permission :permissionList) {
            int checkRes = ContextCompat.checkSelfPermission(mActivity, permission);
            if (checkRes == 0){
                //这里的是已经获取到的权限
                LogUtil.i("检测权限:---已经获取---此时checkRes= "+checkRes+"--------"+permission);
                //加入已允许权限列表
                permissionListAllow.add(permission);
            }
            if(checkRes != PackageManager.PERMISSION_GRANTED){
                if (mActivity instanceof Activity && ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                    //这里的是可以申请的权限(用户未点击不再提示)
                    LogUtil.w("检测权限:---可以申请---此时checkRes="+checkRes+"--------"+permission);
                    //加入到可以再次申请列表
                    permissionListShowAgain.add(permission);
                }else{
                    //这里的是不可以申请的权限(用户点击不再提示)
                    LogUtil.e("检测权限:---不可申请---此时checkRes="+checkRes+"--------"+permission);
                    //加入到-不可申请列表
                    permissionListDontShow.add(permission);
                }
            }
        }
        if (permissionListShowAgain.size() > 0) {
            permissionList.clear();
            LogUtil.e("=================刷新可申请权限列表================================");
            permissionList = new ArrayList<>();
            for (int i = 0; i < permissionListShowAgain.size(); i++) {
                LogUtil.w("刷新需求:---添加需求---"+permissionListShowAgain.get(i));
                permissionList.add(permissionListShowAgain.get(i));
            }
            return true;
        }else {
            LogUtil.i("=================无需获取权限================================");
        }
        return false;
    }

    /**
     * 用于fragment中请求权限
     * @param mFragment
     * @return 本类实例
     */
    public PermissionUtil getPermission(Fragment mFragment){
        mActivity = mFragment.getActivity();
        getPermission();
        return permissionUtil;
    }

    /**
     *  用于Activityt中请求权限
     *  根据permissionList内容获取权限
     * 此处对
     *     'allowList'
     *     'permissionListDontShow'
     *     'permissionListShowAgain'
     * 进行第二次操作
     *
     * @return 本类实例
     */
    public PermissionUtil getPermission(){
        this.mRequestCode = 1998;
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("request permission only can run in MainThread!");
        }
        //如果需要申请权限的话则开始申请权限，
        // 如果不需要权限则回调已经全部获取权限给调用者
        if (needToRequest()) {
            LogUtil.e("=================开始动态获取权限================================");
            for (String s: permissionList) {
                LogUtil.d("需求列表:---"+s);}
            ActivityCompat.requestPermissions(this.mActivity,permissionList.toArray(new String[permissionList.size()]),mRequestCode);
        } else {
            LogUtil.d("======================未进行权限申请=======================================");
            //这里未进行申请权限所以不应对List做操作,直接将needToRequest的处理结果回调给调用者
            //已经获取权限
            if (permissionListAllow.size() != 0){
                LogUtil.d("--------------向调用者回调已获取权限列表-----------------------");
                AllowList(permissionListAllow.toArray(new String[permissionListAllow.size()]));
            }
            //用户点击不再显示权限
            if (permissionListDontShow.size() != 0) {
                LogUtil.d("--------------用户点击不再显示的权限-----------------------");
                //调用回调方法
                onDontShowList(permissionListDontShow.toArray(new String[permissionListDontShow.size()]));
            }else{
                LogUtil.d("向调用者回掉权限已经全部获取权限-----");
                //调用回调方法
                onAllowAllPermissions();
            }
        }
        //返回类实例
        return permissionUtil;
    }

    /**
     * 权限结果返回
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        LogUtil.w("-------------------权限处理方法被调用_打印待处理的消息列表-----------------------");
        LogUtil.d("requestCode:"+requestCode);
        for (int i = 0;i<permissions.length;i++) {
            LogUtil.d("permissions["+i+"]："+permissions[i]);}
        for (int i = 0;i<grantResults.length;i++) {
            LogUtil.d("grantResults["+i+"]："+grantResults[i]);}

        if (requestCode == mRequestCode) {
            permissionListDontShow.clear();
            permissionListDenied.clear();

            LogUtil.w("-------------------判断权限申请结果-----------------------");
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,permissions[i])){
                        //此处为用户不再显示的权限
                        LogUtil.w("---Reject!---"+grantResults[i]+"---"+permissions[i]);
                        permissionListDenied.add(permissions[i]);
                    }else {
                        //此处为用户单次拒绝的权限
                        LogUtil.e("---Don't show!---"+grantResults[i]+"---"+permissions[i]);
                        permissionListDontShow.add(permissions[i]);
                    }
                    isAllGranted = false;
                }else {
                    //此处为用户允许的权限
                    LogUtil.i("---Obtained!---"+grantResults[i]+"---"+permissions[i]);
                    permissionListAllow.add(permissions[i]);
                }
            }
        }
        //开始回调给调用者
        CallbackToCaller();
    }

    private void CallbackToCaller(){
        LogUtil.d("-------------------开始回调给调用者-----------------------");
        //权限申请被用户否定后的回调方法,这个主要场景是当用户点击了否定,但未点击不在弹出,
        if (permissionListDenied.size() != 0) {
            onShowAgainList(permissionListDenied.toArray(new String[permissionListDenied.size()]));
        }

        //用户点击不再显示
        if (permissionListDontShow.size() != 0) {
            onDontShowList(permissionListDontShow.toArray(new String[permissionListDontShow.size()]));
        }

        //权限被用户全部可之后回调的方法
        if (isAllGranted) {
            onAllowAllPermissions();
        }

        if(permissionListAllow.size() != 0){
            AllowList(permissionListAllow.toArray(new String[permissionListAllow.size()]));
        }
    }


    /**
     * 得到全部权限
     */
    void onAllowAllPermissions(){
        if (ResultcallBack != null)ResultcallBack.onAllowAll();
        else Log.e(LogUtil.TAG+"_CallBack","-------------------未设置回调-----------------------");
    }

    /**
     *  已获取权限列表
     * @param permissions
     */
    void AllowList(String... permissions){
        if (ResultcallBack != null)ResultcallBack.onAllowList(permissions);
        else Log.e(LogUtil.TAG+"_CallBack","-------------------未设置回调-----------------------");
    }


    /**
     * 单次被否定可以再次申请权限
     * @param permissions
     */
    void onShowAgainList(String... permissions){
        if (ResultcallBack != null)ResultcallBack.onShowAgainList(permissionListDenied.toArray(new String[permissionListDenied.size()]));
        else Log.e(LogUtil.TAG+"_CallBack","-------------------未设置回调-----------------------");

    }

    /**
     *  被用户点击不再显示，不再提醒，不能重新申请权限列表
     * @param permissions
     */
    void onDontShowList(String... permissions){
        if (ResultcallBack != null)ResultcallBack.onDontShowList(permissionListDontShow.toArray(new String[permissionListDontShow.size()]));
        else Log.e(LogUtil.TAG+"_CallBack","-------------------未设置回调-----------------------");
    }

    /**
     * 控制Log输出的方法
     * @param mark
     * @return
     */
    public static PermissionUtil LogSwitch(boolean mark){
        LogUtil.setFlag(mark);
        return permissionUtil;
    }

    /**
     * 权限Log输出的内部类
     */
    public static class LogUtil{
        public static boolean flag = false;
        private final static String TAG = "Permission";
        public static void setFlag(boolean mark){flag = mark;}
        public static void v(String s){if(flag) Log.v(TAG, s);}
        public static void d(String s){if(flag) Log.d(TAG, s);}
        public static void i(String s){if(flag) Log.i(TAG, s);}
        public static void w(String s){if(flag) Log.w(TAG, s);}
        public static void e(String s){if(flag) Log.e(TAG, s);}

    }
}
