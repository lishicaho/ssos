package com.imooc.myo2o.service;

import javax.servlet.http.HttpServletRequest;

import com.imooc.myo2o.entity.RequestInfo;

public interface TokenService {

	String generateToken(RequestInfo requestInfo);

	String checkToken(String token, HttpServletRequest requestInfo);

	String getUserCode(RequestInfo requestInfo);

	String doLogin(String userID, boolean isSuccess, String macID, HttpServletRequest request);

	String isNeedLogin(String macID);

	boolean tokenCheck(String reqToken, String macID);

	boolean logout(String reqToken, String macID);

}
