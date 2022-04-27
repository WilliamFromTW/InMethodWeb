package inmethod.auth;

public class RoleCatalog {


  public static final String FIELD_ROLE_CATALOG_ID = "RoleCatalogId";
  public static final String FIELD_ROLE_CATALOG_DESC = "RoleCatalogDesc";

  private String sFlowID = null;
  private String aRoleCatalogId = null;
  private String aRoleCatalogDesc = null;
  private int iFieldCount = 2;

  public int getFieldCount(){
    return iFieldCount;
  }

  public void setRoleCatalogId(String param){
    aRoleCatalogId = param;
  }

  public String getRoleCatalogId(){
    return aRoleCatalogId;
  }

  public void setRoleCatalogDesc(String param){
    aRoleCatalogDesc = param;
  }

  public String getRoleCatalogDesc(){
    return aRoleCatalogDesc;
  }

public String toJson(){
    String sJson =  "";
    sJson = sJson + "{\"RoleCatalogId\":"+"\""+aRoleCatalogId+"\",";
    sJson = sJson + "\"RoleCatalogDesc\":"+"\""+aRoleCatalogDesc+"\"}";
    return sJson;
  }

}