package com.yuminsoft.cps.pic.common.kit;

import javax.servlet.http.HttpServletRequest;

public class HttpKit {
	/**
	 * 判断ajax请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request) {
		return (request.getHeader("X-Requested-With") != null
				&& "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")));
	}
}
