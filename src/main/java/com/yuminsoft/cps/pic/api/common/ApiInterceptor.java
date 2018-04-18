/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:56:46
 *
*/

package com.yuminsoft.cps.pic.api.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.yuminsoft.cps.pic.common.aop.ValiType;
import com.yuminsoft.cps.pic.common.aop.Validator;
import com.yuminsoft.cps.pic.common.aop.Validators;

/**
 * ClassName:PaperstypeValidator <br/>
 * Date: 2017年3月17日 上午9:56:46 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class ApiInterceptor implements Interceptor {

	// api接口拦截操作人姓名与工号
	@Override
	public void intercept(Invocation inv) {
		ApiController api = (ApiController) inv.getController();
		if (StrKit.isBlank(api.getPara("operator"))) {
			api.renderJson(Ret.fail("errorcode", "111111").set("errormsg", "操作人姓名(operator)不能为空!"));
			return;
		}
		if (StrKit.isBlank(api.getPara("jobNumber"))) {
			api.renderJson(Ret.fail("errorcode", "111111").set("errormsg", "工号(jobNumber)不能为空!"));
			return;
		}
		// 数据有效性
		if (inv.getMethod().isAnnotationPresent(Validator.class)
				|| inv.getMethod().isAnnotationPresent(Validators.class)) {
			Validator[] validators = inv.getMethod().getAnnotationsByType(Validator.class);
			for (Validator v : validators) {
				if (!validator(api, v)) {
					api.renderJson(Ret.fail("errorcode", "111111").set("errormsg", v.errormsg()));
					return;
				}
			}
		}
		inv.invoke();
	}

	/**
	 * 验证参数
	 * 
	 * @param controller
	 * @param validator
	 * @return
	 */
	private boolean validator(ApiController api, Validator validator) {
		boolean bln = false;
		if (validator.type().equals(ValiType.NotNull)) {
			// 支持类型 String
			if (validator.clazz().isAssignableFrom(String.class)) {
				String str = api.getPara(validator.key());
				if (StrKit.notBlank(str))
					bln = true;
			}
		} else if (validator.type().equals(ValiType.IN)) {
			// 支持类型 String
			if (validator.clazz().isAssignableFrom(String.class)) {
				String str = api.getPara(validator.key());
				if (validator.in().concat(",").indexOf(str.concat(",")) != -1)
					bln = true;
			}
		}
		return bln;
	}
}
