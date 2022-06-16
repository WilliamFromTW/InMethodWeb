package inmethod.auth.adapter;

import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;
import inmethod.hr.Employee;
import inmethod.hr.EmployeeBeanFactory;
import inmethod.commons.rdb.*;
import inmethod.auth.FunctionInfo;
import inmethod.auth.FunctionInfoBeanFactory;
import inmethod.auth.RoleAuthorizedPermission;
import inmethod.auth.UserRoles;
import inmethod.auth.Users;
import inmethod.auth.UsersBeanFactory;
import inmethod.auth.inter_face.*;

import java.util.*;
import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.*;

import com.google.gson.Gson;

/**
 * Facade to control user authentication
 * 
 * @author william
 */
public class InMethodAuth implements WebAuthentication {

	public static String AUTH_USER = "AuthUsers";
	public static String AUTH_KEY = "AuthKeys";
	public static boolean USE_COOKIE_DOMAIN = false;
	public static String COOKIE_DOMAIN = null;
	public static int COOKIE_DOMAIN_AGE = 3600;// seconds
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session = null;

	/**
	 *
	 * @param request
	 *            http request
	 * @param response
	 *            http response
	 */
	public InMethodAuth(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		try {
			Context env = (Context) new InitialContext().lookup("java:comp/env");
			AUTH_USER = (String) env.lookup("AUTH_USER");
			AUTH_KEY = (String) env.lookup("AUTH_KEY");
			//System.out.println((Boolean) env.lookup("USE_COOKIE_DOMAIN"));
			if ((Boolean) env.lookup("USE_COOKIE_DOMAIN"))
				USE_COOKIE_DOMAIN = true;
			else
				USE_COOKIE_DOMAIN = false;
			COOKIE_DOMAIN = (String) env.lookup("COOKIE_DOMAIN");
			COOKIE_DOMAIN_AGE = (Integer) env.lookup("COOKIE_DOMAIN_AGE");
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (request != null)
			session = request.getSession(true);

		
	}

	/**
	 * login .
	 * 
	 * @param sUserID
	 *            user login id
	 * @param sUserPwd
	 *            user password
	 * @return boolean true: login success , false: if login fail
	 */
	public boolean login(String sUserID, String sUserPwd) throws Exception {
		if (session == null) {
		//	System.out.println("login,session is null");
			throw new Exception("inmethod.login , session is null");
		}
		if (checkUserPassword(sUserID, sUserPwd)) {
		//	System.out.println("use cookie domain ?" + USE_COOKIE_DOMAIN);
			Cookie aUserCookie = new Cookie(AUTH_USER, sUserID);
			Cookie aMd5Cookie = new Cookie(AUTH_KEY, Crypt.MD5(sUserID));
			if (USE_COOKIE_DOMAIN) {
				aUserCookie.setDomain(COOKIE_DOMAIN);
				aMd5Cookie.setDomain(COOKIE_DOMAIN);
			}
			aUserCookie.setPath("/");
			aUserCookie.setMaxAge(COOKIE_DOMAIN_AGE);
			aMd5Cookie.setPath("/");
			aMd5Cookie.setMaxAge(COOKIE_DOMAIN_AGE);
			response.addCookie(aUserCookie);
			response.addCookie(aMd5Cookie);
			// session.setAttribute(AUTH_USER,sUserID);
			// session.setAttribute(AUTH_KEY,Crypt.MD5(sUserID));
			return true;
		} else {
		//	System.out.println("login,checkUserPassword failed!");
			return false;
		}
	}

	/**
	 * logout .
	 * 
	 * @param sUserID
	 *            user id to login
	 * @return boolean true: logout success ,false: logout failed
	 */
	public boolean logout(String sUserID) throws Exception {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(AUTH_USER)) {
					cookies[i].setPath("/");
					cookies[i].setMaxAge(0);
					cookies[i].setValue(null);
					response.addCookie(cookies[i]);
				}
			}
		}
		/*
		 * if( session!=null ) session.invalidate();
		 */
		return true;
	}

	/**
	 * check if user password if correct
	 * 
	 * @param sUserID
	 *            user id
	 * @param sUserPwd
	 *            user password
	 */
	public boolean checkUserPassword(String sUserID, String sUserPwd) throws Exception {
		if (sUserID == null || sUserPwd == null)
			return false;
		
		boolean bReturn = false;
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection();) {
			ResultSet aRS = null;
			aRS = SQLTools.getInstance().Select(aConn,  "USERS", "*",
					"user_name='" + sUserID + "' and user_pass='" + sUserPwd + "' and user_validate='Y'");
			if (aRS != null) {
				if (aRS.next()) {
					aRS.close();
					bReturn = true;
				} else
					bReturn = false;
			} else
				bReturn = false;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bReturn;
	}

	/**
	 * set user password
	 * 
	 * @param sUserID
	 *            user id
	 * @param sOldUserPwd
	 *            user password
	 * @param sNewUserPwd
	 *            user password
	 * @return boolean true: set success , false: set failed
	 */
	public boolean setUserPassword(String sUserID, String sOldUserPwd, String sNewUserPwd) throws Exception {
		if (sUserID == null || sOldUserPwd == null || sNewUserPwd == null) {
			//System.out.println("setUserPassword,some parameter is null");
			return false;
		}
		boolean bReturn = false;
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			//System.out.println("UserID=" + sUserID + ",OldPassword=" + sOldUserPwd);

			if (checkUserPassword(sUserID, sOldUserPwd)) {
				SQLTools.getInstance().Update(aConn, "USERS", "user_pass='" + sNewUserPwd + "'",
						"user_name='" + sUserID + "' and user_pass='" + sOldUserPwd + "'");
				bReturn = true;
			} else {
				bReturn = false;
			}
		} catch (Exception ex) {
			throw new Exception("FromtwAuth.checkUserPassword , try catch error");
		}
		return bReturn;
	}

	/**
	 * get user id
	 * 
	 * @return String userid
	 */
	public String getUserPrincipal() throws Exception {
		String sUserNameValue = null;
		String sUserNameMd5 = null;
		String sTemp = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				sTemp = cookies[i].getName();
				//System.out.println("getUserPrincipal.cookie.name=" + sTemp + "," + cookies[i].getValue());
				if (sTemp != null && sTemp.equals(AUTH_USER))
					sUserNameValue = cookies[i].getValue();
				if (sTemp != null && sTemp.equals(AUTH_KEY))
					sUserNameMd5 = cookies[i].getValue();
			}
		}
		return sUserNameValue;
		// return (String)session.getAttribute(AUTH_USER);
	}

	/**
	 * check user permission
	 * 
	 * @param aUserRoles
	 *            the Roles user play
	 * @param sFunction
	 *            function name
	 * @return boolean true-has permission , false : no permission
	 */
	@Override
    public boolean hasPermission(Vector<UserRoles> aUserRoles,String sFunction) throws Exception {
		HashMap aHM = getAuthorizedFunctionInfo(aUserRoles);
	
		if( sFunction.lastIndexOf("?")!=-1) {
		String sFunction2 = sFunction.substring(0,sFunction.lastIndexOf("?"));
		
		//System.out.println("sFunction2"+sFunction2);
		if (aHM.get(sFunction2) != null)
		   return true;
		}
				
		if (aHM.get(sFunction) != null)
			return true;
		else
			return false;
	}

	/**
	 * get All User Roles
	 * 
	 * @param sUserID
	 *            user id
	 * @return Vector
	 */
	public Vector<UserRoles> getUserRoles(String sUserID) throws Exception {
		Vector<UserRoles> aVector = new Vector<UserRoles>();
		UserRoles aUserRoles = null;
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			ResultSet aRS = null;
			aRS = SQLTools.getInstance().Select(aConn, "USER_ROLES", "role_name", "user_name='" + sUserID + "'");
			if (aRS != null)
				while (aRS.next()) {
					//System.out.println("getUserRoles,getRole=" + aRS.getString("role_name"));
					aUserRoles = new UserRoles();
					aUserRoles.setRoleName(aRS.getString("role_name"));
					aUserRoles.setUserName(sUserID);
					
					aVector.add(aUserRoles);
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return aVector;
	}

	/**
	 * Get Auth Function info.
	 * 
	 * <pre>
	 *   the function info may include function_url,function_desc and so on.
	 *   these info will be a Object.
	 * </pre>
	 * 
	 * @param sUserID
	 * @param sFunction function name
	 * @return FunctionInfo object or null if no permission
	 */
	public RoleAuthorizedPermission getAuthorizedFunctionInfo(String sUserID, String sFunction) throws Exception {
		ResultSet aRS = null;
		RoleAuthorizedPermission aFunInfo = null;
		String sTempCondition = "(''";
		Vector<UserRoles> aUserRoles = null;

		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			aUserRoles = getUserRoles(sUserID);
		//	System.out.println(" getAuthFunctionInfo(String,String), aUserRoles #=" + aUserRoles.size());
			if (aUserRoles == null || aUserRoles.size() == 0 || !hasPermission(aUserRoles, sFunction))
				return null;
			for (int i = 0; i < aUserRoles.size(); i++) {
				sTempCondition += ",'" + ((UserRoles)aUserRoles.get(i)).getRoleName().trim() + "'";
			//	System.out.println(" user Roles = " + aUserRoles.get(i));
			}
			sTempCondition += ")";
			//System.out.println("role set = " + sTempCondition);
			if( sFunction.lastIndexOf("?")!=-1) {
				String sFunction2 = sFunction.substring(0,sFunction.lastIndexOf("?"));
				
			aRS = SQLTools.getInstance().Select(aConn, "ROLE_AUTHORIZED_PERMISSION,FUNCTION_INFO", "*",
					"ROLE_AUTHORIZED_PERMISSION.function_name=FUNCTION_INFO.function_name and ROLE_AUTHORIZED_PERMISSION.function_role in "
							+ sTempCondition + " and  ROLE_AUTHORIZED_PERMISSION.function_name='" + sFunction2 + "'");
			if (aRS != null)
				while (aRS.next()) {
					//System.out.println("getAuthFunctionInfo,Function=" + aRS.getString("function_name"));
					if (aFunInfo == null) {
						aFunInfo = new RoleAuthorizedPermission();
						aFunInfo.setFunctionRole(aRS.getString("FUNCTION_ROLE"));
						aFunInfo.setFunctionDesc(aRS.getString("FUNCTION_DESC"));
						aFunInfo.setFunctionName(sFunction2);
						aFunInfo.setFunctionUrl(aRS.getString("FUNCTION_URL"));
						aFunInfo.setFunctionGroup(aRS.getString("FUNCTION_GROUP"));
						aFunInfo.setFunctionDelete(aRS.getString("FUNCTION_DELETE"));
						aFunInfo.setFunctionInsert(aRS.getString("FUNCTION_INSERT"));
						aFunInfo.setFunctionUpdate(aRS.getString("FUNCTION_UPDATE"));
						aFunInfo.setFunctionQuery(aRS.getString("FUNCTION_QUERY"));
						aFunInfo.setFunctionVisible(aRS.getString("FUNCTION_VISIBLE"));
					} else {
						if (aRS.getString("FUNCTION_DELETE").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionDelete(aRS.getString("FUNCTION_DELETE"));
						if (aRS.getString("FUNCTION_INSERT").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionInsert(aRS.getString("FUNCTION_INSERT"));
						if (aRS.getString("FUNCTION_UPDATE").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionUpdate(aRS.getString("FUNCTION_UPDATE"));
						if (aRS.getString("FUNCTION_QUERY").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionQuery(aRS.getString("FUNCTION_QUERY"));
						if (aRS.getString("FUNCTION_VISIBLE").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionVisible(aRS.getString("FUNCTION_VISIBLE"));

					}
				}
			} 					
			aRS = SQLTools.getInstance().Select(aConn, "ROLE_AUTHORIZED_PERMISSION,FUNCTION_INFO", "*",
					"ROLE_AUTHORIZED_PERMISSION.function_name=FUNCTION_INFO.function_name and ROLE_AUTHORIZED_PERMISSION.function_role in "
							+ sTempCondition + " and  ROLE_AUTHORIZED_PERMISSION.function_name='" + sFunction + "'");
			if (aRS != null)
				while (aRS.next()) {
					//System.out.println("getAuthFunctionInfo,Function=" + aRS.getString("function_name"));
					if (aFunInfo == null) {
						aFunInfo = new RoleAuthorizedPermission();
					}
						aFunInfo.setFunctionRole(aRS.getString("FUNCTION_ROLE"));
						aFunInfo.setFunctionDesc(aRS.getString("FUNCTION_DESC"));
						aFunInfo.setFunctionName(sFunction);
						aFunInfo.setFunctionUrl(aRS.getString("FUNCTION_URL"));
						aFunInfo.setFunctionGroup(aRS.getString("FUNCTION_GROUP"));
						aFunInfo.setFunctionDelete(aRS.getString("FUNCTION_DELETE"));
						aFunInfo.setFunctionInsert(aRS.getString("FUNCTION_INSERT"));
						aFunInfo.setFunctionUpdate(aRS.getString("FUNCTION_UPDATE"));
						aFunInfo.setFunctionQuery(aRS.getString("FUNCTION_QUERY"));
						aFunInfo.setFunctionVisible(aRS.getString("FUNCTION_VISIBLE"));
						if (aRS.getString("FUNCTION_DELETE").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionDelete(aRS.getString("FUNCTION_DELETE"));
						if (aRS.getString("FUNCTION_INSERT").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionInsert(aRS.getString("FUNCTION_INSERT"));
						if (aRS.getString("FUNCTION_UPDATE").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionUpdate(aRS.getString("FUNCTION_UPDATE"));
						if (aRS.getString("FUNCTION_QUERY").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionQuery(aRS.getString("FUNCTION_QUERY"));
						if (aRS.getString("FUNCTION_VISIBLE").equalsIgnoreCase("Y"))
							aFunInfo.setFunctionVisible(aRS.getString("FUNCTION_VISIBLE"));
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (aRS != null) {
				try {
					aRS.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		try{
		//System.out.println("FunInfo2Json=" + aFunInfo.toJson() );
		}catch(Exception ee){
			ee.printStackTrace();
		}
		return aFunInfo;
	}

	/**
	 * Get All Auth Function info.
	 * 
	 * <pre>
	 *   the function info may include function_url,function_desc and so on.
	 *   these info will be a Object stored in Vector.
	 * </pre>
	 * 
	 * @param sUserID
	 * @return Vector store FunctionInfo objects, vector size=0 if no Function be authorized
	 */
	public Vector<RoleAuthorizedPermission> getAuthorizedFunctionInfo(String sUserID) throws Exception {
		Vector aVector = new Vector();
		RoleAuthorizedPermission aFunInfo = null;
		HashMap aHM = null;
		Iterator aIT = null;
		Map.Entry aME = null;
		String sTempCondition = "(''";
		Vector<UserRoles> aUserRoles = null;
		try {
			aUserRoles = getUserRoles(sUserID);
			//System.out.println("aUserRoles size=" + aUserRoles.size());
			for (int i = 0; i < aUserRoles.size(); i++) {
				sTempCondition += ",'" + ((UserRoles)aUserRoles.get(i)).getRoleName().trim() + "'";
			//	System.out.println(" user Roles = " + aUserRoles.get(i));
			}
			sTempCondition += ")";
//			System.out.println(" role set = " + sTempCondition);
			aHM = getAuthorizedFunctionInfo(aUserRoles);
	//		System.out.println("getAuthFunctionInfo(String), Authorized Function hashmap size=" + aHM.size());
			aIT = aHM.entrySet().iterator();
			SortedSet<String> keys = new TreeSet<String>(aHM.keySet());
			for (String key : keys) { 
//			   String value = (String)aHM.get(key);
				aFunInfo = (RoleAuthorizedPermission) getAuthorizedFunctionInfo(sUserID, key);
				if (aFunInfo != null)
					aVector.add(aFunInfo);
				//System.out.println("getAuthFunctionInfo(String), Function Name = " +key);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return aVector;
	}

	/**
	 * set user roles
	 * 
	 * @param sUserID
	 * @param aRoles user roles
	 * @return boolean true: set success, false: set failed
	 */
	public boolean setUserRoles(String sUserID, Vector<UserRoles> aRoles) throws Exception {
		//System.out.println("setUserRoles(),this method doesn't work");
		throw new Exception("this method doesn't work");
	}

	/**
	 * Check if user login
	 * 
	 * @param sUserID
	 *            user id
	 * @return boolean true: login , false: not login
	 */
	public boolean isLogin(String sUserID) {
		String sUserNameValue = null;
		String sUserNameMd5 = null;
		String sTemp = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				sTemp = cookies[i].getName();
				if (sTemp != null && sTemp.equals(AUTH_USER))
					sUserNameValue = cookies[i].getValue();
				if (sTemp != null && sTemp.equals(AUTH_KEY))
					sUserNameMd5 = cookies[i].getValue();
			}
		}
		if (sUserID == null)
			return false;
		if (sUserID.equals(sUserNameValue) && Crypt.MD5(sUserNameValue).equals(sUserNameMd5))
			return true;
		else
			return false;
		/*
		 * if( session.getAttribute(AUTH_USER).equals(sUserID) ) return true;
		 * else return false;
		 */
	}

	/**
	 * Get all auth function from a user's all roles
	 * 
	 * @param aRoles user's role
	 * @return HashMap&lt;String,String&gt;  key="function_name",value="role_name",  size=0 if no function be auth
	 * 
	 */
	public HashMap<String,String> getAuthorizedFunctionInfo(Vector<UserRoles> aRoles) {
		if (aRoles == null || aRoles.size() == 0)
			return new HashMap<String,String>();
		String sCondition = null;
		HashMap<String,String> aReturn = new HashMap<String,String>();
		ResultSet aRS = null;

		for (int i = 0; i < aRoles.size(); i++) {
			if (sCondition == null)
				sCondition = "'" +  ((UserRoles)aRoles.get(i)).getRoleName().trim() + "'";
			else
				sCondition += ",'" +  ((UserRoles)aRoles.get(i)).getRoleName().trim() + "'";
		//System.out.println("getAuthFunction(Vector aRoles),Role=" + aRoles.get(i));
		}
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			aRS = SQLTools.getInstance().Select(aConn, "ROLE_AUTHENTICATED_PERMISSION", "*", "role_name in (" + sCondition + ") order by FUNCTION_NAME");
			if (aRS != null)
				while (aRS.next()) {
					if (aReturn.get(aRS.getString("function_name")) == null) {
						//System.out.println("getAuthFunction(Vector ),function_name=" + aRS.getString("function_name"));
						aReturn.put(aRS.getString("function_name"), aRS.getString("role_name"));
					}
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (aRS != null) {
				try {
					aRS.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return aReturn;
	}

	/**
	 * <pre>
	 * Overwrite Webauthention.java.
	 * Object in DataSet is String.
	 * </pre>
	 * 
	 * @return DataSet , null = no data
	 */
	public Users[] getAllUsers() {
		DataSet aDS = null;
		Users aUsers = new Users();
		Employee aEmployee = null;
		Users[] aReturnUsers = null;
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			EmployeeBeanFactory aBF = EmployeeBeanFactory.getInstance(aConn);
			//System.out.println("getAllUsers , BeanFacade.Query(Users)");
			aDS = aBF.Query(aEmployee, "USER_ID");
			if( aDS!=null ) aReturnUsers = new Users[aDS.getTotalCount()];
			int iIndex = -1;
			while(aDS!=null&&aDS.next()){
				aEmployee = (Employee)aDS.getData();
				iIndex++;
				aUsers = new Users();
				aUsers.setUserDesc(aEmployee.getUserEnglishName());
				aUsers.setUserName(aEmployee.getUserId());
				aReturnUsers[iIndex] = aUsers;
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return aReturnUsers;

	}

	/**
	 * get user describe
	 */
	public String getUserNameByUserID(String sUserID) {
		if (sUserID == null)
			return "";
        //System.out.println("getUserNameByUserID="+sUserID);
        if( Employee.getInstance().getEmployee(sUserID)!=null)
		return Employee.getInstance().getEmployee(sUserID).getUserName();
        else
          return "";
	    
	}

}