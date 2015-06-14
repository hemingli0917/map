package com.example.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocalDayWeatherForecast;
import com.example.activity.R;

public class WeatherAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private String mday[];
	List<AMapLocalDayWeatherForecast> forecasts;

	public WeatherAdapter(Context context,
			List<AMapLocalDayWeatherForecast> forecasts, String[] mway) {

		super();
		this.context = context;
		this.forecasts = forecasts;
		this.mday = mway;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

		return forecasts.size() - 1;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.weather_infoitem, null);
			holder = new ViewHolder();
			/*
			 * listview控件初始化
			 */
			holder.mTomorrowTime = (TextView) convertView
					.findViewById(R.id.weather_daytoday);// 明天时间
			holder.mnexttherinfo = (TextView) convertView
					.findViewById(R.id.weather_info);// 下一天天气状况
			holder.mnextthertemp = (TextView) convertView
					.findViewById(R.id.weather_temp);// 下一天温度状况
			holder.mnexttherdir = (TextView) convertView
					.findViewById(R.id.weather_dir);// 下一天风向状况

			AMapLocalDayWeatherForecast forecast = forecasts.get(position + 1);

			holder.mTomorrowTime.setText("星期" + mday[position]);
			holder.mnexttherinfo.setText(forecast.getDayWeather());
			holder.mnextthertemp.setText(forecast.getNightTemp() + "~"
					+ forecast.getDayTemp() + "°");
			holder.mnexttherdir.setText(forecast.getDayWindDir() + "风"
					+ forecast.getDayWindPower() + "级");

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	public static class ViewHolder {
		private TextView mTomorrowTime;// 明天时间
		private TextView mnexttherinfo;// 下一天天气状况
		private TextView mnextthertemp;// 下一天温度状况
		private TextView mnexttherdir;// 下一天风向状况
	}
}
