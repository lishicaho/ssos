package com.imooc.myo2o.service;

import java.util.List;

import com.imooc.myo2o.entity.Area;
/**
 * 区域逻辑业务层
 * @author csp73
 *
 */
public interface AreaService {
	/**
	 * 返回Area所有对象
	 * @return
	 */
	public List<Area> listArea();
}
