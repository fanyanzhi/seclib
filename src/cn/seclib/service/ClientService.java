package cn.seclib.service;

import java.util.List;
import cn.seclib.pojo.Client;

public interface ClientService {

	public List<Client> getAll();
	
	public void insert(Client client);
	
	public Client selectByOpenid(String openid);
	
	public void deleteByOpenidAndCardno(String openid, String cardno);
}
