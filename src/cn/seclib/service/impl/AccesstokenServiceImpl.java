package cn.seclib.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.seclib.mapper.AccesstokenMapper;
import cn.seclib.pojo.Accesstoken;
import cn.seclib.pojo.AccesstokenExample;
import cn.seclib.service.AccesstokenService;

@Service
public class AccesstokenServiceImpl implements AccesstokenService{

	@Autowired
	private AccesstokenMapper accesstokenMapper;

	@Override
	public Accesstoken getToken() {
		AccesstokenExample example = new AccesstokenExample();
		example.setOrderByClause("id asc");
		List<Accesstoken> list = accesstokenMapper.selectByExample(example);
		if(null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public void insertToken(Accesstoken accesstoken) {
		accesstokenMapper.insert(accesstoken);
	}

	@Override
	public void updateToken(Accesstoken accesstoken) {
		accesstokenMapper.updateByPrimaryKey(accesstoken);
	}
}
