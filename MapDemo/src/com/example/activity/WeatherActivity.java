package com.example.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.example.adapter.WeatherAdapter;

public class WeatherActivity extends Activity implements
		AMapLocalWeatherListener {
	/*
	 * ʱ�������
	 */
	private String mYear;
	private String mMonth;
	private String mDay;
	private String mWay;
	private int mtoday;
	private int mHour = 0;
	private String[] daytime;

	private TextView todaycalander;// ʱ��
	private TextView todaytime;// ����
	private TextView weatherinfo;// �������
	private TextView city;// ����
	private TextView wind;// ����
	private TextView humidity;// ����ʪ��
	private TextView temp;// �¶�
	private ImageView weatherlogo;
	private String weathertype;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String[] mListTitles;
	private ListView mDrawerList;
	private Boolean islive = true;

	private LinearLayout mlinearlayout;// ����Ĳ���
	private LinearLayout mlistlinearlayout;// list����
	private ListView mweatherlv;
	private WeatherAdapter mAdapter;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private LocationManagerProxy mLocationManagerProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerLayout.setDrawerShadow(R.drawable.navigation_drawer_shadow,
				GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mListTitles = getResources().getStringArray(R.array.drawer_array);
		mDrawerList = (ListView) findViewById(R.id.navigation_drawer);
		String[] from = { "icon", "text" };
		int[] to = { R.id.nav_icon, R.id.nav_text };
		int[] drawables = { R.drawable.weather, R.drawable.outting,
				R.drawable.alarm, R.drawable.setting };

		List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mListTitles.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("icon", drawables[i]);
			map.put("text", mListTitles[i]);
			menus.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, menus,
				R.layout.navigator_item, from, to);
		mDrawerList.setAdapter(adapter);
		mDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});

		selectItem(0);

	}

	protected void selectItem(int position) {

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mListTitles[position]);

		switch (position) {
		case 0:// ����
			initView();
			StringData();
			init();
			break;

		case 1:// ����
			Intent outintent = new Intent(WeatherActivity.this,
					RouteActivity.class);
			startActivity(outintent);
			break;

		case 2:// ��վ����
			Intent alarmintent = new Intent(WeatherActivity.this,
					BuslineActivity.class);
			startActivity(alarmintent);
			break;

		case 3:// setting
			Intent settingintent = new Intent(WeatherActivity.this,
					SettingActivity.class);
			startActivity(settingintent);
			break;

		default:
			break;

		}
		closeDrawer();

	}

	private void ChangeBackground() {
		// if (mHour <= 18 || mHour >= 8) {
		// mlinearlayout.setBackgroundColor(getResources().getColor(
		// R.color.whiteColor));
		// // mlistlinearlayout.setBackgroundColor(getResources().getColor(
		// // R.color.whiteColor));
		// }

	}

	public void StringData() {
		long time = System.currentTimeMillis();
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		System.out.println("7777777777776666" + mHour);
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // ��ȡ��ǰ���
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// ��ȡ��ǰ�·�
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// ��ȡ��ǰ�·ݵ����ں���
		mtoday = c.get(Calendar.DAY_OF_WEEK);
		DatetimeChange();
		todaycalander.setText(mYear + "��" + mMonth + "��" + mDay + "��");
		todaytime.setText("����" + mWay);
		daytime = new String[3];
		for (int i = 0; i < 3; i++) {
			switch (i) {
			case 0:

				mtoday += 1;
				DatetimeChange();
				daytime[i] = mWay;
				System.out.println("9999999999999999999");
				break;
			case 1:
				mtoday += 1;
				DatetimeChange();
				System.out.println("��mway" + mWay);
				daytime[i] = mWay;
				break;
			case 2:
				mtoday += 1;
				DatetimeChange();
				System.out.println("�ٺ�mway" + mWay);
				daytime[i] = mWay;
				System.out.println("555555555555555daytime[i]" + daytime[i]);
				break;

			default:
				break;
			}

		}

	}

	private void DatetimeChange() {
		if (mtoday % 7 == 1) {
			mWay = "��";
		} else if (2 == mtoday % 7) {
			mWay = "һ";
		} else if (3 == mtoday % 7) {
			mWay = "��";
		} else if (4 == mtoday % 7) {
			mWay = "��";
		} else if (5 == mtoday % 7) {
			mWay = "��";
		} else if (6 == mtoday % 7) {
			mWay = "��";
		} else if (0 == mtoday % 7) {
			mWay = "��";
		}
	}

	private void init() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		// ��ȡʵʱ����Ԥ��
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);
		// ��ȡδ������Ԥ��
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_FORECAST, this);

	}

	private void nextweather() {
		// mLocationManagerProxy = LocationManagerProxy.getInstance(this);

	}

	// ������
	private void initView() {
		/*
		 * �����ֿؼ���ʼ��
		 */
		mlinearlayout = (LinearLayout) findViewById(R.id.weather_linearlayout);
		mweatherlv = (ListView) findViewById(R.id.weather_lv);
		todaytime = (TextView) findViewById(R.id.todaytime);
		todaycalander = (TextView) findViewById(R.id.todaycalander);
		weatherinfo = (TextView) findViewById(R.id.dweatherText);
		city = (TextView) findViewById(R.id.dCityText);
		temp = (TextView) findViewById(R.id.dTempCText);
		wind = (TextView) findViewById(R.id.dWindText);
		humidity = (TextView) findViewById(R.id.dHumidityText);
		weatherlogo = (ImageView) findViewById(R.id.dForecastImage);

	}

	@Override
	public void onWeatherForecaseSearched(
			AMapLocalWeatherForecast weatherforecast) {
		if (weatherforecast != null
				&& weatherforecast.getAMapException().getErrorCode() == 0) {

			List<AMapLocalDayWeatherForecast> forecasts = weatherforecast
					.getWeatherForecast();
			mAdapter = new WeatherAdapter(WeatherActivity.this, forecasts,
					daytime);
			mweatherlv.setAdapter(mAdapter);
			if (islive) {
				selectItem(0);
			} else {

			}

		} else {

			// ��ȡ����Ԥ��ʧ��
			Toast.makeText(this, "��ȡ����Ԥ��ʧ��,������������", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive live) {

		if (live != null && live.getAMapException().getErrorCode() == 0) {
			weatherinfo.setText(live.getWeather());// �������
			city.setText(live.getCity()); // ����
			temp.setText(live.getTemperature() + "��");// �¶�
			wind.setText(live.getWindDir() + "��" + live.getWindPower() + "��");// ����缶

			humidity.setText(live.getHumidity());// ����ʪ��
			weathertype = live.getWeather();
			setWeatherbackground();
			islive = false;

			System.out.println("666666666666666" + live.getWeather());

		} else {
			// ��ȡ����Ԥ��ʧ��
			Toast.makeText(this, "��ȡ����Ԥ��ʧ��,������������", Toast.LENGTH_LONG).show();
		}
	}

	private void setWeatherbackground() {
		if (weathertype.equals("��")) {
			weatherlogo.setBackgroundResource(R.drawable.sun);
		} else if (weathertype.equals("����")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_mostlycloudy);
		} else if (weathertype.equals("��")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_cloudy);
		} else if (weathertype.equals("����")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_rain);
		} else if (weathertype.equals("������")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_chancestorm);
		} else if (weathertype.equals("����")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_lightrain);
		} else if (weathertype.equals("��")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_fog);
		} else if (weathertype.equals("��ɳ")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_flurries);
		} else if (weathertype.equals("���ѩ")) {
			weatherlogo.setBackgroundResource(R.drawable.weather_icyrain);
		}

	}

	public void closeDrawer() {
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout
						.isDrawerOpen(findViewById(R.id.navigation_drawer));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		if (mDrawerLayout != null && isDrawerOpen())
			showGlobalContextActionBar();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	@Override
	protected void onResume() {

		super.onResume();
		setTitle(mListTitles[0]);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
		// ���ٶ�λ
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}

}
