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
				//只要把最新天气信息保存在本地就好了，因为服务在后台运行，再次回到WeatherActivity时又会去显示本地最新更新的天气信息
				ParseUtility.handleWeatherInfo(AutoUpdateService.this, response);
				Log.d("服务开始", "数据已更新 ");
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
		
		//设置闹钟定时更新
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long triggerTime = 60*1000 + SystemClock.elapsedRealtime();
		Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
		//这里的PendingIntent.getXxxxxx()方法一定要跟Intent对象中大终点类类型一致，否则不会崩溃但也不会触发任何操作
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
}
