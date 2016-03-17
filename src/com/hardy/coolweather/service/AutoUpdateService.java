package com.hardy.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hardy.coolweather.receiver.AutoUpdateReceiver;
import com.hardy.coolweather.util.CallBackHttpUtil;
import com.hardy.coolweather.util.HttpUtil;
import com.hardy.coolweather.util.ParseUtility;

public class AutoUpdateService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		HttpUtil.sendHttpRequest(address, new CallBackHttpUtil() {
			
			@Override
			public void onFinished(String response) {
				//ֻҪ������������Ϣ�����ڱ��ؾͺ��ˣ���Ϊ�����ں�̨���У��ٴλص�WeatherActivityʱ�ֻ�ȥ��ʾ�������¸��µ�������Ϣ
				ParseUtility.handleWeatherInfo(AutoUpdateService.this, response);
				Log.d("����ʼ", "�����Ѹ��� ");
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
		
		//�������Ӷ�ʱ����
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long triggerTime = 60*1000 + SystemClock.elapsedRealtime();
		Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
		//�����PendingIntent.getXxxxxx()����һ��Ҫ��Intent�����д��յ�������һ�£����򲻻������Ҳ���ᴥ���κβ���
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
}
