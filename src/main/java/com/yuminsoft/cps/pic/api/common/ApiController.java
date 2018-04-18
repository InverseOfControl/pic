/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日下午5:57:54
 *
*/

package com.yuminsoft.cps.pic.api.common;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.yuminsoft.cps.pic.common.controller.BaseController;

/**
 * ClassName:ApiController <br/>
 * Date: 2017年3月17日 下午5:57:54 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class ApiController extends BaseController {

	private String sysName; // 系统名称
	private String nodeKey; // 业务环节
	private String appNo; // 业务编号
	private String operator;// 操作人姓名
	private String jobNumber;// 工号
	private String ifPatchBolt;// 是否补件

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public String getAppNo() {
		return appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getIfPatchBolt() {
		return ifPatchBolt;
	}

	public void setIfPatchBolt(String ifPatchBolt) {
		this.ifPatchBolt = ifPatchBolt;
	}

	public boolean isBlank(String key, String errormsg) {
		if (StrKit.isBlank(getPara(key))) {
			renderJson(Ret.fail("errorcode", "111111").set("errormsg", errormsg));
			return true;
		}
		return false;
	}
}
