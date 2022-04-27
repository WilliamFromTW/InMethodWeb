package inmethod.auth;

public class Users {


  public static final String FIELD_USER_NAME = "UserName";
  public static final String FIELD_USER_PASS = "UserPass";
  public static final String FIELD_USER_VALIDATE = "UserValidate";
  public static final String FIELD_USER_DESC = "UserDesc";

  private String aUserName = null;
  private String aUserPass = null;
  private String aUserValidate = null;
  private String aUserDesc = null;
  private int iFieldCount = 4;

  public int getFieldCount(){
    return iFieldCount;
  }


  public void setUserName(String param){
    aUserName = param;
  }

  public String getUserName(){
    return aUserName;
  }

  public void setUserPass(String param){
    aUserPass = param;
  }

  public String getUserPass(){
    return aUserPass;
  }

  public void setUserValidate(String param){
    aUserValidate = param;
  }

  public String getUserValidate(){
    return aUserValidate;
  }

  public void setUserDesc(String param){
    aUserDesc = param;
  }

  public String getUserDesc(){
    return aUserDesc;
  }

public String toJson(){
    String sJson =  "";
    if(aUserName!=null)
      sJson = sJson + "\"UserName\":"+"\""+aUserName+"\",";
    if(aUserPass!=null)
      sJson = sJson + "\"UserPass\":"+"\""+aUserPass+"\",";
    if(aUserValidate!=null)
      sJson = sJson + "\"UserValidate\":"+"\""+aUserValidate+"\",";
    if(aUserDesc!=null)
      sJson = sJson + "\"UserDesc\":"+"\""+aUserDesc+"\",";

    sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}