package inmethod.hr;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class DepartmentBeanFactory {

  private Connection aConn = null;
  private static DepartmentBeanFactory aBeanFactory = null;
  private DepartmentBeanFactory() { 
  }
  public static DepartmentBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new DepartmentBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(Department aDepartment) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aDepartment.getDeptId() );
    aVector.add( aDepartment.getDeptName() );
    aVector.add( aDepartment.getDeptLeaderId() );
    aVector.add( aDepartment.getParentdeptId() );
    aVector.add( aDepartment.getDeptValidate() );
    aVector.add( aDepartment.getCatalog());
    return SQLTools.getInstance().preparedInsert(aConn,"insert into DEPARTMENT (DEPT_ID,DEPT_NAME,DEPT_LEADER_ID,PARENTDEPT_ID,DEPT_VALIDATE,CATALOG)  values (?,?,?,?,?,?)",aVector);
  }

  public int delete(Department aDepartment) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aDepartment.getDeptId());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from DEPARTMENT where DEPT_ID=?",aVector);
  }

  public int update(Department aDepartment) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aDepartment.getDeptId() );
    aVector.add( aDepartment.getDeptName() );
    aVector.add( aDepartment.getDeptLeaderId() );
    aVector.add( aDepartment.getParentdeptId() );
    aVector.add( aDepartment.getDeptValidate() );
    aVector.add( aDepartment.getCatalog());
    aVector.add(aDepartment.getDeptId());
    return SQLTools.getInstance().preparedUpdate(aConn,"update DEPARTMENT set DEPT_ID=?,DEPT_NAME=?,DEPT_LEADER_ID=?,PARENTDEPT_ID=?,DEPT_VALIDATE=?,CATALOG=? where DEPT_ID=?" ,aVector);
   }

  public DataSet Query(Department aDepartment,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aDepartment!=null ){
      if(aDepartment.getDeptId()!=null && !aDepartment.getDeptId().equals("") )
        aSqlCondition.and("DEPT_ID='" + aDepartment.getDeptId()+ "'");
      if(aDepartment.getDeptName()!=null && !aDepartment.getDeptName().equals("") )
        aSqlCondition.and("DEPT_NAME='" + aDepartment.getDeptName()+ "'");
      if(aDepartment.getDeptLeaderId()!=null && !aDepartment.getDeptLeaderId().equals("") )
        aSqlCondition.and("DEPT_LEADER_ID='" + aDepartment.getDeptLeaderId()+ "'");
      if(aDepartment.getParentdeptId()!=null && !aDepartment.getParentdeptId().equals("") )
        aSqlCondition.and("PARENTDEPT_ID='" + aDepartment.getParentdeptId()+ "'");
      if(aDepartment.getDeptValidate()!=null && !aDepartment.getDeptValidate().equals("") )
        aSqlCondition.and("DEPT_VALIDATE='" + aDepartment.getDeptValidate()+ "'");
      if(aDepartment.getCatalog()!=null  )
          aSqlCondition.and("CATALOG=" + aDepartment.getCatalog()+ "");
      
    }
    //if( aDepartment!=null )    System.out.println("sadf="+ aDepartment.getCatalog());
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aDepartment==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from DEPARTMENT order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from DEPARTMENT where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aDepartment==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from DEPARTMENT" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from DEPARTMENT where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    Department aTempDepartment = null;
    while( aRS!=null && aRS.next()){
      aTempDepartment = new Department();
      aTempDepartment.setDeptId((String)aRS.getObject("DEPT_ID"));
      aTempDepartment.setDeptName((String)aRS.getObject("DEPT_NAME"));
      aTempDepartment.setDeptLeaderId((String)aRS.getObject("DEPT_LEADER_ID"));
      aTempDepartment.setParentdeptId((String)aRS.getObject("PARENTDEPT_ID"));
      aTempDepartment.setDeptValidate((String)aRS.getObject("DEPT_VALIDATE"));
      aTempDepartment.setCatalog(aRS.getInt("CATALOG"));
      
      aDS.addData(aTempDepartment);
    }
    return aDS;
  }

  public DataSet pageQuery(Department aDepartment,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aDepartment!=null ){
      if(aDepartment.getDeptId()!=null && !aDepartment.getDeptId().equals("") )
        aSqlCondition.and("DEPT_ID='" + aDepartment.getDeptId()+ "'");
      if(aDepartment.getDeptName()!=null && !aDepartment.getDeptName().equals("") )
        aSqlCondition.and("DEPT_NAME='" + aDepartment.getDeptName()+ "'");
      if(aDepartment.getDeptLeaderId()!=null && !aDepartment.getDeptLeaderId().equals("") )
        aSqlCondition.and("DEPT_LEADER_ID='" + aDepartment.getDeptLeaderId()+ "'");
      if(aDepartment.getParentdeptId()!=null && !aDepartment.getParentdeptId().equals("") )
        aSqlCondition.and("PARENTDEPT_ID='" + aDepartment.getParentdeptId()+ "'");
      if(aDepartment.getDeptValidate()!=null && !aDepartment.getDeptValidate().equals("") )
        aSqlCondition.and("DEPT_VALIDATE='" + aDepartment.getDeptValidate()+ "'");
      if(aDepartment.getCatalog()!=null  )
          aSqlCondition.and("CATALOG=" + aDepartment.getCatalog()+ "");
      
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aDepartment==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DEPARTMENT order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DEPARTMENT where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aDepartment==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DEPARTMENT" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DEPARTMENT where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    Department aTempDepartment = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempDepartment = new Department();
      aTempDepartment.setDeptId((String)aRS.getObject("DEPT_ID"));
      aTempDepartment.setDeptName((String)aRS.getObject("DEPT_NAME"));
      aTempDepartment.setDeptLeaderId((String)aRS.getObject("DEPT_LEADER_ID"));
      aTempDepartment.setParentdeptId((String)aRS.getObject("PARENTDEPT_ID"));
      aTempDepartment.setDeptValidate((String)aRS.getObject("DEPT_VALIDATE"));
      aTempDepartment.setCatalog(aRS.getInt("CATALOG"));
      aDS.addData(aTempDepartment);
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