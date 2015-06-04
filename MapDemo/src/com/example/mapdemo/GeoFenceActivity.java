package com.example.mapdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

/**
 * ʹ�õ���Χ����ʵ�� ��ʹ�õ���Χ��ʱ��Ҫ�붨λ���ʹ��
 * **/
public class GeoFenceActivity extends Activity implements AMapLocationListener,
		OnMapClickListener {

	private MapView mMapView;// ��ͼ�ؼ�
	private AMap mAMap;
	private LocationManagerProxy mLocationManagerProxy;// ��λʵ��
	private Marker mGPSMarker;// ��λλ����ʾ
	private PendingIntent mPendingIntent;
	private Circle mCircle;

	public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_geofence);
		init(savedInstanceState);

	}

	private void init(Bundle savedInstanceState) {
		mMapView = (MapView) findViewById(R.id.geomap);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();

		mAMap.setOnMapClickListener(this);
		IntentFilter fliter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		fliter.addAction(GEOFENCE_BROADCAST_ACTION);
		registerReceiver(mGeoFenceReceiver, fliter);

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		Intent intent = new Intent(GEOFENCE_BROADCAST_ACTION);
		mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
				intent, 0);
		// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
		// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����removeUpdates()������ȡ����λ����
		// �ڶ�λ�������ں��ʵ��������ڵ���destroy()����
		// ����������ʱ��Ϊ-1����λֻ��һ��
		// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������removeUpdates()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 2000, 15, this);

		MarkerOptions markOptions = new MarkerOptions();
		markOptions.icon(
				BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.location_marker)))
				.anchor(0.5f, 0.5f);
		mGPSMarker = mAMap.addMarker(markOptions);

		mAMap.setOnMapClickListener(this);

	}

	private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ���ܹ㲥
			if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
				Bundle bundle = intent.getExtras();
				// ���ݹ㲥��status��ȷ�����������ڻ�����������
				int status = bundle.getInt("status");
				if (status == 0) {
					Toast.makeText(getApplicationContext(), "��������",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "��������",
							Toast.LENGTH_SHORT).show();
				}

			}

		}
	};

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location.getAMapException().getErrorCode() == 0) {
			updateLocation(location.getLatitude(), location.getLongitude());

		}
	}

	/*
	 * �����µľ�γ�ȸ���GPSλ�ú����õ�ͼ����
	 */
	private void updateLocation(double latitude, double longtitude) {
		if (mGPSMarker != null) {
			mGPSMarker.setPosition(new LatLng(latitude, longtitude));
		}

	}

	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	protected void onPause() {
		super.onPause();
		// ���ٶ�λ
		mLocationManagerProxy.removeGeoFenceAlert(mPendingIntent);
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.destroy();
		unregisterReceiver(mGeoFenceReceiver);
		mMapView.onPause();

	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();

		mMapView.onDestroy();

	}

	@Override
	public void onMapClick(LatLng latLng) {
		mLocationManagerProxy.removeGeoFenceAlert(mPendingIntent);
		if (mCircle != null) {
			mCircle.remove();
		}
		// ����Χ��ʹ��ʱ��Ҫ�붨λ���󷽷����ʹ��
		// ���õ���Χ����λ�á��뾶����ʱʱ�䡢�����¼�
		mLocationManagerProxy.addGeoFenceAlert(latLng.latitude,
				latLng.longitude, 1000, 1000 * 60 * 30, mPendingIntent);
		// ������Χ����ӵ���ͼ����ʾ
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(latLng).radius(1000)
				.fillColor(Color.argb(180, 224, 171, 10))
				.strokeColor(Color.RED);
		mCircle = mAMap.addCircle(circleOptions);

	}
}
