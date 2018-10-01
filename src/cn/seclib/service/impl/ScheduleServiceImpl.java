package cn.seclib.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

import cn.seclib.pojo.Client;
import cn.seclib.service.ClientService;
import cn.seclib.util.PropertiesUtils;
import cn.seclib.vo.WxToken;

public class ScheduleServiceImpl {

	private static final Logger logger = Logger.getLogger(cn.seclib.service.impl.ScheduleServiceImpl.class);
	
	private static String alephUrl = PropertiesUtils.getPropertyValue("alephurl");
	private static String phone = PropertiesUtils.getPropertyValue("phone");
	private static String preinformdays = PropertiesUtils.getPropertyValue("preinformdays");

	@Autowired
	private ClientService clientService;
	
	//测试
	public void show() {
		System.out.println(1111);
	}
	
	//图书到期提醒
	public void returnBookNotice() {
		int days = 7;
		try {
			Integer temp = new Integer(preinformdays);
			days = temp;
		}catch(Exception e) {
		}
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		
		List<Client> clientList = clientService.getAll();
		if(null != clientList && clientList.size() > 0) {
			for (Client client : clientList) {
				String cardno = client.getCardno();
				String openid = client.getOpenid();
				String urlbor = alephUrl + "/X?op=bor-info&bor_id="+cardno+"&bor_id_type=00&user_name=www-drwxin&user_password=drcwwxff1&con_lng=chi";
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/xml");
				String resone = cn.seclib.util.Common.sendGet(urlbor, headers);
				Document doc = null;
				try {
					doc = DocumentHelper.parseText(resone);
					Element root = doc.getRootElement();
					Iterator itemIter = root.elementIterator("item-l");
					if(null != itemIter) {
						while (itemIter.hasNext()) {
							Element item = (Element) itemIter.next();
							Element z36 = item.element("z36");
							String due_date = z36.elementText("z36-due-date"); // 到期日期 20170829
							String due_hour = z36.elementText("z36-due-hour");// 到期时间 23:59
							
							Date dueDate = sdf2.parse(due_date);
							int res = cn.seclib.util.Common.getIntervalDays(cn.seclib.util.Common.getNowDateShort(), dueDate);
							if(res == days) { //提前7天提醒
								String datetime = sdf1.format(dueDate);
								String keyword2 = datetime + " " + due_hour;
								Element z13 = item.element("z13");
								String keyword1 = z13.elementText("z13-title"); // 书名
								
								//和日期进行比较，符合条件就推送
								String surl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + WxToken.wxToken;
								logger.info("推送模板URL：" + surl);
								JSONObject paramj1 = new JSONObject();
								paramj1.put("touser", openid);
								paramj1.put("template_id", "YtMd2bD6-fOhw8fmNmZoAIg5szLy1c2NYMZkySMsQyk");
								Map<String, Map<String, String>> sm1 = new HashMap<String, Map<String, String>>();
								Map<String, String> m1 = new HashMap<String, String>();
								m1.put("value", "读者您好：您在我馆借阅的图书即将到期，请及时归还以免超期，超期0.2元/册/天");
								m1.put("color", "#173177");
								sm1.put("first", m1);
								
								Map<String, String> m2 = new HashMap<String, String>();
								m2.put("value", keyword1);
								m2.put("color", "#173177");
								sm1.put("keyword1", m2);
								
								Map<String, String> m3 = new HashMap<String, String>();
								m3.put("value", keyword2);
								m3.put("color", "#FF0000");
								sm1.put("keyword2", m3);
								
								Map<String, String> m4 = new HashMap<String, String>();
								m4.put("value", "如有疑问，请致电"+phone);
								m4.put("color", "#173177");
								sm1.put("remark", m4);
								paramj1.put("data", sm1);
								String sendPostRes = cn.seclib.util.Common.sendPost(surl, paramj1.toString());
								logger.info("【发送模板消息返回的结果：】 " + sendPostRes);
							}
						}
					}
				} catch(Exception e) {
					logger.info("【获取读者信息出错: openid 】" + openid + " cardno " + cardno + " 异常 " + e);
				}
			}
		}
	}
	
	public static void main(String[] args) throws ParseException {
		String s = "20170829";
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Date d = sdf2.parse(s);
		int res = cn.seclib.util.Common.getIntervalDays(cn.seclib.util.Common.getNowDateShort(), d);
		System.out.println(d);
		System.out.println(res);
		String ss = sdf1.format(d);
		System.out.println(ss);
	}
}
