/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年1月22日下午4:51:26
 *
*/

package com.yuminsoft.cps.pic.common.ftp;

/**
 * FTPClient配置类，封装了FTPClient的相关配置 <br/>
 * Date: 2017年1月22日 下午4:51:26 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FTPClientConfigure {

	/**
	 * 域名或IP地址
	 */
	private String host;
	/**
	 * 端口号,默认 21
	 */
	private Integer port;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;

	public FTPClientConfigure() {
	}

	public FTPClientConfigure(String host, Integer port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return this.host + this.port + "/" + this.username;
	}
}
