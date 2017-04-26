package com.eneaceolini.simpledatalogger;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By Enea Ceolini 2015
 * Contact: enea.ceolini@gmail.com
 */



public class ReadingsService extends Service {


    private static final String TAG = "ReadingService";
    private final IBinder mBinder = new LocalBinder();
    private BroadcastReceiver mReceiver;

    private SensorManager sensorManager;
    private BufferedWriter file;
    private LocationManager locationManager;
    private Map<Integer, String> sensorTypes = new HashMap<Integer, String>();
    private Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();


    @Override
    public void onCreate() {

        //Register the filter to be told when screen goes on and off
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


        // Get sensors to be captured
        sensorTypes.put(Sensor.TYPE_ACCELEROMETER, "ACCEL");
        sensorTypes.put(Sensor.TYPE_GYROSCOPE, "GYRO");
        sensorTypes.put(Sensor.TYPE_LINEAR_ACCELERATION, "LINEAR");
        sensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD, "MAG");
        sensorTypes.put(Sensor.TYPE_GRAVITY, "GRAV");
        sensorTypes.put(Sensor.TYPE_ROTATION_VECTOR, "ROTATION");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (Integer type : sensorTypes.keySet()) {
            sensors.put(type, sensorManager.getDefaultSensor(type));
        }

        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        startRecording();

    }

    private void startRecording() {
        // Prepare data storage
        File directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String name = "AllData_" + System.currentTimeMillis() + ".csv";
        File filename = new File(directory, name);
        try {
            file = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register sensor listeners
        for (Sensor sensor : sensors.values()) {
            sensorManager.registerListener(sensorListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);
    }

    private void stopRecording() {
        sensorManager.unregisterListener(sensorListener);
        locationManager.removeUpdates(locationListener);
        try {
            file.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.toggle:
                    if (((ToggleButton) v).isChecked()) {
                        startRecording();
                    } else {
                        stopRecording();
                    }
                    break;
                case R.id.btnEnter:
                    write("ENTER");
                    break;
                case R.id.btnExit:
                    write("EXIT");
                    break;
            }
        }

    };

    private SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
//            Log.d("LOG", "sensor recorded");
            write(sensorTypes.get(event.sensor.getType()), event.values);
        }

    };

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            write("GPS",
                    new double[] { location.getLatitude(),
                            location.getLongitude() });
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };



    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startRecording();
        boolean screenOff;
        if (intent != null) {
            if (intent.hasExtra("screen_state")) {
                screenOff = intent.getBooleanExtra("screen_state", false);

                if (!screenOff) { // If screen is on I register the accelerometer that start recording
                    Log.d("LOG", "Screen ON");

                } else { // otherwise (if the screen goes off) I analyze the data recorded in the last session
                    Log.d("LOG", "Screen OFF");                }
            }
        }
        return START_STICKY;
    }



    private void write(String tag, String[] values) {
        if (file == null) {
            return;
        }

        String line = "";
        if (values != null) {
            for (String value : values) {
                line += "," + value;
            }
        }
        line = Long.toString(System.currentTimeMillis()) + "," + tag + line
                + "\n";

        try {
            file.write(line);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void write(String tag, float[] values) {
        String[] array = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Float.toString(values[i]);
        }
        write(tag, array);
    }

    private void write(String tag, double[] values) {
        String[] array = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Double.toString(values[i]);
        }
        write(tag, array);
    }

    private void write(String tag) {
        write(tag, (String[]) null);
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "Getting destroyed");
        if(mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
        stopRecording();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
        super.onDestroy();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public class LocalBinder extends Binder {
        public ReadingsService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ReadingsService.this;
        }
    }
}