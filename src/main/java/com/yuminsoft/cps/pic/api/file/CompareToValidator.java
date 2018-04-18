/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:56:46
 *
*/

package com.yuminsoft.cps.pic.api.file;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.yuminsoft.cps.pic.common.validator.BaseValidator;

/**
 * ClassName:PaperstypeValidator <br/>
 * Date: 2017年3月17日 上午9:56:46 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class CompareToValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		FileDataController p = (FileDataController) c;
		String sysName = c.getPara("sysName");
		String nodeKey = c.getPara("nodeKey");
		String appNo = c.getPara("appNo");
		String appNo2 = c.getPara("appNo2");
		if (StrKit.isBlank(sysName)) {
			addError("系统名称（sysName）必填");
		}
		if (StrKit.isBlank(nodeKey)) {
			addError("业务环节（nodeKey）必填");
		}
		if (StrKit.isBlank(appNo)) {
			addError("业务编号（appNo）必填");
		}
		if (StrKit.isBlank(appNo2)) {
			addError("业务编号（appNo2）必填");
		}
		p.setSysName(sysName);
		p.setNodeKey(nodeKey);
		p.setAppNo(appNo);
	}

	@Override
	protected void handleError(Controller c) {
		if (isAjax(c.getRequest())) {
			c.renderJson(Ret.fail("errorcode", "111111").set("errormsg", getErrorMessage().toString()));
		} else {
			c.setAttr("errormsg", getErrorMessage().toString());
			c.render("error.html");
		}
	}

	/**
	 * 判断ajax请求
	 * 
	 * @param request
	 * @return
	 */
	boolean isAjax(HttpServletRequest request) {
		return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")));
	}
}
