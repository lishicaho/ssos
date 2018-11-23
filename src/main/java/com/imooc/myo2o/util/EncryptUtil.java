package com.imooc.myo2o.util;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @ClassName:  EncryptUtil   
 * @Description:加密/解密 字符串
 * 				DES加密算法属于对称加密。
				即利用指定的密钥，按照密码的长度截取数据，分成数据块，和密钥进行复杂的移位、算数运算或者数据处理等操作
				形成只有特定的密码才能够解开的数据。 加密与解密用的是同一个密钥
 * @author: chengsp
 * @date:   2018年11月8日 上午10:21:04   
 *
 */
public class EncryptUtil {
	// 向量
	private final byte[] DESIV = new byte[] { 0x12, 0x34, 0x56, 120, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef };
	// 加密算法的参数接口
	private AlgorithmParameterSpec iv = null;
	//秘钥
	private Key key = null;
	//对外提供私有化实例对象
	private static EncryptUtil instance = new EncryptUtil();
	
	public static EncryptUtil getInstance(){
		return instance;
	}
	
	/**
	 * 初始化秘钥及加密算法
	 */
	private EncryptUtil() {
		// 自定义密钥
		String deSkey = "9ba45bfd732171109ec03ad8ef1b6e75";
		try {
			//创建DES加密算法对象，并设置编码集
			DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes("utf-8"));
			// 设置向量
			iv = new IvParameterSpec(DESIV);
			// 获得密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			key = keyFactory.generateSecret(keySpec);// 得到密钥对象
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Title: encode   
	 * @Description: 加密 
	 * @param: data 需要解密的数据
	 * @param: @throws Exception      
	 * @return: 加密后的数据  
	 * @author: chengsp 
	 * @date:   2018年11月8日 上午10:55:05      
	 * @throws
	 */
	public String encode(String data) throws Exception {
		// 得到加密对象Cipher
		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 设置工作模式为加密模式，给出密钥和向量
		enCipher.init(Cipher.ENCRYPT_MODE, key, iv);
		// 开始加密并返回结果
		byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(pasByte);
	}
	
	/**
	 * @Title: decode   
	 * @Description: 解密   
	 * @param: data 需要解密的数据
	 * @throws Exception      
	 * @return: 解密后数据  
	 * @author: chengsp
	 * @date:   2018年11月8日 上午11:39:51      
	 * @throws
	 */
	public String decode(String data) throws Exception {
		// 得到加密对象Cipher
		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 设置工作模式为加密模式，给出密钥和向量
		deCipher.init(Cipher.DECRYPT_MODE, key, iv);
		// 开始解密并返回结果
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
		return new String(pasByte, "UTF-8");
	}

}
