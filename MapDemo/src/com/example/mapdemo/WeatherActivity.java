package com.example.mapdemo;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.example.adapter.WeatherAdapter;
import com.example.been.Weather;

public class WeatherActivity extends Activity implements
		AMapLocalWeatherListener {
	/*
	 * 时间和星期
	 */
	private String mYear;
	private String mMonth;
	private String mDay;
	private String mWay;
	private int mHour = 0;

	private TextView todaycalander;// 时间
	private TextView todaytime;// 星期
	private TextView weatherinfo;// 天气情况
	private TextView city;// 城市
	private TextView wind;// 风向
	private TextView humidity;// 空气湿度
	private TextView temp;// 温度
	private LinearLayout mlinearlayout;// 整体的布局
	private ListView mweatherlv;
	private WeatherAdapter mAdapter;

	private TextView mWeatherLocationTextView;// 天气预报地点
	private TextView mTodayTimeTextView;//
	private TextView mTomorrowTimeTextView;//
	private TextView mNextDayTimeTextView;//

	private TextView mTodayWeatherTextView;// 今天天气状况

	private LocationManagerProxy mLocationManagerProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		// 带完善布局
		setContentView(R.layout.weather_info);

		initView();
		StringData();
		ChangeBackground();
		init();
	}

	private void ChangeBackground() {
		if (mHour <= 18 && mHour >= 8) {
			mlinearlayout.setBackgroundColor(getResources().getColor(
					R.color.whiteColor));
		}

	}

	public void StringData() {
		long time = System.currentTimeMillis();
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		System.out.println("7777777777776666" + mHour);
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "天";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		todaycalander.setText(mYear + "年" + mMonth + "月" + mDay + "日");
		todaytime.setText("星期" + mWay);

	}

	private void init() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// 获取未来天气预报
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);
		// mLocationManagerProxy.requestWeatherUpdates(
		// LocationManagerProxy.WEATHER_TYPE_FORECAST, this);

	}

	// 待完善
	private void initView() {
		mlinearlayout = (LinearLayout) findViewById(R.id.weather_linearlayout);
		mweatherlv = (ListView) findViewById(R.id.weather_lv);
		todaytime = (TextView) findViewById(R.id.todaytime);
		todaycalander = (TextView) findViewById(R.id.todaycalander);
		weatherinfo = (TextView) findViewById(R.id.dweatherText);
		city = (TextView) findViewById(R.id.dCityText);
		temp = (TextView) findViewById(R.id.dTempCText);
		wind = (TextView) findViewById(R.id.dWindText);
		humidity = (TextView) findViewById(R.id.dHumidityText);

	}

	@Override
	public void onWeatherForecaseSearched(
			AMapLocalWeatherForecast weatherforecast) {
		if (weatherforecast != null
				&& weatherforecast.getAMapException().getErrorCode() == 0) {
			List<AMapLocalDayWeatherForecast> forecasts = weatherforecast
					.getWeatherForecast();
			mAdapter = new WeatherAdapter(WeatherActivity.this, forecasts);
			mweatherlv.setAdapter(mAdapter);
		} else {

			// 获取天气预报失败
			Toast.makeText(
					this,
					"获取天气预报失败:"
							+ weatherforecast.getAMapException()
									.getErrorMessage(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive live) {
		System.out.println("TGA" + live);
		if (live != null && live.getAMapException().getErrorCode() == 0) {
			weatherinfo.setText(live.getWeather());// 天气情况
			city.setText(live.getCity()); // 城市
			temp.setText(live.getTemperature() + "°");// 温度
			wind.setText(live.getWindDir() + "风" + live.getWindPower() + "级");// 风向风级

			humidity.setText(live.getHumidity());// 空气湿度
			System.out.println("666666666666666" + live.getWeather());

		} else {
			// 获取天气预报失败
			Toast.makeText(this,
					"获取天气预报失败:" + live.getAMapException().getErrorMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 销毁定位
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}

}
