package com.imooc.myo2o.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName MacUtil
 * @Description 获取Mac地址
 * @author csp
 * @date 2018年11月9日 上午10:24:52
 */
public class MacUtil {
	
	public static String getLocalMac() throws Exception {
		InetAddress ia = InetAddress.getLocalHost();
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		System.out.println("本机MAC地址:"+sb.toString().toUpperCase());
		return sb.toString().toUpperCase();
	}
	
	/**
	  * 根据IP地址获取mac地址
	  * @param ipAddress 127.0.0.1
	  * @return
	  * @throws SocketException
	  * @throws UnknownHostException
	  */
	 public static String getLocalMac(String ipAddress) throws SocketException,
	   UnknownHostException {
	  // TODO Auto-generated method stub
	  String str = "";
	  String macAddress = "";
	  final String LOOPBACK_ADDRESS = "127.0.0.1";
	  // 如果为127.0.0.1,则获取本地MAC地址。
	  if (LOOPBACK_ADDRESS.equals(ipAddress)) {
	   InetAddress inetAddress = InetAddress.getLocalHost();
	   // 貌似此方法需要JDK1.6。
	   byte[] mac = NetworkInterface.getByInetAddress(inetAddress)
	     .getHardwareAddress();
	   // 下面代码是把mac地址拼装成String
	   StringBuilder sb = new StringBuilder();
	   for (int i = 0; i < mac.length; i++) {
	    if (i != 0) {
	     sb.append("-");
	    }
	    // mac[i] & 0xFF 是为了把byte转化为正整数
	    String s = Integer.toHexString(mac[i] & 0xFF);
	    sb.append(s.length() == 1 ? 0 + s : s);
	   }
	   // 把字符串所有小写字母改为大写成为正规的mac地址并返回
	   macAddress = sb.toString().trim().toUpperCase();
	   return macAddress;
	  } else {
	   // 获取非本地IP的MAC地址
	   try {
	    Process p = Runtime.getRuntime()
	      .exec("nbtstat -A " + ipAddress);
	    InputStreamReader ir = new InputStreamReader(p.getInputStream());      
	    BufferedReader br = new BufferedReader(ir);    
	    while ((str = br.readLine()) != null) {
	     if(str.indexOf("MAC")>1){
	      macAddress = str.substring(str.indexOf("MAC")+9, str.length());
	      macAddress = macAddress.trim();
	      System.out.println("macAddress:" + macAddress);
	      break;
	     }
	    }
	    p.destroy();
	    br.close();
	    ir.close();
	   } catch (IOException ex) {
	   }
	   return macAddress;
	  }
	 }
}
