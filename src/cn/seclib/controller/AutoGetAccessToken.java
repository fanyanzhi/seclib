package cn.seclib.controller;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import com.alibaba.fastjson.JSONObject;
import cn.seclib.pojo.Accesstoken;
import cn.seclib.service.AccesstokenService;
import cn.seclib.vo.WxToken;

public class AutoGetAccessToken implements SmartLifecycle {
	
	private static final Logger logger = Logger.getLogger(cn.seclib.controller.AutoGetAccessToken.class);
	
	@Autowired
	private AccesstokenService accesstokenService;

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public void start() {
		Executors.newScheduledThreadPool(1, new ThreadFactory() { 
	        public Thread newThread(Runnable r) { 
	            Thread s = Executors.defaultThreadFactory().newThread(r); 
	            s.setDaemon(true); 
	            return s; 
	        } 
	    } ).scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				Accesstoken dbToken = accesstokenService.getToken();
				String wxurl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx4e72191da5c4cc5b&secret=d0a9c12bbd52b83b9f390a7542eaf69d";
				
				if(null != dbToken) {
					Date dbExpiretime = dbToken.getExpiretime();
					Date now = new Date();
					long dbExpireLong = dbExpiretime.getTime()/1000;
					long nowTimeLong = now.getTime()/1000;
					long sub = dbExpireLong - nowTimeLong;
					if(sub <= 600) {
						String resone = cn.seclib.util.Common.sendGet(wxurl, null);
						logger.info("【自动获取accesstoken的结果是：】 " + resone);
						if(StringUtils.isNotBlank(resone)) {
							JSONObject jsonObjectone = JSONObject.parseObject(resone);
							String atoken = jsonObjectone.getString("access_token");
							Integer times = jsonObjectone.getInteger("expires_in");
							String errmsg = jsonObjectone.getString("errmsg");
							Integer errcode = jsonObjectone.getInteger("errcode");
							
							if(StringUtils.isNotBlank(atoken) && (null != times)) {
								dbToken.setAccesstoken(atoken);
								DateTime nowDt = new DateTime();
								DateTime expireTime = nowDt.plusSeconds(times);
								dbToken.setExpiretime(expireTime.toDate());
								try {
									accesstokenService.updateToken(dbToken);
								}catch(Exception e) {
									logger.error("更新accesstoken失败");
								}
								WxToken.wxToken = atoken;
							}else {
								logger.error("【获取accesstoken失败：】" + errmsg + "  " + errcode);
							}
						}
					}else {
						WxToken.wxToken = dbToken.getAccesstoken();
					}
					
				}else {
					String resone = cn.seclib.util.Common.sendGet(wxurl, null);
					logger.info("【获取accesstoken的结果是：】 " + resone);
					if(StringUtils.isNotBlank(resone)) {
						JSONObject jsonObjectone = JSONObject.parseObject(resone);
						String atoken = jsonObjectone.getString("access_token");
						Integer times = jsonObjectone.getInteger("expires_in");
						String errmsg = jsonObjectone.getString("errmsg");
						Integer errcode = jsonObjectone.getInteger("errcode");
						
						if(StringUtils.isNotBlank(atoken) && (null != times)) {
							Accesstoken token = new Accesstoken();
							token.setAccesstoken(atoken);
							DateTime nowDt = new DateTime();
							DateTime expireTime = nowDt.plusSeconds(times);
							token.setExpiretime(expireTime.toDate());
							try {
								accesstokenService.insertToken(token);
							}catch(Exception e) {
								logger.error("插入accesstoken失败");
							}
							WxToken.wxToken = atoken;
						}else {
							logger.error("【获取accesstoken失败：】" + errmsg + "  " + errcode);
						}
					}
				}
				
			}
		}, 1, 600, TimeUnit.SECONDS);
	}
	
	@Override
	public void stop() {
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable arg0) {
	}

}
