package com.yuminsoft.cps.pic.common.kit;

import java.io.Serializable;

public class ImageBean implements Serializable {

	private static final long serialVersionUID = 6309851669478261191L;

	private Integer width;
	private Integer height;
	private String directory;
	private String filename;
	/**
	 * K:ki,M:Mi
	 */
	private String filelength;

	public ImageBean() {
	}

	public ImageBean(Integer width, Integer height, String directory, String filename, String filelength) {
		super();
		this.width = width;
		this.height = height;
		this.directory = directory;
		this.filename = filename;
		this.filelength = filelength;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilelength() {
		return filelength;
	}

	public void setFilelength(String filelength) {
		this.filelength = filelength;
	}
}
