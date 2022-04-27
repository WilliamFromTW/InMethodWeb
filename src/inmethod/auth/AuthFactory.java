package inmethod.auth;

import inmethod.db.DBConnectionManager;
import inmethod.hr.Employee;
import inmethod.hr.EmployeeBeanFactory;

import java.sql.Connection;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.DataSet;

public class AuthFactory {
	

	public static WebAuthentication getWebAuthentication(HttpServletRequest request, HttpServletResponse response) {
		Object obj[] = new Object[2];
		obj[0] = request;
		obj[1] = response;

		try {
			return (WebAuthentication) inmethod.commons.util.Reflection
					.newInstance(inmethod.Global.getInstance().getEnvirenment("AUTHENTICATION_CLASS"), obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	  public static RoleAuthorizedPermission getFunctionInfo(String sFunctionName,HttpServletRequest request, HttpServletResponse response){
		    WebAuthentication aWebAuth = null;
		    RoleAuthorizedPermission aRoleAuthorizedPermission = null;
		    try(Connection aConn = inmethod.db.DBConnectionManager.getWebDBConnection().getConnection();)  {
		      aConn.setAutoCommit(true);
//		      aAF = new getWebAuthentication(request,response);
		      aWebAuth = getWebAuthentication(request,response);
		      aRoleAuthorizedPermission =   aWebAuth.getAuthorizedFunctionInfo(aWebAuth.getUserPrincipal(), sFunctionName)  ;
		      return aRoleAuthorizedPermission;
		    }catch(Exception ex){
		    	ex.printStackTrace();
		    	return null;
		    }
		  }
	

	/**
	 * get Role Catalog Name
	 * 
	 * @param  sRoleName   Role Catalog ID
	 * @return
	 */
	public static String getRoleDescValueByRoleName(String sRoleName) {
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			RoleListBeanFactory aBF = RoleListBeanFactory.getInstance(aConn);
			RoleList aRL = new RoleList();
			aRL.setRoleName(sRoleName);
			DataSet aDS = aBF.Query(aRL, null);
			if (aDS.getTotalCount() == 1 && aDS.next()) {
				return ((RoleList) (aDS.getData())).getRoleDesc();
			} else
				return "";
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * get Role Catalog Name
	 * 
	 * @param sCataLogID
	 * @return
	 */
	public static String getRoleCatalogNameByCatalogID(String sCataLogID) {
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {

			RoleCatalogBeanFactory aBF = RoleCatalogBeanFactory.getInstance(aConn);
			RoleCatalog aRC = new RoleCatalog();
			aRC.setRoleCatalogId(sCataLogID);
			DataSet aDS = aBF.Query(aRC, null);
			if (aDS.getTotalCount() == 1 && aDS.next()) {
				return ((RoleCatalog) (aDS.getData())).getRoleCatalogDesc();
			} else
				return "";
		} catch (Exception e) {
			return "";
		}
	}

	public static String getRoleCatalogSelectOption(String sSelected) {
		DataSet aDS = null;
		String sReturn = "<option > </option>";
		RoleCatalog aRC = null;

		try {
			aDS = getAllRoleCatalog();
			if (aDS != null)
				while (aDS.next()) {
					aRC = (RoleCatalog) aDS.getData();
					if (sSelected != null && sSelected.equals(aRC.getRoleCatalogId()))
						sReturn += "<option value='" + aRC.getRoleCatalogId() + "' selected>" + aRC.getRoleCatalogDesc()
								+ "</option>";
					else
						sReturn += "<option value='" + aRC.getRoleCatalogId() + "'>" + aRC.getRoleCatalogDesc()
								+ "</option>";
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			return sReturn;
		}
	}

	public static void main(String af[]) {
		try {
			;// System.out.println( inmethod.commons.util.Crypt.MD5("ll") );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get All User Roles
	 */
	public static DataSet getAllUserRoles() {
		RoleList aRL = null;
		DataSet aDS = null;
		RoleListBeanFactory aBF = null;
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			aBF = RoleListBeanFactory.getInstance(aConn);
			aRL = new RoleList();
			aConn.setAutoCommit(true);
			aBF.setConnection(aConn);
			aDS = aBF.Query(aRL, RoleList.FIELD_ROLE_NAME);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return aDS;
	}

	/**
	 * Get All User Roles
	 */

	public static DataSet getAllRoleCatalog() {
		RoleCatalog aRC = null;
		DataSet aDS = null;
		RoleCatalogBeanFactory aBF = null;
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			aBF = RoleCatalogBeanFactory.getInstance(aConn);
			aRC = new RoleCatalog();
			aConn.setAutoCommit(true);
			aBF.setConnection(aConn);
			aDS = aBF.Query(aRC, RoleCatalog.FIELD_ROLE_CATALOG_ID);
			return aDS;
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return new DataSet();
	}

	public static String getLoginUserID(HttpServletRequest request, HttpServletResponse response) {
		WebAuthentication aWebAuth = null;
		String sUserID = "";

		try {
			aWebAuth = AuthFactory.getWebAuthentication(request, response);
			sUserID = aWebAuth.getUserPrincipal();
 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sUserID;
	}
	
	public static Employee getEmployee(String sUserID) {
	  Employee aEmployee = null;
	  try  {
            aEmployee = Employee.getInstance().getEmployee(sUserID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return aEmployee;
	}

	public static String getLoginUserName(HttpServletRequest request, HttpServletResponse response) {
		WebAuthentication aWebAuth = null;
		String sUserDesc = "";

		try {
			aWebAuth = AuthFactory.getWebAuthentication(request, response);
			sUserDesc = aWebAuth.getUserNameByUserID(aWebAuth.getUserPrincipal());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sUserDesc;
	}
	
	public static Vector<UserRoles> getUserRoles(HttpServletRequest request, HttpServletResponse response) {
		WebAuthentication aWebAuth = null;
		Vector<UserRoles> aVector = null;

		try {
			aWebAuth = AuthFactory.getWebAuthentication(request, response);
			aVector = aWebAuth.getUserRoles(aWebAuth.getUserPrincipal());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
			return aVector;
		
	}

	public static RoleAuthorizedPermission getAuthorizedFunctionInfo(String sFunctionName, HttpServletRequest request,
			HttpServletResponse response) {
		WebAuthentication aWebAuth = null;
		RoleAuthorizedPermission aAuthFunInfo = null;

		try {
			aWebAuth = AuthFactory.getWebAuthentication(request, response);
			aAuthFunInfo = (RoleAuthorizedPermission) aWebAuth.getAuthorizedFunctionInfo(aWebAuth.getUserPrincipal(), sFunctionName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return aAuthFunInfo;
	}

}