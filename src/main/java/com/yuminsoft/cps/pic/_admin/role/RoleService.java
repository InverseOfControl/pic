/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:03:36
 *
*/

package com.yuminsoft.cps.pic._admin.role;


import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.yuminsoft.cps.pic.common.model.Role;
/**
 * ClassName:FiletagService <br/>
 * Date: 2017年3月14日 下午4:03:36 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class RoleService {

	public static final RoleService me = new RoleService();
	final static Role dao = Role.me;
	//删除时查询是否id被引用
	public BigDecimal countByRoleId(Integer roleId) {
		return Db.queryBigDecimal("SELECT count(*) FROM privilege  WHERE role_id ="+roleId);
	}
	
	public BigDecimal countByNodeKey(Role r) {
		if (null==r.getId()) {
			return Db.queryBigDecimal("SELECT count(*) FROM role  WHERE node_key ='"+r.getNodeKey()+"'");
		}else{
			return Db.queryBigDecimal("SELECT count(*) FROM role  WHERE node_key ='"+r.getNodeKey()+"' and id != "+r.getId());
		}
		
	}
	public List<Role> find(){
		return dao.find("select * from Role order by id");
	}
}
