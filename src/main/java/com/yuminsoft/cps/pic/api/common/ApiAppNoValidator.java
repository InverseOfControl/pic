/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:56:46
 *
*/

package com.yuminsoft.cps.pic.api.common;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.yuminsoft.cps.pic.common.kit.HttpKit;
import com.yuminsoft.cps.pic.common.validator.BaseValidator;

/**
 * ClassName:PaperstypeValidator <br/>
 * Date: 2017年3月17日 上午9:56:46 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class ApiAppNoValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		ApiController p = (ApiController) c;
		String appNo = c.getPara("appNo");
		if (StrKit.isBlank(appNo)) {
			addError("业务编号（appNo）必填");
		}
		p.setAppNo(appNo);
	}

	@Override
	protected void handleError(Controller c) {
		if (HttpKit.isAjax(c.getRequest())) {
			c.renderJson(Ret.fail("errorcode", "111111").set("errormsg", getErrorMessage().toString()));
		} else {
			c.setAttr("errormsg", getErrorMessage().toString());
			c.render("error.html");
		}
	}

}
