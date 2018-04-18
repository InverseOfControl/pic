package com.yuminsoft.cps.pic._admin.syslog;

import java.util.HashMap;
import java.util.Map;

import com.yuminsoft.cps.pic.common.controller.BaseController;

public class SyslogController extends BaseController {

	private SyslogService service = SyslogService.me;

	public void list() {
		keepPara();
		Map<String, Object> log = new HashMap<String, Object>();
		log.put("jobNumber", getPara("jobNumber"));
		log.put("operator", getPara("operator"));
		log.put("operationType", getPara("operationType"));
		log.put("operationStartTime", getPara("operationStartTime"));
		log.put("operationEndTime", getPara("operationEndTime"));
		setAttr("page", service.page(log, getParaToInt("pageNumber", 1), getParaToInt("pageSize", 15)));
	}
}
