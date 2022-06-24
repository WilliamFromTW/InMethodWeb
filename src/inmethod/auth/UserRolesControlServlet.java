package inmethod.auth;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.util.*;
import java.io.*;
import inmethod.auth.adapter.*;
import inmethod.auth.inter_face.WebAuthentication;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;

/** 
 * 使用者擁有之角色維護API
 * @BackEnd
 * <pre>
 * URL: inmethod/auth/UserRolesControlServlet
 * 
 * 功能: 查詢基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有基本資料
 *  [
 *    {"UserName":"Data1","RoleName":"Data1"}
 *    {"UserName":"Data2","RoleName":"Data2"}
 *  ]
 *  
 * 功能: 刪除基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)RoleName=角色
 *      (R)UserName=使用者名稱
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)RoleName=角色
 *      (R)UserName=使用者名稱
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 * 
 * 功能: 修改基本資料       
 * 參數: (R)FlowID=doUpdate
 *      (R)RoleName=角色
 *      (R)UserName=使用者名稱
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * 功能: 取得使用者所有角色
 * 參數: (R)FlowID=getUserRoles
 * 回傳: 
 *  [
 *    {"UserName":"Data1","RoleName":"Data1"}
 *    {"UserName":"Data2","RoleName":"Data2"}
 *  ]
 * </pre>
 * 
 */
@WebServlet(name = "UserRolesControlServlet", urlPatterns = { "/inmethod/auth/UserRolesControlServlet" })
public class UserRolesControlServlet extends HttpServlet {
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
					out.flush();
					out.close();
					aConn.rollback();
					return;
				}
				if (sFlowID.equalsIgnoreCase("getUserRoles")) {
					UserRolesBeanFactory aBF = UserRolesBeanFactory.getInstance(aConn);
					UserRoles aUserRoles = null;
					DataSet aDS = aBF.Query(aUserRoles, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aUserRoles = (UserRoles) aDS.getData();
						sReturn = sReturn + aUserRoles.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
					out.flush();
		            out.close();
		            aConn.commit();
		            return;
				}	
				if (!aWebAuth.checkPermission(sUserID, FUNCTION_NAME)) {
					response.setStatus(1000);
					out.println("{\"NoPermission\":\"FAIL\"}");
					out.flush();
					out.close();
					aConn.rollback();
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
					UserRolesBeanFactory aBF = UserRolesBeanFactory.getInstance(aConn);
					UserRoles aUserRoles = null;
					DataSet aDS = aBF.Query(aUserRoles, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aUserRoles = (UserRoles) aDS.getData();
						sReturn = sReturn + aUserRoles.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					UserRolesBeanFactory aBF = UserRolesBeanFactory.getInstance(aConn);
					UserRoles aUserRoles = new UserRoles();
                    aUserRoles.setUserName( request.getParameter(UserRoles.FIELD_USER_NAME) );
                    aUserRoles.setRoleName( request.getParameter(UserRoles.FIELD_ROLE_NAME) );

					try {
						if (aBF.update(aUserRoles) == 0) {
							response.setStatus(1000);
							out.println("{\"UpdateResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
							out.print("{\"UpdateResult\":\"OK\"}");
							aConn.commit();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"UpdateResult\":\"FAIL\"}");
						aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doAdd")&& aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase("Y")) {
					UserRolesBeanFactory aBF = UserRolesBeanFactory.getInstance(aConn);
					UserRoles aUserRoles = new UserRoles();
                    aUserRoles.setUserName( request.getParameter(UserRoles.FIELD_USER_NAME) );
                    aUserRoles.setRoleName( request.getParameter(UserRoles.FIELD_ROLE_NAME) );

					try {
						if (aBF.insert(aUserRoles) == 0) {
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
					UserRolesBeanFactory aBF = UserRolesBeanFactory.getInstance(aConn);
					UserRoles aUserRoles = new UserRoles();
                    aUserRoles.setRoleName( request.getParameter(UserRoles.FIELD_ROLE_NAME) );
                    aUserRoles.setUserName( request.getParameter(UserRoles.FIELD_USER_NAME) );

					try {
						if (aBF.delete(aUserRoles) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
							out.print("{\"DeleteResult\":\"OK\"}");
							aConn.commit();
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
