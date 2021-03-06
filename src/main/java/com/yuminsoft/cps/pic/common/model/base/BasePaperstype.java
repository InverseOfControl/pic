package com.yuminsoft.cps.pic.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePaperstype<M extends BasePaperstype<M>> extends Model<M> implements IBean {

	public void setId(java.math.BigDecimal id) {
		set("ID", id);
	}

	public java.math.BigDecimal getId() {
		return get("ID");
	}

	public void setOrg(java.lang.String org) {
		set("ORG", org);
	}

	public java.lang.String getOrg() {
		return get("ORG");
	}

	public void setBranchid(java.lang.String branchid) {
		set("BRANCHID", branchid);
	}

	public java.lang.String getBranchid() {
		return get("BRANCHID");
	}

	public void setItemCode(java.lang.String itemCode) {
		set("ITEM_CODE", itemCode);
	}

	public java.lang.String getItemCode() {
		return get("ITEM_CODE");
	}

	public void setItemValue(java.lang.String itemValue) {
		set("ITEM_VALUE", itemValue);
	}

	public java.lang.String getItemValue() {
		return get("ITEM_VALUE");
	}

	public void setSortnum(java.math.BigDecimal sortnum) {
		set("SORTNUM", sortnum);
	}

	public java.math.BigDecimal getSortnum() {
		return get("SORTNUM");
	}

	public void setSysName(java.lang.String sysName) {
		set("SYS_NAME", sysName);
	}

	public java.lang.String getSysName() {
		return get("SYS_NAME");
	}

	public void setFileNumber(java.math.BigDecimal fileNumber) {
		set("FILE_NUMBER", fileNumber);
	}

	public java.math.BigDecimal getFileNumber() {
		return get("FILE_NUMBER");
	}

	public void setFileSize(java.math.BigDecimal fileSize) {
		set("FILE_SIZE", fileSize);
	}

	public java.math.BigDecimal getFileSize() {
		return get("FILE_SIZE");
	}

	public void setFileType(java.lang.String fileType) {
		set("FILE_TYPE", fileType);
	}

	public java.lang.String getFileType() {
		return get("FILE_TYPE");
	}

}
