package com.yuminsoft.cps.pic._admin.syslog;

import java.sql.Timestamp;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.yuminsoft.cps.pic.common.kit.UserKit;
import com.yuminsoft.cps.pic.common.model.Syslog;
import com.yuminsoft.cps.pic.common.thread.SyslogQueue;

public class SyslogService {
	public static SyslogService me = new SyslogService();
	final static Syslog dao = Syslog.me;
	static Log log = Log.getLog(SyslogService.class);

	/**
	 * 添加日志. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月27日 下午5:43:13
	 * @param type
	 * @param content
	 */
	public void saveSyslog(String type, String content) {
		Syslog g = new Syslog();
		g.set("ID", Syslog.seq);
		g.setToken(UserKit.get().getToken());
		g.set("OPERATOR", UserKit.get().getOpreator());
		g.set("JOB_NUMBER", UserKit.get().getJobNumber());
		g.set("OPERATION_TIME", new Timestamp(System.currentTimeMillis()));
		g.setOperationType(type);
		g.setOperationContent(content);
		if (StrKit.isBlank(g.getToken())) {
			log.error("写操作日志,获取Token为空");
		}
		SyslogQueue.add(g);// 启用线程保存日志，避免日志堵塞业务数据返回
	}

	public Page<Syslog> page(Map<String, Object> log, int pageNumber, int pageSize) {
		SqlPara sql = dao.getSqlPara("syslog.page", log);
		return dao.paginate(pageNumber, pageSize, sql);
	}
}
