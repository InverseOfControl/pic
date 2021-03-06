package com.yuminsoft.cps.pic.common.model.base;

import com.jfinal.plugin.activerecord.Model;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSyslog<M extends BaseSyslog<M>> extends Model<M> implements IBean {

	public void setId(java.math.BigDecimal id) {
		set("ID", id);
	}

	public java.math.BigDecimal getId() {
		return get("ID");
	}

	public void setToken(java.lang.String token) {
		set("TOKEN", token);
	}

	public java.lang.String getToken() {
		return get("TOKEN");
	}

	public void setOperator(java.lang.String operator) {
		set("OPERATOR", operator);
	}

	public java.lang.String getOperator() {
		return get("OPERATOR");
	}

	public void setJobNumber(java.lang.String jobNumber) {
		set("JOB_NUMBER", jobNumber);
	}

	public java.lang.String getJobNumber() {
		return get("JOB_NUMBER");
	}

	public void setOperationTime(Timestamp operationTime) {
		set("OPERATION_TIME", operationTime);
	}

	//返回值 oracle.sql.timestamp 暂时无法处理
	public Object getOperationTime() {
		return get("OPERATION_TIME");
	}

	public void setOperationType(java.lang.String operationType) {
		set("OPERATION_TYPE", operationType);
	}

	public java.lang.String getOperationType() {
		return get("OPERATION_TYPE");
	}

	public void setOperationContent(java.lang.String operationContent) {
		set("OPERATION_CONTENT", operationContent);
	}

	public java.lang.String getOperationContent() {
		return get("OPERATION_CONTENT");
	}

}
