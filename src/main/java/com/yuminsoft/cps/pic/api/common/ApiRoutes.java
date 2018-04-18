/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日上午11:44:34
 *
*/

package com.yuminsoft.cps.pic.api.common;

import com.jfinal.config.Routes;
import com.yuminsoft.cps.pic.api.HeartbeatController;
import com.yuminsoft.cps.pic.api.file.FileDataController;
import com.yuminsoft.cps.pic.api.paperstype.PaperstypeController;
import com.yuminsoft.cps.pic.api.picture.PictureController;

/**
 * API <br/>
 * Date: 2017年3月14日 上午11:44:34 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class ApiRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/api");

		addInterceptor(new ApiInterceptor());

		add("/api/filedata", FileDataController.class, "/");
		add("/api/paperstype", PaperstypeController.class, "/");
		add("/api/picture", PictureController.class, "/");
		add("/api/heartbeat", HeartbeatController.class, "/");
	}
}
