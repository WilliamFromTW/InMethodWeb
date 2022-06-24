package inmethod.backend.example;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import inmethod.backend.*;
import inmethod.db.DBConnectionManager;
import inmethod.jakarta.excel.CreateXLSX;
import inmethod.Global;
import inmethod.auth.AuthFactory;
import inmethod.auth.UserRoles;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.DataSet;
import inmethod.commons.rdb.SQLTools;
import inmethod.commons.util.HTMLConverter;

/**
 * 測試-從servlet收到資料,然後回傳json字傳的範例
 * 
 * @BackEnd
 * 
 *          <pre>
 * 
 * URL: inmethod/backend/example/BackEndGenEmployeeExcel
 * 
 * 功能: 查詢
 * 參數: (R)UserID  
 * 回傳: 
 * {"STATUS":"TRUE","MSG":"message","URL":"download url"}
 * 回傳: 
 * {"NoLogin":"FAIL"}  
 * {"NoPermission":"FAIL"}
 *          </pre>
 */
@WebServlet(name = "GenEmployeeExcel", urlPatterns = { "/inmethod/backend/example/GenEmployeeExcel" })
public class GenEmployeeExcel extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html; charset="
			+ inmethod.Global.getInstance().getEnvirenment("ENCODE");
	private static String FUNCTION_NAME =  "GenEmployeeExcel";

	// Initialize global variables
	public void init() throws ServletException {
	}

	// Process the HTTP Get request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(inmethod.Global.getInstance().getEnvirenment("ENCODE"));
		response.setContentType(CONTENT_TYPE);
		WebAuthentication aWebAuth = null;

		String sLoginUserID = null;
		PrintWriter out = response.getWriter();
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			try {
				aConn.setAutoCommit(true);
				aWebAuth = AuthFactory.getWebAuthentication(request, response);
				sLoginUserID = aWebAuth.getUserPrincipal();
				if (sLoginUserID == null || !aWebAuth.isLogin(sLoginUserID)) {
					out.println("{\"NoLogin\":\"FAIL\"}");
					out.flush();
					out.close();
					return;
				}
				
				/*
				Vector<String> aUserRoles=aWebAuth.getUserRoles(sUserID);
				for(String aUserRole:aUserRoles) {
					System.out.println("Role : "+aUserRole);
				}
				*/
				if (aWebAuth.getUserRoles(sLoginUserID) != null
						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sLoginUserID), FUNCTION_NAME)) {
					out.println("{\"NoPermission\":\"FAIL\"}");
					out.flush();
					out.close();
					return;
				}
				//System.out.println("has permission : "+aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME));

				String sUserID =  request.getParameter("UserID") ;
				System.out.println("UserID="+sUserID);
				Vector<String> aParameter = new Vector<String>();
				aParameter.add(sUserID);

				String sReturn = build(aParameter);
				
				out.println(sReturn);
				out.flush();
				out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
	public static void main(String ar[]) {
		try {
			String sUserID = "admin";
			Vector<String> aParameter = new Vector<String>();
			aParameter.add(sUserID);
			
			System.out.println( new GenEmployeeExcel().build(aParameter));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private String build(Vector<String> sParameter) {
	    String sSqlCmd;
	    CreateXLSX aFE;
	    String sReturn = "";
	    FileOutputStream aFO = null;
        ResultSet aRS = null;

	    try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection(); ){

	      String sAccount = sParameter.get(0);
	      if( sAccount==null || sAccount.equals("%" ) )
	            sSqlCmd = "select users.user_name,employee.user_name as local_name,employee.user_english_name,employee.user_mail,users.user_validate,concat(user_roles.role_name,'(', role_desc,')') as role_name ,concat(FUNCTION_INFO.FUNCTION_DESC ,'(',ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME,')') as function_desc " +
	          	                       " from USERS, USER_ROLES ,ROLE_LIST, EMPLOYEE,ROLE_AUTHORIZED_PERMISSION,FUNCTION_INFO  where FUNCTION_INFO.FUNCTION_NAME = ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME and role_list.role_name=ROLE_AUTHORIZED_PERMISSION.FUNCTION_ROLE and role_list.role_name=user_roles.role_name  and users.user_name=user_roles.user_name and users.user_name=employee.user_id "; 
		      else
		    	  sSqlCmd = "select users.user_name,employee.user_name as local_name,employee.user_english_name,employee.user_mail,users.user_validate,concat(user_roles.role_name,'(', role_desc,')') as role_name ,concat(FUNCTION_INFO.FUNCTION_DESC ,'(',ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME,')') as function_desc " +
	 	    	                        " from users, user_roles ,role_list, employee,ROLE_AUTHORIZED_PERMISSION,FUNCTION_INFO  where FUNCTION_INFO.FUNCTION_NAME = ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME and role_list.role_name=ROLE_AUTHORIZED_PERMISSION.FUNCTION_ROLE and role_list.role_name=user_roles.role_name and users.user_name ='"+sAccount +"' and users.user_name=user_roles.user_name and users.user_name=employee.user_id ";
	      sSqlCmd = sSqlCmd.toUpperCase();
	      System.out.println("sql cmd="+sSqlCmd);
	      aRS =  SQLTools.getInstance().Select(aConn, sSqlCmd);
	      String sReportName = FUNCTION_NAME; 
		  sReturn = sReportName+ "-" +  Global.getInstance().getCurrentDateString()+ ".xlsx";

	      aRS = SQLTools.getInstance().Select(aConn, sSqlCmd);
	      aFO = new FileOutputStream( BackEndGlobal.FILE_TEMP+sReturn);
	      aFE = new CreateXLSX(aFO);
	      aFE.setCurrentSheet();
	      aFE.setAutoSizeColumn(true);
	      aFE.setAutoWrapText(false);
	      // 不產生 result set 名稱
	      aFE.setPrintResultSetHeader(false);
	      // 報表簡易表頭
	      aFE.createHeader("員工資料 製表日:"+Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+(Calendar.getInstance().get(Calendar.DAY_OF_MONTH )),(short)0,(short)2,(short)0,(short)7);
		  // excel標題
	      Vector<String> aHeadCol = new Vector<String>( Arrays.asList("帳號","名字","英文名字","email","有效","角色","程式") );
	      DataSet aDS = new DataSet();
	      aDS.addData( aHeadCol );
	      aFE.calculateExcel(aDS);
	      aFE.calculateExcel(aRS);
	      aFE.buildExcel();
	      aFO.flush();aFO.close();
	      String sFileID = downloadDocument.writeDownloadInfo(FUNCTION_NAME,BackEndGlobal.FILE_TEMP+sReturn);
	      System.out.println("finished! msg = "+genMsg("TRUE", "download file id is "+sFileID,sFileID));
	      return genMsg("TRUE",sFileID ,sFileID);
	      
	    }catch(Exception ee) {
			return genMsg("FAIL",ee.getLocalizedMessage(),null);	    	
	    }
		
	}
	
	/**
	 * 
	 * @param sStatus "TRUE" , "FALSE"
	 * @param sMsg  
	 * @param sFileID download FILE UUID
	 * @return
	 */
	private String  genMsg(String sStatus,String sMsg,String sFileID) {		
	  return	"{\"STATUS\":\""+sStatus+"\",\"MSG\":\"" + sMsg + "\",\"URL\":\""+sFileID+"\"}";
	}
}
