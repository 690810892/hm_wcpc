package com.hemaapp.wcpc_driver.view;


import com.hemaapp.wcpc_driver.module.DistrictInfor;

import java.util.Comparator;


/**
 * 拼音比较器
 */
public class PinyinComparator implements Comparator<DistrictInfor> {

	public int compare(DistrictInfor o1, DistrictInfor o2) {
		if (o1.getCharindex().equals("@")
				|| o2.getCharindex().equals("#")) {
			return -1;
		} else if (o1.getCharindex().equals("#")
				|| o2.getCharindex().equals("@")) {
			return 1;
		} else {
			return o1.getCharindex().compareTo(o2.getCharindex());
		}
	}

}
