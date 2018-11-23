package com.imooc.myo2o.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @ClassName:  HttpRequest   
 * @Description:发送doGet请求 
 * @author: chengsp
 * @date:   2018年11月8日 下午3:03:47   
 *
 */
public class HttpRequest {
	public static String doGet(String url, String reqToken) {
		StringBuffer sb = new StringBuffer();
		HttpURLConnection httpURLConnection = null;
		try {
			// 创建URL对象
			String urlString = url + "?reqToken=" + reqToken;
			System.out.println("urlString: \t" + urlString);
			URL urls = new URL(urlString);
			// 得到链接
			httpURLConnection = (HttpURLConnection) urls.openConnection();
			// 设置方法
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			InputStream in = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			isr.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != httpURLConnection) {
				httpURLConnection.disconnect();
			}
		}
		return sb.toString();
	}
}
