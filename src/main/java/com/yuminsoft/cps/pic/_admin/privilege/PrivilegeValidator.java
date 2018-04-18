/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月15日上午10:39:36
 *
*/

package com.yuminsoft.cps.pic._admin.privilege;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

/**
 * ClassName:FiletagValidator <br/>
 * Date: 2017年3月15日 上午10:39:36 <br/>
 * 
 * @author renyz@yuminsoft.com
 */
public class PrivilegeValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequiredString("model.nodeKey", "error", "业务环节不能为空!");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(Ret.fail("msg", c.getAttr("error")));
	}

}
