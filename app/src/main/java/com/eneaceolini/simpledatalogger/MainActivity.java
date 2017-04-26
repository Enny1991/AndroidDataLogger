package com.eneaceolini.simpledatalogger;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

	private static final int TYPE = 1;
	private Map<Integer, String> sensorTypes = new HashMap<Integer, String>();
	private Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();

	private Button stopService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stopService = (Button) findViewById(R.id.stop);
		stopService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopService(new Intent(getApplicationContext(), ReadingsService.class));
			}
		});

		askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.ACCESS_FINE_LOCATION});


	}

	public void askForPermissions(String[] requestedPermission) {
		ArrayList<String> permissionsToAsk = new ArrayList<>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (String aRequestedPermission : requestedPermission) {
				if (ContextCompat.checkSelfPermission(this, aRequestedPermission)
						!= PackageManager.PERMISSION_GRANTED) {
					permissionsToAsk.add(aRequestedPermission);
				}
			}
			String[] askingPermissions = new String[permissionsToAsk.size()];
			for (int i = 0; i < permissionsToAsk.size(); i++)
				askingPermissions[i] = permissionsToAsk.get(i);
			if (askingPermissions.length > 0) {
				ActivityCompat.requestPermissions(this,
						askingPermissions,
						TYPE);
			}
		}
	}

	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
										   @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

				for (int i = 0; i < permissions.length; i++) {
					String tmpPerm = permissions[i];
					switch (tmpPerm) {
						case Manifest.permission.WRITE_EXTERNAL_STORAGE:
							if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

								startService(new Intent(getApplicationContext(), ReadingsService.class));
							}else{
								Toast.makeText(MainActivity.this, "Can't start without permission", Toast.LENGTH_LONG).show();
							}
							break;
						case Manifest.permission.ACCESS_FINE_LOCATION:
							break;
					}
				}


	}


}