/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月27日上午10:48:00
 *
*/

package com.yuminsoft.cps.pic.common.bean;

/**
 * ClassName:UserBean <br/>
 * Date: 2017年3月27日 上午10:48:00 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class UserBean {
	private String token; // 单点登陆 Session Token
	private String opreator; // 操作人，使用Session Token换操作人姓名
	private String jobNumber;// 工号，使用Session Token换操作人工号

	public UserBean(String token, String opreator, String jobNumber) {
		this.token = token;
		this.opreator = opreator;
		this.jobNumber = jobNumber;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOpreator() {
		return opreator;
	}

	public void setOpreator(String opreator) {
		this.opreator = opreator;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
}
