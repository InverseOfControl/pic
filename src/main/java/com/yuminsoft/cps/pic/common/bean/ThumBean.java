package com.yuminsoft.cps.pic.common.bean;

public class ThumBean {
	private String srcPath;
	private String destPath;
	private String uuid;
	private int width;
	private int height;
	private Double quality;

	public ThumBean(String srcPath, String destPath, String uuid, Double quality) {
		this.srcPath = srcPath;
		this.destPath = destPath;
		this.uuid = uuid;
		this.quality = quality;
	}

	public ThumBean(String srcPath, String destPath, String uuid, int width, int height, Double quality) {
		this.srcPath = srcPath;
		this.destPath = destPath;
		this.uuid = uuid;
		this.width = width;
		this.height = height;
		this.quality = quality;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Double getQuality() {
		return quality;
	}

	public void setQuality(Double quality) {
		this.quality = quality;
	}
}
