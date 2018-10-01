package cn.seclib.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.seclib.pojo.Client;
import cn.seclib.service.ClientService;

@Controller
@RequestMapping("/test")
public class TrialController {
	
	private static final Logger logger = Logger.getLogger(cn.seclib.test.TrialController.class);

	@Autowired
	private ClientService clientService;
	
	@RequestMapping("/getallclient")
	@ResponseBody
	public List<Client> show() {
		List<Client> list = clientService.getAll();
		return list;
	}
	
	@RequestMapping("/bindsuccess")
	public String test(Model model) {
		model.addAttribute("cardno", "123321789");
		model.addAttribute("name", "小明");
		model.addAttribute("cardtype", "VIPC级");
		model.addAttribute("sum", "0.0");
		
		return "client/bindsuccess";
	}
}
