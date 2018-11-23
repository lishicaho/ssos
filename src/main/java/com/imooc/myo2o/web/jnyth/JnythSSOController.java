package com.imooc.myo2o.web.jnyth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.imooc.myo2o.entity.RequestInfo;
import com.imooc.myo2o.service.TokenService;
import com.imooc.myo2o.util.TokenCache;
import com.imooc.myo2o.util.TokenProcessor;

/**
 * @ClassName:  JnythSSOController   
 * @Description:For Jnyth SSO Server
 * @author: chengsp
 * @date:   2018年11月7日 下午4:18:59   
 *
 */
@Controller
@RequestMapping("/jnyth")
public class JnythSSOController {
	
	Logger logger = LoggerFactory.getLogger(JnythSSOController.class);
	
	@Autowired
	private TokenService tokenService;
	
	@RequestMapping(value="/generateToken",method=RequestMethod.GET)
	@ResponseBody
	public String generateToken(String ip,String username) {
		RequestInfo requestInfo=new RequestInfo();
		requestInfo.setIp(ip);
		requestInfo.setUsername(username);
		String token=tokenService.generateToken(requestInfo);
		return token;
	}
	
	@RequestMapping(value="/checkToken",method=RequestMethod.GET)
	@ResponseBody
	public String checkToken(String token, HttpServletRequest requestInfo) {
		String mess =tokenService.checkToken(token,requestInfo);
		return mess;
	}
	
	@RequestMapping(value="/getUserCode",method=RequestMethod.GET)
	@ResponseBody
	public String getUserCode(String ip) {
		RequestInfo requestInfo =new RequestInfo();
		requestInfo.setIp(ip);
		String mess=tokenService.getUserCode(requestInfo);
		return mess;
	}
	
	/**
	 * @Title: doLogin   
	 * @Description: Unified login interface
	 * @param: @param userID 用户账号
	 * @param: @param isSuccess 登录是否成功
	 * @param: @param request 请求域
	 * @return: token Be equal to null is fail
	 * 			token Be no equal to null is success
	 * @author: chengsp 
	 * @date:   2018年11月7日 下午5:22:27      
	 * @throws
	 */
	@RequestMapping(value="/doLogin",method=RequestMethod.GET)
	@ResponseBody
	private String doLogin(String userID,boolean isSuccess,String macID,HttpServletRequest request){
		String token=tokenService.doLogin(userID,isSuccess,macID,request);
		return token;
	}
	
	/**
	 * @Title: isNeedLogin   
	 * @Description: Unified Is login interface
	 * @param: request
	 * @return: "1" is login 
	 * 			"0" is no login 
	 * @author: chengsp 
	 * @date:   2018年11月7日 下午5:26:09      
	 * @throws
	 */
	@RequestMapping(value="/isNeedLogin",method=RequestMethod.GET)
	@ResponseBody
	private String isNeedLogin(String macID){
		String result=tokenService.isNeedLogin(macID);
		return result;
	}
	
	/**
	 * @Title: tokenCheck   
	 * @Description: Verify whether the token carried by the user is valid.
	 * @param:  reqToken
	 * @param:  request
	 * @return: String  
	 * @author: chengsp 
	 * @date:   2018年11月7日 下午6:41:57      
	 * @throws
	 */
	@RequestMapping(value="/tokenCheck",method=RequestMethod.GET)
	@ResponseBody
	public boolean tokenCheck(String reqToken,String macID){
		boolean bl=tokenService.tokenCheck(reqToken,macID);
		return bl;
	}
	
	/**
	 * @Title: logout   
	 * @Description: Unified logout interface  
	 * @param: @param reqToken
	 * @param: @param request
	 * @return: boolean  
	 * @author: chengsp 
	 * @date:   2018年11月7日 下午6:58:35      
	 * @throws
	 */
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	@ResponseBody
	public boolean logout(String reqToken,String macID){
		boolean bl=tokenService.logout(reqToken,macID);
		return bl;
	}
}
