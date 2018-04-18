/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:56:46
 *
*/

package com.yuminsoft.cps.pic.api.picture;

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
public class PictureValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		String subclass_sort = c.getPara("subclass_sort");
		if (StrKit.isBlank(subclass_sort)) {
			addError("证件类型（subclass_sort）必填");
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(Ret.fail("errorcode", "111111").set("errormsg", getErrorMessage().toString()));
	}
}
