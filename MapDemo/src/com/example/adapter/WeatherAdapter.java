package com.example.adapter;

import java.util.List;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.example.been.Weather;
import com.example.mapdemo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class WeatherAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	List<AMapLocalDayWeatherForecast> forecasts;

	public WeatherAdapter(Context context,
			List<AMapLocalDayWeatherForecast> forecasts) {

		super();
		this.context = context;
		this.forecasts = forecasts;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

		return forecasts.size();
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.weather_infoitem, null);
			for (int i = 0; i < forecasts.size(); i++) {
				AMapLocalDayWeatherForecast forecast = forecasts.get(i);

				switch (i) {
				case 0:

					break;
				case 1:
					// mWeatherLocationTextView.setText(forecast.getCity());
					// mTodayTimeTextView.setText("明天 ( " + forecast.getDate()
					// + " )");
					// mTodayWeatherTextView.setText(forecast.getDayWeather()
					// + "    " + forecast.getDayTemp() + "℃/"
					// + forecast.getNightTemp() + "℃    "
					// + forecast.getDayWindPower() + "级");

					break;
				case 2:
					// mWeatherLocationTextView.setText(forecast.getCity());
					// mTodayTimeTextView.setText("后天 ( " + forecast.getDate()
					// + " )");
					// mTodayWeatherTextView.setText(forecast.getDayWeather()
					// + "    " + forecast.getDayTemp() + "℃/"
					// + forecast.getNightTemp() + "℃    "
					// + forecast.getDayWindPower() + "级");
					break;
				case 3:

					break;

				default:
					break;
				}
			}
		}

		return convertView;
	}

}
