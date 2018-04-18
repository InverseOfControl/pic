package com.yuminsoft.cps.pic.api;

import java.util.HashMap;
import java.util.Map;

public class PaperstypeControllerTest {
	//业务环节
	static String nodeKey="apply";
	//业务编号
	static String appNo="20170322";
	//操作人姓名
	static String operator="ren";
	//工号
	static String jobNumber="YM10158";
	/**
	 * 获取文件类型集合（文件夹、目录）接口测试
	 * @Title: paperstypeListTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午10:54:47
	 */
	public static void main(String[] args) {
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/paperstype/list";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("nodeKey", nodeKey);
		queryParas.put("appNo", appNo);
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
}
