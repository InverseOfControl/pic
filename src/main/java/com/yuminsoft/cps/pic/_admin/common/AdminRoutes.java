/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日上午11:44:34
 *
*/

package com.yuminsoft.cps.pic._admin.common;

import com.jfinal.config.Routes;
import com.yuminsoft.cps.pic._admin.filetag.FiletagController;
import com.yuminsoft.cps.pic._admin.paperstype.PaperstypeController;
import com.yuminsoft.cps.pic._admin.privilege.PrivilegeController;
import com.yuminsoft.cps.pic._admin.role.RoleController;
import com.yuminsoft.cps.pic._admin.syslog.SyslogController;

/**
 * 后台管理路由<br/>
 * Date: 2017年3月14日 上午11:44:34 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class AdminRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/_admin");

		add("/admin/filetag", FiletagController.class, "/filetag"); // 标签
		add("/admin/paperstype", PaperstypeController.class, "/paperstype"); // 图片类型
		add("/admin/privilege", PrivilegeController.class, "/privilege"); // 权限
		add("/admin/role", RoleController.class, "/role"); // 权限
		add("/admin/syslog", SyslogController.class, "/syslog"); // 日志
	}
}
