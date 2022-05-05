package inmethod.hr;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.util.*;
import java.io.*;

import inmethod.Global;
import inmethod.auth.AuthFactory;
import inmethod.auth.RoleAuthorizedPermission;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;

/**
 * 部門資料維護API
 * @BackEnd
 * <pre> 
 * URL: inmethod/hr/DepartmentControlServlet (維護部門基本資料)
 *     
 * 功能: 查詢部捫基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有部門資料
 *    [
 *      {"DEPT_ID":"70","DEPT_NAME":"管理處","DEPT_LEADER_ID":"920202","PARENTDEPT_ID":"","DEPT_VALIDATE":"Y"},
 *      {"DEPT_ID":"74","DEPT_NAME":"資訊部","DEPT_LEADER_ID":"920405","PARENTDEPT_ID":"70","DEPT_VALIDATE":"Y"}
 *    ]
 *    
 * 功能: 刪除部門基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)DeptId=部門代號 
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增部門基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)DeptId=部門代號 
 *      (R)DeptName=部門名稱
 *      (O)Parentdetp_Id=部門上層代號
 *      (O)Dept_Validate=有效否
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 *  
 * 功能: 修改部門基本資料       
 * 參數: (R)FlowID=doUpdate
 *      (R)DeptName=部門名稱  
 *      (R)Parentdetp_Id=部門上層代號
 *      (O)Dept_Validate=有效否
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * 功能: 取得 jsgrid 需要的 SelectOptions json string      
 * 參數: (R)FlowID=getJsGridSelectOptions  
 * 回傳:
 *    [
 *        {"Name":"","Value":""},
 *        {"Name":"20-研發處","Value":"20"},
 *        {"Name":"21-電子研發部","Value":"21"}
 *    ]
 *    
 * 功能: 取得 jsgrid 需要的  multi selector json string      
 * 參數: (R)FlowID=getJsGridMultiSelectors
 * 回傳:
 *    [
 *        {"Name":"20-研發處"},
 *        {"Name":"21-電子研發部"}
 *    ]
 *    
 * </pre>
 */
@WebServlet(name = "DepartmentControlServlet", urlPatterns = { "/inmethod/hr/DepartmentControlServlet" })
public class DepartmentControlServlet extends HttpServlet {
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
		String sCatalog = request.getParameter("Catalog");
		System.out.println("asdf sFlowID="+sFlowID+ "catalog="+sCatalog+"");
		if( sCatalog!=null && !sCatalog.trim().equals("")  )
			   FUNCTION_NAME = FUNCTION_NAME +"?Catalog="+sCatalog;
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
				 else if (sFlowID.equalsIgnoreCase("getJsGridSelectOptions")) {
						DepartmentBeanFactory aBF = DepartmentBeanFactory.getInstance(aConn);
						Department aDepartment = null;
						if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
							aDepartment = new Department();
							aDepartment.setCatalog(Integer.parseInt(sCatalog));
						}
							
						DataSet aDS = aBF.Query(aDepartment, "DEPT_ID" );
						String sReturn = "";

						while (aDS != null && aDS.next()) {
							aDepartment = (Department) aDS.getData();
							sReturn = sReturn +  "{\"Name\":\""+Global.getCatalogNameByID( aDepartment.getCatalog())+"-"+aDepartment.getDeptName()+"\",\"Value\":\""+aDepartment.getDeptId() +"\"},";
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
					} else if (sFlowID.equalsIgnoreCase("getJsGridMultiSelectors")) {
						DepartmentBeanFactory aBF = DepartmentBeanFactory.getInstance(aConn);
						Department aDepartment = null;
						if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
							aDepartment = new Department();
							aDepartment.setCatalog(Integer.parseInt(sCatalog));
						}
						DataSet aDS = aBF.Query(aDepartment, "DEPT_ID"  );
						String sReturn = "";

