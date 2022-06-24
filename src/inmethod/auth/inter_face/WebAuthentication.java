package inmethod.auth.inter_face;
import java.util.Vector;
import java.util.HashMap;

import inmethod.auth.RoleAuthorizedPermission;
import inmethod.auth.UserRoles;
import inmethod.auth.Users;

/**
 * 也是介面, 且繼承Authentication , 創造符合web使用的認證method.
 * Web Authentication
 * @author william chen
 * @see Authentication
 */
public interface WebAuthentication extends Authentication{

  /**
   * check user permission
   * @param aUserRoles the Roles user play
   * @param sFunction function name
   * @return boolean true-has permission , false : no permission
   */
  public boolean hasPermission(Vector<UserRoles> aUserRoles,String sFunction) throws Exception ;
  
  /**
   * check user permission
   * @param sUserID use ID
   * @param sFunction function name
   * @return boolean true-has permission , false : no permission
   */
  public boolean checkPermission(String sUserID,String sFunction) throws Exception ;

  
  /**
   * Get Authorized Function info.
   * <pre>
   *   the function info may include function_url,function_desc and so on.
   *   these info will be a Object.
   * </pre>
   * @param  sUserID
   * @param  sFunction function name
   * @return json string {
   */
  public RoleAuthorizedPermission getAuthorizedFunctionInfo(String sUserID,String sFunction) throws Exception;

  /**
   * Get All Authorized Function info.
   * <pre>
   *   the function info may include function_url,function_desc and so on.
   *   these info will be a Object stored in Vector.
   * </pre>
   * @param  sUserID
   * @return Vector Function info object
   */
  public Vector<RoleAuthorizedPermission> getAuthorizedFunctionInfo(String sUserID) throws Exception;

  /**
   * Get all Authorized function from a user's all roles
   * @param aRoles user's role
   * @return HashMap
   * <pre>
   * size=0 if no function be auth
   * key : function name
   * value : user role name
   * </pre>
   */
  public HashMap<String,String> getAuthorizedFunctionInfo(Vector<UserRoles> aRoles);

  /**
   * get All User Roles
   * @param sUserID user id
   * @return Vector String user roles
   */
  public Vector<UserRoles> getUserRoles(String sUserID) throws Exception;

  /**
   * set user roles
   * @param sUserID user id
   * @param aRoles user roles
   * @return boolean true: set success , false: set failed
   */
  public boolean setUserRoles(String sUserID,Vector<UserRoles> aRoles) throws Exception ;
  
  /**
   * @return user id and user name
   */
  public Users[] getAllUsers();
  
  /**
   * 
   * @param sUserID use name(user account or user ID)
   * @return
   */
  public String getUserNameByUserID(String sUserID);
  
}  