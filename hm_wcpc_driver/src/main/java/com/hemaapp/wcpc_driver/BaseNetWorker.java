package com.hemaapp.wcpc_driver;

import android.content.Context;

import com.hemaapp.hm_FrameWork.HemaNetWorker;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.wcpc_driver.nettask.AccountRecordListTask;
import com.hemaapp.wcpc_driver.nettask.AdviceAddTask;
import com.hemaapp.wcpc_driver.nettask.AlipayTradeTask;
import com.hemaapp.wcpc_driver.nettask.BankListTask;
import com.hemaapp.wcpc_driver.nettask.ClientGetTask;
import com.hemaapp.wcpc_driver.nettask.ClientLoginTask;
import com.hemaapp.wcpc_driver.nettask.ClientTripsGetTask;
import com.hemaapp.wcpc_driver.nettask.CodeVerifyTask;
import com.hemaapp.wcpc_driver.nettask.ComplainAddTask;
import com.hemaapp.wcpc_driver.nettask.DataListTask;
import com.hemaapp.wcpc_driver.nettask.DistrictListTask;
import com.hemaapp.wcpc_driver.nettask.DriverOrderGetTask;
import com.hemaapp.wcpc_driver.nettask.DriverOrderListTask;
import com.hemaapp.wcpc_driver.nettask.FeeCalculationTask;
import com.hemaapp.wcpc_driver.nettask.FeeRuleListTask;
import com.hemaapp.wcpc_driver.nettask.FileUploadTask;
import com.hemaapp.wcpc_driver.nettask.InitTask;
import com.hemaapp.wcpc_driver.nettask.MyClientListTask;
import com.hemaapp.wcpc_driver.nettask.MyReplyListTask;
import com.hemaapp.wcpc_driver.nettask.MyTripsListTask;
import com.hemaapp.wcpc_driver.nettask.NoResultReturnTask;
import com.hemaapp.wcpc_driver.nettask.NoticeListTask;
import com.hemaapp.wcpc_driver.nettask.NoticeUnreadTask;
import com.hemaapp.wcpc_driver.nettask.TripsListTask;
import com.hemaapp.wcpc_driver.nettask.UnionTradeTask;
import com.hemaapp.wcpc_driver.nettask.WeiXinPayTask;
import com.hemaapp.wcpc_driver.nettask.WorkStatusGetTask;

import java.util.HashMap;

import xtom.frame.XtomConfig;
import xtom.frame.util.Md5Util;
import xtom.frame.util.XtomDeviceUuidFactory;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * 网络请求工具类
 */
public class BaseNetWorker extends HemaNetWorker {
	/**
	 * 实例化网络请求工具类
	 *
	 * @param mContext
	 */
	private Context mContext;

	public BaseNetWorker(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}

	@Override
	public void clientLogin() {
		BaseHttpInformation information = BaseHttpInformation.CLIENT_LOGIN;
		HashMap<String, String> params = new HashMap<>();
		String username = XtomSharedPreferencesUtil.get(mContext, "username");
		params.put("username", username);// 用户登录名 手机号或邮箱
		String password = XtomSharedPreferencesUtil.get(mContext, "password");
		params.put("password", Md5Util.getMd5(XtomConfig.DATAKEY
				+ Md5Util.getMd5(password))); // 登陆密码 服务器端存储的是32位的MD5加密串
		params.put("devicetype", "2"); // 用户登录所用手机类型 1：苹果 2：安卓（方便服务器运维统计）
		String version = HemaUtil.getAppVersionForSever(mContext);
		params.put("lastloginversion", version);// 登陆所用的系统版本号
		// 记录用户的登录版本，方便服务器运维统计

		BaseNetTask task = new ClientLoginTask(information, params);
		executeTask(task);
	}
	/**
	 * 第三方自动登录
	 * */
	@Override
	public boolean thirdSave() {
		return false;
	}
	/**
	 * 系统初始化
	 */
	public void init() {
		BaseHttpInformation information = BaseHttpInformation.INIT;
		HashMap<String, String> params = new HashMap<>();
		params.put("devicetype", "2");
		params.put("lastloginversion", HemaUtil.getAppVersionForSever(mContext));// 版本号码(默认：1.0.0)
		params.put("device_sn", XtomDeviceUuidFactory.get(mContext));// 客户端硬件串号

		BaseNetTask task = new InitTask(information, params);
		executeTask(task);
	}

