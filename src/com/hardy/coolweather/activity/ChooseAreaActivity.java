package com.hardy.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardy.coolweather.R;
import com.hardy.coolweather.model.City;
import com.hardy.coolweather.model.CoolWeatherDB;
import com.hardy.coolweather.model.County;
import com.hardy.coolweather.model.Province;
import com.hardy.coolweather.util.CallBackHttpUtil;
import com.hardy.coolweather.util.HttpUtil;
import com.hardy.coolweather.util.ParseUtility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private TextView areaName;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> listData = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private ProgressDialog progressDialog;
	private List<Province> receiveProvinces;
	private List<City> receiveCities;
	private List<County> receiveCouties;
	private Province selectedProvince;
	private City selectedCity;
	// 记录当前ListView是显示省份、城市还是县级市的信息
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if(preferences.getBoolean("city_selected", false)){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area_activity_layout);
		areaName = (TextView) findViewById(R.id.areaName);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listData);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 如果当前列表是显示省份的，点击后就去显示城市
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = receiveProvinces.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					// 如果当前列表是显示城市的，点击后就去显示县级市
					selectedCity = receiveCities.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("county_code", receiveCouties.get(position)
							.getCountyCode());
					startActivity(intent);
					finish();
				}

			}
		});
		// 第一次打开去查询省份信息
		queryProvinces();

	}

	/*
	 * 去数据库中查询省份信息
	 */
	private void queryProvinces() {
		// 从数据库去读省份信息
		receiveProvinces = coolWeatherDB.loadProvinces();
		// 如果数据库中有数据就直接使用
		if (receiveProvinces.size() > 0) {
			// 清空ListView的data
			listData.clear();
			for (Province province : receiveProvinces) {
				// 信息更新为省份名字
				listData.add(province.getProvinceName());
			}
			// 通知adapter更新数据
			adapter.notifyDataSetChanged();
			// 选中第一项
			listView.setSelection(0);
			// 地区名字设为“中国”
			areaName.setText("中国");
			// 当前ListView显示的是省份级别
			currentLevel = LEVEL_PROVINCE;
		} else {
			// 如果在数据库中查不到，说明是第一次使用，就去服务器中查询
			queryFromServer(null, "province");
		}
	}

	/*
	 * 去数据库中查询城市信息
	 */
	private void queryCities() {
		// 从数据库去读城市信息
		receiveCities = coolWeatherDB.loadCities(selectedProvince.getId());
		// 如果数据库中有数据就直接使用
		if (receiveCities.size() > 0) {
			// 清空ListView的data
			listData.clear();
			for (City city : receiveCities) {
				// 信息更新为城市名字
				listData.add(city.getCityName());
			}
			// 通知adapter更新数据
			adapter.notifyDataSetChanged();
			// 选中第一项
			listView.setSelection(0);
			// 地区名字设为选中的省份名字
			areaName.setText(selectedProvince.getProvinceName());
			// 当前ListView显示的是城市级别
			currentLevel = LEVEL_CITY;
		} else {
			// 如果在数据库中查不到，说明是第一次使用，就去服务器中查询
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/*
	 * 从数据库中查询县级市信息
	 */
	private void queryCounties() {
		// 从数据库去读县级市信息
		receiveCouties = coolWeatherDB.loadCounties(selectedCity.getId());
		// 如果数据库中有数据就直接使用
		if (receiveCouties.size() > 0) {
			// 清空ListView的data
			listData.clear();
			for (County county : receiveCouties) {
				// 信息更新为县级市名字
				listData.add(county.getCountyName());
			}
			// 通知adapter更新数据
			adapter.notifyDataSetChanged();
			// 选中第一项
			listView.setSelection(0);
			// 地区名字设为选中的县级市名字
			areaName.setText(selectedCity.getCityName());
			// 当前ListView显示的是县级市级别
			currentLevel = LEVEL_COUNTY;
		} else {
			// 如果在数据库中查不到，说明是第一次使用，就去服务器中查询
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/*
	 * 从服务器查询
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			// 如果有code值就把地址设为查询城市的网址
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			// 如果没有code值就把地址设为查询省份的网址
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		// 打开进度对话框
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new CallBackHttpUtil() {

			@Override
			public void onFinished(String response) {
				// 记录保存网络数据到数据库的操作的成败
				boolean result = false;
				// 如果查询的是省份信息，就去解析省份
				if ("province".equals(type)) {
					// 往数据库中存省份数据
					result = ParseUtility.handleProvinceResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					// 往数据库中存城市数据
					result = ParseUtility.handleCityResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					// 往数据库中存县级市数据
					result = ParseUtility.handleCountyResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					/*
					 * 因为现在是在sendHttpRequest()中的子线程中执行，所以
					 * 调用Activity中提供的runOnUiThread()方法回到主线程去更新UI，它
					 * 是和Handler一样的异步消息处理机制
					 */
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// 关闭进度对话框
							closeProgressDialog();
							if ("province".equals(type)) {
								// 重新去数据库中查询省份数据
								queryProvinces();
							} else if ("city".equals(type)) {
								// 重新去数据库中查询城市数据
								queryCities();
							} else if ("county".equals(type)) {
								// 重新去数据库中查询县级市数据
								queryCounties();
							}

						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				// 关闭进度对话框
				closeProgressDialog();
				// 提示错误信息
				Toast.makeText(ChooseAreaActivity.this, "加载失败",
						Toast.LENGTH_LONG).show();
			}
		});

	}

	/*
	 * 打开进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*
	 * 重写Activity的onBackPressed()方法
	 * 捕获点击返回键事件，根据currentLevel来判断是返回省份列表、城市列表还是直接退出
	 */
	@Override
	public void onBackPressed() {
		// 屏蔽父类方法，要不然会退出此Avtivity
		// super.onBackPressed();
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
