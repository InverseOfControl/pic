/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日上午10:01:58
 *
*/

package com.yuminsoft.cps.pic.common;

import java.io.File;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.yuminsoft.cps.pic._admin.common.AdminRoutes;
import com.yuminsoft.cps.pic.api.common.ApiRoutes;
import com.yuminsoft.cps.pic.common.bean.Config;
import com.yuminsoft.cps.pic.common.engine.VersionDirective;
import com.yuminsoft.cps.pic.common.interceptor.AuthInterceptor;
import com.yuminsoft.cps.pic.common.interceptor.GlobalInterceptor;
import com.yuminsoft.cps.pic.common.model._MappingKit;
import com.yuminsoft.cps.pic.common.thread.ScheduledExecutor;

/**
 * PIC Config <br/>
 * Date: 2017年3月14日 上午10:01:58 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class PICConfig extends JFinalConfig {

	private static Log log = Log.getLog(PICConfig.class);
	private static Prop p = loadConfig();
	private WallFilter wallFilter;

	/**
	 * 启动入口，运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {

		/**
		 * 特别注意：Eclipse 之下建议的启动方式
		 */
		JFinal.start("src/main/webapp", 80, "/");
	}

	private static Prop loadConfig() {
		// 配置分离
		// -Dglobal.config.path=E:\workspace\pic\src\main\resources\cps_pic.txt(配置文件路径)
		String path = System.getProperty("global.config.path");
		File file = new File(path);
		Prop p = PropKit.use(file);
		return p;
	}

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(p.getBoolean("devMode", false));
		me.setJsonFactory(MixedJsonFactory.me());
		Ret.setToOldWorkMode();
		// 最大文件限制
		me.setMaxPostSize(p.getInt("file.MaxPostSize", 20) * 1024 * 1024);
		// 文件缓存路径
		me.setBaseUploadPath(p.get("upload.dir") + p.get("upload.dir.cache"));
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new FrontRoutes());
		me.add(new ApiRoutes());
		me.add(new AdminRoutes());
	}

	/**
	 * 配置模板引擎，通常情况只需配置共享的模板函数
	 */
	@Override
	public void configEngine(Engine me) {
		me.addDirective("version", new VersionDirective());
		me.addSharedFunction("/_admin/common/__layout.html");
		me.addSharedStaticMethod(StrKit.class);
	}

	public static DruidPlugin getDruidPlugin() {
		String password = p.get("password").trim();
		try {
			password = ConfigTools.decrypt(p.get("publicKey").trim(), password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), password);
		LogKit.info("configPlugin 使用数据库类型是 oracle");
		druidPlugin.setValidationQuery("select 1 FROM DUAL"); // 连接验证语句
		return druidPlugin;
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin druidPlugin = getDruidPlugin();
		wallFilter = new WallFilter(); // 加强数据库安全
		wallFilter.setDbType(JdbcUtils.ORACLE);
		druidPlugin.addFilter(wallFilter);
		druidPlugin.addFilter(new StatFilter()); // 添加 StatFilter 才会有统计数据
		me.add(druidPlugin);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		// SQL模版
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/sql");
		arp.addSqlTemplate("all.sql");
		arp.setDialect(new OracleDialect());
		_MappingKit.mapping(arp);
		if (p.getBoolean("devMode", false)) {
			arp.setShowSql(true);
		}
		me.add(arp);

		// 缓存
		me.add(new EhCachePlugin());
		// 定时任务
		me.add(new Cron4jPlugin(p, "cron4j"));
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// 全局异常拦截器
		me.addGlobalActionInterceptor(new GlobalInterceptor());
		me.addGlobalActionInterceptor(new AuthInterceptor());
		// 事物
		// me.add(new TxByMethodRegex("(.*save.*|.*update.*)"));
		// me.add(new TxByActionKeys("/api/filedata/upload",
		// "/api/filedata/uploadfile"));
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
	}

	@Override
	public void afterJFinalStart() {
		// 让 druid 允许在 sql 中使用 union
		// https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE-wallfilter
		wallFilter.getConfig().setSelectUnionCheck(false);

		// 启动线程
		ScheduledExecutor.start();

		/**
		 * 配置文件有效性、服务器配置验证
		 */
		// 上传目录
		File uploadDir = new File(Config.me.getUploadDir());
		if (!uploadDir.isDirectory()) {
			log.error("cps_pic.properties中upload.dir目录不存在");
		}
		// 文件缓存目录
		File uploadDirCache = new File(Config.me.getUploadDir() + Config.me.getUploadDirCache());
		if (!uploadDirCache.isDirectory()) {
			log.error("cps_pic.properties中upload.dir.cache目录不存在");
		}
		// 验证系统目录
		for (String sysname : Config.me.getSystemNameArray()) {
			File sysfile = new File(Config.me.getUploadDir().concat(sysname));
			if (!sysfile.isDirectory()) {
				log.error("cps_pic.properties中system.name[" + sysfile.getPath() + "]目录不存在");
			}
			if (!sysfile.canRead()) {
				log.error("cps_pic.properties中system.name[" + sysfile.getPath() + "]不可读");
			}
			if (!sysfile.canWrite()) {
				log.error("cps_pic.properties中system.name[" + sysfile.getPath() + "]不可写");
			}
		}
	}

	@Override
	public void beforeJFinalStop() {
		ScheduledExecutor.shutdown();
	}
}
