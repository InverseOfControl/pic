/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日下午3:08:58
 *
*/

package com.yuminsoft.cps.pic.common.interceptor;

import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.render.FileRender;
import com.jfinal.render.JsonRender;
import com.jfinal.render.NullRender;
import com.jfinal.render.Render;
import com.jfinal.render.TemplateRender;
import com.jfinal.render.TextRender;

/**
 * 日志记录及异常拦截 <br/>
 * Date: 2017年3月17日 下午3:08:58 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class GlobalInterceptor implements Interceptor {
	private static final Log log = Log.getLog(GlobalInterceptor.class);
	private static int maxOutputLengthOfParaValue = 512;

	@Override
	public void intercept(Invocation inv) {
		long start = System.currentTimeMillis();
		String token = UUID.randomUUID().toString().replace("-", "");
		MDC.put("token", token);
		try {
			/**
			 * 此方法之前不能使用任何获取参数的方法，比如 inv.getController().getPara("a")
			 */
			inv.invoke();
			if (!PropKit.getBoolean("devMode", false)) {
				log.info(report(inv.getController(), inv.getMethodName(), start));
			}
		} catch (Exception e) {
			if (!PropKit.getBoolean("devMode", false)) {
				log.error(report(inv.getController(), inv.getMethodName(), start), e);
			} else {
				log.error("系统异常", e);
			}
			if (isAjax(inv.getController().getRequest()) || isUploadFile(inv.getController().getRequest())) {
				inv.getController().renderJson(Ret.fail("errorcode", "111111").set("errormsg", "失败"));
			} else {
				inv.getController().renderText(e.getMessage());
			}
		}
		MDC.remove("token");
	}

	/**
	 * 判断ajax请求
	 * 
	 * @param request
	 * @return
	 */
	boolean isAjax(HttpServletRequest request) {
		return (request.getHeader("X-Requested-With") != null
				&& "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")));
	}

	boolean isUploadFile(HttpServletRequest request) {
		return (request.getHeader("Content-Type") != null
				&& request.getHeader("Content-Type").toLowerCase().startsWith("multipart/form-data"));
	}

	private String report(Controller controller, String methodName, long start) {
		StringBuilder sb = new StringBuilder("\n");
		sb.append("Url        : ").append(controller.getRequest().getMethod()).append(" ")
				.append(controller.getRequest().getRequestURI()).append("\n");
		Class<? extends Controller> cc = controller.getClass();
		sb.append("Controller : ").append(cc.getName()).append(".(").append(cc.getSimpleName()).append(".java:1)");
		sb.append("\nMethod     : ").append(methodName).append("\n");

		String urlParas = controller.getPara();
		if (urlParas != null) {
			sb.append("UrlPara    : ").append(urlParas).append("\n");
		}

		// print all parameters
		HttpServletRequest request = controller.getRequest();
		Enumeration<String> e = request.getParameterNames();
		if (e.hasMoreElements()) {
			sb.append("Parameter  : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = request.getParameterValues(name);
				if (values.length == 1) {
					sb.append(name).append("=");
					if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
						sb.append(values[0].substring(0, maxOutputLengthOfParaValue)).append("...");
					} else {
						sb.append(values[0]);
					}
				} else {
					sb.append(name).append("[]={");
					for (int i = 0; i < values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}
					sb.append("}");
				}
				sb.append("  ");
			}
			sb.append("\n");
		}
		// 耗时统计
		DateTime startDate = new DateTime(start);
		DateTime endDate = DateTime.now();
		sb.append("Resp Time  : ");
		sb.append(Seconds.secondsBetween(startDate, endDate).getSeconds() % 60);
		sb.append("\n");
		// 服务器状态
		/*
		 * MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
		 * sb.append("Memory     : "); sb.append("Heap Memory Usage=");
		 * sb.append(memorymbean.getHeapMemoryUsage());
		 * sb.append("\tNon-Heap Memory Usage=");
		 * sb.append(memorymbean.getNonHeapMemoryUsage()); sb.append("\n");
		 */
		// 出参
		Render render = controller.getRender();
		if (null != render) {
			sb.append("View       : ");
			sb.append(render.getClass().getSimpleName());
			sb.append("\t").append(null != render.getView() ? render.getView() : "");
			try {
				sb.append("\nReturn     : ");
				if (render instanceof JsonRender) {
					JsonRender r = (JsonRender) render;
					sb.append(r.getJsonText());
				} else if (render instanceof TemplateRender) {
					TemplateRender r = (TemplateRender) render;
					sb.append(r.getContentType());
				} else if (render instanceof TextRender) {
					TextRender r = (TextRender) render;
					sb.append(r.getText());
				} else if (render instanceof FileRender) {
					sb.append("download file");
				} else if (render instanceof NullRender) {
					sb.append("Null");
				} else {
					sb.append(render.getClass().getSimpleName());
				}
			} catch (Exception e2) {
				sb.append(e2.getMessage());
				log.error("打印出参异常", e2);
			}
		}
		return sb.toString();
	}
}
