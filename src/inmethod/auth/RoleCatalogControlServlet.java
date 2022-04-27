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
 * 角色分類維護API
 * @BackEnd
 * <pre>
 * URL: inmethod/auth/RoleCatalogControlServlet
 * 
 * 功能: 查詢基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有基本資料
 *  [
 *    {"RoleCatalogId":"Data","RoleCatalogDesc":"Data"}
 *    {"RoleCatalogId":"Data","RoleCatalogDesc":"Data"}
 *  ]
 *  
 * 功能: 刪除基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)RoleCatalogId=角色分類ID
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)RoleCatalogId=角色分類ID
 *      (O)RoleCatalogDesc=名稱
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 * 
 * 功能: 修改基本資料       
 * 參數: (R)FlowID=doUpdate
 *      (R)RoleCatalogId=角色分類ID
 *      (O)RoleCatalogDesc=名稱
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗
 *              
 * 功能: 角色分類列表(html select option)       
 * 參數: (R)FlowID=getJsGridSelectOptions
 * 回傳:
 *      [
 *        {"Name":"","Value":""},
 *        {"Name":"1","Value":"系統管理"},
 *        {"Name":"2","Value":"一般角色"}
 *      ]
 * </pre>
 * 
 */
@WebServlet(name = "RoleCatalogControlServlet", urlPatterns = { "/inmethod/auth/RoleCatalogControlServlet" })
public class RoleCatalogControlServlet extends HttpServlet {
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
					RoleCatalogBeanFactory aBF = RoleCatalogBeanFactory.getInstance(aConn);
					RoleCatalog aRoleCatalog = null;
					DataSet aDS = aBF.Query(aRoleCatalog, "ROLE_CATALOG_ID");
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aRoleCatalog = (RoleCatalog) aDS.getData();
						sReturn = sReturn + "{\"Name\":\""+aRoleCatalog.getRoleCatalogDesc()+"\",\"Value\":\""+aRoleCatalog.getRoleCatalogId()+"\"},";
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
					RoleCatalogBeanFactory aBF = RoleCatalogBeanFactory.getInstance(aConn);
					RoleCatalog aRoleCatalog = null;
					DataSet aDS = aBF.Query(aRoleCatalog, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aRoleCatalog = (RoleCatalog) aDS.getData();
						sReturn = sReturn + aRoleCatalog.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					RoleCatalogBeanFactory aBF = RoleCatalogBeanFactory.getInstance(aConn);
					RoleCatalog aRoleCatalog = new RoleCatalog();
                    aRoleCatalog.setRoleCatalogId( request.getParameter(RoleCatalog.FIELD_ROLE_CATALOG_ID) );
                    aRoleCatalog.setRoleCatalogDesc( request.getParameter(RoleCatalog.FIELD_ROLE_CATALOG_DESC) );

					try {
						if (aBF.update(aRoleCatalog) == 0) {
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
					RoleCatalogBeanFactory aBF = RoleCatalogBeanFactory.getInstance(aConn);
					RoleCatalog aRoleCatalog = new RoleCatalog();
                    aRoleCatalog.setRoleCatalogId( request.getParameter(RoleCatalog.FIELD_ROLE_CATALOG_ID) );
                    aRoleCatalog.setRoleCatalogDesc( request.getParameter(RoleCatalog.FIELD_ROLE_CATALOG_DESC) );

					try {
						if (aBF.insert(aRoleCatalog) == 0) {
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
					RoleCatalogBeanFactory aBF = RoleCatalogBeanFactory.getInstance(aConn);
					RoleCatalog aRoleCatalog = new RoleCatalog();
                    aRoleCatalog.setRoleCatalogId( request.getParameter(RoleCatalog.FIELD_ROLE_CATALOG_ID) );

					try {
						if (aBF.delete(aRoleCatalog) == 0) {
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
