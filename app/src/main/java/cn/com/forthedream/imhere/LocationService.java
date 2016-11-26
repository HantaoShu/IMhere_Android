package cn.com.forthedream.imhere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;
import static java.lang.Thread.sleep;

public class LocationService extends Service {
    String test;
    Context mcontext;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 60000;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ser","on");
      //  test = intent.getStringExtra("test");
       // Log.d("ser",test);
        Log.d("ser","finish");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                        final BluetoothManager bluetoothManager =
                                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                        if (bluetoothManager == null) Log.d("ibeacon", "null5");
                        mBluetoothAdapter = bluetoothManager.getAdapter();
                        Log.d("ibeacon", "in");
                        if (mBluetoothAdapter != null) {
                            mBluetoothAdapter.enable();
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            while (!mBluetoothAdapter.isEnabled()) {
                                Log.d("ibeacon", "null4");
                            }
                            Log.d("ibeacon","3");
                            mBluetoothAdapter.startLeScan(mLeScanCallback);
                            Log.d("ibeacon","4");
                        } else Log.d("ibeacon", "null1");
                    }



        }).start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    Log.d("ibeacon","2");
                    final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecord);
                    if(ibeacon==null) {
                        Log.d("ibeacon","null2");

                    }
                    else {
                        if(ibeacon.bluetoothAddress==null||ibeacon.bluetoothAddress.equals("")) Log.d("ibeacon","null3");
                        else {
                            Log.d("ibeacon",ibeacon.bluetoothAddress);
                            long d = System.currentTimeMillis();
                            Intent intent = new Intent("cn.com.forthedream.locationreceiver");
                            intent.putExtra("time",d);
                            intent.putExtra("MAC",ibeacon.bluetoothAddress);
                            intent.putExtra("id",ibeacon.proximityUuid);
                            intent.putExtra("power",ibeacon.txPower);
                            sendBroadcast(intent);
                        }
                    }
                }
            };

}