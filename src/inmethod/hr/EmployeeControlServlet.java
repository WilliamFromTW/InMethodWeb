package inmethod.hr;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.util.*;
import java.io.*;

import inmethod.auth.AuthFactory;
import inmethod.auth.RoleAuthorizedPermission;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;

/**
 * 員工資料維護API
 * @BackEnd
 * <pre>
 * URL: inmethod/hr/EmployeeControlServlet (維護員工基本資料)
 *     
 * 功能: 查詢員工基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有員工資料
 *  [
 *    {"USER_ID":"920405","USER_NAME":"林春嬌","USER_ENGLISH_NAME":"bianca.lin","USER_DEPT_ID":"74","USER_MAIL":"bianca.lin@gmail.com","USER_ONBORAD_DATE":"20030420","USER_DEPARTURE_DATE":""},
 *    {"USER_ID":"961101","USER_NAME":"王志明","USER_ENGLISH_NAME":"peter.wang","USER_DEPT_ID":"21","USER_MAIL":"peter.wang@gmail.com","USER_ONBORAD_DATE":"20071120","USER_DEPARTURE_DATE":""}
 *  ]
 *  
 * 功能: 刪除員工基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)UserId=員工代號 
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增員工基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)UserId=員工代號 
 *      (O)UserName=員工姓名
 *      (O)UserEnglishName=員工英文名字
 *      (O)UserDeptId=員工部門代號
 *      (O)UserMail=員工Mail
 *      (O)UserOnboradDate=員工到職日
 *      (O)UserDepartureDate=員工離職日
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 * 
 * 功能: 修改員工基本資料       
 * 參數:  (R)FlowID=doUpdate
 *       (O)UserName=員工姓名
 *       (O)UserEnglishName=員工英文名字
 *       (O)UserDeptId=員工部門代號
 *       (O)UserMail=員工Mail
 *       (O)UserOnboradDate=員工到職日
 *       (O)UserDepartureDate=員工離職日
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * 功能: 取得員工列表(for jsgrid select option)      
 * 參數: (R)FlowID=getJsGridSelectOptions  
 * 回傳:
 *  [
 *   {"Name":"","Value":""},
 *   {"Name":"920405-林春嬌","Value":"920405"},
 *   {"Name":"961101-王志明","Value":"961101"}
 *  ] 
 *  
 * 功能: 取得授權過員工列表 , admin , 大pm , pm ,部門主管可取得所有員工列表 , 專案成員只能取得自己員工代號 (for JSGRID select option)      
 * 參數: (R)FlowID=getAuthJsGridSelectOptions  
 * 回傳:
 *  [
 *   {"Name":"","Value":""},
 *   {"Name":"920405-林春嬌","Value":"920405"},
 *   {"Name":"961101-王志明","Value":"961101"}
 *  ] 
 *  
 * 功能: 取得授權過員工列表 , admin , 大pm , pm 可取得所有員工列表 ,部門主管可取得部門員工列表, 專案成員只能取得自己員工代號 (for JSGRID select option)      
 * 參數: (R)FlowID=getJsDeptAllMembersHtmlOptions  
 * 回傳:
 *  [
 *   {"Name":"","Value":""},
 *   {"Name":"920405-林春嬌","Value":"920405"},
 *   {"Name":"961101-王志明","Value":"961101"}
 *  ] 
 *
 * 功能: 取得部門所有人員ID (若角色不是department_leader就只能查到自己)
 * 參數: (R)FlowID=getDeptAllMembersHtmlOptions  
 * 回傳:
 *   &lt;option value='920405'&gt;920405-林春嬌&lt;/option&gt;
 * </pre>
 * 
 */
