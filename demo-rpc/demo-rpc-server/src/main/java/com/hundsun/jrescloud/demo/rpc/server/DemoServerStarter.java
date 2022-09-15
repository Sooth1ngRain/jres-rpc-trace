package com.hundsun.jrescloud.demo.rpc.server;

import com.hundsun.jrescloud.common.boot.CloudApplication;
import com.hundsun.jrescloud.common.boot.CloudBootstrap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CloudApplication
public class DemoServerStarter {
	public static void main(String[] args) {

		CloudBootstrap.run(DemoServerStarter.class, args);
	}
}
