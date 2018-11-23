package com.imooc.myo2o.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import sun.misc.BASE64Encoder;
/**
 * @ClassName:  TokenProcessor   
 * @Description:令牌生成器
 * @author: chengsp
 * @date:   2018年11月7日 下午3:17:02   
 *
 */
public class TokenProcessor {
	private TokenProcessor(){}
	
    private static TokenProcessor instance = new TokenProcessor();
    
    public static TokenProcessor getInstance(){
        return instance;
    }
    
    /**
     * @Title: generateTokeCode   
     * @Description: 生成令牌  
     * @param: @return      
     * @return: String  
     * @author: chengsp 
     * @date:   2018年11月7日 下午3:18:27      
     * @throws
     */
    public String generateTokeCode(){
		String value = System.currentTimeMillis()+new Random().nextInt()+"";
		//获取数据指纹，指纹是唯一的
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] b = md.digest(value.getBytes());//产生数据的指纹
			//Base64编码
			BASE64Encoder be = new BASE64Encoder();
			be.encode(b);
			return be.encode(b);//制定一个编码
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
