package inmethod.auth;

public class RoleList {


  public static final String FIELD_ROLE_NAME = "RoleName";
  public static final String FIELD_ROLE_DESC = "RoleDesc";
  public static final String FIELD_ROLE_CATALOG_ID = "RoleCatalogId";

  private String aRoleName = null;
  private String aRoleDesc = null;
  private String aRoleCatalogId = null;
  private int iFieldCount = 3;

  public int getFieldCount(){
    return iFieldCount;
  }


  public void setRoleName(String param){
    aRoleName = param;
  }

  public String getRoleName(){
    return aRoleName;
  }

  public void setRoleDesc(String param){
    aRoleDesc = param;
  }

  public String getRoleDesc(){
    return aRoleDesc;
  }

  public void setRoleCatalogId(String param){
    aRoleCatalogId = param;
  }

  public String getRoleCatalogId(){
    return aRoleCatalogId;
  }

public String toJson(){
    String sJson =  "";
    if(aRoleName!=null)
      sJson = sJson + "\"RoleName\":"+"\""+aRoleName+"\",";
    if(aRoleDesc!=null)
      sJson = sJson + "\"RoleDesc\":"+"\""+aRoleDesc+"\",";
    if(aRoleCatalogId!=null)
      sJson = sJson + "\"RoleCatalogId\":"+"\""+aRoleCatalogId+"\",";

    sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}