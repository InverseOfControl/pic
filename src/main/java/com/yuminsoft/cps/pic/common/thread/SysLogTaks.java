/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月27日下午4:20:14
 *
*/

package com.yuminsoft.cps.pic.common.thread;

import org.apache.log4j.MDC;

import com.jfinal.log.Log;
import com.yuminsoft.cps.pic.common.model.Syslog;

/**
 * 日志队列 <br/>
 * Date: 2017年3月27日 下午4:20:14 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class SysLogTaks implements Runnable {

	private static Log log = Log.getLog(SysLogTaks.class);

	@Override
	public void run() {
		while (!SyslogQueue.isEmpty()) {
			Syslog syslog = SyslogQueue.get();
			MDC.put("token", syslog.getToken());
			if (null != syslog) {
				syslog.save();
				log.debug("操作日志保存成功!");
			}
			MDC.remove("token");
		}
	}
}
