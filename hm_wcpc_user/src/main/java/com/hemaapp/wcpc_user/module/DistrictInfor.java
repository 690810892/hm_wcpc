package com.hemaapp.wcpc_user.module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * 地区信息
 * */
public class DistrictInfor extends XtomObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键id
	private String name; //地区名称
	private String parentid; //父级别主键
	private String nodepath; //节点主键路径串
	private String namepath; //节点名称路径串
	private String charindex; //名称拼音首字母索引
	private String level; //节点层级
	private String orderby; //排序优先级
	
	private String checkflag; //0否1是
	private String city_id; //	城市主键id
	private String price; //	路线基本价格	keyid>0返回
	private String areaItems; //	服务区域列表
	private ArrayList<Area> areas=new ArrayList<>();
	public DistrictInfor(JSONObject jsonObject) throws DataParseException {
		if(jsonObject!=null){
			try {
				id = get(jsonObject, "id");
				name = get(jsonObject, "name");
				parentid = get(jsonObject, "parentid");
				nodepath = get(jsonObject, "nodepath");
				namepath = get(jsonObject, "namepath");
				charindex = get(jsonObject, "charindex");
				level = get(jsonObject, "level");
				orderby = get(jsonObject, "orderby");
				checkflag = get(jsonObject, "checkflag");
				city_id = get(jsonObject, "city_id");
				price = get(jsonObject, "price");
				areaItems = get(jsonObject, "areaItems");
				if (!jsonObject.isNull("areaItems")
						&& !isNull(jsonObject.getString("areaItems"))) {
					JSONArray jsonList = jsonObject.getJSONArray("areaItems");
					int size = jsonList.length();
					for (int i = 0; i < size; i++) {
						areas.add(new Area(jsonList.getJSONObject(i)));
					}
				}
				log_i(toString());
			} catch (JSONException e) {
				throw new DataParseException(e);
			}
		}
	}
	
	public String getCheckflag() {
		return checkflag;
	}

	public DistrictInfor(String id, String name, String parentid, String nodepath,
                         String namepath, String charindex, String level, String orderby) {
		this.id = id;
		this.name = name;
		this.parentid = parentid;
		this.nodepath = nodepath;
		this.namepath = namepath;
		this.charindex = charindex;
		this.level = level;
		this.orderby = orderby;
	}

	@Override
	public String toString() {
		return "DistrictInfor{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", parentid='" + parentid + '\'' +
				", nodepath='" + nodepath + '\'' +
				", namepath='" + namepath + '\'' +
				", charindex='" + charindex + '\'' +
				", level='" + level + '\'' +
				", orderby='" + orderby + '\'' +
				", checkflag='" + checkflag + '\'' +
				", city_id='" + city_id + '\'' +
				", price='" + price + '\'' +
				", areaItems='" + areaItems + '\'' +
				", areas=" + areas +
				'}';
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getParentid() {
		return parentid;
	}

	public String getNodepath() {
		return nodepath;
	}

	public String getNamepath() {
		return namepath;
	}

	public String getCity_id() {
		return city_id;
	}

	public String getPrice() {
		return price;
	}

	public String getAreaItems() {
		return areaItems;
	}

	public ArrayList<Area> getAreas() {
		return areas;
	}

	public String getCharindex() {
		if (isNull(charindex))
			charindex="#";
		return charindex;
	}

	public String getLevel() {
		return level;
	}

	public String getOrderby() {
		return orderby;
	}
}
