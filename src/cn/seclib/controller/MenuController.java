package cn.seclib.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import cn.seclib.vo.FirstButton;
import cn.seclib.vo.OuterButton;
import cn.seclib.vo.SubButton;
import cn.seclib.vo.WxToken;

@Controller
@RequestMapping("/menu")
public class MenuController {
	
	private static final Logger logger = Logger.getLogger(cn.seclib.controller.MenuController.class);
	
	@RequestMapping("/create")
	@ResponseBody
	public void show(Model model) {
		SubButton s1 = new SubButton();
		s1.setName("当前借阅");
		s1.setType("view");
		s1.setUrl("http://www.bplisn.net.cn/user/auth/login.html");
		SubButton s2 = new SubButton();
		s2.setName("馆藏书目查询");
		s2.setType("view");
		s2.setUrl("http://primo.clcn.net.cn:1701/primo_library/libweb/action/search.do?vid=CW");
		SubButton s3 = new SubButton();
		s3.setName("自助修改密码");
		s3.setType("view");
		s3.setUrl("http://www.bplisn.net.cn/user/passport/forgot-password.html");
		
		List<SubButton> slist1 = new ArrayList<SubButton>();
		slist1.add(s1);
		slist1.add(s2);
		slist1.add(s3);
		
		FirstButton f1 = new FirstButton();
		f1.setName("Mybook");
		f1.setSub_button(slist1);
		
		
		SubButton ss1 = new SubButton();
		ss1.setName("我的读者卡");
		String url1 = URLEncoder.encode("http://wx2.cwlib.com/seclib/client/clientbindshow");
		ss1.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx4e72191da5c4cc5b&redirect_uri="+url1+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
		ss1.setType("view");
		
		List<SubButton> slist2 = new ArrayList<SubButton>();
		slist2.add(ss1);
		
		FirstButton f2 = new FirstButton();
		f2.setName("个人中心");
		f2.setSub_button(slist2);
		
		List<FirstButton> olist = new ArrayList<FirstButton>();
		olist.add(f1);
		olist.add(f2);
		
		OuterButton ob = new OuterButton();
		ob.setButton(olist);
		
		String res = JSON.toJSONString(ob);
		
		String wxurl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + WxToken.wxToken;
		logger.info("【创建菜单的URL】:" + wxurl);
		String sendPostRes = cn.seclib.util.Common.sendPost(wxurl, res);
		logger.info("【创建菜单的结果】:" + sendPostRes);
	}
	
	
	public static void main(String[] args) {
		SubButton s1 = new SubButton();
		s1.setName("当前借阅");
		s1.setType("view");
		s1.setUrl("http://www.bplisn.net.cn/user/auth/login.html");
		SubButton s2 = new SubButton();
		s2.setName("馆藏书目查询");
		s2.setType("view");
		s2.setUrl("http://primo.clcn.net.cn:1701/primo_library/libweb/action/search.do?vid=CW");
		SubButton s3 = new SubButton();
		s3.setName("自助修改密码");
		s3.setType("view");
		s3.setUrl("http://www.bplisn.net.cn/user/passport/forgot-password.html");
		SubButton s4 = new SubButton();
		s4.setName("首图书目检索");
		s4.setType("view");
		s4.setUrl("http://www.bplisn.net.cn/search.html");
		SubButton s5 = new SubButton();
		s5.setName("我的图书馆");
		s5.setUrl("http://www.bplisn.net.cn/user/auth/login.html");
		s5.setType("view");
		
		List<SubButton> slist1 = new ArrayList<SubButton>();
		slist1.add(s1);
		slist1.add(s2);
		slist1.add(s3);
		slist1.add(s4);
		slist1.add(s5);
		
		FirstButton f1 = new FirstButton();
		f1.setName("Mybook");
		f1.setSub_button(slist1);
		
		
		SubButton ss1 = new SubButton();
		ss1.setName("绑定读者卡");
		ss1.setUrl("http://wx2.cwlib.com/seclib/client/clientbindshow");
		ss1.setType("view");
		
		List<SubButton> slist2 = new ArrayList<SubButton>();
		slist2.add(ss1);
		
		FirstButton f2 = new FirstButton();
		f2.setName("个人中心");
		f2.setSub_button(slist2);
		
		List<FirstButton> olist = new ArrayList<FirstButton>();
		olist.add(f1);
		olist.add(f2);
		
		OuterButton ob = new OuterButton();
		ob.setButton(olist);
		
		String jsonString = JSON.toJSONString(ob);
		System.out.println(jsonString);
	}

}
