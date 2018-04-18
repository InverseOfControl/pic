/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月14日下午5:29:01
 *
*/

package com.yuminsoft.cps.pic._admin.paperstype;

import java.math.BigDecimal;
import java.util.List;


import com.jfinal.plugin.activerecord.Db;
import com.yuminsoft.cps.pic.common.model.Paperstype;
/**
 * 文件类型 <br/>
 * Date: 2017年3月14日 下午5:29:01 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class PaperstypeService {

	public static final PaperstypeService me = new PaperstypeService();
	final static Paperstype dao = Paperstype.me;
	/**
	 * 获取列表. <br/>
	 *
	 * @author gaojf@yuminsoft.com
	 * @date: 2017年3月14日 下午5:31:55
	 * @return
	 */
	public List<Paperstype> list() {
		return dao.find("select * from PAPERSTYPE order by sortnum");
	}
	/**
	 * 	
	 * @Title: findBySortNum 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月15日 下午2:34:27 
	 * @param pp
	 * @return
	 */
	public BigDecimal findBySortNum(Paperstype pp){
		if (null==pp.getId()) {
			return Db.queryBigDecimal("select count(*) from PAPERSTYPE where SORTNUM = "+pp.getSortnum()+" order by sortnum");
		}else{
			return Db.queryBigDecimal("select count(*) from PAPERSTYPE where SORTNUM = "+pp.getSortnum()+" and id !="+pp.getId()+" order by sortnum");
		}
	}
	public BigDecimal findByItemCode(Paperstype pp){
		if (null==pp.getId()) {
			return Db.queryBigDecimal("select count(*) from PAPERSTYPE where item_code = '"+pp.getItemCode()+"' order by sortnum");
		}else{
			return Db.queryBigDecimal("select count(*) from PAPERSTYPE where item_code = '"+pp.getItemCode()+"' and id !="+pp.getId()+" order by sortnum");
		}
	}
	/**
	 * 下拉框查询
	 * @Title: sysNameList 
	 * @author renyz@yuminsoft.com
	 * @date: 2017年3月15日 下午4:30:03 
	 * @return
	 *//*
	public List<Picture> sysNameList(){
		return pdao.find("select distinct sys_name from PICTURE");
	}*/
	public Paperstype findById(String id){
		return dao.findById(id);
	}
	public List<Paperstype> find(){
		return dao.find("select * from PAPERSTYPE order by sortnum");
	}
	public Paperstype findByItemCodeAndSysName(String itemCode) {
		return dao.findFirst("select * from PAPERSTYPE t where t.item_code = ? ",itemCode);
	}
	public BigDecimal countById(Integer id) {
		return Db.queryBigDecimal("select count(*) from PRIVILEGE t where t.paperstype_id = ?", id);
	}
}
