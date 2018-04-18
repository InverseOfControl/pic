/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午10:02:19
 *
*/

package com.yuminsoft.cps.pic.common.validator;

import com.jfinal.validate.Validator;

/**
 * 扩展 Validator <br/>
 * Date: 2017年3月17日 上午10:02:19 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public abstract class BaseValidator extends Validator {
	
	protected StringBuffer errorMessage = new StringBuffer();

	protected void addError(String errorMessage) {
		invalid = true;
		this.errorMessage.append(errorMessage);
	}

	protected StringBuffer getErrorMessage() {
		return errorMessage;
	}
}
