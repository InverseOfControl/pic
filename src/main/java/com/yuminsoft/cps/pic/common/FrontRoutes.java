/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日上午11:44:34
 *
*/

package com.yuminsoft.cps.pic.common;

import com.jfinal.config.Routes;
import com.yuminsoft.cps.pic.index.IndexController;

/**
 * 前端 <br/>
 * Date: 2017年3月14日 上午11:44:34 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class FrontRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/_view");

		add("/", IndexController.class, "/index");
	}
}
