package cn.seclib.service;

import cn.seclib.pojo.Accesstoken;

public interface AccesstokenService {
	
	public Accesstoken getToken();
	
	/**
	 * 添加
	 * @param accesstoken
	 */
	public void insertToken(Accesstoken accesstoken);
	
	/**
	 * 更新
	 * @param accesstoken
	 */
	public void updateToken(Accesstoken accesstoken);
}
