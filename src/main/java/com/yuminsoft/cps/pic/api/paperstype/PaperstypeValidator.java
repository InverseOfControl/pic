/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:56:46
 *
*/

package com.yuminsoft.cps.pic.api.paperstype;

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
public class PaperstypeValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		PaperstypeController p = (PaperstypeController) c;
		String nodeKey = c.getPara("nodeKey");
		if (StrKit.isBlank(nodeKey)) {
			addError("业务环节（nodeKey）必填");
		}
		p.setNodeKey(nodeKey);
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(Ret.fail("errorcode", "111111").set("errormsg", getErrorMessage().toString()));
	}
}
