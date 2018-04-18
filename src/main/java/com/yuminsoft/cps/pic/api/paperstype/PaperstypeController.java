/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:46:47
 *
*/

package com.yuminsoft.cps.pic.api.paperstype;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.yuminsoft.cps.pic._admin.syslog.SyslogService;
import com.yuminsoft.cps.pic.api.common.ApiController;
import com.yuminsoft.cps.pic.api.common.ApiPlatformValidator;
import com.yuminsoft.cps.pic.api.common.ApiValidator;

/**
 * 文件类型接口 <br/>
 * Date: 2017年3月17日 上午9:46:47 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
@Before(ApiPlatformValidator.class)
public class PaperstypeController extends ApiController {

	static PaperstypeService service = PaperstypeService.me;
	static SyslogService logService = SyslogService.me;

	/**
	 * 根据业务环节获取 文件类型 . <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月17日 上午9:47:56
	 */
	@Before(ApiValidator.class)
	public void list() {
		List<Record> list = service.list(this.getNodeKey(), this.getAppNo());
		logService.saveSyslog("获取文件列表", "获取文件类型集合(文件夹、目录)");
		renderJson(Ret.ok("errorcode", "000000").set("result", list));
	}
}
