package com.example.mapdemo;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.example.adapter.WeatherAdapter;
import com.example.been.Weather;

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

	private LinearLayout mlinearlayout;// ����Ĳ���
	private LinearLayout mlistlinearlayout;// list����
	private ListView mweatherlv;
	private WeatherAdapter mAdapter;

	private LocationManagerProxy mLocationManagerProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);// ����ʾ����ı�����
		// �����Ʋ���
		setContentView(R.layout.weather_info);

		initView();
		StringData();
		ChangeBackground();
		init();
		// nextweather();
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

		// ��ȡδ������Ԥ��
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);

	}

	private void nextweather() {
		// mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// mLocationManagerProxy.requestWeatherUpdates(
		// LocationManagerProxy.WEATHER_TYPE_FORECAST, this);
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

		} else {

			// ��ȡ����Ԥ��ʧ��
			Toast.makeText(
					this,
					"��ȡ����Ԥ��ʧ��:"
							+ weatherforecast.getAMapException()
									.getErrorMessage(), Toast.LENGTH_SHORT)
					.show();
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

			System.out.println("666666666666666" + live.getWeather());

		} else {
			// ��ȡ����Ԥ��ʧ��
			Toast.makeText(this,
					"��ȡ����Ԥ��ʧ��:" + live.getAMapException().getErrorMessage(),
					Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onPause() {
		super.onPause();
		// ���ٶ�λ
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}

}
