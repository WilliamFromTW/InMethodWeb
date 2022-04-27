package inmethod.auth;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.util.*;
import java.io.*;
import inmethod.auth.inter_face.WebAuthentication;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;
/**
 * 
 * 使用者資料維護API
 * 
* @BackEnd
* <pre>
* URL: inmethod/auth/UsersControlServlet (維護使用者帳號密碼)
*     
* 功能: 查詢使用者       
* 參數: (R)FlowID=doQuery
*      (O)UserName=帳號名稱(不給將回傳全部使用者名稱)       
* 回傳:  
*      [    
*        {"UserName":"william","PassWord":"xxxx","Memo":"i am william"},    
*        {"UserName":"peter","PassWord":"yyyy","Memo":"i am peter"}    
*      ]
*                         
* 功能: 刪除帳號     
* 參數: FlowID=doDelete 
* 參數: UserName=帳號名稱    
* 回傳:      
*      {"DeleteResult":"OK"} 成功       
*      {"DeleteResult":"FAIL"} 失敗
*      
* 功能: 新增帳號   
* 參數: (R)FlowID=doAdd 
*      (R)FormUserName=帳號名稱
*      (R)FormUserPass=密碼
*      (R)FormUserValidate=Y or N
*      (O)FormUserDesc=備註
* 回傳:         
*      {"AddResult":"OK"} 成功       
*      {"AddResult":"FAIL"} 失敗        
*            
* 功能: 修改
* 參數: (R)FlowID=doUpdate
*      (R)FormUserName=帳號名稱
*      (O)FormUserPass=密碼
*      (O)FormUserValidate=Y or N
*      (O)FormUserDesc=備註
*  回傳:         
*      {"UpdateResult":"OK"} 成功       
*      {"UpdateResult":"FAIL"} 失敗
*                    
 * 功能: 帳號列表(html select option)       
 * 參數: (R)FlowID=getJsGridSelectOptions
 * 回傳:
 *      [
 *        {"Name":"","Value":""},
 *        {"Name":"admin-系統管理","Value":"admin"},
 *        {"Name":"920405-william","Value":"920405"}
 *      ]
*  </pre>
 */
@WebServlet(name = "UsersControlServlet", urlPatterns = { "/inmethod/auth/UsersControlServlet" })
public class UsersControlServlet extends HttpServlet {
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
						
						String sReturn = "";
						Users[] aUsers = aWebAuth.getAllUsers();
						for(Users aUser:aUsers){
							sReturn = sReturn + "{\"Name\":\""+aUser.getUserName()+"("+aUser.getUserDesc() +")\",\"Value\":\""+aUser.getUserName()+"\"},";
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
					UsersBeanFactory aBF = UsersBeanFactory.getInstance(aConn);
					Users aUsers = null;
					DataSet aDS = aBF.Query(aUsers, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aUsers = (Users) aDS.getData();
						sReturn = sReturn + aUsers.toJson() + ",";
					}
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					UsersBeanFactory aBF = UsersBeanFactory.getInstance(aConn);
					Users aUsers = new Users();
                    aUsers.setUserName( request.getParameter(Users.FIELD_USER_NAME) );
                    aUsers.setUserPass( request.getParameter(Users.FIELD_USER_PASS) );
                    aUsers.setUserValidate( request.getParameter(Users.FIELD_USER_VALIDATE) );
                    aUsers.setUserDesc( request.getParameter(Users.FIELD_USER_DESC) );

					try {
						if (aBF.update(aUsers) == 0) {
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
					UsersBeanFactory aBF = UsersBeanFactory.getInstance(aConn);
					Users aUsers = new Users();
                    aUsers.setUserName( request.getParameter(Users.FIELD_USER_NAME) );
                    aUsers.setUserPass( request.getParameter(Users.FIELD_USER_PASS) );
                    aUsers.setUserValidate( request.getParameter(Users.FIELD_USER_VALIDATE) );
                    aUsers.setUserDesc( request.getParameter(Users.FIELD_USER_DESC) );

					try {
						if (aBF.insert(aUsers) == 0) {
							response.setStatus(1000);
							out.println("{\"AddResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
							UserRolesBeanFactory aUserRolesBeanFactory = UserRolesBeanFactory.getInstance(aConn);
							UserRoles aUserRoles = new UserRoles();
		                    aUserRoles.setUserName( request.getParameter(UserRoles.FIELD_USER_NAME) );
		                    aUserRoles.setRoleName( "common" );

							try {
								if (aUserRolesBeanFactory.insert(aUserRoles) == 0) {
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

						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"AddResult\":\"FAIL\"}");
						aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doDelete") && aRoleAuthorizedPermission.getFunctionDelete().equalsIgnoreCase("Y")) {
					UsersBeanFactory aBF = UsersBeanFactory.getInstance(aConn);
					Users aUsers = new Users();
                    aUsers.setUserName( request.getParameter(Users.FIELD_USER_NAME) );

					try {
						if (aBF.delete(aUsers) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
							aConn.rollback();
						} else {
							DataSet aDS = null;
							UserRoles aUserRoles = new UserRoles();
							aUserRoles.setUserName(request.getParameter(Users.FIELD_USER_NAME));
							aDS = UserRolesBeanFactory.getInstance(aConn).Query(aUserRoles, null);
							try{
								while(aDS!=null&&aDS.next()){
									aUserRoles = (UserRoles)aDS.getData();
									UserRolesBeanFactory.getInstance(aConn).delete(aUserRoles);
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
