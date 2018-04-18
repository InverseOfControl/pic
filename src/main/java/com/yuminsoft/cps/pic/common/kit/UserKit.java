/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月27日上午10:48:53
 *
*/

package com.yuminsoft.cps.pic.common.kit;

import com.yuminsoft.cps.pic.common.bean.UserBean;

/**
 * 任何地方获取当前用户 <br/>
 * Date: 2017年3月27日 上午10:48:53 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class UserKit {

	private static ThreadLocal<UserBean> local = new ThreadLocal<UserBean>();

	public static UserBean get() {
		return local.get();
	}

	public static void put(UserBean user) {
		local.set(user);
	}

	public static void remove() {
		local.remove();
	}
}
