/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月16日上午10:19:37
 *
*/

package com.yuminsoft.cps.pic.common.engine;

import java.io.Writer;
import java.util.UUID;

import com.jfinal.kit.PropKit;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.stat.Scope;

/**
 * 版本,如果开发模式返回随机数(避免JS与CSS缓存) <br/>
 * Date: 2017年3月16日 上午10:19:37 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class VersionDirective extends Directive {

	@Override
	public void exec(Env env, Scope scope, Writer writer) {
		String version;
		if (PropKit.getBoolean("devMode", false)) {
			version = UUID.randomUUID().toString();
		} else {
			version = PropKit.get("version", "99.0");
		}
		write(writer, version);
	}
}
