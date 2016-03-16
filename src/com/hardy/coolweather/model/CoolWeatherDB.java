package com.hardy.coolweather.model;

import java.util.ArrayList;
import java.util.List;

import com.hardy.coolweather.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * ���ݿ��ʵ���࣬��һЩ���ݿ������װ����
 */
public class CoolWeatherDB {
	/**
	 * ���ݿ���
	 */
	private static final String DB_NAME = "cool_weather";
	/**
	 * ���ݿ�汾
	 */
	private static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase sqLiteDatabase;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(
				context, DB_NAME, null, VERSION);
		sqLiteDatabase = coolWeatherOpenHelper.getWritableDatabase();
	}

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues contentValues = new ContentValues();
			/*
			 * ��Ϊ����ʱ��Ϊid�Զ����������Բ������id��Ϣ
			 */
			// contentValues.put("id", province.getId());
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			sqLiteDatabase.insert("Province", null, contentValues);
		}
	}
	
	public List<Province> loadProvinces(){
		List<Province> allProvinces = new ArrayList<Province>();
		Cursor cursor = sqLiteDatabase.query("Province", null, null, null, null, null, null);
		//���cursorΪ�գ���moveToFirst()�᷵�ؿ�
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				allProvinces.add(province);
			}while(cursor.moveToNext());//��û����һ������ʱ���ؿ�
		}
		if(cursor != null){
			cursor.close();
		}
		return allProvinces;
	}
	
	public void saveCity(City city){
		if (city != null) {
			ContentValues contentValues = new ContentValues();
			/*
			 * ��Ϊ����ʱ��Ϊid�Զ����������Բ������id��Ϣ
			 */
			// contentValues.put("id", city.getId());
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			sqLiteDatabase.insert("City", null, contentValues);
		}
	}
	
	public List<City> loadCities(int provinceId){
		List<City> allCities = new ArrayList<City>();
		Cursor cursor = sqLiteDatabase.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		//���cursorΪ�գ���moveToFirst()�᷵�ؿ�
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				allCities.add(city);
			}while(cursor.moveToNext());//��û����һ������ʱ���ؿ�
		}
		if(cursor != null){
			cursor.close();
		}
		return allCities;
	}
	
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues contentValues = new ContentValues();
			/*
			 * ��Ϊ����ʱ��Ϊid�Զ����������Բ������id��Ϣ
			 */
			// contentValues.put("id", province.getId());
			contentValues.put("county_name", county.getCountyName());
			contentValues.put("county_code", county.getCountyCode());
			contentValues.put("city_id", county.getCityId());
			sqLiteDatabase.insert("County", null, contentValues);
		}
	}
	
	public List<County> loadCounties(int cityId){
		List<County> allCounties = new ArrayList<County>();
		Cursor cursor = sqLiteDatabase.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		//���cursorΪ�գ���moveToFirst()�᷵�ؿ�
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				allCounties.add(county);
			}while(cursor.moveToNext());//��û����һ������ʱ���ؿ�
		}
		if(cursor != null){
			cursor.close();
		}
		return allCounties;
	}
	

}
