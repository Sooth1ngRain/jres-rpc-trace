package com.hundsun.jrescloud.demo.rpc.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hundsun.jrescloud.common.exception.BaseBizException;
import com.hundsun.jrescloud.common.t2.dataset.IDatasets;
import com.hundsun.jrescloud.demo.rpc.api.pojo.Page;
import com.hundsun.jrescloud.demo.rpc.api.pojo.UserAccount;
import com.hundsun.jrescloud.demo.rpc.api.service.UserService;
import com.hundsun.jrescloud.rpc.annotation.CloudComponent;
import com.hundsun.jrescloud.rpc.api.IRpcContext;
import com.hundsun.jrescloud.rpc.result.RpcResultDTO;
import org.springframework.beans.factory.annotation.Value;

@CloudComponent
public class UserInfoServiceImpl implements UserService {

	@Autowired
	private IRpcContext rpcContext;

	@Value("${xx.config:adslkfjl}")
	private String xx;

	@Override
	public String login(UserAccount userAccount) {
		return "登录成功！";
	}

	@Override
	public RpcResultDTO<Page<UserAccount>> list(UserAccount userAccount) {
		Page<UserAccount> page = new Page<UserAccount>();
		List<UserAccount> list = new ArrayList<UserAccount>();
		list.add(userAccount);
		page.setList(list);
		RpcResultDTO<Page<UserAccount>> dto = new RpcResultDTO<Page<UserAccount>>(page);
		return dto;
	}
	
	@Override
	public String teste() {
		System.out.println(rpcContext.getProcessID());
		System.out.println(rpcContext.getLocale());
		throw new BaseBizException("9111", "test1", "test2");
	}

	@Override
	public IDatasets testd(IDatasets data) {
		try {
			Thread.sleep(5000);
			System.out.println(Thread.currentThread());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return data;
	}

}
