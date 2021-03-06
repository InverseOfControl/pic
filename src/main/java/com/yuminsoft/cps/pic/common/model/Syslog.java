package com.yuminsoft.cps.pic.common.model;

import com.yuminsoft.cps.pic.common.model.base.BaseSyslog;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Syslog extends BaseSyslog<Syslog> {
	public static Syslog me = new Syslog().dao();
	/**
	 * 序列名称
	 */
	public static final String seq = "SYSLOG_SEQ.nextval";

	private String operationStartTime;
	private String operationEndTime;

	public String getOperationStartTime() {
		return operationStartTime;
	}

	public void setOperationStartTime(String operationStartTime) {
		this.operationStartTime = operationStartTime;
	}

	public String getOperationEndTime() {
		return operationEndTime;
	}

	public void setOperationEndTime(String operationEndTime) {
		this.operationEndTime = operationEndTime;
	}
}