@WebServlet(name = "EmployeeControlServlet", urlPatterns = { "/inmethod/hr/EmployeeControlServlet" })
public class EmployeeControlServlet extends HttpServlet {
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
		if( sCatalog!=null && !sCatalog.trim().equals("")  )
			   FUNCTION_NAME = FUNCTION_NAME +"?Catalog="+sCatalog;
		PrintWriter out = response.getWriter();
		try (Connection aConn =DBConnectionManager.getWebDBConnection().getConnection() ) {
			try {
				aConn.setAutoCommit(false);
				aWebAuth = AuthFactory.getWebAuthentication(request, response); 
				sUserID = aWebAuth.getUserPrincipal();
				Vector aUserRoles = aWebAuth.getUserRoles(sUserID);
				
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
				else  if (sFlowID.equalsIgnoreCase("getJsGridSelectOptions")) {
					
					EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
					Employee aEmployee = null;
					if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
						aEmployee = new Employee();
						aEmployee.setCatalog(Integer.parseInt(sCatalog));
					}					
					DataSet aDS = aBF.Query(aEmployee,  "USER_ID");
					String sReturn = "";
                    
					while (aDS != null && aDS.next()) {
						aEmployee = (Employee) aDS.getData();
						sReturn = sReturn +  "{\"Name\":\""+aEmployee.getCatalog()+"-"+aEmployee.getUserId()+"-"+((Employee) aEmployee).getUserName()+"\",\"Value\":\""+aEmployee.getUserId() +"\"},";
					}
					if (sReturn.length() == 0) {
						sReturn = "[{\"Name\":\"\",\"Value\":\"\"}]";
					} else {
						sReturn = "[{\"Name\":\"\",\"Value\":\"\"},"+ sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					
					out.println(sReturn);
					out.flush();
					out.close();
					aConn.commit();
					return;
				}else  if (sFlowID.equalsIgnoreCase("getAuthJsGridSelectOptions")) {
					
					EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
					Employee aEmployee = null;
					DataSet aDS = aBF.Query(aEmployee,  "USER_ID");
					String sReturn = "";
                    
					// admin , 大pm, pm , 部門主管可以指派人員
					while (aDS != null && aDS.next()) {
						aEmployee = (Employee) aDS.getData();
						for(int i=0;i<aUserRoles.size();i++){
						  if( aUserRoles.get(i)!=null && !aEmployee.getUserId().equals(sUserID) &&( aUserRoles.get(i).equals("admin") ||  aUserRoles.get(i).equals("pm_watcher") ||  aUserRoles.get(i).equals("pm_leader") ||  aUserRoles.get(i).equals("pm") || aUserRoles.get(i).equals("department_leader")) )
							sReturn = sReturn +  "{\"Name\":\""+aEmployee.getUserId()+"-"+((Employee) aEmployee).getUserName()+"\",\"Value\":\""+aEmployee.getUserId() +"\"},";
						  else if( aUserRoles.get(i)!=null && aEmployee.getUserId().equals(sUserID) && aUserRoles.get(i).equals("pm_member")){
							sReturn = sReturn +  "{\"Name\":\""+aEmployee.getUserId()+"-"+((Employee) aEmployee).getUserName()+"\",\"Value\":\""+aEmployee.getUserId() +"\"},";
						  }
							  
						}
					}
					if (sReturn.length() == 0) {
						sReturn = "[{\"Name\":\"\",\"Value\":\"\"}]";
					} else {
						sReturn = "[{\"Name\":\"\",\"Value\":\"\"},"+sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					
					out.println(sReturn);
					out.flush();
					out.close();
					aConn.commit();
					return;
				}else  if (sFlowID.equalsIgnoreCase("getOnlyMyIDSelectOptions")) {
					
						String sReturn = "[{\"Name\":\""+sUserID+"\",\"Value\":\""+sUserID+"\"}]";
						//System.out.println(sReturn);
						out.println(sReturn);
						out.flush();
						out.close();
						aConn.commit();
						return;
				}else if(sFlowID.equalsIgnoreCase("getJsDeptAllMembersHtmlOptions")){
					//System.out.println("ddd");
			      String sReturn = "";
				  boolean bListAll = false;
				  boolean bIsDepartmentLeaderRole = false;
			 	  for(int i=0;i<aUserRoles.size();i++ ){		
				    if( aUserRoles.get(i)!=null && ( aUserRoles.get(i).equals("admin") ||  aUserRoles.get(i).equals("pm_watcher") ||  aUserRoles.get(i).equals("pm_leader") ||  aUserRoles.get(i).equals("pm")) ){
					  bListAll=true;
					  break;
					}else if ( aUserRoles.get(i).equals("department_leader") ){
						bIsDepartmentLeaderRole = true;
						break;
					}
				  }
            	  SQLTools.getInstance().setPrintLog(true);
                  if( bListAll ){
				    Employee aEmployee = new Employee();
				    //aEmployee.setUserDeptId(request.getParameter(Department.FIELD_DEPT_ID));
				    EmployeeBeanFactory aEmployeeBeanFactory = EmployeeBeanFactory.getInstance(aConn);
				    DataSet aDS = aEmployeeBeanFactory.Query(aEmployee, "USER_DEPT_ID");
				    if( aDS!=null){
					  while(aDS.next()){
						  if( ((Employee)aDS.getData()).getUserDeptName()!=null && !((Employee)aDS.getData()).getUserDeptName().trim().equals("") )
						    sReturn = sReturn+  "{\"Name\":\""+((Employee)aDS.getData()).getUserDeptName() +"-"+ ((Employee)aDS.getData()).getUserName()+"\",\"Value\":\""+((Employee)aDS.getData()).getUserId() +"\"},";
						//  else
						  //  sReturn = sReturn+  "{\"Name\":\""+aEmployee.getUserId()+"-"+((Employee) aEmployee).getUserName()+"\",\"Value\":\""+((Employee)aDS.getData()).getUserId() +"\"},";
					  }
				    }
				    sReturn = "[{\"Name\":\"選人員\",\"Value\":\"\"},"+ sReturn.substring(0,sReturn.length()-1) + "]";  
				    //System.out.println("ddd"+sReturn);
                  }else if( bIsDepartmentLeaderRole ){
                    ResultSet aRS = SQLTools.getInstance().Select(aConn, "select * from EMPLOYEE where USER_DEPT_ID in (select DEPT_ID  from DEPARTMENT where PARENTDEPT_ID in (select DEPT_ID from DEPARTMENT where PARENTDEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"') or DEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"')) or DEPT_ID in (select DEPT_ID from DEPARTMENT where PARENTDEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"') or DEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"'))) order by USER_ID");
                    if(aRS!=null){
                    	while(aRS.next()){
						    sReturn = sReturn+  "{\"Name\":\""+aRS.getString("User_Name")+"\",\"Value\":\""+aRS.getString("USER_ID") +"\"},";
                    	}
                    }
				    sReturn = "[{\"Name\":\"選人員\",\"Value\":\"SelectOne\"},"+ sReturn.substring(0,sReturn.length()-1) + "]";  

                  }
                  else sReturn = "[{\"Name\":\">預設值\",\"Value\":\""+sUserID+"\"}]";
 				  System.out.println(sReturn);
				  out.println(sReturn);
				  out.flush();
				  out.close();
				  aConn.commit();
				  return;
				}else if(sFlowID.equalsIgnoreCase("getDeptAllMembersHtmlOptions")){
					//System.out.println("ddd");
			      String sReturn = "";
				  boolean bListAll = false;
				  boolean bIsDepartmentLeaderRole = false;
			 	  for(int i=0;i<aUserRoles.size();i++ ){		
				    if( aUserRoles.get(i)!=null && ( aUserRoles.get(i).equals("admin") ||  aUserRoles.get(i).equals("pm_watcher") ||  aUserRoles.get(i).equals("pm_leader") ||  aUserRoles.get(i).equals("pm")) ){
					  bListAll=true;
					  break;
					}else if ( aUserRoles.get(i).equals("department_leader") ){
						bIsDepartmentLeaderRole = true;
						break;
					}
				  }
            	  SQLTools.getInstance().setPrintLog(true);
                  if( bListAll ){
				    Employee aEmployee = new Employee();
				    //aEmployee.setUserDeptId(request.getParameter(Department.FIELD_DEPT_ID));
				    EmployeeBeanFactory aEmployeeBeanFactory = EmployeeBeanFactory.getInstance(aConn);
				    DataSet aDS = aEmployeeBeanFactory.Query(aEmployee, "USER_DEPT_ID");
				    if( aDS!=null){
					  while(aDS.next()){
						  if( ((Employee)aDS.getData()).getUserDeptName()!=null && !((Employee)aDS.getData()).getUserDeptName().trim().equals("") )
						    sReturn = sReturn+ "<option value='" +((Employee)aDS.getData()).getUserId()+"'>"+ ((Employee)aDS.getData()).getUserDeptName() +"-"+ ((Employee)aDS.getData()).getUserName()+"</option>\n";
						  else
						    sReturn = sReturn+ "<option value='" +((Employee)aDS.getData()).getUserId()+"'>"+  ((Employee)aDS.getData()).getUserName()+"</option>\n";
					  }
				    }
				    sReturn = "<option value='ALL'>全部</option>"+sReturn;  
                  }else if( bIsDepartmentLeaderRole ){
                    ResultSet aRS = SQLTools.getInstance().Select(aConn, "select * from EMPLOYEE where USER_DEPT_ID in (select DEPT_ID  from DEPARTMENT where PARENTDEPT_ID in (select DEPT_ID from DEPARTMENT where PARENTDEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"') or DEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"')) or DEPT_ID in (select DEPT_ID from DEPARTMENT where PARENTDEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"') or DEPT_ID in (select USER_DEPT_ID from EMPLOYEE where USER_ID='"+sUserID +"'))) order by USER_ID");
                    String sAllDeptOptions = "";
                    if(aRS!=null){
                    	while(aRS.next()){
                    		sReturn = sReturn+ "<option value='" +aRS.getString("USER_ID")+"'>"+  aRS.getString("User_Name") +"</option>\n";
                    		sAllDeptOptions = sAllDeptOptions +"'"+aRS.getString("USER_ID")+"',";
                    	}
                    }
				    sReturn = "<option value=\"ALL_DEPT"+sAllDeptOptions.substring(0,sAllDeptOptions.length()-1)+"\">全部部門</option>"+sReturn;  

                  }
                  else sReturn = "<option value='" +sUserID+"'>預設值</option>";
 				  System.out.println(sReturn);
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
                 if (sFlowID.equalsIgnoreCase("doQuery") && aRoleAuthorizedPermission!=null && aRoleAuthorizedPermission.getFunctionQuery().equalsIgnoreCase("Y")) {
					EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
					Employee aEmployee = null;
					//System.out.println("adsasdf catalog = "+ sCatalog);
					if( sCatalog!=null  && !sCatalog.equals("")) {
						aEmployee = new Employee();
						aEmployee.setCatalog(Integer.parseInt(sCatalog));
					}		
					DataSet aDS = aBF.Query(aEmployee, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aEmployee = (Employee) aDS.getData();
						sReturn = sReturn + aEmployee.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
					System.out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission!=null && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
					Employee aEmployee = new Employee();
                    aEmployee.setUserId( request.getParameter(Employee.FIELD_USER_ID) );
                    aEmployee.setUserName( request.getParameter(Employee.FIELD_USER_NAME) );
                    aEmployee.setUserEnglishName( request.getParameter(Employee.FIELD_USER_ENGLISH_NAME) );
                    aEmployee.setUserDeptId( request.getParameter(Employee.FIELD_USER_DEPT_ID) );
                    aEmployee.setUserMail( request.getParameter(Employee.FIELD_USER_MAIL) );
                    aEmployee.setUserOnboradDate( request.getParameter(Employee.FIELD_USER_ONBORAD_DATE) );
                    aEmployee.setUserDepartureDate( request.getParameter(Employee.FIELD_USER_DEPARTURE_DATE) );
                    aEmployee.setUserLanguage( request.getParameter(Employee.FIELD_USER_LANGUAGE) );
                    aEmployee.setCatalog(Integer.parseInt( request.getParameter(Employee.FIELD_CATALOG) ));

					try {
						if (aBF.update(aEmployee) == 0) {
							response.setStatus(1000);
							out.println("{\"UpdateResult\":\"FAIL\"}");
                           aConn.rollback();
						} else {
							out.print("{\"UpdateResult\":\"OK\"}");
                           aConn.commit();
       					   Employee.getInstance().refreshAllEmployee();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"UpdateResult\":\"FAIL\"}");
					    aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doAdd") && aRoleAuthorizedPermission!=null && aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase("Y")) {
					System.out.println("sadf"+request.getParameter(Employee.FIELD_CATALOG));
					EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
					Employee aEmployee = new Employee();
                    aEmployee.setUserId( request.getParameter(Employee.FIELD_USER_ID) );
                    aEmployee.setUserName( request.getParameter(Employee.FIELD_USER_NAME) );
                    aEmployee.setUserEnglishName( request.getParameter(Employee.FIELD_USER_ENGLISH_NAME) );
                    aEmployee.setUserDeptId( request.getParameter(Employee.FIELD_USER_DEPT_ID) );
                    aEmployee.setUserMail( request.getParameter(Employee.FIELD_USER_MAIL) );
                    aEmployee.setUserOnboradDate( request.getParameter(Employee.FIELD_USER_ONBORAD_DATE) );
                    aEmployee.setUserDepartureDate( request.getParameter(Employee.FIELD_USER_DEPARTURE_DATE) );
                    aEmployee.setUserLanguage( request.getParameter(Employee.FIELD_USER_LANGUAGE) );
                    aEmployee.setCatalog(Integer.parseInt( request.getParameter(Employee.FIELD_CATALOG) ));
					try {
						if (aBF.insert(aEmployee) == 0) {
							response.setStatus(1000);
							out.println("{\"AddResult\":\"FAIL\"}");
                           aConn.rollback();
						} else {
							out.print("{\"AddResult\":\"OK\"}");
                           aConn.commit();
       					   Employee.getInstance().refreshAllEmployee();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"AddResult\":\"FAIL\"}");
                       aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doDelete") && aRoleAuthorizedPermission!=null && aRoleAuthorizedPermission.getFunctionDelete().equalsIgnoreCase("Y")) {
					EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
					Employee aEmployee = new Employee();
                    aEmployee.setUserId( request.getParameter(Employee.FIELD_USER_ID) );

					try {
						if (aBF.delete(aEmployee) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
                           aConn.rollback();
						} else {
							out.print("{\"DeleteResult\":\"OK\"}");
                           aConn.commit();
       					   Employee.getInstance().refreshAllEmployee();
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
