package cn.seclib.controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.seclib.service.impl.CoreServiceImpl;
import cn.seclib.util.SHA1cnki;
import cn.seclib.vo.AesException;

@Controller
public class UrlVerifyController {
	
	private static final Logger logger = Logger.getLogger(cn.seclib.controller.UrlVerifyController.class);

	private static final String token = "giraffescholar";

	@RequestMapping("/wx")
	public void verifyUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String method = request.getMethod().toLowerCase();
		if("get".equals(method)) {
			String msgSignature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");
			
			List<String> list = new ArrayList<String>();
			list.add(timestamp);
			list.add(nonce);
			list.add(token);
			Collections.sort(list);
			String result = "";
			for (String str : list) {
				result += str;
			}
			SHA1cnki sha = new SHA1cnki();
			String signature = sha.Digest(result).toLowerCase();

			if (!signature.equals(msgSignature)) {
				return;
			}
			
			response.getWriter().print(echostr);
		}
		if("post".equals(method)){
			request.setCharacterEncoding("UTF-8");
	        response.setCharacterEncoding("UTF-8");
	        // 调用核心业务类接收消息、处理消息
	        String respXml = CoreServiceImpl.processRequest(request);

	        // 响应消息
	        PrintWriter out = response.getWriter();
	        out.print(respXml);
	        out.close();

		}
	}

}
