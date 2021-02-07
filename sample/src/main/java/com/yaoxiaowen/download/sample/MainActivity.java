package com.yaoxiaowen.download.sample;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.squareup.haha.perflib.Main;
import com.yaoxiaowen.download.DownloadConstant;
import com.yaoxiaowen.download.DownloadStatus;
import com.yaoxiaowen.download.FileInfo;
import com.yaoxiaowen.download.DownloadHelper;
import com.yaoxiaowen.download.sample.utils.NotificationsCheckUtil;
import com.yaoxiaowen.download.sample.utils.Utils_Parse;
import com.yaoxiaowen.download.utils.DebugUtils;
import com.yaoxiaowen.download.utils.LogUtils;
import com.yaoxiaowen.download.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author   www.yaoxiaowen.com
 * time:  2017/12/20 20:23
 * @since 1.0.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    
    public static final String TAG = "weny SimpleMainActivity";

    //Todo 同程旅游的下载地址 ：     "http://s.ly.com/tTV79";
    //为什么下载不下来，这个 网页做了什么, 回头要研究

    //豌豆荚 app 下载地址
    private static final String firstUrl = Constanst.WAN_DOU_JIA_URL;
    private File firstFile;
    private String firstName = Constanst.WAN_DOU_JIA_NAME;
    private static final String FIRST_ACTION = "download_helper_first_action";


    //美团 app 下载地址
    private static final String secondUrl = Constanst.MEI_TUAN_URL;
    private File secondFile;
    private String secondName = Constanst.MEI_TUAN_NAME;
    private static final String SECOND_ACTION = "download_helper_second_action";

    // 12306 APP 下载地址
    private static final String thirdUrl = Constanst.TRAIN_12306_URL;
    private File thirdFile;
    private String thirdName = Constanst.TRAIN_12306_NAME;
    private static final String THIRD_ACTION = "download_helper_third_action";


    private File fourFile;
    private String fourName = Constanst.TRAIN_12306_NAME;
    private static final String FOUR_ACTION = "download_helper_four_action";

    private DownloadHelper mDownloadHelper;
    private File dir;

    private static final String START = "开始";
    private static final String PAUST = "暂停";


    private static int textColor1 = Color.parseColor("#333333");
    private static int textColor2 = Color.parseColor("#666666");
    private static int textColor3 = Color.parseColor("#999999");
    private static int textColorBlock = Color.parseColor("#000000");
    private static int textColorRandarRed = Color.parseColor("#FF0000");
    private static int textColorGreen = Color.parseColor("#46BCFF");

    private TextView firstTitle;
    private ProgressBar firstProgressBar;
    private Button firstBtn;

    private TextView secondTitle;
    private ProgressBar secondProgressBar;
    private Button secondBtn;

    private TextView thirdTitle;
    private ProgressBar thirdProgressBar;
    private Button thirdBtn;

    private Button deleteAllBtn;
    private Button jumpTestActyBtn;


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent){
                switch (intent.getAction()){
                    case FIRST_ACTION: {
                        FileInfo firstFileInfo = (FileInfo) intent.getSerializableExtra(DownloadConstant.EXTRA_INTENT_DOWNLOAD);
                        try {
                            updateTextview(firstTitle, firstProgressBar, firstFileInfo, firstName, firstBtn);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case SECOND_ACTION: {
                        FileInfo secondFileInfo = (FileInfo) intent.getSerializableExtra(DownloadConstant.EXTRA_INTENT_DOWNLOAD);
                        try {
                            updateTextview(secondTitle, secondProgressBar, secondFileInfo, secondName, secondBtn);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case THIRD_ACTION: {
                        FileInfo thirdFileInfo = (FileInfo) intent.getSerializableExtra(DownloadConstant.EXTRA_INTENT_DOWNLOAD);
                        try {
                            updateTextview(thirdTitle, thirdProgressBar, thirdFileInfo, thirdName, thirdBtn);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    default:

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.setDebug(true);

        initData();
        initView();
        initListener();
    }

    private void initData(){
        firstFile = new File(getDir(), firstName);
        secondFile = new File(getDir(), secondName);
        thirdFile = new File(getDir(), thirdName);

        mDownloadHelper = DownloadHelper.getInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(FIRST_ACTION);
        filter.addAction(SECOND_ACTION);
        filter.addAction(THIRD_ACTION);

        registerReceiver(receiver, filter);

    }

    private void initView(){
        firstTitle = (TextView) findViewById(R.id.firstTitle);
        firstProgressBar = (ProgressBar) findViewById(R.id.firstProgressBar);
        firstBtn = (Button) findViewById(R.id.firstBtn);
        firstBtn.setText(START);

        secondTitle = (TextView) findViewById(R.id.secondTitle);
        secondProgressBar = (ProgressBar) findViewById(R.id.secondProgressBar);
        secondBtn = (Button) findViewById(R.id.secondBtn);
        secondBtn.setText(START);

        thirdTitle = (TextView) findViewById(R.id.thirdTitle);
        thirdProgressBar = (ProgressBar) findViewById(R.id.thirdProgressBar);
        thirdBtn = (Button) findViewById(R.id.thirdBtn);
        thirdBtn.setText(START);

        deleteAllBtn = (Button) findViewById(R.id.deleteAllBtn);

        jumpTestActyBtn = (Button)findViewById(R.id.jumpTestActyBtn);


        findViewById(R.id.aa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = NotificationsCheckUtil.areNotificationsEnabled(MainActivity.this);
                Log.i("aaa","这是第一个判断权限"+b);
                boolean enabled = isNotificationEnabled(MainActivity.this);
                Log.i("aaa",""+enabled);
                if (!b) {
                    Intent localIntent = new Intent();
                    //直接跳转到应用通知设置的代码：
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        localIntent.putExtra("app_package", MainActivity.this.getPackageName());
                        localIntent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                    } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        localIntent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                    } else {
                        //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 9) {
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                        } else if (Build.VERSION.SDK_INT <= 8) {
                            localIntent.setAction(Intent.ACTION_VIEW);
                            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                            localIntent.putExtra("com.android.settings.ApplicationPkgName", MainActivity.this.getPackageName());
                        }
                    }
                    MainActivity.this.startActivity(localIntent);
                }


               /* SoulPermission.getInstance().checkAndRequestPermissions(
                        Permissions.build(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
                        ),
                        new CheckRequestPermissionsListener() {
                            @Override
                            public void onAllPermissionOk(com.qw.soul.permission.bean.Permission[] allPermissions) {
                                Log.i("aaa","直接走ok里面有了");
                            }

                            @Override
                            public void onPermissionDenied(com.qw.soul.permission.bean.Permission[] refusedPermissions) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
                                        *//*initTopPop();
                                        popupWindow.showAtLocation(b_re,Gravity.CENTER,0,0);*//*
                                        Log.i("aaa","denied---没有");

                                    }else{
                                        Log.i("aaa","有了");


                                    }
                                    }
                            }
                        });*/
            }
        });
    }
    /**
     * 获取通知权限
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private void initListener(){
        firstBtn.setOnClickListener(this);
        secondBtn.setOnClickListener(this);
        thirdBtn.setOnClickListener(this);

        deleteAllBtn.setOnClickListener(this);
        jumpTestActyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.firstBtn:
                onFirstApkClick();
                break;
            case R.id.secondBtn:
                onSecondApkClick();
                break;
            case R.id.thirdBtn:
                onThirdApkClick();
                break;
            case R.id.deleteAllBtn:
                deleteAllFile();
                break;
            case R.id.jumpTestActyBtn:
                Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
                break;
        }
    }



    private File getDir(){
        if (dir!=null && dir.exists()){
            return dir;
        }

        dir = new File(getExternalCacheDir(), "download");
        if (!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    private void onFirstApkClick(){
        String firstContent = firstBtn.getText().toString().trim();
        if (TextUtils.equals(firstContent, START)){
            mDownloadHelper.addTask(firstUrl, firstFile, FIRST_ACTION).submit(MainActivity.this);
            firstBtn.setText(PAUST);
            firstBtn.setBackgroundResource(R.drawable.shape_btn_orangle);
        }else {
            mDownloadHelper.pauseTask(firstUrl, firstFile, FIRST_ACTION).submit(MainActivity.this);
            firstBtn.setText(START);
            firstBtn.setBackgroundResource(R.drawable.shape_btn_blue);
        }
    }

    private void onSecondApkClick(){
        String secondContent = secondBtn.getText().toString().trim();
        if (TextUtils.equals(secondContent, START)){
            mDownloadHelper.addTask(secondUrl, secondFile, SECOND_ACTION).submit(MainActivity.this);
            secondBtn.setText(PAUST);
            secondBtn.setBackgroundResource(R.drawable.shape_btn_orangle);
        }else {
            mDownloadHelper.pauseTask(secondUrl, secondFile, SECOND_ACTION).submit(MainActivity.this);
            secondBtn.setText(START);
            secondBtn.setBackgroundResource(R.drawable.shape_btn_blue);
        }
    }

    private void onThirdApkClick(){
        String thirdContent = thirdBtn.getText().toString().trim();
        if (TextUtils.equals(thirdContent, START)){
            mDownloadHelper.addTask(thirdUrl, thirdFile, THIRD_ACTION).submit(MainActivity.this);
            thirdBtn.setText(PAUST);
            thirdBtn.setBackgroundResource(R.drawable.shape_btn_orangle);
        }else {
            mDownloadHelper.pauseTask(thirdUrl, thirdFile, THIRD_ACTION).submit(MainActivity.this);
            thirdBtn.setText(START);
            thirdBtn.setBackgroundResource(R.drawable.shape_btn_blue);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    private void updateTextview(TextView textView, ProgressBar progressBar,  FileInfo fileInfo, String fileName, Button btn) throws IOException {
        float pro = (float) (fileInfo.getDownloadLocation()*1.0/ fileInfo.getSize());
        int progress = (int)(pro*100);
        float downSize = fileInfo.getDownloadLocation() / 1024.0f / 1024;
        float totalSize = fileInfo.getSize()  / 1024.0f / 1024;

//        StringBuilder sb = new StringBuilder();
        ////        sb.append(fileName  + "\t  ( " + progress + "% )" + "\n");
//        sb.append("状态: " + DebugUtils.getStatusDesc(fileInfo.getDownloadStatus()) + " \t ");
//        sb.append(Utils_Parse.getTwoDecimalsStr(downSize) + "M/" + Utils_Parse.getTwoDecimalsStr(totalSize) + "M\n");

        // 我们将字体颜色设置的好看一些而已
        int count = 0;
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(fileName);
        sb.setSpan(new ForegroundColorSpan(textColorBlock), 0, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        count = sb.length();
        sb.append("\t  ( " + progress + "% )" + "\n");
        sb.setSpan(new ForegroundColorSpan(textColor3), count, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        count = sb.length();
        sb.append("状态:");
        sb.setSpan(new ForegroundColorSpan(textColor2), count, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        count = sb.length();

        sb.append(DebugUtils.getStatusDesc(fileInfo.getDownloadStatus()) + " \t \t\t \t\t\t");
        sb.setSpan(new ForegroundColorSpan(textColorGreen), count, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        count = sb.length();

        sb.append(Utils_Parse.getTwoDecimalsStr(downSize) + "M/" + Utils_Parse.getTwoDecimalsStr(totalSize) + "M\n");
        sb.setSpan(new ForegroundColorSpan(textColorRandarRed), count, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);



        textView.setText(sb);


        progressBar.setProgress(progress);

        if (fileInfo.getDownloadStatus() == DownloadStatus.COMPLETE){
            btn.setText("下载完成");
            btn.setBackgroundColor(0xff5c0d);
            //下载视频的更新图库
            /*saveFile2Album(thirdFile,true);*/
            installApp(firstFile);
        }
    }

    private void deleteAllFile(){
       if (firstFile!=null && firstFile.exists()){
           firstFile.delete();
       }

       if (secondFile!=null && secondFile.exists()){
           secondFile.delete();
       }

       if (thirdFile!=null && thirdFile.exists()){
           thirdFile.delete();
       }
    }
    public void updateUI(final Context context) {
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //此时已在主线程中，可以更新UI了
                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void saveFile2Album(File file, boolean video) throws IOException {
        if (file == null){
            Log.i("aaa","空的");
            return;
        }
        Log.i("aaa","不空----"+file.getPath());
        ContentResolver contentResolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("title", thirdFile+"");
        values.put("_display_name", thirdFile+"");
        values.put("datetaken", System.currentTimeMillis());
        values.put("date_modified", System.currentTimeMillis());
        values.put("date_added", System.currentTimeMillis());
        values.put("_data", thirdFile+"");
        values.put("_size", thirdFile.length());
        Uri uri;
        if (video) {
            values.put("mime_type", "video/mp4");
            values.put("duration", "60000");
            uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            Log.i("aaa","values     ---"+values);
            Log.i("aaa","file     ---"+file);
            Log.i("aaa","走道了video里面了     ---"+uri);
        } else {
            values.put("mime_type", "image/jpeg");
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(thirdFile+""))));
        ContentValues a = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, "file://"+file);
        values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
        Uri r = MainActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, a);        // 最后通知图库更新
        Log.i("aaa","fileName---"+file);
        MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));

    }
    /**
     * 安装新版本应用
     *
     * @param appFile 下载的新apk文件
     */
    private void installApp(File appFile) {
        if (appFile == null || !appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT < 24) {
            intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.senmass.ilock.safedelivery.fileprovider", appFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
