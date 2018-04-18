/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月15日上午10:39:36
 *
*/

package com.yuminsoft.cps.pic._admin.paperstype;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

/**
 * ClassName:PaperstypeValidator <br/>
 * Date: 2017年3月15日 上午10:39:36 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class PaperstypeValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateRequiredString("model.itemCode", "error", "编号不能为空!");
		validateRequiredString("model.fileNumber", "error", "附件个数不能为空!");
		validateRequiredString("model.fileSize", "error", "附件大小不能为空!");
		validateRequiredString("model.fileType", "error", "附件类型不能为空!");
		validateInteger("model.fileSize","error","附件大小必须为数字!");
		validateInteger("model.sortnum","error","排序必须为数字!");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(Ret.fail("msg", c.getAttr("error")));
	}

}
