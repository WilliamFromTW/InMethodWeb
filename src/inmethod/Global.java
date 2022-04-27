package inmethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import inmethod.commons.util.HtmlMultiPart;
import inmethod.commons.util.MailTool;
import inmethod.commons.util.SystemConfig;

public class Global {

	Context env;
	static Global global;
	
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
			return aParseWebXml.lookup("WebContent/WEB-INF/web.xml",sKey);
		}
	}
	
}