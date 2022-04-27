package inmethod.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.sql.*;

import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.*;

/**
 * 認證類API(登入,變更密碼,所有登入者名稱)
 * 
 
 * @BackEnd
 * <pre>
 * URL: inmethod/auth/AuthenticationControlServlet
 * 功能: 登入
 * 參數: 
 *    (R)FlowID=Login
 *    (R)j_username=帳號名稱
 *    (R)j_password=密碼
 * 回傳:
 *   {"Login":"OK"} 登入成功
 *   {"Login":"FAIL"} 登入失敗
 *
 * 功能: 修改密碼
 * 參數: 
 *    (R)FlowID=Update
 *    (R)OldPassword=舊密碼
 *    (R)NewPassword=新密碼
 * 回傳:
 *   {"Update":"OK"} 登入成功
 *   {"Update":"FAIL"} 登入失敗
 *   
 * 功能: 取得所有使用者之(名稱,帳號)
 * 參數:
 *    (R)FlowID=getAllUsers
 * 回傳:
 *   [
 *     {"Name":"癮方法","Value":"admin"},
 *     {"Name":"william chen","Value":"920405"}
 *   ]  
 *   
 * 功能: 取得登入者對某程式之功能授權(新增刪除修改查詢)
 * 參數:
 *   (R)Flow=getAuthorizedFunctionInfo
 *   (R)FunctionName=程式名稱
 * 回傳:  
 *   {"Query":"false","Update":"false","Insert":"false","Delete":"false"}
 *
 * 功能: 取得地區分類
 * 參數:
 *   (R)Flow=getCatalogSelection
 *   (R)Catalog=地區分類
 * 回傳:  
 *  [{"Name":"","Value": ""},{"Name": "台灣(TW)","Value": "1"},{"Name": "苏州(SU)","Value": "2"},{"Name": "Việt Nam(VN)","Value": "3"}]
 *   
 * 功能: 取得News地區分類
 * 參數:
 *   (R)Flow=getNewsCatalogSelection
 *   (R)Catalog=地區分類
 * 回傳:  
 *  [{"Name":"","Value": ""},{"Name": "台灣(TW)","Value": "1"},{"Name": "苏州(SU)","Value": "2"},{"Name": "Việt Nam(VN)","Value": "3"}]
 *   
 *   
 *   
 * </pre>
 *
 *
 */
