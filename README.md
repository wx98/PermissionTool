# PermissionTool - Wx
## 引入工具类
### 直接引入jar
[PermissionToool.jar]()   
''' java   
dependencies {   
    compile files('libs/PerissionsTool.jar')   
    compile files('libs/android-support-v4.jar')   
}   
'''   
### 远程引用   
<pre>
allprojects {   
&#9;repositories {   
        google()   
        jcenter()   
        maven { url 'https://jitpack.io' }   
    }   
}   
dependencies {   
    compile 'com.github.wx98:PermissionTool:v3.0'
}
</pre>
## 如何使用
### 引入
>import com.wx1998.perissions.PermissionsTool;   
import com.wx1998.perissions.RequestPermissionsResultCallBack;
### 获取本类实例
ermissionsTool.getInstance(this);
### 设置回调setCallback(CallBack)
## 方法
