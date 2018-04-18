package com.yuminsoft.cps.pic.api.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;

public class ClearCacheFileInterceptor implements Interceptor {
	private static Log log = Log.getLog(ClearCacheFileInterceptor.class);

	@Override
	public void intercept(Invocation inv) {
		try {
			inv.invoke();
		} catch (Exception e) {
			inv.getController().renderJson(Ret.fail("errorcode", "111111").set("errormsg", "系统异常"));
			log.error("上传文件异常", e);
		}
		// 清理缓存文件
		try {
			UploadFile file = inv.getController().getFile();
			if (null != file && file.getFile().isFile()) {
				file.getFile().delete();
			}
		} catch (Exception e) {
			// log.error("清理文件缓存异常", e);
		}
	}

}
