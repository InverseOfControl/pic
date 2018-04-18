package com.yuminsoft.cps.pic.api;

import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.yuminsoft.cps.pic.api.common.ApiController;

/**
 * 健康检查页面
 * 
 * @author YM10138
 *
 */
@Clear
public class HeartbeatController extends ApiController {

	public void index() {
		Record r = Db.findFirst("select 1 FROM DUAL");
		if (null != r)
			renderText("Ok");
		else {
			// 408 Request Timeout
			// 请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送。客户端可以随时再次提交这一请求而无需进行任何更改。
			getResponse().setStatus(408);
			renderText("Request Timeout");
		}
	}
}
