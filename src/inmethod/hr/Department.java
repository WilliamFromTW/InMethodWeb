package inmethod.hr;

public class Department {


  public static final String FIELD_DEPT_ID = "DeptId";
  public static final String FIELD_DEPT_NAME = "DeptName";
  public static final String FIELD_DEPT_LEADER_ID = "DeptLeaderId";
  public static final String FIELD_PARENTDEPT_ID = "ParentdeptId";
  public static final String FIELD_DEPT_VALIDATE = "DeptValidate";
  public static final String FIELD_CATALOG = "Catalog";


  private String aDeptId = null;
  private String aDeptName = null;
  private String aDeptLeaderId = null;
  private String aParentdeptId = null;
  private String aDeptValidate = null;
  private Integer aCatalog = null;
  private int iFieldCount = 6;

  public int getFieldCount(){
    return iFieldCount;
  }

  public void setDeptId(String param){
    aDeptId = param;
  }

  public String getDeptId(){
    return aDeptId;
  }

  public void setDeptName(String param){
    aDeptName = param;
  }

  public String getDeptName(){
    return aDeptName;
  }

  public void setDeptLeaderId(String param){
    aDeptLeaderId = param;
  }

  public String getDeptLeaderId(){
    return aDeptLeaderId;
  }

  public void setParentdeptId(String param){
    aParentdeptId = param;
  }

  public String getParentdeptId(){
    return aParentdeptId;
  }

  public void setDeptValidate(String param){
    aDeptValidate = param;
  }

  public String getDeptValidate(){
    return aDeptValidate;
  }

  public void setCatalog(int param){
	aCatalog = param;
  }

  public Integer getCatalog(){
	return aCatalog;
  }
  
public String toJson(){
    String sJson =  "";
    if(aDeptId!=null)
        sJson = sJson + "\"DeptId\":"+"\""+aDeptId+"\",";
      if(aDeptName!=null)
        sJson = sJson + "\"DeptName\":"+"\""+aDeptName+"\",";
      if(aDeptLeaderId!=null)
        sJson = sJson + "\"DeptLeaderId\":"+"\""+aDeptLeaderId+"\",";
      if(aParentdeptId!=null)
        sJson = sJson + "\"ParentdeptId\":"+"\""+aParentdeptId+"\",";
      if(aDeptValidate!=null)
        sJson = sJson + "\"DeptValidate\":"+"\""+aDeptValidate+"\",";
      if(aCatalog!=null)
          sJson = sJson + "\"Catalog\":"+"\""+aCatalog+"\",";

      sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}