package com.example.been;

import android.widget.TextView;

public class Weather {
	private String city;// 地点
	private String WeatherTextView;// 天气
	private String WeatherTemperature;// 气温
	private String WindDirction;// 风向
	private String WindPower;// 风力
	private String AirHumidity;// 空气湿度
	private String WeatherPublish;// 发布时间

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getWeatherTextView() {
		return WeatherTextView;
	}

	public void setWeatherTextView(String weatherTextView) {
		WeatherTextView = weatherTextView;
	}

	public String getWeatherTemperature() {
		return WeatherTemperature;
	}

	public void setWeatherTemperature(String weatherTemperature) {
		WeatherTemperature = weatherTemperature;
	}

	public String getWindDirction() {
		return WindDirction;
	}

	public void setWindDirction(String windDirction) {
		WindDirction = windDirction;
	}

	public String getWindPower() {
		return WindPower;
	}

	public void setWindPower(String windPower) {
		WindPower = windPower;
	}

	public String getAirHumidity() {
		return AirHumidity;
	}

	public void setAirHumidity(String airHumidity) {
		AirHumidity = airHumidity;
	}

	public String getWeatherPublish() {
		return WeatherPublish;
	}

	public void setWeatherPublish(String weatherPublish) {
		WeatherPublish = weatherPublish;
	}

	public Weather() {
		super();

	}

	@Override
	public String toString() {
		return "Weather [city=" + city + ", WeatherTextView=" + WeatherTextView
				+ ", WeatherTemperature=" + WeatherTemperature
				+ ", WindDirction=" + WindDirction + ", WindPower=" + WindPower
				+ ", AirHumidity=" + AirHumidity + ", WeatherPublish="
				+ WeatherPublish + "]";
	}

}
