package com.hardy.coolweather.util;

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
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
		//��TextUtils.isEmpty()�жϿ���ͬʱ�ж��ַ����Ƿ�Ϊ��ָ��null����������Ϊ�գ�0-length��
		//��response.isEmpty()ֻ���ж������Ƿ�Ϊ�գ����ܻ�����ַ���ָ��Ϊ�յ����
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			for(String singleProvince:allProvinces){
				/*split()�����е��ַ������Ǽ򵥵��ַ���������һ��������ʽ����������
				��ǰҪ���ϡ�\��,����Ϊ��\��������ת���ַ�������Ҫд�ɡ�\\|��*/
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
	 * �����ؼ�������
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
