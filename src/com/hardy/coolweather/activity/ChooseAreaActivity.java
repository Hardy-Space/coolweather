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
	// ��¼��ǰListView����ʾʡ�ݡ����л����ؼ��е���Ϣ
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
				// �����ǰ�б�����ʾʡ�ݵģ�������ȥ��ʾ����
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = receiveProvinces.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					// �����ǰ�б�����ʾ���еģ�������ȥ��ʾ�ؼ���
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
		// ��һ�δ�ȥ��ѯʡ����Ϣ
		queryProvinces();

	}

	/*
	 * ȥ���ݿ��в�ѯʡ����Ϣ
	 */
	private void queryProvinces() {
		// �����ݿ�ȥ��ʡ����Ϣ
		receiveProvinces = coolWeatherDB.loadProvinces();
		// ������ݿ��������ݾ�ֱ��ʹ��
		if (receiveProvinces.size() > 0) {
			// ���ListView��data
			listData.clear();
			for (Province province : receiveProvinces) {
				// ��Ϣ����Ϊʡ������
				listData.add(province.getProvinceName());
			}
			// ֪ͨadapter��������
			adapter.notifyDataSetChanged();
			// ѡ�е�һ��
			listView.setSelection(0);
			// ����������Ϊ���й���
			areaName.setText("�й�");
			// ��ǰListView��ʾ����ʡ�ݼ���
			currentLevel = LEVEL_PROVINCE;
		} else {
			// ��������ݿ��в鲻����˵���ǵ�һ��ʹ�ã���ȥ�������в�ѯ
			queryFromServer(null, "province");
		}
	}

	/*
	 * ȥ���ݿ��в�ѯ������Ϣ
	 */
	private void queryCities() {
		// �����ݿ�ȥ��������Ϣ
		receiveCities = coolWeatherDB.loadCities(selectedProvince.getId());
		// ������ݿ��������ݾ�ֱ��ʹ��
		if (receiveCities.size() > 0) {
			// ���ListView��data
			listData.clear();
			for (City city : receiveCities) {
				// ��Ϣ����Ϊ��������
				listData.add(city.getCityName());
			}
			// ֪ͨadapter��������
			adapter.notifyDataSetChanged();
			// ѡ�е�һ��
			listView.setSelection(0);
			// ����������Ϊѡ�е�ʡ������
			areaName.setText(selectedProvince.getProvinceName());
			// ��ǰListView��ʾ���ǳ��м���
			currentLevel = LEVEL_CITY;
		} else {
			// ��������ݿ��в鲻����˵���ǵ�һ��ʹ�ã���ȥ�������в�ѯ
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/*
	 * �����ݿ��в�ѯ�ؼ�����Ϣ
	 */
	private void queryCounties() {
		// �����ݿ�ȥ���ؼ�����Ϣ
		receiveCouties = coolWeatherDB.loadCounties(selectedCity.getId());
		// ������ݿ��������ݾ�ֱ��ʹ��
		if (receiveCouties.size() > 0) {
			// ���ListView��data
			listData.clear();
			for (County county : receiveCouties) {
				// ��Ϣ����Ϊ�ؼ�������
				listData.add(county.getCountyName());
			}
			// ֪ͨadapter��������
			adapter.notifyDataSetChanged();
			// ѡ�е�һ��
			listView.setSelection(0);
			// ����������Ϊѡ�е��ؼ�������
			areaName.setText(selectedCity.getCityName());
			// ��ǰListView��ʾ�����ؼ��м���
			currentLevel = LEVEL_COUNTY;
		} else {
			// ��������ݿ��в鲻����˵���ǵ�һ��ʹ�ã���ȥ�������в�ѯ
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/*
	 * �ӷ�������ѯ
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			// �����codeֵ�Ͱѵ�ַ��Ϊ��ѯ���е���ַ
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			// ���û��codeֵ�Ͱѵ�ַ��Ϊ��ѯʡ�ݵ���ַ
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		// �򿪽��ȶԻ���
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new CallBackHttpUtil() {

			@Override
			public void onFinished(String response) {
				// ��¼�����������ݵ����ݿ�Ĳ����ĳɰ�
				boolean result = false;
				// �����ѯ����ʡ����Ϣ����ȥ����ʡ��
				if ("province".equals(type)) {
					// �����ݿ��д�ʡ������
					result = ParseUtility.handleProvinceResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					// �����ݿ��д��������
					result = ParseUtility.handleCityResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					// �����ݿ��д��ؼ�������
					result = ParseUtility.handleCountyResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					/*
					 * ��Ϊ��������sendHttpRequest()�е����߳���ִ�У�����
					 * ����Activity���ṩ��runOnUiThread()�����ص����߳�ȥ����UI����
					 * �Ǻ�Handlerһ�����첽��Ϣ�������
					 */
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// �رս��ȶԻ���
							closeProgressDialog();
							if ("province".equals(type)) {
								// ����ȥ���ݿ��в�ѯʡ������
								queryProvinces();
							} else if ("city".equals(type)) {
								// ����ȥ���ݿ��в�ѯ��������
								queryCities();
							} else if ("county".equals(type)) {
								// ����ȥ���ݿ��в�ѯ�ؼ�������
								queryCounties();
							}

						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				// �رս��ȶԻ���
				closeProgressDialog();
				// ��ʾ������Ϣ
				Toast.makeText(ChooseAreaActivity.this, "����ʧ��",
						Toast.LENGTH_LONG).show();
			}
		});

	}

	/*
	 * �򿪽��ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*
	 * ��дActivity��onBackPressed()����
	 * ���������ؼ��¼�������currentLevel���ж��Ƿ���ʡ���б������б���ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		// ���θ��෽����Ҫ��Ȼ���˳���Avtivity
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
