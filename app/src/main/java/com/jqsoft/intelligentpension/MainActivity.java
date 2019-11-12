package com.jqsoft.intelligentpension;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.webkit.JavascriptInterface;

import android.webkit.WebView;
import android.widget.Toast;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebViewClient;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {
    private X5WebView mWebView;



    NetWorkStateReceiver netWorkStateReceiver;
    private String myurl="";


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
//
//        ImmersionBar.with(this)
//                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
//                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
//                .statusBarColor(R.color.colorPrimary)     //状态栏颜色，不写默认透明色
//                .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
//                .statusBarAlpha(0.3f)  //状态栏透明度，不写默认0.0f
//                .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
//                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
//                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
//                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
//                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
//                .autoStatusBarDarkModeEnable(true,0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
//                .autoNavigationBarDarkModeEnable(true,0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
//                .fullScreen(true)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
//
//                .fitsSystemWindows(true)    //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
//                .supportActionBar(true) //支持ActionBar使用
//
//                .removeSupportAllView() //移除全部view支持
//                .navigationBarEnable(true)   //是否可以修改导航栏颜色，默认为true
//                .navigationBarWithKitkatEnable(true)  //是否可以修改安卓4.4和emui3.x手机导航栏颜色，默认为true
//                .navigationBarWithEMUI3Enable(true) //是否可以修改emui3.x手机导航栏颜色，默认为true
//                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  //单独指定软键盘模式
//                .addTag("tag")  //给以上设置的参数打标记
//                .getTag("tag")  //根据tag获得沉浸式参数
//                .reset()  //重置所以沉浸式参数
//                .init();  //必须调用方可应用以上所配置的参数

        EventBus.getDefault().register(this);

        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;


        mWebView=(X5WebView)findViewById(R.id.webview);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);     //设置js可以直接打开窗口，如window.open()，默认为false
        mWebView.getSettings().setJavaScriptEnabled(true);     //是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebView.getSettings().setSupportZoom(true);           //是否可以缩放，默认true
        mWebView.getSettings().setAllowFileAccess(true);       // 设置允许访问文件数据
        mWebView.getSettings().setBuiltInZoomControls(true);   //是否显示缩放按钮，默认false
        mWebView.getSettings().setUseWideViewPort(true);       //设置此属性，可任意比例缩放。大视图模式
        mWebView.getSettings().setLoadWithOverviewMode(true);  //和setUseWideViewPort(true)一起解决网页自适应问题
        mWebView.getSettings().setAppCacheEnabled(true);       //是否使用缓存
        mWebView.getSettings().setDomStorageEnabled(true);     //DOM Storage


        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
//           mWebView.addJavascriptInterface(new JsInterface(MainActivity.this), "PartnerHome");





        mWebView.setVerticalScrollbarOverlay(true);
        //mWebView
        mWebView.getSettings().setJavaScriptEnabled(true);


        //支持缩放
//        mWebView.getSettings().setSupportZoom(true);
////设置出现缩放工具
//        mWebView.getSettings().setBuiltInZoomControls(true);
////扩大比例的缩放
//        mWebView.getSettings().setUseWideViewPort(true);
////js交互
//        mWebView.getSettings().setJavaScriptEnabled(true);
////        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        mWebView.getSettings().setLoadWithOverviewMode(true);

//        String url="https://www.cnblogs.com/dame/p/8085917.html";
//        String url="http://192.168.44.140:8091/DisplayPages/RoomElderInfo/?mac="+mymei;
//       String url="http://rjkfyy.365yiyang.com/DisplayPages/RoomElderInfo/?mac="+mymei;


        String url="http://app.jg.365yiyang.com/Home/Index/";

//        String url="http://wit.365yiyang.com/DisplayPages/NurseStation/%E9%9D%99%E5%AE%89/FloorDetailInfo.aspx?speed=30&floorID=51e419db-cc09-4480-9986-6f035b1eb9a9&ddlOrg=39008901-68d7-4fa5-bd04-959976764d8a";

//        String url="http://10.1.82.8:8090/test/floor12.html";

        myurl=url;
        mWebView.loadUrl(url);

        Beta.checkUpgrade();

//        设置是否显示消息通知
//        如果你不想在通知栏显示下载进度，你可以将这个接口设置为false，默认值为true。
        Beta.enableNotification = true;


        /* 设置更新状态回调接口 */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeSuccess(boolean isManual) {

            }

            @Override
            public void onUpgradeFailed(boolean isManual) {

            }

            @Override
            public void onUpgrading(boolean isManual) {

            }

            @Override
            public void onDownloadCompleted(boolean b) {

            }

            @Override
            public void onUpgradeNoVersion(boolean isManual) {

            }
        };



        installProcess();

    }


    //安装应用的流程
    private void installProcess() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {//没有权限
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                builder.setTitle("提示");
                builder.setMessage("安装应用需要打开未知来源权限，请去设置中开启权限!");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startInstallPermissionSettingActivity();
                        }
                    }
                });


            }
        }
        //有权限，开始安装应用程序

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10086);
    }



    @JavascriptInterface
    public void setTimeOut(){
        //MyDebug.log(TAG,"setTimeOut");
        Log.e("tst","setTimeOut");
    }



    /**
     * 设置点击返回按钮，跳转到上一个html页面，而不是退出当前activity
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                //获取webView的浏览记录
                WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
                //这里的判断是为了让页面在有上一个页面的情况下，跳转到上一个html页面，而不是退出当前activity
                if (mWebBackForwardList.getCurrentIndex() > 0) {
                    String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getUrl();
                    if (!historyUrl.contains("Home/Index")) {

                        mWebView.goBack();
                        return true;
                    } else {
                        exit();
                        return true;
                    }
                }
            } else {
                exit();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private long mExitTime;
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出!", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    //在onResume()方法注册
    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        System.out.println("注册");
        super.onResume();
    }

    //onPause()方法注销
    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        super.onPause();
    }

    @Subscribe
    public void onEventMainThread(FirstEvent event) {

        String msg =  event.getMsg();
//        Log.d("harvic", msg);
        if(msg==null )
            msg="";
        mWebView.loadUrl(myurl);
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventB
    }
}