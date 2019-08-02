# PermissionTool - Wx
## 引入工具类
### 直接引入jar
[PermissionToool.jar]()   
<pre>
dependencies {   
    compile files('libs/PerissionsTool.jar')
}   
</pre>   
### 远程引用   
<pre>
allprojects {   
    repositories {
        maven { url 'https://jitpack.io' }   
    }   
}   
dependencies {   
    compile 'com.github.wx98:PermissionTool:v3.0'
}
</pre>
## 如何使用
### 引入
<pre>
import com.wx1998.perissions.PermissionsTool;   
import com.wx1998.perissions.RequestPermissionsResultCallBack;
</pre>
### 获取本类实例
<pre>
PermissionsTool.getInstance(this);
</pre>
### 设置回调setCallback(CallBack)
## 方法
