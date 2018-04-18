/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月27日下午4:06:00
 *
*/

package com.yuminsoft.cps.pic.common.kit;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Web 工具类 <br/>
 * Date: 2017年3月27日 下午4:06:00 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class WebKit {

	/**
	 * 
	 * 获取客户端IP地址
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 
	 * 获取完整请求路径(含内容路径及请求参数)
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 */
	public static String getRequestURIWithParam(HttpServletRequest request) {
		return request.getRequestURI() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
	}

	/**
	 * 
	 * 获取ParameterMap
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 */
	public static Map<String, String> getParamMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> enume = request.getParameterNames();
		while (enume.hasMoreElements()) {
			String name = (String) enume.nextElement();
			map.put(name, request.getParameter(name));
		}
		return map;
	}

}
