package com.yuminsoft.cps.pic.common.kit;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

/**
 * 对应级别的日志只存在于对应的日志文件
 * @author YM10138
 *
 */
public class Log4jKit extends DailyRollingFileAppender {

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		// 只判断是否相等，而不判断优先级
		return this.getThreshold().equals(priority);
	}
}
