package com.imooc.myo2o.dao;


import java.util.List;

import com.imooc.myo2o.entity.Area;
/**
 * 区域数据交互层
 * @author csp73
 *
 */
public interface AreaDao {
	/**
	 * 返回Area所有对象
	 * @return
	 */
	public List<Area> listArea();
}
