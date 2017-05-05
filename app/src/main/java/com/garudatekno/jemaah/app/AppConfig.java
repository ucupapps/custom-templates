package com.garudatekno.jemaah.app;

public class AppConfig {
	// Server user login url
//	public static String URL_HOME = "http://192.168.0.9";
//	public static String URL_LOGIN = "http://192.168.0.10/api/jemaah/login";
//	public static String URL_REGISTER = "http://192.168.0.8/api/jemaah/register";
//	public static String URL_PROFILE = "http://192.168.0.9/api/jemaah/profile";
//	public static String URL_GETPROFILE = "http://192.168.0.9/api/jemaah/get-profile?id=";
//	public static String URL_GET_PANDUAN = "http://192.168.0.9/api/jemaah/get-panduan?id=";
//	public static String URL_PANDUAN = "http://192.168.0.9/api/jemaah/panduan";
//	public static String URL_PANDUAN_JENIS = "http://192.168.0.9/api/jemaah/panduan-jenis";
//	public static String URL_EMERGENCY = "http://192.168.0.8/api/jemaah/emergency";
//	public static String URL_GET_FAMILY_PHONE = "http://192.168.0.8/api/jemaah/family-phone?id=";
//	public static String URL_GET_INBOX = "http://192.168.0.7/api/jemaah/get-inbox?id=";
//	public static String URL_GET_INBOX_VIEW = "http://192.168.0.7/api/jemaah/get-inbox-view?id=";
//	public static String URL_PIN_LOCATION = "http://192.168.0.8/api/jemaah/pinlocation";
//	public static String URL_RATING= "http://192.168.0.9/api/jemaah/rating";
//	public static String URL_DOAKAN= "http://192.168.0.10/api/jemaah/doakan";
//	public static String URL_TITIPAN_DOA = "http://192.168.0.10/api/jemaah/titipan-doa";
//	public static String URL_POI= "http://192.168.0.10/api/jemaah/poi";
//	public static String URL_GET_POI= "http://192.168.0.10/api/jemaah/get-poi?id=";

	public static String URL_HOME = "https://gohajj.id";
	public static String URL_LOGIN = "https://gohajj.id/api/jemaah/login";
	public static String URL_REGISTER = "https://gohajj.id/api/jemaah/register";
	public static String URL_PROFILE = "https://gohajj.id/api/jemaah/profile";
	public static String URL_GETPROFILE = "https://gohajj.id/api/jemaah/get-profile?id=";
	public static String URL_GET_PANDUAN = "https://gohajj.id/api/jemaah/get-panduan?id=";
	public static String URL_PANDUAN = "https://gohajj.id/api/jemaah/panduan";
	public static String URL_PANDUAN_JENIS = "https://gohajj.id/api/jemaah/panduan-jenis";
	public static String URL_TITIPAN_DOA = "https://gohajj.id/api/jemaah/titipan-doa";
	public static String URL_EMERGENCY = "https://gohajj.id/api/jemaah/emergency";
	public static String URL_GET_FAMILY_PHONE = "https://gohajj.id/api/jemaah/family-phone?id=";
	public static String URL_GET_INBOX = "https://gohajj.id/api/jemaah/get-inbox?id=";
	public static String URL_GET_INBOX_VIEW = "https://gohajj.id/api/jemaah/get-inbox-view?id=";
	public static String URL_RATING= "https://gohajj.id/api/jemaah/rating";
	public static String URL_DOAKAN= "https://gohajj.id/api/jemaah/doakan";
	public static String URL_POI= "https://gohajj.id/api/jemaah/poi";
	public static String URL_GET_POI= "https://gohajj.id/api/jemaah/get-poi?id=";

	public static final String KEY_PHONE= "phone";
	public static final String KEY_MESSAGE= "message";
	public static final String KEY_PHONE_FAMILY1= "family_phone1";
	public static final String KEY_PHONE_FAMILY2= "family_phone2";
	public static final String KEY_PHONE_FAMILY3= "family_phone3";
	public static final String KEY_EMAIL_FAMILY1= "family_email1";
	public static final String KEY_EMAIL_FAMILY2= "family_email2";
	public static final String KEY_EMAIL_FAMILY3= "family_email3";
	public static final String KEY_TRAVEL_AGENT= "travel_agent";
	public static final String KEY_HOTEL_MEKKAH= "hotel_mekkah";
	public static final String KEY_HOTEL_MADINAH= "hotel_madinah";
	public static final String KEY_PEMBIMBING = "pembimbing";
	public static final String KEY_PASSPORT = "passport";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_DISTANCE = "distance";
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
	public static final String KEY_RATING = "rating";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_JENIS = "jenis";
	public static final String KEY_FILE = "file";
	public static final String KEY_ARAB = "arab";

	public static final String KEY_JUMLAH= "jumlah";
	public static final String KEY_CON_NO = "no";
	public static final String KEY_NAME = "name";
	public static final String KEY_FROM = "from";
	public static final String KEY_TO = "to";
	public static final String KEY_STATUS = "status";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_PENILAIID = "penilaiID";
	public static final String KEY_PEMBIMBINGID = "pembimbingID";
	public static final String KEY_NILAI_PEMBIMBING = "nilai_pembimbing";
	public static final String KEY_PEMIMPIN_TUR = "pemimpin_tur";
	public static final String KEY_PEMIMPIN_TURID = "pemimpin_turID";
	public static final String KEY_NILAI_PEMIMPIN_TUR = "nilai_pemimpin_tur";

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
