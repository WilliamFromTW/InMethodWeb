package inmethod.auth;

public class UserRoles {


  public static final String FIELD_USER_NAME = "UserName";
  public static final String FIELD_ROLE_NAME = "RoleName";

  private String aUserName = null;
  private String aRoleName = null;
  private int iFieldCount = 2;

  public int getFieldCount(){
    return iFieldCount;
  }

  public void setUserName(String param){
    aUserName = param;
  }

  public String getUserName(){
    return aUserName;
  }

  public void setRoleName(String param){
    aRoleName = param;
  }

  public String getRoleName(){
    return aRoleName;
  }

public String toJson(){
    String sJson =  "";
    sJson = sJson + "{\"UserName\":"+"\""+aUserName+"\",";
    sJson = sJson + "\"RoleName\":"+"\""+aRoleName+"\"}";
    return sJson;
  }

}