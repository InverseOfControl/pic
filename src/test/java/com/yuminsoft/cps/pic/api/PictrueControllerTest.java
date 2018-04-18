package com.yuminsoft.cps.pic.api;

import java.util.HashMap;
import java.util.Map;

public class PictrueControllerTest {
	//业务环节
	static String nodeKey="apply";
	//业务编号
	static String appNo="20170322";
	//操作人姓名
	static String operator="ren";
	//工号
	static String jobNumber="YM10158";
	/**
	 * 获取文件集合接口测试
	 * @Title: listTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:23:07
	 */
	public static void listTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/picture/list";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("subclass_sort", "E");//文件类型编号
		queryParas.put("appNo", appNo);
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
	/**
	 * 文件重命名接口测试
	 * @Title: renameTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:27:45
	 */
	public static void renameTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/picture/rename";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("value", "车产信息111");//文件名称
		queryParas.put("pk", "35059");//文件id
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
	/**
	 * 文件删除接口测试
	 * @Title: deleteTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:27:45
	 */
	public static void deleteTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/picture/delete";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("ids", "35059,35058");//文件id集合
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
	/**
	 * 文件移动接口测试
	 * @Title: moveTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:27:45
	 */
	public static void moveTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/picture/move";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("ids", "1242,1243");//文件id集合
		queryParas.put("paperstypeId", "60");//文件类型id
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
	/**
	 * 文件作废接口测试
	 * @Title: wasteTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:27:45
	 */
	public static void wasteTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/picture/waste";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("ids", "1242,1243");//文件id集合
		queryParas.put("ifWaste", "Y");//是否作废(Y或N)
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
	/**
	 * 文件补件接口测试
	 * @Title: patchBoltTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:27:45
	 */
	public static void patchBoltTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/picture/patchBolt";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("ids", "1242,1243");//文件id集合
		queryParas.put("ifPatchBolt", "Y");//是否补件(Y或N)
		queryParas.put("operator", operator);
		queryParas.put("jobNumber", jobNumber);
		String result = test.post(url, queryParas);
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		//获取文件集合接口测试
		//listTest();
		//重命名测试
		//renameTest();
		//删除测试
		//deleteTest();
		//文件移动
		//moveTest();
		//文件作废
		//wasteTest();
		//文件补件
		//patchBoltTest();
	}
}
