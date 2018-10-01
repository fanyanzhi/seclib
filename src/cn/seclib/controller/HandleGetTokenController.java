package cn.seclib.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.seclib.pojo.Accesstoken;
import cn.seclib.service.AccesstokenService;
import cn.seclib.util.PropertiesUtils;
import cn.seclib.vo.WxToken;

@Controller
@RequestMapping("/handle")
public class HandleGetTokenController {

	private static final Logger logger = Logger.getLogger(cn.seclib.controller.HandleGetTokenController.class);
	
	private static String switcher = PropertiesUtils.getPropertyValue("switch");
	
	@Autowired
	private AccesstokenService accesstokenService;
	
	@RequestMapping("/get")
	@ResponseBody
	public String getToken() {
		if(!"1".equals(switcher)) {
			return "0";
		}
		String wxurl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx4e72191da5c4cc5b&secret=d0a9c12bbd52b83b9f390a7542eaf69d";
		String resone = cn.seclib.util.Common.sendGet(wxurl, null);
		logger.info("【主动获取accesstoken的结果是：】 " + resone);
		if(StringUtils.isNotBlank(resone)) {
			JSONObject jsonObjectone = JSONObject.parseObject(resone);
			String atoken = jsonObjectone.getString("access_token");
			Integer times = jsonObjectone.getInteger("expires_in");
			String errmsg = jsonObjectone.getString("errmsg");
			Integer errcode = jsonObjectone.getInteger("errcode");
			
			if(StringUtils.isNotBlank(atoken) && (null != times)) {
				Accesstoken dbToken = accesstokenService.getToken();
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
		return "1";
	}
}
