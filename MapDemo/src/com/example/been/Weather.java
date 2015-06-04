package com.example.been;

import android.widget.TextView;

public class Weather {
	private String city;// �ص�
	private String WeatherTextView;// ����
	private String WeatherTemperature;// ����
	private String WindDirction;// ����
	private String WindPower;// ����
	private String AirHumidity;// ����ʪ��
	private String WeatherPublish;// ����ʱ��

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
