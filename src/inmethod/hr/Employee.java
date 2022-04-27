package inmethod.hr;

import java.sql.Connection;
import java.util.HashMap;

import inmethod.commons.rdb.DataSet;
import inmethod.db.DBConnectionManager;

public class Employee {


  public static final String FIELD_USER_ID = "UserId";
  public static final String FIELD_USER_NAME = "UserName";
  public static final String FIELD_USER_ENGLISH_NAME = "UserEnglishName";
  public static final String FIELD_USER_DEPT_ID = "UserDeptId";
  public static final String FIELD_USER_MAIL = "UserMail";
  public static final String FIELD_USER_ONBORAD_DATE = "UserOnboradDate";
  public static final String FIELD_USER_DEPARTURE_DATE = "UserDepartureDate";
  public static final String FIELD_USER_DEPT_NAME = "UserDeptName";
  public static final String FIELD_USER_LANGUAGE = "UserLanguage";
  public static final String FIELD_CATALOG = "Catalog";
  private static HashMap<String,Employee> aAllEmployee = null;
  private static Employee aEmployee = null;
  
  private String aUserId = null;
  private String aUserName = null;
  private String aUserEnglishName = null;
  private String aUserDeptId = null;
  private String aUserMail = null;
  private String aUserOnboradDate = null;
  private String aUserDepartureDate = null;
  private String aUserDeptName = null;
  private String aUserLanguage = null;
  private Integer aCatalog = null;
  private int iFieldCount = 10;

  
  public static Employee getInstance(){
	  if( aEmployee== null ) aEmployee = new Employee();
	  if( aAllEmployee==null){
		  aAllEmployee = new HashMap<String,Employee>();
		  aEmployee.refreshAllEmployee();
	  }
	  return aEmployee;
  }
  
  public void refreshAllEmployee(){
	  System.out.println("refresh employee value objects");
	  try (Connection aConn =DBConnectionManager.getWebDBConnection().getConnection() ) {
		  aAllEmployee.clear();
		  Employee aTmpEmployee = null;
		  EmployeeBeanFactory aEmployeeBeanFactory = EmployeeBeanFactory.getInstance(aConn);
		  DataSet aDS = aEmployeeBeanFactory.Query(aTmpEmployee, null);
		  if(aDS!=null)
		    while(aDS.next()){
		    	aTmpEmployee = (Employee)aDS.getData();
		    	//System.out.println("refresh employee id = "+aTmpEmployee.getUserId() );
		    	aAllEmployee.put(aTmpEmployee.getUserId(),aTmpEmployee);
		    	//System.out.println( aAllEmployee.get(aTmpEmployee.getUserId() ).getUserLanguage());
		    }
		  
	  }catch(Exception ee){
		  ee.printStackTrace();
	  }
  }
  public Employee getEmployee(String sUserID){
	//  System.out.println("sadfsadf="+sUserID);
	return   aAllEmployee.get(sUserID);
  }
  public int getFieldCount(){
    return iFieldCount;
  }

  public void setUserId(String param){
    aUserId = param;
  }

  public String getUserId(){
    return aUserId;
  }

  public void setUserName(String param){
    aUserName = param;
  }

  public String getUserName(){
    return aUserName;
  }

  public void setUserEnglishName(String param){
    aUserEnglishName = param;
  }

  public String getUserEnglishName(){
    return aUserEnglishName;
  }

  public void setUserDeptId(String param){
    aUserDeptId = param;
  }

  public String getUserDeptId(){
    return aUserDeptId;
  }

  public void setUserMail(String param){
    aUserMail = param;
  }

  public String getUserMail(){
    return aUserMail;
  }

  public void setUserOnboradDate(String param){
    aUserOnboradDate = param;
  }

  public String getUserOnboradDate(){
    return aUserOnboradDate;
  }

  public void setUserDepartureDate(String param){
    aUserDepartureDate = param;
  }

  public String getUserDepartureDate(){
    return aUserDepartureDate;
  }
  
  public void setUserDeptName(String param){
	aUserDeptName = param;
  }
  
  public String getUserDeptName(){
	return aUserDeptName;
  }

  public void setUserLanguage(String param){
	aUserLanguage = param;
  }
  
  public String getUserLanguage(){
	return aUserLanguage;
  }

  public void setCatalog(int param){
	aCatalog = param;
  }
  
  public Integer getCatalog(){
	return aCatalog;
  }
  
  
public String toJson(){
    String sJson =  "";
    if(aUserId!=null)
        sJson = sJson + "\"UserId\":"+"\""+aUserId+"\",";
      if(aUserName!=null)
        sJson = sJson + "\"UserName\":"+"\""+aUserName+"\",";
      if(aUserEnglishName!=null)
        sJson = sJson + "\"UserEnglishName\":"+"\""+aUserEnglishName+"\",";
      if(aUserDeptId!=null)
        sJson = sJson + "\"UserDeptId\":"+"\""+aUserDeptId+"\",";
      if(aUserMail!=null)
        sJson = sJson + "\"UserMail\":"+"\""+aUserMail+"\",";
      if(aUserOnboradDate!=null)
        sJson = sJson + "\"UserOnboradDate\":"+"\""+aUserOnboradDate+"\",";
      if(aUserDepartureDate!=null)
        sJson = sJson + "\"UserDepartureDate\":"+"\""+aUserDepartureDate+"\",";
      if(aUserDeptName!=null)
       sJson = sJson + "\"UserDeptName\":"+"\""+aUserDeptName+"\",";
      if(aUserDeptName!=null)
          sJson = sJson + "\"UserLanguage\":"+"\""+aUserLanguage+"\",";
      if(aCatalog!=null)
          sJson = sJson + "\"Catalog\":"+"\""+aCatalog+"\",";
      
      sJson = "{"+sJson.substring(0,sJson.length()-1)+"}";
    return sJson;
  }

}