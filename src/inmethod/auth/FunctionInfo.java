package inmethod.auth;

public class FunctionInfo {


  public static final String FIELD_FUNCTION_NAME = "FunctionName";
  public static final String FIELD_FUNCTION_URL = "FunctionUrl";
  public static final String FIELD_FUNCTION_DESC = "FunctionDesc";

  private String aFunctionName = null;
  private String aFunctionUrl = null;
  private String aFunctionDesc = null;
  private int iFieldCount = 3;

  public int getFieldCount(){
    return iFieldCount;
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

public String toJson(){
    String sJson =  "";
    if(aFunctionName!=null)
        sJson = sJson + "\"FunctionName\":"+"\""+aFunctionName+"\",";
      if(aFunctionUrl!=null)
        sJson = sJson + "\"FunctionUrl\":"+"\""+aFunctionUrl+"\",";
      if(aFunctionDesc!=null)
        sJson = sJson + "\"FunctionDesc\":"+"\""+aFunctionDesc+"\",";
      sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}