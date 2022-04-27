package inmethod.auth.inter_face;
import inmethod.commons.rdb.*;
import inmethod.auth.Users;
/**
 * 認證介面,規範了登入、登出、檢查密碼等基本功能.
 * @author william chen
 */
public interface Authentication{

  /**
   * login .
   * @param sUserID user login id
   * @param sUserPwd user password
   * @return boolean true: login success, false: login fail
   */
  public boolean login(String sUserID,String sUserPwd) throws Exception ;

  /**
   * Logout specify user id.
   * @param sUserID user id
   * @return boolean true: logout success , false: fail
   */
  public boolean logout(String sUserID) throws Exception ;

  /**
   * Check if user password if correct
   * @param sUserID
   * @param sUserPwd user password
   */
  public boolean checkUserPassword(String sUserID,String sUserPwd) throws Exception ;

  /**
   * Set user password
   * @param sUserID user id
   * @param sOldUserPwd user password
   * @param sNewUserPwd user password
   * @return boolean true: set success , false: set failed
   */
  public boolean setUserPassword(String sUserID,String sOldUserPwd , String sNewUserPwd) throws Exception;

  /**
   * get user id
   * @return String userid
   */
  public String getUserPrincipal() throws Exception;

  /**
   * Check if user login
   * @param sUserID
   * @return boolean true: login , false: not login
   */
   public boolean isLogin(String sUserID);

  /**
   * Get All Account.
   * @return DataSet
   */
   public Users[] getAllUsers();
}