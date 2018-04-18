/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午3:14:41
 *
*/

package com.yuminsoft.cps.pic.index;

import com.yuminsoft.cps.pic.common.controller.BaseController;

/**
 * 首页控制器 <br/>
 * Date: 2017年3月14日 下午3:14:41 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class IndexController extends BaseController {

	public void index() {
		render("index.html");
	}
}
