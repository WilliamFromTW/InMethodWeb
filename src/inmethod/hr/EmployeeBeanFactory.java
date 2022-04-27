package inmethod.hr;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class EmployeeBeanFactory {

  private Connection aConn = null;
  private static EmployeeBeanFactory aBeanFactory = null;
  //private 
  private EmployeeBeanFactory() { 
  }
  public static EmployeeBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new EmployeeBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(Employee aEmployee) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aEmployee.getUserId() );
    aVector.add( aEmployee.getUserName() );
    aVector.add( aEmployee.getUserEnglishName() );
    aVector.add( aEmployee.getUserDeptId() );
    aVector.add( aEmployee.getUserMail() );
    aVector.add( aEmployee.getUserOnboradDate() );
    aVector.add( aEmployee.getUserDepartureDate() );
    aVector.add( aEmployee.getUserLanguage());
    aVector.add( aEmployee.getCatalog());
    return SQLTools.getInstance().preparedInsert(aConn,"insert into EMPLOYEE (USER_ID,USER_NAME,USER_ENGLISH_NAME,USER_DEPT_ID,USER_MAIL,USER_ONBORAD_DATE,USER_DEPARTURE_DATE,USER_LANGUAGE,CATALOG)  values (?,?,?,?,?,?,?,?,?)",aVector);
  }

  public int delete(Employee aEmployee) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aEmployee.getUserId());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from EMPLOYEE where USER_ID=?",aVector);
  }

  public int update(Employee aEmployee) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aEmployee.getUserId() );
    aVector.add( aEmployee.getUserName() );
    aVector.add( aEmployee.getUserEnglishName() );
    aVector.add( aEmployee.getUserDeptId() );
    aVector.add( aEmployee.getUserMail() );
    aVector.add( aEmployee.getUserOnboradDate() );
    aVector.add( aEmployee.getUserDepartureDate() );
    aVector.add(aEmployee.getUserLanguage()  );
    aVector.add(aEmployee.getCatalog() );
    aVector.add(aEmployee.getUserId());
    return SQLTools.getInstance().preparedUpdate(aConn,"update EMPLOYEE set USER_ID=?,USER_NAME=?,USER_ENGLISH_NAME=?,USER_DEPT_ID=?,USER_MAIL=?,USER_ONBORAD_DATE=?,USER_DEPARTURE_DATE=?,USER_LANGUAGE=?,CATALOG=?  where USER_ID=?" ,aVector);
   }

  public DataSet Query(Employee aEmployee,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aEmployee!=null ){
      if(aEmployee.getUserId()!=null && !aEmployee.getUserId().equals("") )
        aSqlCondition.and("USER_ID='" + aEmployee.getUserId()+ "'");
      if(aEmployee.getUserName()!=null && !aEmployee.getUserName().equals("") )
        aSqlCondition.and("USER_NAME='" + aEmployee.getUserName()+ "'");
      if(aEmployee.getUserEnglishName()!=null && !aEmployee.getUserEnglishName().equals("") )
        aSqlCondition.and("USER_ENGLISH_NAME='" + aEmployee.getUserEnglishName()+ "'");
      if(aEmployee.getUserDeptId()!=null && !aEmployee.getUserDeptId().equals("") )
        aSqlCondition.and("USER_DEPT_ID='" + aEmployee.getUserDeptId()+ "'");
      if(aEmployee.getUserMail()!=null && !aEmployee.getUserMail().equals("") )
        aSqlCondition.and("USER_MAIL='" + aEmployee.getUserMail()+ "'");
      if(aEmployee.getUserOnboradDate()!=null && !aEmployee.getUserOnboradDate().equals("") )
        aSqlCondition.and("USER_ONBORAD_DATE='" + aEmployee.getUserOnboradDate()+ "'");
      if(aEmployee.getUserDepartureDate()!=null && !aEmployee.getUserDepartureDate().equals("") )
        aSqlCondition.and("USER_DEPARTURE_DATE='" + aEmployee.getUserDepartureDate()+ "'");
      if(aEmployee.getUserLanguage()!=null && !aEmployee.getUserLanguage().equals("") )
          aSqlCondition.and("USER_LANGUAGE='" + aEmployee.getUserLanguage()+ "'");
      if(aEmployee.getCatalog() !=null  )
          aSqlCondition.and("employee.CATALOG=" + aEmployee.getCatalog()+ "");
      
    }
    ResultSet aRS = null;
