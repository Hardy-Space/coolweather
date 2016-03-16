package com.hardy.coolweather.util;

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
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
		//用TextUtils.isEmpty()判断可以同时判断字符串是否为空指向（null）或是内容为空（0-length）
		//而response.isEmpty()只能判断内容是否为空，可能会出现字符串指向为空的情况
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			for(String singleProvince:allProvinces){
				/*split()方法中的字符串不是简单的字符串，它是一个正则表达式，在区分字
				符前要加上‘\’,又因为‘\’本身是转义字符，所以要写成‘\\|’*/
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
	public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			for(String singleCity:allCities){
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
	public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			for(String singleCounty:allCounties){
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
}
