/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:03:36
 *
*/

package com.yuminsoft.cps.pic._admin.privilege;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.yuminsoft.cps.pic._admin.paperstype.PaperstypeService;
import com.yuminsoft.cps.pic._admin.role.RoleService;
import com.yuminsoft.cps.pic.common.model.Paperstype;
import com.yuminsoft.cps.pic.common.model.Privilege;
import com.yuminsoft.cps.pic.common.model.Role;


/**
 * ClassName:FiletagService <br/>
 * Date: 2017年3月14日 下午4:03:36 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class PrivilegeService {

	public static final PrivilegeService me = new PrivilegeService();
	final static Privilege dao = Privilege.me;
	final static RoleService rservice = RoleService.me;
	final static PaperstypeService pservice = PaperstypeService.me;
	
	/**
	 * 获取业务环节列表. <br/>
	 *
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午5:31:55
	 * @return
	 */
	public List<Role> flist() {
		return rservice.find();
	}
	/**
	 * 获取其他列表. <br/>
	 *
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月14日 下午5:31:55
	 * @return
	 */
	public List<Privilege> alllist() {
		return dao.find("select t.*,ps.sys_name,ps.item_value,ps.item_code from PRIVILEGE t left join PAPERSTYPE ps on t.paperstype_id = ps.id order by ps.sortnum");
	}
	/**
	 * 下拉框查询
	 * @Title: sysNameList 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月15日 下午4:30:03 
	 * @return
	 */
	public List<Paperstype> itemValueList(){
		return pservice.find();
	}
	/**
	 * 通过环节编号，名称，查询出重复条数
	 * @Title: countByNode 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月17日 上午9:34:16 
	 * @param nodeKey
	 * @param nodeKey2
	 * @param id
	 * @return
	 */
	public BigDecimal countByNode(Privilege pl, BigDecimal id,String roleId) {
		if (null==pl.getId()) {
			return Db.queryBigDecimal("select count(*) from PRIVILEGE where  role_id = "+roleId+" and paperstype_id = "+id);
		}else{
			return Db.queryBigDecimal("select count(*) from PRIVILEGE where  role_id = "+roleId+" and id != "+pl.getId()+" and paperstype_id = "+id);
		}
		
	}
	public int deleteByPaperstypeId(Integer paperstypeId){
		return Db.update("delete privilege where paperstype_id = ? ", paperstypeId);
	}
	public List<Privilege> findByRoleId(Integer roleId){
		return dao.find("select * from PRIVILEGE where  role_id = ?",roleId);
	}
	public int deleteByRoleId(Integer roleId) {
		return Db.update("delete privilege where role_id = ? ", roleId);
	}
}
