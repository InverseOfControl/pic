/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月27日上午10:53:47
 *
*/

package com.yuminsoft.cps.pic.common.interceptor;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.MDC;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.yuminsoft.cps.pic.common.bean.UserBean;
import com.yuminsoft.cps.pic.common.kit.StringKit;
import com.yuminsoft.cps.pic.common.kit.UserKit;

/**
 * 用户拦截器 <br/>
 * Date: 2017年3月27日 上午10:53:47 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class AuthInterceptor implements Interceptor {
	private static final Log log = Log.getLog(AuthInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		String operator = inv.getController().getPara("operator");
		String jobNumber = inv.getController().getPara("jobNumber");
		if (StrKit.notBlank(operator) && StringKit.isMessyCode(operator))
			try {
				operator = new String(operator.getBytes("iso8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("操作人姓名转码异常", e);
			}
		UserKit.put(new UserBean(MDC.get("token").toString(), operator, jobNumber));
		inv.invoke();
	}
}
