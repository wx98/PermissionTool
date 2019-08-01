package com.wx1998.perissions;

public interface RequestPermissionsResultCallBack {


    /**
     * 当全部权限的申请被用户允许之后,该方法会被调用
     */
    void onAllowAll();

    /**
     * 已经获得权限列表
     * @param permissions
     */
    void onAllowList(String... permissions);

    /**
     * 当权限申请中的某一个或多个权限,被用户否定了,但没有确认不再提醒时,也就是权限窗体申请时,但被否定了之后,
     * 该方法将会被调用.
     * @param permissions
     */
    void onShowAgainList(String... permissions);


    /**
     * 当权限申请中的某一个或多个权限,被用户以前否定了,并确认了不再提醒时,也就是权限的申请窗体不能再弹出时,
     * 该方法将会被调用
     * @param permissions
     */
    void onDontShowList(String... permissions);

}
