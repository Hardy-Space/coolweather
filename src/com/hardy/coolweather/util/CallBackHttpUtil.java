package com.hardy.coolweather.util;

public interface CallBackHttpUtil {
	public void onFinished(String response);
	public void onError(Exception e);
}