@WebServlet(name = "AuthenticationControlServlet", urlPatterns = { "/inmethod/auth/AuthenticationControlServlet" })
public class AuthenticationControlServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html; charset=" + inmethod.Global.getInstance().getEnvirenment("ENCODE");
	private static final String FORM_USER_NAME = "j_username";
	private static final String FORM_USER_PASSWORD = "j_password";
	private static final String FORM_USER_NEW_PASSWORD = "NewPassword";
	private static final String FORM_USER_OLD_PASSWORD = "OldPassword";
	private static String FUNCTION_NAME = null;// "AuthenticationControlServlet";
	private static final String FLOW_ID = "FlowID";

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
		FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		String sUserID = request.getParameter(FORM_USER_NAME);
		String sPassword = request.getParameter(FORM_USER_PASSWORD);
		String sFlowID = request.getParameter(FLOW_ID);
		PrintWriter out = response.getWriter();

		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			try {
				aConn.setAutoCommit(false);
				aWebAuth = AuthFactory.getWebAuthentication(request, response);
				if (sFlowID == null) return;

				if (sFlowID.equalsIgnoreCase("Update")) {
					sUserID = aWebAuth.getUserPrincipal();
					if (aWebAuth.isLogin(sUserID)) {
						if (aWebAuth.setUserPassword(sUserID, request.getParameter(FORM_USER_OLD_PASSWORD),
								request.getParameter(FORM_USER_NEW_PASSWORD))) {
							out.println("{\"Update\":\"OK\"}");
							aConn.commit();
						} else {
							out.println("{\"Update\":\"FAIL\"}");
							response.setStatus(1000);
							aConn.rollback();
						}
					} else {
						out.println("{\"Login\":\"FAIL\"}");
						response.setStatus(1000);
						aConn.rollback();
					}
					// commit
					aConn.commit();
					out.flush();
					return;

				} else if (sFlowID.equalsIgnoreCase("Login")) {
					//System.out.println("userID=" + sUserID);
					if (aWebAuth.login(sUserID, sPassword)) {
						if (aWebAuth.getUserRoles(sUserID) != null
								&& aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {
							
							out.println("{\"Login\":\"OK\"}");
						} else {
							out.println("{\"Login\":\"FAIL\"}");
							response.setStatus(1000);
						}
					} else {
						out.println("{\"Login\":\"FAIL\"}");
						response.setStatus(1000);
					}
				} else if (sFlowID.equalsIgnoreCase("Logout")) {
					if (aWebAuth.logout(null)) {
						out.println("{\"Logout\":\"OK\"}");
					} else {
						out.println("{\"Logout\":\"FAIL\"}");
						response.setStatus(1000);						
					}
				} else if (sFlowID.equalsIgnoreCase("getAllUsers")) {
					//System.out.println(aWebAuth.getUserPrincipal());
					if (aWebAuth.getUserPrincipal()==null || aWebAuth.getUserPrincipal().equals("") ){
					  response.setStatus(1000);
					  out.println("{\"Logout\":\"FAIL\"}");
					}
					else{
					  Users[] aUsers = aWebAuth.getAllUsers();
					  String sReturn = "";
					  for(Users aUser:aUsers){
						  sReturn = sReturn + "{\"Name\":\""+aUser.getUserName()+"("+aWebAuth.getUserNameByUserID(aUser.getUserName()) +")\",\"Value\":\""+aUser.getUserName()+"\"},";
					  }

					  if(sReturn.equals("")) sReturn = "[{\"Name\":\"\",\"Value\":\"\"}]";
					  else{
						  sReturn = "[{\"Name\":\"\",\"Value\":\"\"}," + sReturn.substring(0, sReturn.length() - 1) + "]";
					  }
				//	  System.out.println(sReturn);
					  out.println(sReturn);
					}
				}else if (sFlowID.equalsIgnoreCase("getAuthorizedFunctionInfo")) {
					String sFunctionName = request.getParameter("FunctionName");
					sUserID = aWebAuth.getUserPrincipal(); 
					System.out.println("getAuthorizedFunctionInfo , sFuncaionName="+sFunctionName+","+sUserID);
					RoleAuthorizedPermission aRoleAuthorizedPermission = aWebAuth.getAuthorizedFunctionInfo(sUserID, sFunctionName);
					//System.out.println( "aRoleAuthorizedPermission="+aRoleAuthorizedPermission );
					//System.out.println("Delete="+ConvertYesNoToBooleanString(aRoleAuthorizedPermission.getFunctionDelete()));
					if(aRoleAuthorizedPermission!=null ){
					  out.println("{\"Query\":\""+ConvertYesNoToBooleanString(aRoleAuthorizedPermission.getFunctionQuery())+"\",\"Update\":\""+ConvertYesNoToBooleanString(aRoleAuthorizedPermission.getFunctionUpdate())+"\",\"Insert\":\""+ConvertYesNoToBooleanString(aRoleAuthorizedPermission.getFunctionInsert())+"\",\"Delete\":\""+ConvertYesNoToBooleanString(aRoleAuthorizedPermission.getFunctionDelete())+"\"}");	
					}else{
					  out.println("{\"Query\":\"false\",\"Update\":\"false\",\"Insert\":\"false\",\"Delete\":\"false\"}");	
					}
				} else if (sFlowID.equalsIgnoreCase("getCatalogSelection") ) {
					String sReturn = null;
					String sCatalog =  request.getParameter("Catalog");
					if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
						 sReturn = "[{\"Name\":\"\",\"Value\": \"\"}," ;
						if( sCatalog.equals("1") )
								sReturn = sReturn +  "{\"Name\": \"台灣(TW)\",\"Value\": \"1\"}";
						else if( sCatalog.equals("2") )
							sReturn = sReturn +  "{\"Name\": \"苏州(SU)\",\"Value\": \"2\"}";
						else if( sCatalog.equals("3") )
							sReturn = sReturn +  "{\"Name\": \"Việt Nam(VN)\",\"Value\": \"3\"}";
				//		System.out.println(sReturn+"]");
						out.print(sReturn+"]");
					}
					else {
						sReturn = "[{\"Name\":\"\",\"Value\": \"\"}" +
                                            ","+
                                            "{\"Name\": \"台灣(TW)\",\"Value\": \"1\"}"+
                                            ","+
                                            "{\"Name\": \"苏州(SU)\",\"Value\": \"2\"}"+
                                            ","+
                                            "{\"Name\": \"Việt Nam(VN)\",\"Value\": \"3\"}]";
	                    aConn.commit();
	              //      System.out.println(sReturn);
	                    out.print(sReturn);
					}				
    				out.flush();
    				out.close();
    				return;
				}else if (sFlowID.equalsIgnoreCase("getNewsCatalogSelection") ) {
					String sReturn = null;
					String sCatalog =  request.getParameter("Catalog");
						if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
							 sReturn = "[{\"Name\":\"\",\"Value\": \"\"}," ;
							if( sCatalog.equals("11") )
									sReturn = sReturn +  "{\"Name\": \"一般公告(TW1)\",\"Value\": \"11\"}";
							else if( sCatalog.equals("12") )
								sReturn = sReturn +  "{\"Name\": \"綜合公告(TW2)\",\"Value\": \"12\"}";
							else if( sCatalog.equals("21") )
								sReturn = sReturn +  "{\"Name\": \"一般公告(SU1)\",\"Value\": \"21\"}";
							else if( sCatalog.equals("22") )
								sReturn = sReturn +  "{\"Name\": \"綜合公告(SU2)\",\"Value\": \"22\"}";
							else if( sCatalog.equals("31") )
								sReturn = sReturn +  "{\"Name\": \"Việt Nam1(VN1)\",\"Value\": \"31\"}";
							else if( sCatalog.equals("32") )
								sReturn = sReturn +  "{\"Name\": \"Việt Nam2(VN2)\",\"Value\": \"32\"}";
						//	System.out.println(sReturn+"]");
							out.print(sReturn+"]");
						}
						else {
							sReturn = "[{\"Name\":\"\",\"Value\": \"\"}" +
	                                            ","+
	                                            "{\"Name\": \"一般公告(TW1)\",\"Value\": \"11\"}"+
	                                            ","+
	                                            "{\"Name\": \"綜合公告(TW2)\",\"Value\": \"12\"}"+
	                                            ","+
	                                            "{\"Name\": \"一般公告(SU1)\",\"Value\": \"21\"}"+
	                                            ","+
	                                            "{\"Name\": \"綜合公告(SU2)\",\"Value\": \"22\"}"+
	                                            ","+
	                                            "{\"Name\": \"Việt Nam1(VN1)\",\"Value\": \"31\"}" +
	                                            ","+
	                                            "{\"Name\": \"Việt Nam2(VN2)\",\"Value\": \"32\"}]";
		                    aConn.commit();
		              //      System.out.println(sReturn);
		                    out.print(sReturn);
						}				
	    				out.flush();
	    				out.close();
	    				return;
					
				}else if (sFlowID.equalsIgnoreCase("getDocCatalogSelection") ) {
					String sReturn = null;
					String sCatalog =  request.getParameter("Catalog");
						if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
							 sReturn = "[{\"Name\":\"\",\"Value\": \"\"}," ;
							if( sCatalog.equals("11") )
									sReturn = sReturn +  "{\"Name\": \"規章辦法(TW1)\",\"Value\": \"11\"}";
							else if( sCatalog.equals("12") )
								sReturn = sReturn +  "{\"Name\": \"空白表格(TW2)\",\"Value\": \"12\"}";
							else if( sCatalog.equals("21") )
								sReturn = sReturn +  "{\"Name\": \"規章辦法(SU1)\",\"Value\": \"21\"}";
							else if( sCatalog.equals("22") )
								sReturn = sReturn +  "{\"Name\": \"空白表格(SU2)\",\"Value\": \"22\"}";
							else if( sCatalog.equals("31") )
								sReturn = sReturn +  "{\"Name\": \"quy định(VN1)\",\"Value\": \"31\"}";
							else if( sCatalog.equals("32") )
								sReturn = sReturn +  "{\"Name\": \"tấm(VN2)\",\"Value\": \"32\"}";
					//		System.out.println(sReturn+"]");
							out.print(sReturn+"]");
						}
						else {
							sReturn = "[{\"Name\":\"\",\"Value\": \"\"}" +
	                                            ","+
	                                            "{\"Name\": \"規章辦法(TW1)\",\"Value\": \"11\"}"+
	                                            ","+
	                                            "{\"Name\": \"空白表格(TW2)\",\"Value\": \"12\"}"+
	                                            ","+
	                                            "{\"Name\": \"規章辦法(SU1)\",\"Value\": \"21\"}"+
	                                            ","+
	                                            "{\"Name\": \"空白表格(SU2)\",\"Value\": \"22\"}"+
	                                            ","+
	                                            "{\"Name\": \"quy định(VN1)\",\"Value\": \"31\"}" +
	                                            ","+
	                                            "{\"Name\": \"tấm(VN2)\",\"Value\": \"32\"}]";
		                    aConn.commit();
		           //         System.out.println(sReturn);
		                    out.print(sReturn);
						}				
	    				out.flush();
	    				out.close();
	    				return;
					
				}else if (sFlowID.equalsIgnoreCase("getFileTypeSelection")) {
				  String  sReturn =" [{"+
                       "\"Name\":\"\",  "+
                       "\"Value\":\"\" "+
                       "}, { "+
                       "\"Name\": \"Attach(附件)\",\"Value\":\"1\"}]";
				  //System.out.println("asdf"+sReturn);
				   out.print(sReturn);
				}
				// commit
				aConn.commit();
				out.flush();
				out.close();
				return;
			} catch (Exception eee) {
				eee.printStackTrace();
				//System.out.println(eee.getMessage());
				aConn.rollback();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    private String ConvertYesNoToBooleanString(String s){
      if(s==null || s.equals("")||s.equalsIgnoreCase("N") ) return "false"; 
      else if(s.equalsIgnoreCase("Y") ) return "true";
      else return "false";
    }
	// Clean up resources
	public void destroy() {
	}
}