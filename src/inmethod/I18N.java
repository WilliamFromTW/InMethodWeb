package inmethod;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import inmethod.auth.AuthFactory;
import inmethod.commons.util.SystemConfig;
import inmethod.hr.Employee;

public class I18N {

	private static SystemConfig aChineseConfig = new SystemConfig("resources.webinfo",Locale.CHINA);
	private static SystemConfig aTaiwanConfig = new SystemConfig("resources.webinfo",Locale.TAIWAN);
	private static SystemConfig aVNConfig = new SystemConfig("resources.webinfo",new Locale("vi","VN"));
	private static SystemConfig aConfig = new SystemConfig("resources.webinfo",Locale.ENGLISH);
	
	public static String getChineseValue( String sKey) {
		return aChineseConfig.getValue(sKey);
	}
	
	public static String getTaiwanValue( String sKey) {
		try {
		return aTaiwanConfig.getValue(sKey);
		}catch(Exception ee) {
			ee.printStackTrace();
			return sKey;
		}
	}
	
	public static  String getVietNamValue(String sKey) {
		try {
		return aVNConfig.getValue(sKey);
		}catch(Exception ee) {
			ee.printStackTrace();
			return sKey;
		}
	}
	
	public static String getValue(String sKey) {
		try {
		 return aConfig.getValue(sKey);
		}catch(Exception ee) {
			ee.printStackTrace();
			return sKey;
		}
	}	
	public static String getValue(HttpServletRequest request, HttpServletResponse response,String sKey) {
		String sUserID = AuthFactory.getLoginUserID(request,response);
		//System.out.println("sUserID="+sUserID);
		Employee aEmployee =  AuthFactory. getEmployee(sUserID);
		//System.out.println("aEmployee="+aEmployee);
		if( aEmployee!=null) {
			//System.out.println("aEmployee="+aEmployee.getUserName()+","+aEmployee.getUserLanguage());
			if( aEmployee.getUserLanguage().equals("1")) // taiwan chinese
				return getTaiwanValue(sKey);
			else if(aEmployee.getUserLanguage().equals("2") )
				return aConfig.getValue(sKey);
			else  if(aEmployee.getUserLanguage().equals("3") )
				return getVietNamValue(sKey);
		}
	    return aConfig.getValue(sKey);
	}
	
	public static void main(String [] a) {
		System.out.println( "EN = "+ getValue ("Account") );
		System.out.println( "TW = "+ getTaiwanValue ("Account") );
		System.out.println( "VN = "+getVietNamValue ("Account") );
	}
	
}
