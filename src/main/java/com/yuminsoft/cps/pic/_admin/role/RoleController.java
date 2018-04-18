/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午4:00:46
 *
*/

package com.yuminsoft.cps.pic._admin.role;




import java.math.BigDecimal;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.yuminsoft.cps.pic._admin.privilege.PrivilegeService;
import com.yuminsoft.cps.pic.common.controller.BaseController;
import com.yuminsoft.cps.pic.common.model.Privilege;
import com.yuminsoft.cps.pic.common.model.Role;

/**
 * 角色表相关操作接口
 * @ClassName: RoleController 
 * @Description: TODO
 * @author: renyz@yuminsoft.com
 * @date: 2017年3月27日 上午10:13:08
 */
public class RoleController extends BaseController {
	static RoleService service = RoleService.me;
	static PrivilegeService privilegeService = PrivilegeService.me;
	/**
	 * 新增窗口准备
	 * @Title: add 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:15:31
	 */
	public void add() {
	}
	/**
	 * 保存数据
	 * @Title: save 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:15:42
	 */
	@Before(RoleValidator.class)
	public void save(){
		Role r = getBean(Role.class, "model");
		BigDecimal i = service.countByNodeKey(r);
		if (i.compareTo(BigDecimal.ZERO)==1) {
			renderJson(Ret.fail("msg", "业务环节已存在!"));
		}else{
		boolean bln = r.set("ID", Role.seq).save();
		if (bln)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
		}
	}
	/**
	 * 编辑数据准备
	 * @Title: edit 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:16:29
	 */
	public void edit() {
		if (null==getParaToInt()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{
		setAttr("model", Role.me.findById(getParaToInt()));
		}
	}
	/**
	 * 编辑数据更新
	 * @Title: update 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:16:42
	 */
	@Before(RoleValidator.class)
	public void update() {
		Role r = getBean(Role.class, "model");
		BigDecimal i = service.countByNodeKey(r);
		if (i.compareTo(BigDecimal.ZERO)==1) {
			renderJson(Ret.fail("msg", "业务环节已存在!"));
		}else{
			boolean bln = r.update();
			if (bln)
				renderJson(Ret.ok());
			else
				renderJson(Ret.fail("msg", "失败"));
		}
	}
	
	/**
	 * 根据id删除数据
	 * @Title: delete 
	 * @Description: TODO
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月27日 上午10:16:57
	 */
	public void delete() {
		if (null==getParaToInt()) {
			renderJson(Ret.fail("errorcode", "111111").set("crrormsg","参数(id)传值不能为空"));
			return;
		}else{
				int i=0; 
				List<Privilege> p = privilegeService.findByRoleId(getParaToInt());
				boolean bln = getBean(Role.class, "model").deleteById(getParaToInt());
				//删除权限表数据
				i =privilegeService.deleteByRoleId(getParaToInt());
				if (bln && i==p.size())
					renderJson(Ret.ok());
				else
					renderJson(Ret.fail("msg", "失败"));
			}
	}
	public void countById(){
		BigDecimal i = service.countByRoleId(getParaToInt());
		if (i.compareTo(BigDecimal.ZERO)==1)
			renderJson(Ret.ok());
		else
			renderJson(Ret.fail("msg", "失败"));
	}
}
