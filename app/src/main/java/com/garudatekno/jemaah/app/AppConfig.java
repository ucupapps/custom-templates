package com.garudatekno.jemaah.app;

public class AppConfig {
	// Server user login url
//	public static String URL_HOME = "http://192.168.0.8";
//	public static String URL_LOGIN = "http://192.168.0.8/api/jemaah/login";
//	public static String URL_REGISTER = "http://192.168.0.8/api/jemaah/register";
//	public static String URL_PROFILE = "http://192.168.0.8/api/jemaah/profile";
//	public static String URL_GETPROFILE = "http://192.168.0.8/api/jemaah/get-profile?id=";
//	public static String URL_GET_DOA = "http://192.168.0.8/api/jemaah/get-doa";
//	public static String URL_EMERGENCY = "http://192.168.0.8/api/jemaah/emergency";
//	public static String URL_GET_FAMILY_PHONE = "http://192.168.0.8/api/jemaah/family-phone?id=";
//	public static String URL_GET_INBOX = "http://192.168.0.8/api/jemaah/get-inbox?id=";

	public static String URL_HOME = "https://gohajj.id";
	public static String URL_LOGIN = "https://gohajj.id/api/jemaah/login";
	public static String URL_REGISTER = "https://gohajj.id/api/jemaah/register";
	public static String URL_PROFILE = "https://gohajj.id/api/jemaah/profile";
	public static String URL_GETPROFILE = "https://gohajj.id/api/jemaah/get-profile?id=";
	public static String URL_GET_DOA = "https://gohajj.id/api/jemaah/get-doa";
	public static String URL_EMERGENCY = "https://gohajj.id/api/jemaah/emergency";
	public static String URL_GET_FAMILY_PHONE = "https://gohajj.id/api/jemaah/family-phone?id=";
	public static String URL_GET_INBOX = "https://gohajj.id/api/jemaah/get-inbox?id=";

	public static final String URL_GET_SAMPLE = "http://apps.cikarangdryport.com/api/driver/get-task?id=";
	public static final String URL_STATUS_START = "http://apps.cikarangdryport.com/api/driver/start?id=";
	public static final String URL_STATUS_DONE = "http://apps.cikarangdryport.com/api/driver/done?id=";
	public static final String URL_ORDER_LIST = "http://apps.cikarangdryport.com/api/driver/order-list?id=";
	public static final String URL_ORDER_HISTORY = "http://apps.cikarangdryport.com/api/driver/order-history?id=";


	public static final String URL_DELETE_SAMPLE = "http://demo.garudatekno.com/jemaah/delete-sample?id=";
	public static final String KEY_PHONE= "phone";
	public static final String KEY_MESSAGE= "message";
	public static final String KEY_PHONE_FAMILY1= "family_phone1";
	public static final String KEY_PHONE_FAMILY2= "family_phone2";
	public static final String KEY_PHONE_FAMILY3= "family_phone3";
	public static final String KEY_TRAVEL_AGENT= "travel_agent";
	public static final String KEY_HOTEL_MEKKAH= "hotel_mekkah";
	public static final String KEY_HOTEL_MADINAH= "hotel_madinah";
	public static final String KEY_PEMBIMBING = "pembimbing";
	public static final String KEY_PASSPORT = "passport";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_TOWN = "town";
	public static final String KEY_PROVINCE = "province";
	public static final String UPLOAD_KEY = "image";
	public static final String KEY_USERID = "userid";
	public static final String KEY_DATETIME = "datetime";
	public static final String KEY_DATE = "date";
	public static final String KEY_TIME = "time";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";

	static final String KEY_CON_ID= "id";
	public static final String KEY_CON_NO = "no";
	public static final String KEY_NAME = "name";
	public static final String KEY_FROM = "from";
	public static final String KEY_TO = "to";
	public static final String KEY_STATUS = "status";

	//category survey

	public static final String URL_GET_ALL_CATEGORY = "http://demo.garudatekno.com/jemaah/view-category";
	public static final String URL_GET_ALL_KUESIONER = "http://demo.garudatekno.com/jemaah/get-kuesioner?id=";

	// Server user register url
//	public static String URL_REGISTER = "http://aia.pilar-systems.com/cdpapplication/register";

	//Keys that will be used to send the request to php scripts
	public static final String KEY_ID= "id";

	//JSON Tags
	public static final String TAG_JSON_ARRAY="result";
	public static final String TAG_ID = "id";
	public static final String TAG_NAME = "name";
	public static final String TAG_LAT = "lat";
	public static final String TAG_LNG = "lng";
	public static final String TAG_NO= "no";
	public static final String TAG_TYPE = "type";
	public static final String TAG_SIZE = "size";

	//employee id to pass with intent
	public static final String EMP_ID = "emp_id";

}
