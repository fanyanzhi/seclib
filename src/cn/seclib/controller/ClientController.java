package cn.seclib.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import cn.seclib.pojo.Client;
import cn.seclib.service.ClientService;
import cn.seclib.util.PropertiesUtils;
import cn.seclib.vo.WxToken;

@Controller
@RequestMapping("/client")
public class ClientController {

	private static final Logger logger = Logger.getLogger(cn.seclib.controller.ClientController.class);

	private static String alephUrl = PropertiesUtils.getPropertyValue("alephurl");

	@Autowired
	private ClientService clientService;

	@RequestMapping("/clientbindshow")
	public String show(Model model, HttpServletRequest request) {
		String code = request.getParameter("code");
		logger.info("【code】:" + code);
		if (StringUtils.isNotBlank(code)) {
			String wxurl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx4e72191da5c4cc5b&secret=d0a9c12bbd52b83b9f390a7542eaf69d&code="
					+ code + "&grant_type=authorization_code";
			String resone = cn.seclib.util.Common.sendGet(wxurl, null);
			JSONObject json = JSONObject.parseObject(resone);
			logger.info("【获取网页授权的结果是：】 " + resone);
			if (null != json) {
				String openid = json.getString("openid");
				// 去数据库查询的逻辑
				Client dbClient = clientService.selectByOpenid(openid);
				if (null != dbClient) {
					String cardno = dbClient.getCardno();
					String password = dbClient.getPassword();
					model.addAttribute("cardno", cardno);
					model.addAttribute("openid", openid);

					// 得到详情
					String url1 = alephUrl + "/X?op=bor-auth-valid&Library=STL50&bor_id=" + cardno + "&verification="
							+ password + "&Sub_Library=STL50&user_name=www-drwxin&user_password=drcwwxff1";
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Content-Type", "application/xml");
					String restwo = cn.seclib.util.Common.sendGet(url1, headers);
					Document doc = null;
					try {
						doc = DocumentHelper.parseText(restwo);
						Element root = doc.getRootElement();
						Element z303 = root.element("z303");
						if (null != z303) {
							String name = z303.elementText("z303-name"); // 姓名
							String birthday = z303.elementText("z303-birth-date"); // 生日
							model.addAttribute("name", name);
							model.addAttribute("birthday", birthday);

							Element z304 = root.element("z304");
							if (null != z304) {
								String telephone = z304.elementText("z304-telephone"); // 电话
								model.addAttribute("telephone", telephone);
							}

							Element z305 = root.element("z305");
							if (null != z305) {
								String cardtype = z305.elementText("z305-bor-status"); // 读者类型
								String sum = z305.elementText("z305-sum"); // 余额
								model.addAttribute("cardtype", cardtype);
								model.addAttribute("sum", sum);
							}
						}
					} catch (Exception e) {
						// 不去异常页面，可能密码变了，如果报错则不显示详情，直接去解绑
					}

					return "client/bindsuccess";
				} else {
					model.addAttribute("openid", openid);
					return "client/bindshow";
				}
			}
		}
		return null;
	}

	@RequestMapping("/test")
	public String test() {
		return "client/bindshow";
	}

	@RequestMapping("/unbind")
	public String unbind(String openid, String cardno) {
		try {
			logger.info("openid " + openid + "cardno " + cardno);
			clientService.deleteByOpenidAndCardno(openid, cardno);
			return "client/unbindsuccess";
		} catch (Exception e) {
			return "client/unbindfail";
		}
	}

	@RequestMapping("/curload")
	@ResponseBody
	public List<Map<String, String>> curLoan(String cardno) {
		String urlbor = alephUrl + "/X?op=bor-info&bor_id=" + cardno
				+ "&bor_id_type=00&user_name=www-drwxin&user_password=drcwwxff1&con_lng=chi";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/xml");
		String resone = cn.seclib.util.Common.sendGet(urlbor, headers);
		List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(resone);
			Element root = doc.getRootElement();
			Iterator itemIter = root.elementIterator("item-l");
			if (null != itemIter) {
				while (itemIter.hasNext()) {
					map = new HashMap<String, String>();
					Element item = (Element) itemIter.next();
					Element z36 = item.element("z36");
					String due_date = z36.elementText("z36-due-date"); // 到期日期
																		// 20170829
					String due_hour = z36.elementText("z36-due-hour");// 到期时间
																		// 23:59

					Element z13 = item.element("z13");
					String title = z13.elementText("z13-title"); // 书名
					String author = z13.elementText("z13-author");
					String ssh = z13.elementText("z13-call-no");
					map.put("title", title);
					map.put("author", author);
					map.put("ssh", ssh);
					map.put("dutedate", due_date);
					lst.add(map);
				}
			}
		} catch (Exception e) {
			logger.info("【获取读者信息出错: 】 cardno " + cardno + " 异常 " + e);
		}
		return lst;
	}

	/*
	 * 绑定读者卡
	 */
	@RequestMapping("/bind")
	public String bind(Model model, String openid, String cardno, String password) {
		String url1 = alephUrl + "/X?op=bor-auth-valid&Library=STL50&bor_id=" + cardno + "&verification=" + password
				+ "&Sub_Library=STL50&user_name=www-drwxin&user_password=drcwwxff1";
		logger.info("【绑定读者卡时，认证的url】:" + url1);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/xml");
		String resone = cn.seclib.util.Common.sendGet(url1, headers);
		// logger.info("【绑定读者卡返回】:" + resone);
		if (StringUtils.isBlank(resone)) {
			return "client/bindfail";
		}

		Document doc = null;
		try {
			doc = DocumentHelper.parseText(resone);
			Element root = doc.getRootElement();
			Element z303 = root.element("z303");
			if (null != z303) {
				String borid = z303.elementText("z303-id");
				String name = z303.elementText("z303-name"); // 姓名
				String birthday = z303.elementText("z303-birth-date"); // 生日
				model.addAttribute("name", name);
				model.addAttribute("birthday", birthday);
				model.addAttribute("cardno", cardno);
				model.addAttribute("openid", openid);

				Element z304 = root.element("z304");
				if (null != z304) {
					String telephone = z304.elementText("z304-telephone"); // 电话
					model.addAttribute("telephone", telephone);
				}

				Element z305 = root.element("z305");
				if (null != z305) {
					String cardtype = z305.elementText("z305-bor-status"); // 读者类型
					String sum = z305.elementText("z305-sum"); // 余额
					model.addAttribute("cardtype", cardtype);
					model.addAttribute("sum", sum);
				}

				Client client = new Client();
				client.setOpenid(openid);
				client.setCardno(cardno);
				client.setPassword(password);
				client.setBorid(borid);
				client.setCreatetime(new Date());
				clientService.insert(client);

				return "client/bindsuccess";
			} else {
				return "client/bindfail";
			}
		} catch (DocumentException e) {
			logger.error("【绑定读者卡失败】" + e);
			return "client/bindfail";
		}
	}
}
