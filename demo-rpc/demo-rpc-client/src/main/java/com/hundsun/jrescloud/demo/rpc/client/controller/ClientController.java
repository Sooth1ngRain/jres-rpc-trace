package com.hundsun.jrescloud.demo.rpc.client.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.rpc.RpcContext;
import com.hundsun.jrescloud.common.t2.dataset.CommonDatasets;
import com.hundsun.jrescloud.common.t2.dataset.DatasetColumnType;
import com.hundsun.jrescloud.common.t2.dataset.DatasetService;
import com.hundsun.jrescloud.common.t2.dataset.IDataset;
import com.hundsun.jrescloud.common.t2.dataset.IDatasets;
import com.hundsun.jrescloud.demo.rpc.api.pojo.UserAccount;
import com.hundsun.jrescloud.demo.rpc.api.service.UserService;
import com.hundsun.jrescloud.rpc.annotation.CloudReference;
import com.hundsun.jrescloud.rpc.constant.RpcConstants;

@RestController
public class ClientController {
	@CloudReference
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		UserAccount userAccount = new UserAccount();
		userAccount.setId("id");
		userAccount.setName("name");
		return "调用生产者：" + userService.login(userAccount);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list() {
		UserAccount userAccount = new UserAccount();
		userAccount.setId("id");
		userAccount.setName("name");
		return "调用生产者：" + userService.list(userAccount);
	}

	@RequestMapping(value = "/teste", method = RequestMethod.GET)
	public String teste() {
		RpcContext.getContext().set(RpcConstants.BODYINFO_KEY_CONTENT, "test");
		RpcContext.getContext().set(RpcConstants.BODYINFO_KEY_LOCALE, "zh_TW");
		return "调用生产者：" + userService.teste();
	}

	@RequestMapping(value = "/testd", method = RequestMethod.GET)
	public String testd() {
		IDataset subDataset = DatasetService.getDefaultInstance().getDataset();
		subDataset.addColumn("subid", DatasetColumnType.DS_INT);
		subDataset.addColumn("subname", DatasetColumnType.DS_STRING);
		subDataset.appendRow();
		subDataset.updateValue("subid", 1);
		subDataset.updateValue("subname", "name");
		
		IDatasets datasets = new CommonDatasets();
		IDataset dataset = DatasetService.getDefaultInstance().getDataset();
		dataset.addColumn("id", DatasetColumnType.DS_INT);
		dataset.addColumn("name", DatasetColumnType.DS_STRING);
		dataset.addColumn("sub", DatasetColumnType.DS_SUB_DATASET);
		dataset.appendRow();
		dataset.updateValue("id", 1);
		dataset.updateValue("name", "name");
		dataset.updateSubDataset("sub", subDataset);
		datasets.putDataset(dataset);

		return "调用生产者：" + userService.testd(datasets);
	}
}
