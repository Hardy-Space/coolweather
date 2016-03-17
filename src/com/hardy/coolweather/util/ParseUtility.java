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
 * �������ݽ�����
 */
public class ParseUtility {
	/*
	 * ����ʡ������
	 */
	public synchronized static boolean handleProvinceResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		// ��TextUtils.isEmpty()�жϿ���ͬʱ�ж��ַ����Ƿ�Ϊ��ָ��null����������Ϊ�գ�0-length��
		// ��response.isEmpty()ֻ���ж������Ƿ�Ϊ�գ����ܻ�����ַ���ָ��Ϊ�յ����
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			for (String singleProvince : allProvinces) {
				/*
				 * split()�����е��ַ������Ǽ򵥵��ַ���������һ��������ʽ����������
				 * ��ǰҪ���ϡ�\��,����Ϊ��\��������ת���ַ�������Ҫд�ɡ�\\|��
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
	 * ������������
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
	 * �����ؼ�������
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
	 * ����������Ϣ��Json���ݣ�
	 */
	public static boolean handleWeatherInfo(Context context, String response) {
		try {// Context�����ǽ������̵߳������ģ����ڴ����Ǹ������ĵ�SharedPreferences
			JSONObject object1 = new JSONObject(response);
			JSONObject object2 = object1.getJSONObject("weatherinfo");
			String cityName = object2.getString("city");
			//����������Ϣ�Ĵ�����Ϊ�˸��¼�ʱ����
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
	 * ��������Ϣ���浽���أ�Ϊ���´δ�����Զ������ϴ��˳�ʱ�ĳ��е�������Ϣ�����ڻ����������µģ�
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String highTem, String lowTem, String weather,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��");
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		//����������Ϣ�Ĵ�����Ϊ�˸��¼�ʱ����
		editor.putString("weather_code", weatherCode);
		editor.putString("high_tem", highTem);
		editor.putString("low_tem", lowTem);
		editor.putString("weather", weather);
		editor.putString("publish_time", publishTime);
		editor.putString("date", sdf.format(new Date()));
		// һ����Ҫ����commit()!!!!!!!!
		editor.commit();
	}
}
