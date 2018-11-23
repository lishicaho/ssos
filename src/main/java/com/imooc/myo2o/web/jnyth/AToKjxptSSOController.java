package com.imooc.myo2o.web.jnyth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.imooc.myo2o.util.EncryptUtil;
import com.imooc.myo2o.util.HttpRequest;
import com.imooc.myo2o.util.TokenCache;
import com.imooc.myo2o.util.TokenProcessor;

/**
 * @ClassName:  AToKjxptSSOController   
 * @Description:A++ to KJXPT SSO Server
 * @author: chengsp
 * @date:   2018年11月8日 上午9:54:21   
 *
 */
@Controller
@RequestMapping("/aToKjxpt")
public class AToKjxptSSOController {
	
	private static Logger logger = LoggerFactory.getLogger(AToKjxptSSOController.class);
	
	/**
	 * @Title: getCheckInfo   Interact with you
	 * @Description: for kjxpt user info and token
	 * @param: request
	 * @return: Map<String,Object> 
	 * @author: chengsp 
	 * @date:   2018年11月8日 上午10:13:06      
	 * @throws
	 */
	@RequestMapping(value="/getCheckInfo",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getCheckInfo(HttpServletRequest request){
		logger.info("start private data to xkjpt");
		HttpSession session = request.getSession();
		//Get userID from session
		String userID = "zhangsan";
		//token into session 
		String token = TokenProcessor.getInstance().generateTokeCode();
		session.setAttribute("token", token);
		 //放入本地缓存
        TokenCache.setkey(TokenCache.TOKEN_PREFIX+userID,token);
		//Encrypt userID and token
		Map<String, Object> resuleMap = new HashMap<String,Object>();
		try {
			resuleMap.put("userID", EncryptUtil.getInstance().encode(userID));
			resuleMap.put("token", EncryptUtil.getInstance().encode(token));
		} catch (Exception e) {
			logger.info("Encrypt exception");
		}
		logger.info("end private data to xkjpt");
		return resuleMap;
	}
	
	/**
	 * @Title: checkToken   
	 * @Description:   
	 * @param: @param reqToken
	 * @param: @param request
	 * @param: @return      
	 * @return: boolean  
	 * @author: chengsp
	 * @date:   2018年11月8日 下午2:04:48      
	 * @throws
	 */
	@RequestMapping(value="/checkToken",method=RequestMethod.GET)
	@ResponseBody
	public String checkToken(String reqToken,String userID){
		//从本地缓存中获取token
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+userID);
		try {
			logger.info("Declassified passing token");
			String decodeToken = EncryptUtil.getInstance().decode(reqToken);
			//String decodeToken=token;
			if(token != null && token.equals(decodeToken)){
				logger.info("check token success");
				return "1";
			}
		} catch (Exception e) {
			logger.info("Encrypt exception");
		}
		logger.info("check token failure");
		return "0";
	}
	
	
	@RequestMapping(value="/checkSSOLogin",method=RequestMethod.GET)
	@ResponseBody
	public String checkSSOLogin(String reqUserID,String reqToken){
		try {
			//按约定解密
			String userID = EncryptUtil.getInstance().decode(reqUserID);
			String token = EncryptUtil.getInstance().decode(reqToken);
			//拿着userID校验用户有没有问题
			//全局缓存解密后的token
			//拿着reqToken去A++平台校验有没有问题
			String result = HttpRequest.doGet("http://localhost:8080/ssos/aToKjxpt/checkToken.do"
			//String result = HttpRequest.doGet("http://localhost:8050/ssos/aToKjxpt/checkToken.do"
			//String result = HttpRequest.doGet("http://39.106.0.91:3306/ssos/aToKjxpt/checkToken.do"
					, reqToken);
			// 1:成功 ； 2.失败
			if("1".equals(result)){
				logger.info("验证成功");
				return "1";
			}
		} catch (Exception e) {
			logger.info("Encrypt exception");
		}
		return "0";
	}
}
