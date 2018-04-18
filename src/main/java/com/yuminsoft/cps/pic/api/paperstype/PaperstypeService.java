/**
 * Copyright (c) 2017, gaojf@yuminsoft.com All Rights Reserved. <br/>
 * Date:2017年3月17日上午9:48:51
 *
*/

package com.yuminsoft.cps.pic.api.paperstype;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.yuminsoft.cps.pic.common.model.Paperstype;

/**
 * 文件类型接口 <br/>
 * Date: 2017年3月17日 上午9:48:51 <br/>
 * 
 * @author gaojf@yuminsoft.com
 */
public class PaperstypeService {
	public static final PaperstypeService me = new PaperstypeService();
	final static Paperstype dao =new Paperstype().dao();

	/**
	 * 详细参数
	 * @param nodeKey
	 * @param appNo
	 * @return
	 */
	public List<Record> list(String nodeKey,String appNo) {
		List<Record> list = Db.find(dao.getSql("api.paperstypeList"), appNo, appNo, appNo, nodeKey);
		if (null != appNo && appNo.length() <= 9) {
			for (Record r : list) {
				if ("M".equals(r.getStr("code"))) {
					BigDecimal fileAmount = Db.queryBigDecimal("SELECT COUNT(0) FROM OLD_PICTURE WHERE LOAN_ID=?",
							appNo);
					if (null == fileAmount)
						fileAmount = new BigDecimal("0");
					BigDecimal fa = r.getBigDecimal("fileAmount");
					if (null == fa)
						fa = new BigDecimal("0");
					r.set("fileAmount", fileAmount.add(fa));
				}
			}
		}
		return list;
	}
}
