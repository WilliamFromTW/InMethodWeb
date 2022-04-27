package inmethod.auth;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.util.*;
import java.io.*;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;

/** 
 * 角色執行權限維護API
 * @BackEnd
 * <pre>
 * URL: inmethod/auth/RoleAuthenticatedPermissionControlServlet
 * 
 * 功能: 查詢基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有基本資料
 *  [
 *    {"RoleName":"Data","FunctionName":"Data"}
 *    {"RoleName":"Data","FunctionName":"Data"}
 *  ]
 *  
 * 功能: 刪除基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)FunctionName=程式
 *      (R)RoleName=角色
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)FunctionName=程式
 *      (R)RoleName=角色
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 * 
 * 功能: 修改基本資料       
 * 參數: (R)FlowID=doUpdate
 *      (R)FunctionName=程式
 *      (R)RoleName=角色
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * </pre>
 * 
 */
@WebServlet(name = "RoleAuthenticatedPermissionControlServlet", urlPatterns = { "/inmethod/auth/RoleAuthenticatedPermissionControlServlet" })
public class RoleAuthenticatedPermissionControlServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private static String FUNCTION_NAME = null;
	private static final String FLOW_ID = "FlowID";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		doPost(req, resp);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType(CONTENT_TYPE);
		WebAuthentication aWebAuth = null;
		FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		String sFlowID = request.getParameter(FLOW_ID);
		String sUserID = null;
		PrintWriter out = response.getWriter();
		try (Connection aConn =DBConnectionManager.getWebDBConnection().getConnection() ) {
			try {
				aConn.setAutoCommit(false);
				aWebAuth = AuthFactory.getWebAuthentication(request, response); 
				sUserID = aWebAuth.getUserPrincipal();
				if (sUserID == null || !aWebAuth.isLogin(sUserID)) {
					response.setStatus(1000);
					out.println("{\"NoLogin\":\"FAIL\"}");
					aConn.rollback();
					out.flush();
					out.close();
					return;
				}
				if (aWebAuth.getUserRoles(sUserID) != null
						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {
					response.setStatus(1000);
					out.println("{\"NoPermission\":\"FAIL\"}");
					aConn.rollback();
					out.flush();
					out.close();
					return;
				}
				if (sFlowID == null)
					return;
	                RoleAuthorizedPermission aRoleAuthorizedPermission = aWebAuth.getAuthorizedFunctionInfo(sUserID, FUNCTION_NAME);
	                if( aRoleAuthorizedPermission==null){ 
	                  response.setStatus(1000);
	                  out.println("{\"NoPermission\":\"FAIL\"}");
		              out.flush();
		              out.close();
		              aConn.rollback();
	                }
				    else if (sFlowID.equalsIgnoreCase("doQuery") && aRoleAuthorizedPermission.getFunctionQuery().equalsIgnoreCase("Y")) {
					RoleAuthenticatedPermissionBeanFactory aBF = RoleAuthenticatedPermissionBeanFactory.getInstance(aConn);
					RoleAuthenticatedPermission aRoleAuthenticatedPermission = null;
					DataSet aDS = aBF.Query(aRoleAuthenticatedPermission, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aRoleAuthenticatedPermission = (RoleAuthenticatedPermission) aDS.getData();
						sReturn = sReturn + aRoleAuthenticatedPermission.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					aConn.commit();
					out.println(sReturn);
					} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					RoleAuthenticatedPermissionBeanFactory aBF = RoleAuthenticatedPermissionBeanFactory.getInstance(aConn);
					RoleAuthenticatedPermission aRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
                    aRoleAuthenticatedPermission.setRoleName( request.getParameter(RoleAuthenticatedPermission.FIELD_ROLE_NAME) );
                    aRoleAuthenticatedPermission.setFunctionName( request.getParameter(RoleAuthenticatedPermission.FIELD_FUNCTION_NAME) );

					try {
						if (aBF.update(aRoleAuthenticatedPermission) == 0) {
							response.setStatus(1000);
							out.println("{\"UpdateResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
							out.print("{\"UpdateResult\":\"OK\"}");
							aConn.commit();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						aConn.rollback();
						out.println("{\"UpdateResult\":\"FAIL\"}");
					}
					} else if (sFlowID.equalsIgnoreCase("doAdd")&& aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase("Y")) {
					RoleAuthenticatedPermissionBeanFactory aBF = RoleAuthenticatedPermissionBeanFactory.getInstance(aConn);
					RoleAuthenticatedPermission aRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
                    aRoleAuthenticatedPermission.setRoleName( request.getParameter(RoleAuthenticatedPermission.FIELD_ROLE_NAME) );
                    aRoleAuthenticatedPermission.setFunctionName( request.getParameter(RoleAuthenticatedPermission.FIELD_FUNCTION_NAME) );

					try {
						if (aBF.insert(aRoleAuthenticatedPermission) == 0) {
							response.setStatus(1000);
							out.println("{\"AddResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
						  out.print("{\"AddResult\":\"OK\"}");
						  aConn.commit();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"AddResult\":\"FAIL\"}");
						aConn.rollback();
					}
					} else if (sFlowID.equalsIgnoreCase("doDelete") && aRoleAuthorizedPermission.getFunctionDelete().equalsIgnoreCase("Y")) {
					RoleAuthenticatedPermissionBeanFactory aBF = RoleAuthenticatedPermissionBeanFactory.getInstance(aConn);
					RoleAuthenticatedPermission aRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
                    aRoleAuthenticatedPermission.setFunctionName( request.getParameter(RoleAuthenticatedPermission.FIELD_FUNCTION_NAME) );
                    aRoleAuthenticatedPermission.setRoleName( request.getParameter(RoleAuthenticatedPermission.FIELD_ROLE_NAME) );

					try {
						if (aBF.delete(aRoleAuthenticatedPermission) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
							aRoleAuthorizedPermission = new RoleAuthorizedPermission();
		                    aRoleAuthorizedPermission.setFunctionRole( request.getParameter(RoleAuthorizedPermission.FIELD_FUNCTION_ROLE) );
		                    aRoleAuthorizedPermission.setFunctionName( request.getParameter(RoleAuthorizedPermission.FIELD_FUNCTION_NAME) );
							try{
							  RoleAuthorizedPermissionBeanFactory.getInstance(aConn).delete(aRoleAuthorizedPermission);
							  out.print("{\"DeleteResult\":\"OK\"}");
							  aConn.commit();
							}catch(Exception ee){
								response.setStatus(1000);
								ee.printStackTrace();
								out.print("{\"DeleteResult\":\"FAIL\"}");
								aConn.rollback();
							}
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"DeleteResult\":\"FAIL\"}");
						aConn.rollback();
					}
				} else {
					// 當FlowID值不是我們想要的做甚麼事情
				}
				out.flush();
				out.close();
				return;
			} catch (Exception ex) {
				aConn.rollback();
				ex.printStackTrace();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
