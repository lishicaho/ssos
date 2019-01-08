package com.imooc.myo2o.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.imooc.myo2o.entity.RequestInfo;
import com.imooc.myo2o.service.TokenService;
import com.imooc.myo2o.util.GetMacAddress;
import com.imooc.myo2o.util.IPUtil;
import com.imooc.myo2o.util.MacUtil;
import com.imooc.myo2o.util.TokenCache;
import com.imooc.myo2o.util.TokenProcessor;
import com.imooc.myo2o.util.UdpGetClientMacAddr;
import com.imooc.myo2o.web.jnyth.JnythSSOController;

@Service
public class NjTokenServiceImpl implements TokenService {
	
	Logger logger = LoggerFactory.getLogger(JnythSSOController.class);

	@Value("${pty.security.checkToken}")
	private String checkToken;

	
	/** 南京的sso服务的api: 例如->http://192.168.12.11:8080/sso macID=%s"*/
	 
	@Value("${pty.security.ssoUri}")
	private String ssoUri;

	private RestTemplate restTemplate;

	private ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	private void init() {
		// TODO(lsc): 考虑性能，外边初始化,网上查下
		restTemplate = new RestTemplateBuilder().build();
	}

	@Override
	public String generateToken(RequestInfo requestInfo) {
		try {
			// TODO(lsc): 这个地方获取的ip是否有问题?参考IpUtil
			//String macId = MacUtil.getLocalMac(requestInfo.getIp());
			
			String macId = null;
    		//获取请求IP
			String ip = requestInfo.getIp();
			long startTime=System.currentTimeMillis();
			//判断是否是本机请求
			/*if(ip.equals(IPUtil.getLocalIP())){
				macId = MacUtil.getLocalMac();
			}else{
				UdpGetClientMacAddr ugcm = new UdpGetClientMacAddr(ip);
				//A方案获取，IE首推
				macId = ugcm.GetRemoteMacAddr();
			}*/
			 macId = new GetMacAddress().getMacAddress(ip);
			String newMacId=macId.toUpperCase(); 
			String url = String.format(ssoUri + "/jnyth/doLogin?userID=%s&isSuccess=true&macID=%s",
					requestInfo.getUsername(), newMacId);
			String token = restTemplate.getForObject(url, String.class);
			return token;
		} catch (Exception ex) {
			throw new RuntimeException("获取token失败:" + ex.getMessage());
		}
	}


	@Override
	public String checkToken(String token, HttpServletRequest requestInfo) {
		try {
			//String ip="192.168.60.224";
			//String macId = MacUtil.getLocalMac(IPUtil.getRemoteIp(requestInfo));
			String macId = new GetMacAddress().getMacAddress(IPUtil.getRemoteIp(requestInfo));
			String newMacId=macId.toUpperCase(); 
			//String macId = MacUtil.getLocalMac(ip);
			String url = String.format(ssoUri + "/jnyth/tokenCheck?reqToken=%s&macID=%s", token, newMacId);
		    //String url = String.format("http://localhost:8080/ssos/jnyth/tokenCheck?reqToken=%s&macID=%s", token, macId);
			Boolean isOk = restTemplate.getForObject(url, Boolean.class);
			if (isOk) {
				return token;
			} else {
				throw new RuntimeException("token 无效");
			}
		} catch (Exception ex) {
			throw new RuntimeException("校验token失败:" + ex.getMessage());
		}
	}

	@Override
	public String getUserCode(RequestInfo requestInfo) {
		try {
			//String macId = MacUtil.getLocalMac(requestInfo.getIp());
			String macId = new GetMacAddress().getMacAddress(requestInfo.getIp());
			String newMacId=macId.toUpperCase(); 
			String url = String.format(ssoUri + "/jnyth/isNeedLogin?macID=%s", newMacId);
		    //String url = String.format("http://localhost:8080/ssos/jnyth/isNeedLogin?macID=%s", macId);
			String userInfo = restTemplate.getForObject(url, String.class);
			if (!"0".equals(userInfo)) {
				Map userMap = mapper.readValue(userInfo, Map.class);
				return (String)userMap.get("userID");
			} else {
				throw new RuntimeException("");
			}
		} catch (Exception ex) {
			throw new RuntimeException("获取用户信息失败");
		}
	}

	@Override
	public String doLogin(String userID, boolean isSuccess, String macID, HttpServletRequest request) {
		long startTimeS=System.currentTimeMillis();
		logger.info("====into sso server doLogin start====");
		String token = null;
		//Login success
		if(isSuccess){
			logger.info("setting login information to TokenCache");
			token = TokenProcessor.getInstance().generateTokeCode();
			TokenCache.setkey("isLogin"+macID, "true");
			TokenCache.setkey("userID"+macID, userID);
			TokenCache.setkey("token"+macID, token.replace("+", "="));
		}
		logger.info("====into sso server doLogin end====");
		//执行方法
		long endTimeS=System.currentTimeMillis();
		float excTimeS=(float)(endTimeS-startTimeS)/1000;
		System.out.println("结束请求sso"+"执行时间："+excTimeS+"s");
		return token;
	}

	@Override
	public String isNeedLogin(String macID) {
		//result set
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//If login, it will be released directly.Set success result set
		System.out.println(TokenCache.getKey("isLogin"+macID));
		if("true".equals(TokenCache.getKey("isLogin"+macID))){
			logger.info("User is logged in, set parameters");
			String userID = TokenCache.getKey("userID"+macID);
			String token = TokenCache.getKey("token"+macID);
			resultMap.put("userID",userID);
			resultMap.put("token", token);
			resultMap.put("status", true);
			Gson gson = new Gson();
			return gson.toJson(resultMap).toString();
		}else{
			//If no login,Return to state 0, notify the login page of the request system.
			return "0";
		}
	}

	@Override
	public boolean tokenCheck(String reqToken, String macID) {
		logger.info("User request validation token");
		String token =  TokenCache.getKey("token"+macID);
		if(null != token && token.equals(reqToken)){
			logger.info("Verification success");
			return true;
		}
		logger.info("Verification failure");
		return false;
	}

	@Override
	public boolean logout(String reqToken, String macID) {
		String token = TokenCache.getKey("token"+macID);
		logger.info("Subsystem request to exit wait clean token");
		if(null != token && token.equals(reqToken)){
			//clean token
			TokenCache.cleanKey("isLogin"+macID);
			TokenCache.cleanKey("userID"+macID);
			TokenCache.cleanKey("token"+macID);
			logger.info("Exit success");
			return true;
		}
		logger.info("Exit failure");
		return false;
	}

}
