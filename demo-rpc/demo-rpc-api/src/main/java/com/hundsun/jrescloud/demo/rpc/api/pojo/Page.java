package com.hundsun.jrescloud.demo.rpc.api.pojo;

import java.util.List;

public class Page<T> {
	private List<T> list;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
}
