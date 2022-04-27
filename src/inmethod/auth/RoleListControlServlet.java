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
 * 角色列表維護API
 * @BackEnd
 * <pre>
 * URL: inmethod/auth/RoleListControlServlet
 * 
 * 功能: 查詢基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有基本資料
 *  [
 *    {"RoleName":"Data","RoleDesc":"Data","RoleCatalogId":"Data"}
 *    {"RoleName":"Data","RoleDesc":"Data","RoleCatalogId":"Data"}
 *  ]
 *  
 * 功能: 刪除基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)RoleName=角色
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)RoleName=角色
 *      (O)RoleDesc=名稱
 *      (O)RoleCatalogId=角色分類ID
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 * 
 * 功能: 修改基本資料       
 * 參數: (R)FlowID=doUpdate
 *      (R)RoleName=角色
 *      (O)RoleDesc=名稱
 *      (O)RoleCatalogId=角色分類ID
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * 功能: 角色列表(html select option)       
 * 參數: (R)FlowID=getJsGridSelectOptions
 * 回傳:
 *      [
 *        {"Name":"","Value":""},
 *        {"Name":"admin","Value":"系統管理者"},
 *        {"Name":"hr","Value":"人事管理者"}
 *      ]
 * </pre>
 * 
 */
@WebServlet(name = "RoleListControlServlet", urlPatterns = { "/inmethod/auth/RoleListControlServlet" })
public class RoleListControlServlet extends HttpServlet {
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
				if (sFlowID == null)
					return;
				 else if(sFlowID.equalsIgnoreCase("getJsGridSelectOptions")){
						RoleListBeanFactory aBF = RoleListBeanFactory.getInstance(aConn);
						RoleList aRoleList = null;
						DataSet aDS = aBF.Query(aRoleList, "ROLE_CATALOG_ID");
						String sReturn = "";
						while (aDS != null && aDS.next()) {
							aRoleList = (RoleList) aDS.getData();
							sReturn = sReturn + "{\"Name\":\""+aRoleList.getRoleDesc()+"\",\"Value\":\""+aRoleList.getRoleName()+"\"},";
						}
						if (sReturn.length() == 0) {
							sReturn = "[{\"Name\":\"\",\"Value\":\"\"}]";
						} else {
							sReturn = "[{\"Name\":\"\",\"Value\":\"\"}," + sReturn.substring(0, sReturn.length() - 1) + "]";
						}
						
						out.println(sReturn);
						out.flush();
						out.close();
						aConn.commit();
						return;
					}				
				if (aWebAuth.getUserRoles(sUserID) != null
						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {
					response.setStatus(1000);
					out.println("{\"NoPermission\":\"FAIL\"}");
					out.flush();
					out.close();
					aConn.rollback();
					return;
				}
                RoleAuthorizedPermission aRoleAuthorizedPermission = aWebAuth.getAuthorizedFunctionInfo(sUserID, FUNCTION_NAME);
                if( aRoleAuthorizedPermission==null){ 
                  response.setStatus(1000);
                  out.println("{\"NoPermission\":\"FAIL\"}");
	              out.flush();
	              out.close();
	              aConn.rollback();
                }
			    else if (sFlowID.equalsIgnoreCase("doQuery") && aRoleAuthorizedPermission.getFunctionQuery().equalsIgnoreCase("Y")) {
					RoleListBeanFactory aBF = RoleListBeanFactory.getInstance(aConn);
					RoleList aRoleList = null;
					DataSet aDS = aBF.Query(aRoleList, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aRoleList = (RoleList) aDS.getData();
						sReturn = sReturn + aRoleList.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					RoleListBeanFactory aBF = RoleListBeanFactory.getInstance(aConn);
					RoleList aRoleList = new RoleList();
                    aRoleList.setRoleName( request.getParameter(RoleList.FIELD_ROLE_NAME) );
                    aRoleList.setRoleDesc( request.getParameter(RoleList.FIELD_ROLE_DESC) );
                    aRoleList.setRoleCatalogId( request.getParameter(RoleList.FIELD_ROLE_CATALOG_ID) );

					try {
						if (aBF.update(aRoleList) == 0) {
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
					RoleListBeanFactory aBF = RoleListBeanFactory.getInstance(aConn);
					RoleList aRoleList = new RoleList();
                    aRoleList.setRoleName( request.getParameter(RoleList.FIELD_ROLE_NAME) );
                    aRoleList.setRoleDesc( request.getParameter(RoleList.FIELD_ROLE_DESC) );
                    aRoleList.setRoleCatalogId( request.getParameter(RoleList.FIELD_ROLE_CATALOG_ID) );

					try {
						if (aBF.insert(aRoleList) == 0) {
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
					RoleListBeanFactory aBF = RoleListBeanFactory.getInstance(aConn);
					RoleList aRoleList = new RoleList();
                    aRoleList.setRoleName( request.getParameter(RoleList.FIELD_ROLE_NAME) );
                    boolean bSuccess = true;
					try {
						if (aBF.delete(aRoleList) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
                            DataSet aDS = null;
							
							UserRoles aUserRoles = new UserRoles();
							aUserRoles.setRoleName(request.getParameter(RoleList.FIELD_ROLE_NAME));
							aDS = UserRolesBeanFactory.getInstance(aConn).Query(aUserRoles, null);
							try{
								while(aDS!=null&&aDS.next()){
									aUserRoles = (UserRoles)aDS.getData();
									UserRolesBeanFactory.getInstance(aConn).delete(aUserRoles);
								}
							}catch(Exception ee){
								ee.printStackTrace();
								bSuccess = false;
							}							

							RoleAuthenticatedPermission aRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
							aRoleAuthenticatedPermission.setRoleName(request.getParameter(RoleList.FIELD_ROLE_NAME));
							aDS = RoleAuthenticatedPermissionBeanFactory.getInstance(aConn).Query(aRoleAuthenticatedPermission, null);
							try{
								while(aDS!=null&&aDS.next()){
									aRoleAuthenticatedPermission = (RoleAuthenticatedPermission)aDS.getData();
									RoleAuthenticatedPermissionBeanFactory.getInstance(aConn).delete(aRoleAuthenticatedPermission);
								}
							}catch(Exception ee){
								ee.printStackTrace();
							}
							aRoleAuthorizedPermission = new RoleAuthorizedPermission();
							aRoleAuthorizedPermission.setFunctionRole(request.getParameter(RoleList.FIELD_ROLE_NAME));
							aDS = RoleAuthorizedPermissionBeanFactory.getInstance(aConn).Query(aRoleAuthorizedPermission, null);
							try{
								while(aDS!=null&&aDS.next()){
									aRoleAuthorizedPermission = (RoleAuthorizedPermission)aDS.getData();
									RoleAuthorizedPermissionBeanFactory.getInstance(aConn).delete(aRoleAuthorizedPermission);
								}
								out.print("{\"DeleteResult\":\"OK\"}");
							}catch(Exception ee){
								ee.printStackTrace();
								bSuccess = false;
							}
							if(bSuccess) aConn.commit();
							else aConn.rollback();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"DeleteResult\":\"FAIL\"}");
						aConn.rollback();
					}
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
