package inmethod.auth;

public class RoleAuthenticatedPermission {

  public static final String FIELD_ROLE_NAME = "RoleName";
  public static final String FIELD_FUNCTION_NAME = "FunctionName";

  private String aRoleName = null;
  private String aFunctionName = null;
  private int iFieldCount = 2;

  public int getFieldCount(){
    return iFieldCount;
  }


  public void setRoleName(String param){
    aRoleName = param;
  }

  public String getRoleName(){
    return aRoleName;
  }

  public void setFunctionName(String param){
    aFunctionName = param;
  }

  public String getFunctionName(){
    return aFunctionName;
  }

public String toJson(){
    String sJson =  "";
    sJson = sJson + "{\"RoleName\":"+"\""+aRoleName+"\",";
    sJson = sJson + "\"FunctionName\":"+"\""+aFunctionName+"\"}";
    return sJson;
  }

}