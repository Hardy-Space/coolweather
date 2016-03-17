package com.hardy.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.hardy.coolweather.model.City;
import com.hardy.coolweather.model.CoolWeatherDB;
import com.hardy.coolweather.model.County;
import com.hardy.coolweather.model.Province;

/*
 * 网络数据解析类
 */
public class ParseUtility {
	/*
	 * 解析省份数据
	 */
	public synchronized static boolean handleProvinceResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		// 用TextUtils.isEmpty()判断可以同时判断字符串是否为空指向（null）或是内容为空（0-length）
		// 而response.isEmpty()只能判断内容是否为空，可能会出现字符串指向为空的情况
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			for (String singleProvince : allProvinces) {
				/*
				 * split()方法中的字符串不是简单的字符串，它是一个正则表达式，在区分字
				 * 符前要加上‘\’,又因为‘\’本身是转义字符，所以要写成‘\\|’
				 */
				String[] provinceInfo = singleProvince.split("\\|");
				Province province = new Province();
				province.setProvinceCode(provinceInfo[0]);
				province.setProvinceName(provinceInfo[1]);
				coolWeatherDB.saveProvince(province);
			}
			return true;
		}
		return false;
	}

	/*
	 * 解析城市数据
	 */
	public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			for (String singleCity : allCities) {
				String[] cityInfo = singleCity.split("\\|");
				City city = new City();
				city.setCityCode(cityInfo[0]);
				city.setCityName(cityInfo[1]);
				city.setProvinceId(provinceId);
				coolWeatherDB.saveCity(city);
			}
			return true;
		}
		return false;
	}

	/*
	 * 解析县级市数据
	 */
	public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			for (String singleCounty : allCounties) {
				String[] countyInfo = singleCounty.split("\\|");
				County county = new County();
				county.setCountyCode(countyInfo[0]);
				county.setCountyName(countyInfo[1]);
				county.setCityId(cityId);
				coolWeatherDB.saveCounty(county);
			}
			return true;
		}
		return false;
	}

	/*
	 * 解析天气信息（Json数据）
	 */
	public static boolean handleWeatherInfo(Context context, String response) {
		try {// Context对象是接收主线程的上下文，用于创建那个上下文的SharedPreferences
			JSONObject object1 = new JSONObject(response);
			JSONObject object2 = object1.getJSONObject("weatherinfo");
			String cityName = object2.getString("city");
			//接收天气信息的代号是为了更新即时数据
			String weatherCode = object2.getString("cityid");
			String highTem = object2.getString("temp1");
			String lowTem = object2.getString("temp2");
			String weather = object2.getString("weather");
			String publishTime = object2.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, highTem, lowTem,
					weather, publishTime);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * 将天气信息保存到本地，为了下次打开软件自动更新上次退出时的城市的天气信息（现在还并不是最新的）
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String highTem, String lowTem, String weather,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		//保存天气信息的代号是为了更新即时数据
		editor.putString("weather_code", weatherCode);
		editor.putString("high_tem", highTem);
		editor.putString("low_tem", lowTem);
		editor.putString("weather", weather);
		editor.putString("publish_time", publishTime);
		editor.putString("date", sdf.format(new Date()));
		// 一定不要忘了commit()!!!!!!!!
		editor.commit();
	}
}
