package com.hardy.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	//�������
	//����ʡ�ݱ������
	private static final String CREATE_PROVINCE = "create table Province(" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)";
	//�������б������
	private static final String CREATE_CITY = "create table City(" +
			"id int primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)";
	
	//�����ؼ��б������
	private static final String CREATE_COUNTY = "create table County(" +
			"id integer primary key autoincrement," +
			"county_name text," +
			"county_code text," +
			"city_id integer)";

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//����ʡ�ݱ�
		db.execSQL(CREATE_PROVINCE);
		//�������б�
		db.execSQL(CREATE_CITY);
		//�����ؼ��б�
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}