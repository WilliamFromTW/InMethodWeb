package inmethod.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import inmethod.db.DBConnectionManager;
import inmethod.commons.rdb.SQLTools;
import inmethod.commons.util.HtmlMultiPart;
import inmethod.commons.util.MailTool;


public class testEmailGetFromEmployee {
	
	public static void main(String arg[]){
	  testEmailGetFromEmployee aTestGetDbDataThenEmail = new testEmailGetFromEmployee();
	  aTestGetDbDataThenEmail.checkDbAndSendEmail();
	}	
	public void checkDbAndSendEmail(){
	 try (Connection aAuthConn = DBConnectionManager.getWebDBConnection().getConnection();Connection aHrConn = DBConnectionManager.getWebDBConnection().getConnection();) {
	    try {
	      Vector aTo = new Vector();
		  aAuthConn.setAutoCommit(false);
		  aHrConn.setAutoCommit(false);
		  SQLTools.getInstance().setPrintLog(true);
		  ResultSet aRS = SQLTools.getInstance().Select(aHrConn, "select * from EMPLOYEE where USER_ID='pm'");
		  if(aRS!=null){
		    System.out.println("test");
		    while(aRS.next()){
		      aTo.add(aRS.getString("USER_MAIL"));
		      break;
		    }
		    aRS.close();
		  }
          if( aTo.size()>0){
            inmethod.Global.getInstance().sendMail ("william@kafeiou.pw",aTo,"世界你好 Hello World","中文測試");		    
          }
		  aAuthConn.commit();
          aHrConn.commit();
		  return;

		} catch (Exception ex) {
		  aAuthConn.rollback();
		  aHrConn.rollback();
		  ex.printStackTrace();
		}
	  } catch (Exception ee) {
		ee.printStackTrace();
	  }
	
	}
  


}	