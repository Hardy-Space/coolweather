package com.hardy.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hardy.coolweather.R;
import com.hardy.coolweather.util.CallBackHttpUtil;
import com.hardy.coolweather.util.HttpUtil;
import com.hardy.coolweather.util.ParseUtility;

/*
 * ��ʾ������Ϣ��
 */
public class WeatherActivity extends Activity implements OnClickListener {
	private TextView cityName;
	private TextView publishTime;
	private LinearLayout linearWeather;
	private TextView currentDate;
	private TextView weatherInfo;
	private TextView temperature;
	private ImageButton home;
	private ImageButton flush;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityName = (TextView) findViewById(R.id.cityName);
		publishTime = (TextView) findViewById(R.id.publicTime);
		linearWeather = (LinearLayout) findViewById(R.id.linearWeather);
		currentDate = (TextView) findViewById(R.id.date);
		weatherInfo = (TextView) findViewById(R.id.weatherInfo);
		temperature = (TextView) findViewById(R.id.tempearture);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			publishTime.setText("ͬ����...");
			cityName.setVisibility(View.INVISIBLE);
			linearWeather.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);

		} else {
			showWeatherInfo();
		}
		home = (ImageButton) findViewById(R.id.home);
		home.setOnClickListener(this);
		flush = (ImageButton) findViewById(R.id.flush);
		flush.setOnClickListener(this);

	}

	/*
	 * ��ѯ������Ϣ����
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/*
	 * ��ѯ������Ϣ
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	/*
	 * �ӷ����������ݣ���������·��һ���ǽ������ؼ��е�������Ϣ���ţ�Ȼ����ȥ��ѯ������Ϣ����һ����֪����������Ϣ����ȥ��ѯ������Ϣ��
	 */
	private void queryFromServer(final String address, final String type) {// type�����յ�����countyCode����weatherCode
		HttpUtil.sendHttpRequest(address, new CallBackHttpUtil() {

			@Override
			public void onFinished(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] countyResponse = response.split("\\|");
						if (countyResponse != null
								&& countyResponse.length == 2) {
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
						publishTime.setText("ͬ��ʧ��");

					}
				});
			}
		});
	}

	/*
	 * �����߳���ʾ������Ϣ
	 */
	private void showWeatherInfo() {
		cityName.setText(preferences.getString("city_name", ""));
		publishTime.setText("����" + preferences.getString("publish_time", "")
				+ "����");
		currentDate.setText(preferences.getString("date", ""));
		weatherInfo.setText(preferences.getString("weather", ""));
		temperature.setText(preferences.getString("low_tem", "") + "~"
				+ preferences.getString("high_tem", ""));
		linearWeather.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("home_return", true);
			startActivity(intent);
			finish();
			break;
		case R.id.flush:
			publishTime.setText("ͬ����...");
			String weatherCode = preferences.getString("weather_code", "");
			//������ʱ�ж��Ƿ�Ϊ�յ�ϰ��
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;

		}
	}

}
