package inmethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import inmethod.auth.AuthFactory;
import inmethod.auth.RoleAuthorizedPermission;
import inmethod.auth.UserRoles;
import inmethod.auth.Users;
import inmethod.commons.rdb.DataSet;
import inmethod.commons.util.HtmlMultiPart;
import inmethod.commons.util.MailTool;
import inmethod.commons.util.SystemConfig;
import inmethod.hr.Employee;

public class Global {

	Context env;
	static Global global;
	private static HashMap<String, Vector <RoleAuthorizedPermission> > aAllUserAuthenticatedFunctionInfoList = null;
	
	private Global() {
		try {
			env = (Context)new InitialContext().lookup("java:comp/env");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	};
	
	public static Global getInstance(){
		if(global==null){
		  global = new Global();
		}
		return global;
	}
	
	public static String getCurrentDateString() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
	}
		
	
	/**
	 * 寄發email
	 * @param aFrom 由誰寄發
	 * @param aTo  記給誰
	 * @param sSubject 標題
	 * @param sMessage 內容
	 */
	public void sendMail(String aFrom,Vector<String> aTo,String sSubject,String sMessage){
      try{
			    // cc
			    Vector cc = null;
			    
			    // bcc
			    Vector bcc = null;
			    
			    // email session
			    javax.mail.Session session = MailTool.buildSession( inmethod.Global.getInstance().getEnvirenment("MAIL_SERVER_HOST") );
			    
			    HtmlMultiPart aHMP= new HtmlMultiPart(inmethod.Global.getInstance().getEnvirenment("ENCODE"));
			    aHMP.setContent(sMessage);
//			    aHMP.buildAttachment(file);
			    MailTool.setEncode(inmethod.Global.getInstance().getEnvirenment("ENCODE"));
			    MailTool.mailSend(session,aTo,cc,bcc,aFrom,sSubject,aHMP.getMultipart() ,null,null);
		    }catch(Exception ee){
		      ee.printStackTrace();	
		    }  
	}	  	
	
	/**
	 * 
	 * @param sKey get variable value
	 * @return
	 */
	public String getEnvirenment(String sKey){
		try {
			return (String) env.lookup(sKey);
		} catch (Exception e) {
			e.printStackTrace();
			parseWebXml aParseWebXml = new parseWebXml();
			String sReturn = aParseWebXml.lookup("WebContent/WEB-INF/web.xml",sKey);
			if( sReturn==null ) {
				sReturn =  aParseWebXml.lookup("WEB-INF/web.xml",sKey);
				System.out.println("try to read tomcat xml file : "+sReturn );
				return sReturn;
			}
			else return sReturn;
		}
	}

	public static String getCatalogNameByID(int iID) {
		if( iID==1)
		return  "台灣(TW)";
		else if( iID==2) 
			return "苏州(SU)";
		else if( iID==3)
			return "Việt Nam(VN)";
		else 
			return "unknown";

	}
	public static boolean checkPermission(String sUserID, String sFunction) {
		if( aAllUserAuthenticatedFunctionInfoList==null )
			refreshAuthenticatedFunctionInfoList();
		  Vector<RoleAuthorizedPermission> aRoleAuthorizedPermission;
	       try{
      	    //aRoleAuthorizedPermission = aWebaWebAuth.getAuthorizedFunctionInfo(aWebaWebAuth.getUserPrincipal());
     	    aRoleAuthorizedPermission = aAllUserAuthenticatedFunctionInfoList.get(sUserID);
     	   // System.out.println("aRoleAuthorizedPermission="+aRoleAuthorizedPermission);
     	    if( aRoleAuthorizedPermission!=null)
     	    for(RoleAuthorizedPermission aFunInfo:aRoleAuthorizedPermission){
     	    	if( aFunInfo.getFunctionName().equalsIgnoreCase(sFunction) )
     	    	return true;
     	    }
       }catch(Exception ee){
      	 ee.printStackTrace();
       }
	return false;	
	}
	public static void refreshAuthenticatedFunctionInfoList() {
		System.out.println("refreshAuthenticatedFunctionInfoList ");
		if( aAllUserAuthenticatedFunctionInfoList!=null ) {
			
		    for (Entry<String, Vector<RoleAuthorizedPermission>> entry : aAllUserAuthenticatedFunctionInfoList.entrySet()) {
		    	entry.getValue().clear();
	        }
		    aAllUserAuthenticatedFunctionInfoList.clear();
		}
		aAllUserAuthenticatedFunctionInfoList = null;
		
		if( aAllUserAuthenticatedFunctionInfoList==null) {
			aAllUserAuthenticatedFunctionInfoList  = new HashMap<String, Vector <RoleAuthorizedPermission> >();
		   DataSet aAllUsers = AuthFactory.getAllUsersByValidated(true);
		 while( aAllUsers!=null && aAllUsers.next()) {
			try {
				Users aTempUsers = (Users) aAllUsers.getData();
				//System.out.println("aTempUsers name="+aTempUsers.getUserName());
				aAllUserAuthenticatedFunctionInfoList.put(aTempUsers.getUserName(),  AuthFactory.getWebAuthentication(null, null).getAuthorizedFunctionInfo(aTempUsers.getUserName()));
				
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		   }
		}
//		getAuthorizedFunctionInfo
	}
	
	public static String  getJsonAuthorizedFunctionInfoList(String sUserID) {
		if( aAllUserAuthenticatedFunctionInfoList==null )
			refreshAuthenticatedFunctionInfoList();
	      String sJson = "[";
	      Vector<RoleAuthorizedPermission> aRoleAuthorizedPermission;
	       try{
       	    //aRoleAuthorizedPermission = aWebaWebAuth.getAuthorizedFunctionInfo(aWebaWebAuth.getUserPrincipal());
      	    aRoleAuthorizedPermission = aAllUserAuthenticatedFunctionInfoList.get(sUserID);
      	   // System.out.println("aRoleAuthorizedPermission="+aRoleAuthorizedPermission);
      	    if( aRoleAuthorizedPermission!=null)
      	    for(RoleAuthorizedPermission aFunInfo:aRoleAuthorizedPermission){
      	    	if( aFunInfo.getFunctionVisible().equals("Y"))
      	    	  sJson = sJson + aFunInfo.toJson()+",";
      	    }
        }catch(Exception ee){
       	 ee.printStackTrace();
        }
          return sJson.substring(0, sJson.length()-1)+"]";
	}
	
}