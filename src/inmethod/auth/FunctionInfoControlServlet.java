package inmethod.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.sql.*;
import java.util.*;
import java.io.*;

import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;


/**
 * 程式基本資料API
 * 
 * @BackEnd
 * <pre>  
 * URL: inmethod/auth/FunctionInfoControlServlet (維護程式基本資料)
 * 
 * 功能: 查詢程式基本資料       
 * 參數: (R)FlowID=doQuery    
 *      (O)FUNCTION_NAME=程式名稱
 *      (O)FUNCTION_URL=程式相對路徑
 *      (O)FUNCTION_DESC=程式備註
 * 回傳:
 *    [     
 *        {"FUNCTION_NAME":"AuthenticationControlServlet","FUNCTION_URL":"inmethod/auth/AuthenticationControlServlet","FUNCTION_DESC":"登入驗證與修改密碼程式"},    
 *        {"FUNCTION_NAME":"AuthenticationControlServlet2","FUNCTION_URL":"inmethod/auth/AuthenticationControlServlet2","FUNCTION_DESC":"登入驗證與修改密碼程式2"}    
 *    ]
 *    
 * 功能: 刪除程式基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)FUNCTION_NAME=程式名稱
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 * 
 * 功能: 新增程式基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)FUNCTION_NAME=程式名稱
 *      (R)FUNCTION_URL=程式相對路徑
 *      (O)FUNCTION_DESC=程式備註
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 *      
 * 功能: 修改程式基本資料       
 * 參數: (R)FlowID=doUpate    
 *      (R)FUNCTION_NAME=程式名稱
 *      (O)FUNCTION_URL=程式相對路徑
 *      (O)FUNCTION_DESC=程式備註
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * 功能: 取得所有程式列表( html select option ) 
 * 參數: (R)FlowID=getJsGridSelectOptions
 * 回傳:
 *      [
 *        {"Name":"","Value":""},
 *        {"Name":"程式名稱1","Value":"程式代號1"},
 *        {"Name":"程式名稱2","Value":"程式代號2"}
 *      ]
 * </pre>
 *
 */
@WebServlet(name = "FunctionInfoControlServlet", urlPatterns = { "/inmethod/auth/FunctionInfoControlServlet" })
public class FunctionInfoControlServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html; charset=" + inmethod.Global.getInstance().getEnvirenment("ENCODE");
	private static String FUNCTION_NAME = null;
	private static final String FLOW_ID = "FlowID";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		request.setCharacterEncoding(inmethod.Global.getInstance().getEnvirenment("ENCODE"));
		response.setContentType(CONTENT_TYPE);
		WebAuthentication aWebAuth = null;
		FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);

		String sFlowID = request.getParameter(FLOW_ID);
		String sUserID = null;
		PrintWriter out = response.getWriter();
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
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
				 else  if(sFlowID.equalsIgnoreCase("getJsGridSelectOptions")){
						FunctionInfoBeanFactory aBF = FunctionInfoBeanFactory.getInstance(aConn);
						FunctionInfo aFunctionInfo = null;
						DataSet aDS = aBF.Query(aFunctionInfo, "FUNCTION_NAME");
						String sReturn = "";

						while (aDS != null && aDS.next()) {
							aFunctionInfo = (FunctionInfo) aDS.getData();
							sReturn = sReturn +  "{\"Name\":\""+aFunctionInfo.getFunctionName()+"<br>"+aFunctionInfo.getFunctionDesc()+"\",\"Value\":\""+aFunctionInfo.getFunctionName() +"\"},";
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
				
				if (!aWebAuth.checkPermission(sUserID, FUNCTION_NAME)) {
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

					FunctionInfoBeanFactory aBF = FunctionInfoBeanFactory.getInstance(aConn);
					FunctionInfo aFunctionInfo = null;
					DataSet aDS = aBF.Query(aFunctionInfo,  "FUNCTION_NAME");
					String sReturn = "";

					while (aDS != null && aDS.next()) {
						aFunctionInfo = (FunctionInfo) aDS.getData();
						sReturn = sReturn + aFunctionInfo.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);

				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					FunctionInfoBeanFactory aBF = FunctionInfoBeanFactory.getInstance(aConn);
					FunctionInfo aFunctionInfo = new FunctionInfo();
					aFunctionInfo.setFunctionName(request.getParameter(FunctionInfo.FIELD_FUNCTION_NAME));
					aFunctionInfo.setFunctionUrl(request.getParameter(FunctionInfo.FIELD_FUNCTION_URL));
					aFunctionInfo.setFunctionDesc(request.getParameter(FunctionInfo.FIELD_FUNCTION_DESC));
					try {
						if (aBF.update(aFunctionInfo) == 0) {
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
				} else if (sFlowID.equalsIgnoreCase("doAdd") && aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase("Y")) {
					FunctionInfoBeanFactory aBF = FunctionInfoBeanFactory.getInstance(aConn);
					FunctionInfo aFunctionInfo = new FunctionInfo();
					aFunctionInfo.setFunctionName(request.getParameter(FunctionInfo.FIELD_FUNCTION_NAME));
					aFunctionInfo.setFunctionUrl(request.getParameter(FunctionInfo.FIELD_FUNCTION_URL));
					aFunctionInfo.setFunctionDesc(request.getParameter(FunctionInfo.FIELD_FUNCTION_DESC));
					try {
						if (aBF.insert(aFunctionInfo) == 0) {
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
					FunctionInfoBeanFactory aBF = FunctionInfoBeanFactory.getInstance(aConn);
					FunctionInfo aFunctionInfo = new FunctionInfo();
					aFunctionInfo.setFunctionName(request.getParameter(FunctionInfo.FIELD_FUNCTION_NAME));
					try {
						
						if (aBF.delete(aFunctionInfo) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
							aConn.rollback();							
						} else {
							DataSet aDS = null;
							
							RoleAuthenticatedPermission aRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
							aRoleAuthenticatedPermission.setFunctionName(request.getParameter(FunctionInfo.FIELD_FUNCTION_NAME));
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
							aRoleAuthorizedPermission.setFunctionName(request.getParameter(FunctionInfo.FIELD_FUNCTION_NAME));
							aDS = RoleAuthorizedPermissionBeanFactory.getInstance(aConn).Query(aRoleAuthorizedPermission, null);
							try{
								while(aDS!=null&&aDS.next()){
									aRoleAuthorizedPermission = (RoleAuthorizedPermission)aDS.getData();
									RoleAuthorizedPermissionBeanFactory.getInstance(aConn).delete(aRoleAuthorizedPermission);
								}
								out.print("{\"DeleteResult\":\"OK\"}");
								aConn.commit();
							}catch(Exception ee){
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