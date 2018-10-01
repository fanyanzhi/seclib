package cn.seclib.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.seclib.mapper.ClientMapper;
import cn.seclib.pojo.Client;
import cn.seclib.pojo.ClientExample;
import cn.seclib.pojo.ClientExample.Criteria;
import cn.seclib.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService{
	
	@Autowired
	private ClientMapper clientMapper;

	@Override
	public List<Client> getAll() {
		ClientExample example = new ClientExample();
		example.setOrderByClause("createtime desc");
		List<Client> list = clientMapper.selectByExample(example);
		return list;
	}

	@Override
	public void insert(Client client) {
		clientMapper.insert(client);
	}

	@Override
	public Client selectByOpenid(String openid) {
		ClientExample example = new ClientExample();
		Criteria criteria = example.createCriteria();
		criteria.andOpenidEqualTo(openid);
		List<Client> list = clientMapper.selectByExample(example);
		if(null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public void deleteByOpenidAndCardno(String openid, String cardno) {
		ClientExample example = new ClientExample();
		Criteria criteria = example.createCriteria();
		criteria.andOpenidEqualTo(openid);
		criteria.andCardnoEqualTo(cardno);
		clientMapper.deleteByExample(example);
	}

}
