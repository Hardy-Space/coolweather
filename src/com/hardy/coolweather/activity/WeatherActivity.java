package com.hardy.coolweather.activity;

import java.security.PublicKey;

import com.hardy.coolweather.R;
import com.hardy.coolweather.util.CallBackHttpUtil;
import com.hardy.coolweather.util.HttpUtil;
import com.hardy.coolweather.util.ParseUtility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 显示天气信息类
 */
public class WeatherActivity extends Activity {
	private TextView cityName;
	private TextView publishTime;
	private LinearLayout linearWeather;
	private TextView currentDate;
	private TextView weatherInfo;
	private TextView temperature;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		cityName = (TextView) findViewById(R.id.cityName);
		publishTime = (TextView) findViewById(R.id.publicTime);
		linearWeather = (LinearLayout) findViewById(R.id.linearWeather);
		currentDate = (TextView) findViewById(R.id.date);
		weatherInfo = (TextView) findViewById(R.id.weatherInfo);
		temperature = (TextView) findViewById(R.id.tempearture);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			publishTime.setText("同步中...");
			cityName.setVisibility(View.INVISIBLE);
			linearWeather.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);

		} else {
			showWeatherInfo();
		}
	}

	/*
	 * 查询天气信息代号
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/*
	 * 查询天气信息
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	/*
	 * 从服务器查数据（包括两条路，一条是解析出县级市的天气信息代号，然后再去查询天气信息，另一条是知道了天气信息代号去查询天气信息）
	 */
	private void queryFromServer(final String address, final String type) {// type来接收到底是countyCode还是weatherCode
		HttpUtil.sendHttpRequest(address, new CallBackHttpUtil() {

			@Override
			public void onFinished(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] countyResponse = response.split("\\|");
						if (countyResponse != null && countyResponse.length == 2) {
							String weatherCode = countyResponse[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					ParseUtility.handleWeatherInfo(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showWeatherInfo();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						publishTime.setText("同步失败");

					}
				});
			}
		});
	}

	/*
	 * 在主线程显示天气信息
	 */
	private void showWeatherInfo() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityName.setText(preferences.getString("city_name", ""));
		publishTime.setText("今天"+preferences.getString("publish_time", "")+"发布");
		currentDate.setText(preferences.getString("date", ""));
		weatherInfo.setText(preferences.getString("weather", ""));
		temperature.setText(preferences.getString("low_tem", "")+"~"+preferences.getString("high_tem", ""));
		linearWeather.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
	}

}