//    System.out.println("aEmployee.getCatalog()="+aEmployee.getCatalog());
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aEmployee==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from EMPLOYEE left join DEPARTMENT on DEPARTMENT.DEPT_ID=EMPLOYEE.USER_DEPT_ID and DEPARTMENT.CATALOG=EMPLOYEE.CATALOG order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from EMPLOYEE left join DEPARTMENT on DEPARTMENT.DEPT_ID=EMPLOYEE.USER_DEPT_ID and DEPARTMENT.CATALOG=EMPLOYEE.CATALOG where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aEmployee==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from EMPLOYEE left join DEPARTMENT on DEPARTMENT.DEPT_ID=EMPLOYEE.USER_DEPT_ID and DEPARTMENT.CATALOG=EMPLOYEE.CATALOG" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from EMPLOYEE left join DEPARTMENT on DEPARTMENT.DEPT_ID=EMPLOYEE.USER_DEPT_ID and DEPARTMENT.CATALOG=EMPLOYEE.CATALOG where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    Employee aTempEmployee = null;
    while( aRS!=null && aRS.next()){
      aTempEmployee = new Employee();
      aTempEmployee.setUserId((String)aRS.getObject("USER_ID"));
      aTempEmployee.setUserName((String)aRS.getObject("USER_NAME"));
      aTempEmployee.setUserEnglishName((String)aRS.getObject("USER_ENGLISH_NAME"));
      aTempEmployee.setUserDeptId((String)aRS.getObject("USER_DEPT_ID"));
      aTempEmployee.setUserMail((String)aRS.getObject("USER_MAIL"));
      aTempEmployee.setUserOnboradDate((String)aRS.getObject("USER_ONBORAD_DATE"));
      aTempEmployee.setUserDepartureDate((String)aRS.getObject("USER_DEPARTURE_DATE"));
      if( aRS.getObject("DEPT_NAME")!=null )
        aTempEmployee.setUserDeptName((String)aRS.getObject("DEPT_NAME"));
      else
    	aTempEmployee.setUserDeptName("");
      aTempEmployee.setUserLanguage(((String)aRS.getObject("USER_LANGUAGE")));
      aTempEmployee.setCatalog( aRS.getInt("CATALOG"));
      
      //System.out.println("dept name="+(String)aRS.getObject("DEPT_NAME"));
      aDS.addData(aTempEmployee);
    }
    return aDS;
  }

  public DataSet pageQuery(Employee aEmployee,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aEmployee!=null ){
      if(aEmployee.getUserId()!=null && !aEmployee.getUserId().equals("") )
        aSqlCondition.and("USER_ID='" + aEmployee.getUserId()+ "'");
      if(aEmployee.getUserName()!=null && !aEmployee.getUserName().equals("") )
        aSqlCondition.and("USER_NAME='" + aEmployee.getUserName()+ "'");
      if(aEmployee.getUserEnglishName()!=null && !aEmployee.getUserEnglishName().equals("") )
        aSqlCondition.and("USER_ENGLISH_NAME='" + aEmployee.getUserEnglishName()+ "'");
      if(aEmployee.getUserDeptId()!=null && !aEmployee.getUserDeptId().equals("") )
        aSqlCondition.and("USER_DEPT_ID='" + aEmployee.getUserDeptId()+ "'");
      if(aEmployee.getUserMail()!=null && !aEmployee.getUserMail().equals("") )
        aSqlCondition.and("USER_MAIL='" + aEmployee.getUserMail()+ "'");
      if(aEmployee.getUserOnboradDate()!=null && !aEmployee.getUserOnboradDate().equals("") )
        aSqlCondition.and("USER_ONBORAD_DATE='" + aEmployee.getUserOnboradDate()+ "'");
      if(aEmployee.getUserDepartureDate()!=null && !aEmployee.getUserDepartureDate().equals("") )
        aSqlCondition.and("USER_DEPARTURE_DATE='" + aEmployee.getUserDepartureDate()+ "'");
      if(aEmployee.getUserLanguage()!=null && !aEmployee.getUserLanguage().equals("") )
          aSqlCondition.and("USER_LANGUAGE='" + aEmployee.getUserLanguage()+ "'");
      if(aEmployee.getCatalog()!=null && !aEmployee.getCatalog().equals("") )
          aSqlCondition.and("CATALOG=" + aEmployee.getCatalog()+ "");
      
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aEmployee==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from EMPLOYEE order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from EMPLOYEE where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aEmployee==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from EMPLOYEE" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from EMPLOYEE where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    Employee aTempEmployee = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempEmployee = new Employee();
      aTempEmployee.setUserId((String)aRS.getObject("USER_ID"));
      aTempEmployee.setUserName((String)aRS.getObject("USER_NAME"));
      aTempEmployee.setUserEnglishName((String)aRS.getObject("USER_ENGLISH_NAME"));
      aTempEmployee.setUserDeptId((String)aRS.getObject("USER_DEPT_ID"));
      aTempEmployee.setUserMail((String)aRS.getObject("USER_MAIL"));
      aTempEmployee.setUserOnboradDate((String)aRS.getObject("USER_ONBORAD_DATE"));
      aTempEmployee.setUserDepartureDate((String)aRS.getObject("USER_DEPARTURE_DATE"));
      aTempEmployee.setUserLanguage((String)aRS.getObject("USER_LANGUAGE"));
      aTempEmployee.setCatalog(aRS.getInt("CATALOG"));
      aDS.addData(aTempEmployee);
    }
    return aDS;
  }

  public long getTableTotalCount(String sTable){
      if( sTable==null || sTable.equals("") ) return -1;
      ResultSet aRS = null;
      try{
        aRS = SQLTools.getInstance().Select(aConn, "select count(*) as NUM from " + sTable );
        if( aRS!=null && aRS.next() )
          return aRS.getLong("NUM");
        else
          return -1;
      }catch(Exception ex){
        return -1;
      }
    }

}