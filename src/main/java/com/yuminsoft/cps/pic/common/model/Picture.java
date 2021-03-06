package com.yuminsoft.cps.pic.common.model;

import java.math.BigDecimal;

import com.yuminsoft.cps.pic.common.model.base.BasePicture;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Picture extends BasePicture<Picture> {
	/**
	 * 序列名称
	 */
	public static final String seq = "PICTURE_SEQ.nextval";
	private String url;
	private Integer MaxSortsid;
	private String thumUrl;
	private BigDecimal oldId;
	private String name;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getMaxSortsid() {
		return MaxSortsid;
	}

	public void setMaxSortsid(Integer maxSortsid) {
		MaxSortsid = maxSortsid;
	}

	public String getThumUrl() {
		return thumUrl;
	}

	public void setThumUrl(String thumUrl) {
		this.thumUrl = thumUrl;
	}

	public BigDecimal getOldId() {
		return oldId;
	}

	public void setOldId(BigDecimal oldId) {
		this.oldId = oldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}