						while (aDS != null && aDS.next()) {
							aDepartment = (Department) aDS.getData();
							sReturn = sReturn +  "{\"Name\":\""+ Global.getCatalogNameByID( aDepartment.getCatalog())+"-"+aDepartment.getDeptName()+"\"},";
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
	               System.out.println("aRoleAuthorizedPermission"+aRoleAuthorizedPermission);
	               if( aRoleAuthorizedPermission==null){ 
	                 response.setStatus(1000);
	                 out.println("{\"NoPermission\":\"FAIL\"}");
		              out.flush();
		              out.close();
		              aConn.rollback();
	               }
				    else if (sFlowID.equalsIgnoreCase("doQuery")  && aRoleAuthorizedPermission.getFunctionQuery().equalsIgnoreCase("Y")) {
					DepartmentBeanFactory aBF = DepartmentBeanFactory.getInstance(aConn);
					Department aDepartment = null;
					if( sCatalog!=null  && !sCatalog.equals("")) {
						aDepartment = new Department();
						aDepartment.setCatalog(Integer.parseInt(sCatalog));
					}		
					DataSet aDS = aBF.Query(aDepartment, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aDepartment = (Department) aDS.getData();
						sReturn = sReturn + aDepartment.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					DepartmentBeanFactory aBF = DepartmentBeanFactory.getInstance(aConn);
					Department aDepartment = new Department();
                    aDepartment.setDeptId( request.getParameter(Department.FIELD_DEPT_ID) );
                    aDepartment.setDeptName( request.getParameter(Department.FIELD_DEPT_NAME) );
                    aDepartment.setDeptLeaderId( request.getParameter(Department.FIELD_DEPT_LEADER_ID) );
                    aDepartment.setParentdeptId( request.getParameter(Department.FIELD_PARENTDEPT_ID) );
                    aDepartment.setDeptValidate( request.getParameter(Department.FIELD_DEPT_VALIDATE) );
                    aDepartment.setCatalog( Integer.parseInt( request.getParameter(Department.FIELD_CATALOG) ));

					try {
						if (aBF.update(aDepartment) == 0) {
							response.setStatus(1000);
							out.println("{\"UpdateResult\":\"FAIL\"}");
                            aConn.rollback();
						} else {
							if( request.getParameter(Department.FIELD_DEPT_ID).equalsIgnoreCase(request.getParameter(Department.FIELD_PARENTDEPT_ID))){

								response.setStatus(1000);
								out.println("{\"UpdateResult\":\"FAIL\"}");
	                           aConn.rollback();
							}else{
							  out.print("{\"UpdateResult\":\"OK\"}");
                              aConn.commit();
							}  
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"UpdateResult\":\"FAIL\"}");
					    aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doAdd") && aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase("Y")) {
					
					DepartmentBeanFactory aBF = DepartmentBeanFactory.getInstance(aConn);
					Department aDepartment = new Department();
                    aDepartment.setDeptId( request.getParameter(Department.FIELD_DEPT_ID) );
                    aDepartment.setDeptName( request.getParameter(Department.FIELD_DEPT_NAME) );
                    aDepartment.setDeptLeaderId( request.getParameter(Department.FIELD_DEPT_LEADER_ID) );
                    aDepartment.setParentdeptId( request.getParameter(Department.FIELD_PARENTDEPT_ID) );
                    aDepartment.setDeptValidate( request.getParameter(Department.FIELD_DEPT_VALIDATE) );
                    aDepartment.setCatalog(Integer.parseInt( request.getParameter(Employee.FIELD_CATALOG) ));

					try {
						if (aBF.insert(aDepartment) == 0) {
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
					DepartmentBeanFactory aBF = DepartmentBeanFactory.getInstance(aConn);
					Department aDepartment = new Department();
                    aDepartment.setDeptId( request.getParameter(Department.FIELD_DEPT_ID) );

					try {
						if (aBF.delete(aDepartment) == 0) {
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
				aConn.commit();
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
