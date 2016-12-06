package cn.com.forthedream.imhere;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;

import static java.lang.Math.abs;
import static java.lang.Math.pow;


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    private View perference;
    private View main;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private Uri imageUri;
    private ListView listView;
    private TextView username;
    private IntentFilter locationIntentFilter;
    private LocationReceiver locationReceiver;
    NavigationView navigationView;
    private final static String usernameString="用户名:";
    private static long beforetime=0;
    private final static int UPDATE_LOCATION = 1;
    private final static int NOTIFACTION = 2;
    private final static int CHANGE = 3;
    WebView webView;
    private static String nowMac;
    private static String uuid="-";
    private static int txpower;
    private static double distence;
    private static Handler hander;
    private String Name;
    private boolean hadNotification = false;
    private static long beforeshuaxintime=0;
    private static int maxdist = 10000;
    private static final int REQUEST_GET_THE_THUMBNAIL = 4000;
    private static final long ANIMATION_DURATION = 200;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private  final static String IP="http://192.168.1.101:8082";
    private static String name;
    private static String UUID = "e20a39f4-73f5-4bc4-a12f-17d1ad07a961";
    NotificationManager manger;
    @SuppressLint("SetTextI18n")
    private void init(){
        main = findViewById(R.id.main);
        assert main != null;
        main.setVisibility(View.VISIBLE);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        View header = navigationView.getHeaderView(0);
        username = (TextView) header.findViewById(R.id.left_bar_username);
        username.setText(usernameString+"sht");
        Intent FromLoginintent = getIntent();
        //username.setText(FromLoginintent.getStringExtra("username"));
        locationIntentFilter = new IntentFilter();
        locationIntentFilter.addAction("cn.com.forthedream.locationreceiver");
        locationReceiver = new LocationReceiver();
        registerReceiver(locationReceiver,locationIntentFilter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        init();
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        final com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton rightLowerButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);


        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.addyemian));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.addzhaopian));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.addricheng));


        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .attachTo(rightLowerButton)
                .build();
        Log.d("aaa",""+rightLowerMenu.getSubActionItems().size());

        ArrayList<FloatingActionMenu.Item> items=rightLowerMenu.getSubActionItems();
        items.get(2).view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"caonima",Toast.LENGTH_SHORT).show();
                webView.loadUrl("http://imhere.zhranklin.com/new_richeng");
            }
        });
        items.get(1).view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(IP+"/print");
            }
        });
        items.get(0).view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://imhere.zhranklin.com/new_url/"+Name+"/"+uuid);
            }
        });
        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerBut ton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });
        Intent intent = new Intent(this,LocationService.class);
        intent.putExtra("test","cccc");
        startService(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                if (id == R.id.bt_homepage) {
                    Log.d("weizhi","home");
                    // Handle the camera action
                    webView.loadUrl("http://imhere.zhranklin.com/items/"+uuid+"/"+Name);
                }
                else if (id == R.id.bt_richeng) {
                    webView.loadUrl("http://imhere.zhranklin.com/richeng/"+Name);
                    // webView.loadUrl("http://192.168.4.117:8082/print");
                }
                else if (id == R.id.bt_config){
                    webView.loadUrl("http://imhere.zhranklin.com/pref/"+Name);
                }
                else if (id== R.id.bt_perference) {
                    webView.loadUrl("http://imhere.zhranklin.com/profile/"+Name);
                }
                else if (id== R.id.bt_weizhi){
                    String url = "http://imhere.zhranklin.com/place_detail/-"+"/-"+"/-"+"/-"+"/-";
                    webView.loadUrl(url);
                }
                else if (id == R.id.bt_dayin){
                    webView.loadUrl(IP+"/print/files");
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("http://imhere.zhranklin.com/items/"+uuid+"/"+Name);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        hander=new Handler() {
            public void handleMessage(Message msg){
                switch (msg.what){
                    case UPDATE_LOCATION:
                        Log.d("main","1:"+System.currentTimeMillis());
                        break;
                    case NOTIFACTION:
                        Log.d("main",webView.getUrl());
                        Pattern r = Pattern.compile(".*?place_detail");
                        Matcher m = r.matcher(webView.getUrl());
                        if(m.find()){
                            Log.d("main","caonimacaonima");

                            String url = "http://imhere.zhranklin.com/place_detail/"+uuid+"/"+name+"/"+ nowMac+"/"+txpower+"/"+String.format("%.2f",distence);
                            if(!url.equals(webView.getUrl()))
                                webView.loadUrl(url);
                        }
                        r = Pattern.compile(".*?items.*");
                        m = r.matcher(webView.getUrl());
                        if(m.find()){
                            String url = "http://imhere.zhranklin.com/items/"+uuid+"/"+Name;
                            webView.loadUrl(url);
                        }
                        if(!hadNotification) {

                            Log.d("main","no"+hadNotification);
                            hadNotification = true;
                            String tmp [] = {"","二基楼","教学楼","校车站","宿舍","江安花园站","红旗超市"};
                            int now = uuid.charAt(uuid.length()-1)-'0';

                            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                                    .setContentTitle("有一个新的通知")
                                    .setContentText("您已到达"+tmp[now]+"，点击此处查看")
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.ic_menu_manage)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setTicker("IMHere有新通知");
                            Intent it = new Intent(MainActivity.this,MainActivity.class);
                            PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this,0,it,PendingIntent.FLAG_CANCEL_CURRENT);
                            builder.setContentIntent(pIntent);
                            Notification notification = builder.build();
                            manger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manger.notify(99,notification);
                        }
                        break;
                    case CHANGE:
                        r = Pattern.compile(".*?place_detail");
                        m = r.matcher(webView.getUrl() + "a");
                        if (m.find()) {
                            String url = "http://imhere.zhranklin.com/place_detail/" + "-" + "/" + "-" + "/" + "-" + "/" + "-" + "/" + "-";
                            webView.loadUrl(url);
                        }
                        r = Pattern.compile(".*?items.*");
                        m = r.matcher(webView.getUrl());
                        if(m.find()){
                            String url = "http://imhere.zhranklin.com/items/"+uuid+"/"+Name;
                            webView.loadUrl(url);
                        }


                }

            }
        };
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (true){
                    Log.d("main", "2:" + System.currentTimeMillis()+"\n is:"+beforetime+"\n less:"+(System.currentTimeMillis()-beforetime));
                    if (System.currentTimeMillis() - beforetime > 1000*5 || distence>18) {
                        if(webView!=null) {
                                Message message = new Message();
                                message.what = CHANGE;
                                hander.sendMessage(message);

                        }
                        Log.d("main","in");
                        Message message = new Message();
                        message.what = UPDATE_LOCATION;
                        hander.sendMessage(message);
                        Log.d("main","cgfalse");
                        try {
                            manger.cancel(99);
                        }catch (Exception e){}
                        hadNotification=false;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Intent backintent = getIntent();
        Name = backintent.getStringExtra("name");
        Boolean f = backintent.getBooleanExtra("caonima",false);
        if(f){
            webView.loadUrl("http://imhere.zhranklin.com/register");
        }
        username.setText(usernameString+backintent.getStringExtra("name"));
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(getBaseContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
		/*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
		    drawer.closeDrawer(GravityCompat.START);
		} else {
		    super.onBackPressed();
		}*/
        moveTaskToBack(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bt_weizhi) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class LocationReceiver extends BroadcastReceiver{

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            nowMac = intent.getStringExtra("MAC");
            beforetime = intent.getLongExtra("time",0);
            txpower = intent.getIntExtra("power",0);
            uuid = intent.getStringExtra("uuid");
            distence=(abs(txpower)-59)/20.0;
            distence = pow(10,distence)*5;
            Log.d("main","dis:"+distence);
            Log.d("main","is"+beforetime);
            Log.d("main","nowmac"+nowMac);
                Message message = new Message();
                message.what = NOTIFACTION;
                hander.sendMessage(message);

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReceiver);
    }



}