package com.yuminsoft.cps.pic.common.bean;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

/**
 * 配置信息
 * 
 * @author YM10138
 *
 */
public class Config {
	public static final Config me = new Config();
	// true开发模式，false生产模式
	private boolean devMode;
	// 版本号
	private String version;
	// 上传目录(与旧系统配置保存一致) ,末尾必须加上 斜杠
	private String uploadDir;
	// 文件缓存目录 ,末尾必须加上 斜杠。
	// upload.dir.cache目录必须是upload.dir目录的子目录
	// 实际缓存目录为：${upload.dir}/${upload.dir.cache}
	private String uploadDirCache;
	// 最大文件大小限制(M)
	private Integer fileMaxPostSize;
	// 访问域名或IP,末尾必须加上 斜杠
	private String picUrl;
	// 文件访问域名
	// nginx映射目录必须与 {upload.dir}目录一致
	private String picFileView;
	// 指定需要图片缩略图后缀
	private String imageThumRegex;
	// GraphicsMagick 安装路径
	private String gmpath;
	// 旧图片服务器FTP配置
	private String ftpHost;
	private int ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	// Log日志输出级别 开发测试环境DEBUG 生产环境 INFO
	private String log4jPic;
	// Log日志输出目录,末尾不加反斜杠
	private String log4jFilePath;
	// 旧数据映射地址(40.31服务器),末尾必须加上 斜杠
	private String nginxUrl;
	// 业务系统名称,在此注明的可以调用PIC接口,在{upload.dir}目录下必须有这些子目录
	private String systemName;

	// 虚拟属性
	private String[] systemNameArray;

	static {
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setVersion(PropKit.get("version", "0.0.0"));
		me.setUploadDir(PropKit.get("upload.dir"));
		me.setUploadDirCache(PropKit.get("upload.dir.cache"));
		me.setFileMaxPostSize(PropKit.getInt("file.MaxPostSize", 50));
		me.setPicUrl(PropKit.get("pic.url"));
		me.setPicFileView(PropKit.get("pic.file.view"));
		me.setImageThumRegex(PropKit.get("image.thum.regex"));
		me.setGmpath(PropKit.get("gmpath"));
		me.setFtpHost(PropKit.get("ftp.host"));
		me.setFtpPort(PropKit.getInt("ftp.port", 21));
		me.setFtpUsername(PropKit.get("ftp.username"));
		me.setFtpPassword(PropKit.get("ftp.password"));
		me.setLog4jPic(PropKit.get("log4j.pic"));
		me.setLog4jFilePath(PropKit.get("log4j.file.path"));
		me.setNginxUrl(PropKit.get("nginx.url"));
		me.setSystemName(PropKit.get("system.name"));

		// 虚拟属性处理
		if (StrKit.notBlank(me.getSystemName())) {
			me.setSystemNameArray(me.getSystemName().split(","));
		}
	}

	public Config() {
	}

	public boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public String getUploadDirCache() {
		return uploadDirCache;
	}

	public void setUploadDirCache(String uploadDirCache) {
		this.uploadDirCache = uploadDirCache;
	}

	public Integer getFileMaxPostSize() {
		return fileMaxPostSize;
	}

	public void setFileMaxPostSize(Integer fileMaxPostSize) {
		this.fileMaxPostSize = fileMaxPostSize;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getPicFileView() {
		return picFileView;
	}

	public void setPicFileView(String picFileView) {
		this.picFileView = picFileView;
	}

	public String getImageThumRegex() {
		return imageThumRegex;
	}

	public void setImageThumRegex(String imageThumRegex) {
		this.imageThumRegex = imageThumRegex;
	}

	public String getGmpath() {
		return gmpath;
	}

	public void setGmpath(String gmpath) {
		this.gmpath = gmpath;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getLog4jPic() {
		return log4jPic;
	}

	public void setLog4jPic(String log4jPic) {
		this.log4jPic = log4jPic;
	}

	public String getLog4jFilePath() {
		return log4jFilePath;
	}

	public void setLog4jFilePath(String log4jFilePath) {
		this.log4jFilePath = log4jFilePath;
	}

	public String getNginxUrl() {
		return nginxUrl;
	}

	public void setNginxUrl(String nginxUrl) {
		this.nginxUrl = nginxUrl;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String[] getSystemNameArray() {
		return systemNameArray;
	}

	public void setSystemNameArray(String[] systemNameArray) {
		this.systemNameArray = systemNameArray;
	}
}