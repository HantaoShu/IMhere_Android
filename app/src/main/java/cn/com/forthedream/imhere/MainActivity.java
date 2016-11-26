package cn.com.forthedream.imhere;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    private View perference;
    private View main;

    private ListView listView;
    private static TextView locationTextView;
    private TextView username;
    private IntentFilter locationIntentFilter;
    private LocationReceiver locationReceiver;
    NavigationView navigationView;
    private final static String usernameString="用户名:";
    private final static String locationString="MAC:";
    private static long beforetime=0;
    private final static int UPDATE_LOCATION = 1;
    private final static int NOTIFACTION = 2;
    WebView webView;
    private static String nowMac;
    private static Handler hander;
    private boolean hadNotification = false;
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
        username.setText(usernameString+"tmp");
        locationTextView = (TextView) header.findViewById(R.id.left_bar_location);
        Intent FromLoginintent = getIntent();
        username.setText(FromLoginintent.getStringExtra("username"));
        locationTextView.setText(locationString+"none");
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
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        final com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton rightLowerButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place_light));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_gallery));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_gallery));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_gallery));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightLowerButton)
                .build();
        Log.d("aaa",""+rightLowerMenu.getSubActionItems().size());

        ArrayList<FloatingActionMenu.Item> items=rightLowerMenu.getSubActionItems();
        items.get(1).view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"caonima",Toast.LENGTH_SHORT).show();

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
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
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
                    webView = (WebView) findViewById(R.id.webview);

                    webView.loadUrl("http://imhere.zhranklin.com/items/a/a");

                    main.setVisibility(View.VISIBLE);
                }
                else if (id == R.id.bt_perference){

                }
                else if (id == R.id.bt_weizhi){
                    Log.d("weizhi","weizhi");


                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("http://imhere.zhranklin.com/items/a/a");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        hander=new Handler() {
            public void handleMessage(Message msg){
                switch (msg.what){
                    case UPDATE_LOCATION:
                        Log.d("main","1:"+System.currentTimeMillis());
                        locationTextView.setText(locationString+"none");
                        break;
                    case NOTIFACTION:
                        if(!hadNotification) {
                            Log.d("main","no"+hadNotification);
                            hadNotification = true;
                            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                                    .setContentTitle("有一个新的通知")
                                    .setContentText("通知发生，具体请到app中查看")
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
                }

            }
        };
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (true){
                    Log.d("main", "2:" + System.currentTimeMillis()+"\n is:"+beforetime+"\n less:"+(System.currentTimeMillis()-beforetime));
                    if (System.currentTimeMillis() - beforetime > 1000*10) {
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
            Log.d("main","is"+beforetime);
            locationTextView.setText(locationString+nowMac);
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
    public void initButton(){

    }


}