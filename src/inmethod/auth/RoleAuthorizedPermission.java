package inmethod.auth;

public class RoleAuthorizedPermission {



  public static final String FIELD_FUNCTION_ROLE = "FunctionRole";
  public static final String FIELD_FUNCTION_NAME = "FunctionName";
  public static final String FIELD_FUNCTION_URL = "FunctionUrl";
  public static final String FIELD_FUNCTION_DESC = "FunctionDesc";
  public static final String FIELD_FUNCTION_GROUP = "FunctionGroup";
  public static final String FIELD_FUNCTION_DELETE = "FunctionDelete";
  public static final String FIELD_FUNCTION_INSERT = "FunctionInsert";
  public static final String FIELD_FUNCTION_UPDATE = "FunctionUpdate";
  public static final String FIELD_FUNCTION_QUERY = "FunctionQuery";
  public static final String FIELD_FUNCTION_VISIBLE = "FunctionVisible";
  private String aFunctionRole = null;
  private String aFunctionName = null;
  private String aFunctionUrl = null;
  private String aFunctionDesc = null;
  private String aFunctionGroup = null;
  private String aFunctionDelete = null;
  private String aFunctionInsert = null;
  private String aFunctionUpdate = null;
  private String aFunctionQuery = null;
  private String aFunctionVisible = null;
  private int iFieldCount = 10;

  public int getFieldCount(){
    return iFieldCount;
  }

  public void setFunctionRole(String param){
    aFunctionRole = param;
  }

  public String getFunctionRole(){
    return aFunctionRole;
  }

  public void setFunctionName(String param){
    aFunctionName = param;
  }

  public String getFunctionName(){
    return aFunctionName;
  }
  
  public void setFunctionUrl(String param){
    aFunctionUrl = param;
  }

  public String getFunctionUrl(){
    return aFunctionUrl;
  }
 
  public void setFunctionDesc(String param){
    aFunctionDesc = param;
  }

  public String getFunctionDesc(){
    return aFunctionDesc;
  }
  
  public void setFunctionGroup(String param){
    aFunctionGroup = param;
  }

  public String getFunctionGroup(){
    return aFunctionGroup;
  }

  public void setFunctionDelete(String param){
    aFunctionDelete = param;
  }

  public String getFunctionDelete(){
    return aFunctionDelete;
  }

  public void setFunctionInsert(String param){
    aFunctionInsert = param;
  }

  public String getFunctionInsert(){
    return aFunctionInsert;
  }

  public void setFunctionUpdate(String param){
    aFunctionUpdate = param;
  }

  public String getFunctionUpdate(){
    return aFunctionUpdate;
  }

  public void setFunctionQuery(String param){
    aFunctionQuery = param;
  }

  public String getFunctionQuery(){
    return aFunctionQuery;
  }

  public void setFunctionVisible(String param){
    aFunctionVisible = param;
  }

  public String getFunctionVisible(){
    return aFunctionVisible;
  }

public String toJson(){
    String sJson =  "";
    
    if(aFunctionRole!=null)
      sJson = sJson + "\"FunctionRole\":"+"\""+aFunctionRole+"\",";
    if(aFunctionName!=null)
      sJson = sJson + "\"FunctionName\":"+"\""+aFunctionName+"\",";
    if(aFunctionUrl!=null)
      sJson = sJson + "\"FunctionUrl\":"+"\""+aFunctionUrl+"\",";
    if(aFunctionDesc!=null)
      sJson = sJson + "\"FunctionDesc\":"+"\""+aFunctionDesc+"\",";
    if(aFunctionGroup!=null)
      sJson = sJson + "\"FunctionGroup\":"+"\""+aFunctionGroup+"\",";
    if(aFunctionDelete!=null)
      sJson = sJson + "\"FunctionDelete\":"+"\""+aFunctionDelete+"\",";
    if(aFunctionInsert!=null)
      sJson = sJson + "\"FunctionInsert\":"+"\""+aFunctionInsert+"\",";
    if(aFunctionUpdate!=null)
      sJson = sJson + "\"FunctionUpdate\":"+"\""+aFunctionUpdate+"\",";
    if(aFunctionQuery!=null)
      sJson = sJson + "\"FunctionQuery\":"+"\""+aFunctionQuery+"\",";
    if(aFunctionVisible!=null)
      sJson = sJson + "\"FunctionVisible\":"+"\""+aFunctionVisible+"\",";

      sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    
    return sJson;
  }

}