	/**
	 * 验证用户名是否合法
	 */
	public void clientVerify(String username, String clienttype) {
		BaseHttpInformation information = BaseHttpInformation.CLIENT_VERIFY;
		HashMap<String, String> params = new HashMap<>();
		params.put("username", username);// 用户登录名 手机号或邮箱
		params.put("clienttype", clienttype);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 申请随机验证码
	 */
	public void codeGet(String username) {
		BaseHttpInformation information = BaseHttpInformation.CODE_GET;
		HashMap<String, String> params = new HashMap<>();
		params.put("username", username);// 用户登录名 手机号或邮箱

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 验证随机码
	 */
	public void codeVerify(String username, String code) {
		BaseHttpInformation information = BaseHttpInformation.CODE_VERIFY;
		HashMap<String, String> params = new HashMap<>();
		params.put("username", username);// 用户登录名 手机号或邮箱
		params.put("code", code);// 6位随机号码 测试阶段固定向服务器提交“123456”

		BaseNetTask task = new CodeVerifyTask(information, params);
		executeTask(task);
	}

	/**
	 * 登录
	 *
	 * @param username, password
	 */
	public void clientLogin(String username, String password) {
		BaseHttpInformation information = BaseHttpInformation.CLIENT_LOGIN;
		HashMap<String, String> params = new HashMap<>();
		params.put("username", username);// 用户登录名 手机号或邮箱
		params.put("password", Md5Util.getMd5(XtomConfig.DATAKEY
				+ Md5Util.getMd5(password))); // 登陆密码 服务器端存储的是32位的MD5加密串
		params.put("devicetype", "2"); // 用户登录所用手机类型 1：苹果 2：安卓（方便服务器运维统计）
		String version = HemaUtil.getAppVersionForSever(mContext);
		params.put("lastloginversion", version);// 登陆所用的系统版本号
		// 记录用户的登录版本，方便服务器运维统计
		BaseNetTask task = new ClientLoginTask(information, params);
		executeTask(task);
	}
	/**
	 * 上传文件（图片，音频，视频）
	 */
	public void fileUpload(String token, String keytype, String keyid,
                           String duration, String orderby, String content, String temp_file) {
		BaseHttpInformation information = BaseHttpInformation.FILE_UPLOAD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);//
		params.put("keytype", keytype); //
		params.put("keyid", keyid); //
		params.put("duration", duration); //
		params.put("orderby", orderby); //
		params.put("content", content);// 内容描述 有的项目中，展示性图片需要附属一段文字说明信息。默认传"无"
		HashMap<String, String> files = new HashMap<>();
		files.put("temp_file", temp_file); //

		BaseNetTask task = new FileUploadTask(information, params, files);
		executeTask(task);
	}
	/**
	 * 硬件注册保存接口
	 * */
	public void deviceSave(String token, String deviceid,
                           String channelid) {
		BaseHttpInformation information = BaseHttpInformation.DEVICE_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("deviceid", deviceid);// 登陆手机硬件码 对应百度推送userid
		params.put("devicetype", "2");// 登陆手机类型 1:苹果 2:安卓
		params.put("channelid", channelid);// 百度推送渠道id 方便直接从百度后台进行推送测试
		params.put("clienttype", "2");

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 重设密码
	 */
	public void passwordReset(String temp_token, String keytype, String clienttype,
                              String new_password) {
		BaseHttpInformation information = BaseHttpInformation.PASSWORD_RESET;
		HashMap<String, String> params = new HashMap<>();
		params.put("temp_token", temp_token);// 临时令牌
		params.put("new_password", Md5Util.getMd5(XtomConfig.DATAKEY
				+ Md5Util.getMd5(new_password)));// 新密码
		params.put("keytype", keytype);// 密码类型 1：登陆密码 2：支付密码
		params.put("clienttype", "2");

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 *  计费规则接口
	 * */
	public void feeRuleList(String franchisee_id, String district){
		BaseHttpInformation information = BaseHttpInformation.FEE_RULE_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("franchisee_id", franchisee_id);
		params.put("district", district);

		BaseNetTask task = new FeeRuleListTask(information, params);
		executeTask(task);
	}

	/**
	 * 保存当前用户坐标接口
	 * */
	public void positionSave(String token, String clienttype, String lng, String lat){
		BaseHttpInformation information = BaseHttpInformation.POSITION_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 临时令牌
		params.put("clienttype", clienttype);
		params.put("lng", lng);
		params.put("lat", lat);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 费用计算接口
	 * */
	public void feeCalculation(String franchisee_id, String distance, String numbers, String district){
		BaseHttpInformation information = BaseHttpInformation.FEE_CALCULATION;
		HashMap<String, String> params = new HashMap<>();
		params.put("franchisee_id", franchisee_id);
		params.put("distance", distance);
		params.put("numbers", numbers);
		params.put("district", district);

		BaseNetTask task = new FeeCalculationTask(information, params);
		executeTask(task);
	}

	/**
	 * 发布行程接口
	 * */
	public void tripsAdd(String token, String startaddress, String endaddress,
                         String begintime, String numbers, String carpoolflag, String thankfee, String remarks,
                         String lng_start, String lat_start, String lng_end, String lat_end, String successfee,
                         String failfee, String distance, String lng, String lat, String address, String district){
		BaseHttpInformation information = BaseHttpInformation.TRIPS_ADD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("startaddress", startaddress);
		params.put("endaddress", endaddress);
		params.put("begintime", begintime);
		params.put("numbers", numbers);
		params.put("carpoolflag", carpoolflag);
		params.put("thankfee", thankfee);
		params.put("remarks", remarks);
		params.put("lng_start", lng_start);
		params.put("lat_start", lat_start);
		params.put("lng_end", lng_end);
		params.put("lat_end", lat_end);
		params.put("successfee", successfee);
		params.put("failfee", failfee);
		params.put("distance", distance);
		params.put("lat", lat);
		params.put("lng", lng);
		params.put("address", address);
		params.put("district", district);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 行程列表接口
	 * */
	public void tripsList(String token, String keytype, String keyid, String orderby, int page, String district){
		BaseHttpInformation information = BaseHttpInformation.TRIPS_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("orderby", orderby);
		params.put("district", district);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new TripsListTask(information, params);
		executeTask(task);
	}
	public void tripsList(String token, String keytype,int page){
		BaseHttpInformation information = BaseHttpInformation.TRIPS_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new TripsListTask(information, params);
		executeTask(task);
	}

	/**
	 * 获取用户个人资料接口
	 * */
	public void clientGet(String token, String id){
		BaseHttpInformation information = BaseHttpInformation.CLIENT_GET;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("driver_id", id);

		BaseNetTask task = new ClientGetTask(information, params);
		executeTask(task);
	}

	/**
	 * 获取消息通知列表接口
	 * */
	public void noticeList(String token, String keytype, String clienttype, int page){
		BaseHttpInformation information = BaseHttpInformation.NOTICE_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("clienttype", clienttype);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new NoticeListTask(information, params);
		executeTask(task);
	}

	/**
	 * 保存用户通知操作接口
	 * */
	public void noticeSaveOperate(String token, String id,  String clienttype, String operatetype ){
		BaseHttpInformation information = BaseHttpInformation.NOTICE_SAVEOPERATE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("id", id);
		params.put("operatetype", operatetype);
		params.put("clienttype", "2");

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 保存用户资料接口
	 * */
	public void clientSave(String token, String realname, String sex, String email, String IDtype, String IDnumber, String mobile){
		BaseHttpInformation information = BaseHttpInformation.CLIENT_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("realname", realname);
		params.put("sex", sex);
		params.put("email", email);
		params.put("IDtype", IDtype);
		params.put("IDnumber", IDnumber);
		params.put("mobile", mobile);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 修改并保存密码接口
	 * */
	public void passwordSave(String token, String keytype, String clienttype,
                             String old_password, String new_password){
		BaseHttpInformation information = BaseHttpInformation.PASSWORD_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("keytype", keytype);
		params.put("clienttype", clienttype);
		params.put("old_password", old_password);
		params.put("new_password", new_password);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 退出登录接口
	 * */
	public void clientLoginout(String token, String keytype){
		BaseHttpInformation information = BaseHttpInformation.CLIENT_LOGINOUT;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("clienttype", keytype);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 我的行程接口
	 * */
	public void myTripsList(String token, String keytype, String clienttype, int page){
		BaseHttpInformation information = BaseHttpInformation.MY_TRIPS_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("keytype", keytype);
		params.put("clienttype", clienttype);
		params.put("page", String.valueOf(page));// 当前列表翻页索引 第一页时请传递page=0，翻页时依次递增。

		BaseNetTask task = new MyTripsListTask(information, params);
		executeTask(task);
	}

	/**
	 * 我的行程操作接口
	 * */
	public void myTripsOperate(String token, String clienttype, String keytype, String keyid){
		BaseHttpInformation information = BaseHttpInformation.MY_TRIPS_OPERATE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("clienttype", clienttype);
		params.put("keytype", keytype);
		params.put("keyid", keyid);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 订单操作接口
	 * */
	public void orderOperate(String token, String keytype, String keyid, String reason_str,
                             String content){
		BaseHttpInformation information = BaseHttpInformation.ORDER_OPERATE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("reason_str", reason_str);
		params.put("content", content);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}
	public void orderOperate(String token, String keytype, String trip_id,  String cancel_reason_ids, String cancel_reason,String param) {
		BaseHttpInformation information = BaseHttpInformation.ORDER_OPERATE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("trip_id", trip_id);
		params.put("cancel_reason_ids", cancel_reason_ids);
		params.put("cancel_reason", cancel_reason);
		params.put("param", param);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 司机端订单列表接口
	 * */
	public void driverOrderList(String token, String keytype, int page, String order, String lng, String lat){
		BaseHttpInformation information = BaseHttpInformation.DRIVER_ORDER_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("order", order);
		params.put("lng", lng);
		params.put("lat", lat);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new DriverOrderListTask(information, params);
		executeTask(task);
	}

	/**
	 *  抢单接口
	 * */
	public void grapTrips(String token, String trips_id){
		BaseHttpInformation information = BaseHttpInformation.GRAP_TRIPS;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("trips_id", trips_id);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 司机端订单详情接口
	 * */
	public void driverOrderGet(String token, String order_id){
		BaseHttpInformation information = BaseHttpInformation.DRIVER_ORDER_GET;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("order_id", order_id);

		BaseNetTask task = new DriverOrderGetTask(information, params);
		executeTask(task);
	}

	/**
	 * 用户行程详情(抢单详情)接口
	 * */
	public void clientTripsGet(String trips_id){
		BaseHttpInformation information = BaseHttpInformation.CLIENT_TRIPS_GET;
		HashMap<String, String> params = new HashMap<>();
		params.put("trips_id", trips_id);

		BaseNetTask task = new ClientTripsGetTask(information, params);
		executeTask(task);
	}

	/**
	 * 登录状态保存接口
	 * */
	public void loginflagSave(String token, String loginflag){
		BaseHttpInformation information = BaseHttpInformation.LOGINFLAG_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("loginflag", loginflag);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 我的乘客接口
	 * */
	public void myClientList(String token, int page){
		BaseHttpInformation information = BaseHttpInformation.MY_CLIENT_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new MyClientListTask(information, params);
		executeTask(task);
	}

	/**
	 * 数据获取接口
	 * */
	public void dataList(String keytype){
		BaseHttpInformation information = BaseHttpInformation.DATA_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("keytype", keytype);

		BaseNetTask task = new DataListTask(information, params);
		executeTask(task);
	}

	/**
	 * 通知未读接口
	 * */
	public void noticeUnread(String token, String keytype, String clienttype){
		BaseHttpInformation information = BaseHttpInformation.NOTICE_UNREAD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("clienttype", clienttype);

		BaseNetTask task = new NoticeUnreadTask(information, params);
		executeTask(task);
	}

	/**
	 * 支付宝信息保存接口
	 * */
	public void alipaySave(String token, String alipay_no, String alipay_name){
		BaseHttpInformation information = BaseHttpInformation.ALIPAY_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("clienttype", "2");
		params.put("alipay_no", alipay_no);
		params.put("alipay_name", alipay_name);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 *  申请提现接口
	 * */
	public void cashAdd(String token, String keytype, String applyfee, String paypassword){
		BaseHttpInformation information = BaseHttpInformation.CASH_ADD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("clienttype", "2");
		params.put("keytype", keytype);
		params.put("applyfee", applyfee);
		params.put("paypassword", Md5Util.getMd5(XtomConfig.DATAKEY
				+ Md5Util.getMd5(paypassword)));

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 获取银行列表
	 */
	public void bankList(String token, int page) {
		BaseHttpInformation information = BaseHttpInformation.BANK_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("page", String.valueOf(page));// 当前列表翻页索引 第一页时请传递page=0，翻页时依次递增。

		BaseNetTask task = new BankListTask(information, params);
		executeTask(task);
	}

	/**
	 * 银行卡信息保存接口
	 * */
	public void bankSave(String token, String bankuser, String bankcard, String bankname){
		BaseHttpInformation information = BaseHttpInformation.BANK_SAVE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("clienttype", "2");
		params.put("bankuser", bankuser);
		params.put("bankcard", bankcard);
		params.put("bankname", bankname);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}

	/**
	 * 地区列表接口
	 * */
	public void districtList(String parentid){
		BaseHttpInformation information = BaseHttpInformation.DISTRICT_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("parentid", parentid);

		BaseNetTask task = new DistrictListTask(information, params);
		executeTask(task);
	}

	/**
	 * 设置接单距离接口
	 * */
	public void myLength(String token, String mylength){
		BaseHttpInformation information = BaseHttpInformation.MY_LENGTH;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("mylength", mylength);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}
	public void tripsOperate(String token, String keytype, String trip_id, String param){
		BaseHttpInformation information = BaseHttpInformation.TRIPS_SAVEOPERATE;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("trip_id", trip_id);
		params.put("keytype", keytype);
		params.put("param", param);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}
	public void workStatusGet(String token){
		BaseHttpInformation information = BaseHttpInformation.WORKSTATUS_GET;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);

		BaseNetTask task = new WorkStatusGetTask(information, params);
		executeTask(task);
	}
	public void openWorkStatus(String token, String lng, String lat, String address){
		BaseHttpInformation information = BaseHttpInformation.OPEN_WORKSTATUS;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("lng", lng);
		params.put("lat", lat);
		params.put("address", address);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}
	/**
	 * 账户明细接口
	 */
	public void accountRecordList(String token, String begindate, String enddate, int page) {
		BaseHttpInformation information = BaseHttpInformation.ACCOUNT_RECORD_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("begindate", begindate);
		params.put("enddate", enddate);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new AccountRecordListTask(information, params);
		executeTask(task);
	}
	/**
	 * 获取支付宝交易签名串
	 */
	public void alipay(String token, String keytype, String keyid, String total_fee) {
		BaseHttpInformation information = BaseHttpInformation.ALIPAY;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("paytype", "1");
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("total_fee", total_fee);

		BaseNetTask task = new AlipayTradeTask(information, params);
		executeTask(task);
	}

	/**
	 * 获取银联交易签名串
	 */
	public void unionpay(String token, String keytype, String keyid, String total_fee) {
		BaseHttpInformation information = BaseHttpInformation.UNIONPAY;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("paytype", "2");
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("total_fee", total_fee);

		BaseNetTask task = new UnionTradeTask(information, params);
		executeTask(task);
	}

	/**
	 * 获取微信交易签名串
	 */
	public void weixin(String token, String keytype, String keyid, String total_fee) {
		BaseHttpInformation information = BaseHttpInformation.WEI_XIN;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);// 登陆令牌
		params.put("paytype", "3");
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("total_fee", total_fee);

		BaseNetTask task = new WeiXinPayTask(information, params);
		executeTask(task);
	}

	/**
	 * 添加评论接口
	 */
	public void replyAdd(String token, String keytype, String keyid, String reply_str,
						 String point, String content, String reply_str_text) {
		BaseHttpInformation information = BaseHttpInformation.REPLY_ADD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("reply_str", reply_str);
		params.put("point", point);
		params.put("content", content);
		params.put("reply_str_text", reply_str_text);
		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}
	public void complainAdd(String token, String trip_id, String driver_id, String idstr, String idstr_text, String content) {
		BaseHttpInformation information = BaseHttpInformation.COMPLAIN_ADD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("trip_id", trip_id);
		params.put("driver_id", driver_id);
		params.put("idstr", idstr);
		params.put("idstr_text", idstr_text);
		params.put("content", content);
		BaseNetTask task = new ComplainAddTask(information, params);
		executeTask(task);
	}
	public void replyList(String token, String keytype, String keyid, int page) {
		BaseHttpInformation information = BaseHttpInformation.REPLY_LIST;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("page", String.valueOf(page));

		BaseNetTask task = new MyReplyListTask(information, params);
		executeTask(task);
	}
	/**
	 * 意见反馈接口
	 */
	public void adviceAdd(String token, String device, String version,
						  String brand, String system, String content, String clienttype) {
		BaseHttpInformation information = BaseHttpInformation.ADVICE_ADD;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("devicetype", device);
		params.put("version", version);
		params.put("mobilebrand", brand);
		params.put("systemtype", system);
		params.put("content", content);
		params.put("clienttype", "2");

		BaseNetTask task = new AdviceAddTask(information, params);
		executeTask(task);
	}
	public void shareCallback(String token, String keytype, String keyid, String sharetype){
		BaseHttpInformation information = BaseHttpInformation.SHARE_CALLBACK;
		HashMap<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("keytype", keytype);
		params.put("keyid", keyid);
		params.put("sharetype", sharetype);

		BaseNetTask task = new NoResultReturnTask(information, params);
		executeTask(task);
	}
}
