package inmethod.backend.example;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import com.google.gson.JsonArray;

import inmethod.backend.*;
import inmethod.db.DBConnectionManager;
import inmethod.jakarta.excel.CreateXLSX;
import inmethod.auth.AuthFactory;
import inmethod.auth.UserRoles;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.DataSet;
import inmethod.commons.rdb.SQLTools;
import inmethod.commons.util.HTMLConverter;
import inmethod.commons.util.JsonUtil;

/**
 * 測試-從servlet收到資料,然後回傳json字傳的範例
 * 
 * @BackEnd
 * 
 *          <pre>
 * 
 * URL: inmethod/backend/BackEndGenEmployeeTableServlet
 * 
 * 功能: 查詢
 * 參數: (R)UserID  
 * 回傳: 
 * {"STATUS":"TRUE","TABLE_NAME":"html table name","JSON":[{}]}
 * 回傳: 
 * {"NoLogin":"FAIL"}  
 * {"NoPermission":"FAIL"}
 *          </pre>
 */
@WebServlet(name = "GenEmployeeTable", urlPatterns = { "/inmethod/backend/example/GenEmployeeTable" })
public class GenEmployeeTable extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html; charset="
			+ inmethod.Global.getInstance().getEnvirenment("ENCODE");
	private static String FUNCTION_NAME = "GenEmployeeTable";

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

		String sUserID = null;
		PrintWriter out = response.getWriter();
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			try {
				aConn.setAutoCommit(true);
				aWebAuth = AuthFactory.getWebAuthentication(request, response);
				sUserID = aWebAuth.getUserPrincipal();
				if (sUserID == null || !aWebAuth.isLogin(sUserID)) {
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
				if (aWebAuth.getUserRoles(sUserID) != null
						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {
					out.println("{\"NoPermission\":\"FAIL\"}");
					out.flush();
					out.close();
					return;
				}
				//System.out.println("has permission : "+aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME));

				sUserID =  request.getParameter("UserID") ;
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
			
			System.out.println( new GenEmployeeTable().build(aParameter));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private String build(Vector<String> sParameter) {
	    String sSqlCmd;
	  
        ResultSet aRS = null;
        String sFieldName = null;
	    try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection(); ){

	      String sAccount = sParameter.get(0);
	      System.out.println("sAccount="+sAccount);
	      if( sAccount==null || sAccount.equals("%" ) )
            sSqlCmd = "select users.user_name,employee.user_name as local_name,employee.user_english_name,employee.user_mail,users.user_validate,concat(user_roles.role_name,'(', role_desc,')') as role_name ,concat(FUNCTION_INFO.FUNCTION_DESC ,'(',ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME,')') as function_desc " +
          	                       " from users, user_roles ,role_list, employee,ROLE_AUTHORIZED_PERMISSION,FUNCTION_INFO  where FUNCTION_INFO.FUNCTION_NAME = ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME and role_list.role_name=ROLE_AUTHORIZED_PERMISSION.FUNCTION_ROLE and role_list.role_name=user_roles.role_name  and users.user_name=user_roles.user_name and users.user_name=employee.user_id "; 
	      else
	    	  sSqlCmd = "select users.user_name,employee.user_name as local_name,employee.user_english_name,employee.user_mail,users.user_validate,concat(user_roles.role_name,'(', role_desc,')') as role_name ,concat(FUNCTION_INFO.FUNCTION_DESC ,'(',ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME,')') as function_desc " +
 	    	                        " from users, user_roles ,role_list, employee,ROLE_AUTHORIZED_PERMISSION,FUNCTION_INFO  where FUNCTION_INFO.FUNCTION_NAME = ROLE_AUTHORIZED_PERMISSION.FUNCTION_NAME and role_list.role_name=ROLE_AUTHORIZED_PERMISSION.FUNCTION_ROLE and role_list.role_name=user_roles.role_name and users.user_name ='"+sAccount +"' and users.user_name=user_roles.user_name and users.user_name=employee.user_id"; 

	      System.out.println("sql cmd="+sSqlCmd);
	      
	      aRS = SQLTools.getInstance().Select(aConn, sSqlCmd);
	      
		  // excel標題, data-field對應到sql statement,大小寫要一致
	      sFieldName = "<th data-sortable='true' data-valign='middle' data-align='center' data-field='user_name'>帳號</th>"+
	    		              "<th data-sortable='true' data-valign='middle' data-align='center' data-field='local_name' data-sortable='true' data-valign='middle' data-align='center'>名字</th>" +
	    		              "<th data-sortable='true' data-valign='middle' data-align='center' data-field='user_english_name'>英文名字</th>" +
	    		              "<th data-sortable='true' data-valign='middle' data-align='center' data-field='user_mail'>email</th>" +
	    		              "<th data-sortable='true' data-valign='middle' data-align='center' data-field='user_validate'>有效</th>" +
                              "<th data-sortable='true' data-valign='middle' data-align='center' data-field='role_name'>角色</th>" +
                              "<th data-sortable='true' data-valign='middle' data-align='left' data-field='function_desc'>程式</th>";
	      
	       
	      JsonArray aJA = JsonUtil.ResultSetToJsonArray(aRS);
	      //或者 JsonArray aJA = JsonUtil  .ResultSetToJsonArraySkipTheSameColValue(aRS);
	      
	      System.out.println("finished! msg = "+genMsg("TRUE",sFieldName, aJA.toString() ));
	      
	      return  genMsg("TRUE",sFieldName,aJA.toString() );
	      
	    }catch(Exception ee) {
			return genMsg("FAIL",sFieldName,ee.getLocalizedMessage());	    	
	    }
		
	}
	
	/**
	 * 
	 * @param sStatus "TRUE" , "FALSE"
	 * @param sMsg  
	 * @param sReturn download url
	 * @return
	 */
	private String  genMsg(String sStatus,String sFieldName,String sJson) {
		
	  return	"{\"STATUS\":\""+sStatus+"\",\"FIELD_NAME\":\""+sFieldName +"\",\"JSON\":" + sJson + "}";
	}
}
