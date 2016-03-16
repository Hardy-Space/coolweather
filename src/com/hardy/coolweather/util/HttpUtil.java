package com.hardy.coolweather.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/*
 * 发送Http网络请求实用（Utility）类
 */
public class HttpUtil {

	public static void sendHttpRequest(final String address,
			final CallBackHttpUtil listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String response = null;
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(address);
					HttpResponse httpResponse = client.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						response = EntityUtils.toString(entity, "utf-8");
					}
					if (listener != null) {
						listener.onFinished(response);
					}

				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				}

			}
		}).start();
	}

}
