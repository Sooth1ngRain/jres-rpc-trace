package com.hundsun.jrescloud.demo.rpc.server;

import com.hundsun.jrescloud.common.boot.CloudApplication;
import com.hundsun.jrescloud.common.boot.CloudBootstrap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CloudApplication
public class DemoServerStarter {
	public static void main(String[] args) {
		Matcher m = Pattern.compile("\\$\\{([^}]+)}").matcher("adsjfklajdkf ${xx} ${kljadf}");
		while(m.find()) {
			System.out.println(m.group(1));
//			m.replaceAll()
		}
//		CloudBootstrap.run(DemoServerStarter.class, args);
	}
}
