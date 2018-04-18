/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年4月5日上午9:22:02
 *
*/

package com.yuminsoft.cps.pic.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传示例 （JDK1.7以上 ），原生 HttpURLConnection方案，不依赖任何框架<br/>
 * Date: 2017年4月5日 上午9:22:02 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FileDataControllerTest {

	/**
	 * 文件上传. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年4月5日 上午9:23:09
	 */
	public static void uploadTest() {
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/filedata/upload";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("nodeKey", "apply");//参数nodeKey
		queryParas.put("sysName", "aps");//参数sysName
		queryParas.put("appNo", "2017032202");//参数appNo
		queryParas.put("operator", "ren");//参数operator
		queryParas.put("jobNumber", "YM10158");//参数jobNumber
		File file = new File("D:\\公积金资料1.jpg");//参数file
		String result = test.post(url, queryParas, file);
		System.out.println(result);
	}
	/**
	 * 文件上传至指定目录
	 * @Title: uploadfileTest 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年4月5日 上午11:58:03
	 */
	public static void uploadfileTest(){
		HttpKit test = new HttpKit();
		String url = "http://172.16.230.50:8080/pic-app/api/filedata/uploadfile";
		Map<String, String> queryParas = new HashMap<>();
		queryParas.put("nodeKey", "apply");//参数nodeKey
		queryParas.put("sysName", "aps");//参数sysName
		queryParas.put("appNo", "2017032202");//参数appNo
		queryParas.put("code", "E");//目录编号
		queryParas.put("operator", "ren");//参数operator
		queryParas.put("jobNumber", "YM10158");//参数jobNumber
		File file = new File("D:\\公积金资料1.jpg");//参数file
		String result = test.post(url, queryParas, file);
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		//文件上传
		uploadTest();
		//文件指定目录上传
		//uploadfileTest();
		
	}
}
