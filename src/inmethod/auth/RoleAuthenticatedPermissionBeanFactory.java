package inmethod.auth;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class RoleAuthenticatedPermissionBeanFactory {

  private Connection aConn = null;
  private static RoleAuthenticatedPermissionBeanFactory aBeanFactory = null;
  private RoleAuthenticatedPermissionBeanFactory() { 
  }
  public static RoleAuthenticatedPermissionBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new RoleAuthenticatedPermissionBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(RoleAuthenticatedPermission aRoleAuthenticatedPermission) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleAuthenticatedPermission.getRoleName() );
    aVector.add( aRoleAuthenticatedPermission.getFunctionName() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into ROLE_AUTHENTICATED_PERMISSION (ROLE_NAME,FUNCTION_NAME)  values (?,?)",aVector);
  }

  public int delete(RoleAuthenticatedPermission aRoleAuthenticatedPermission) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aRoleAuthenticatedPermission.getFunctionName());
    aVector.add(aRoleAuthenticatedPermission.getRoleName());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from ROLE_AUTHENTICATED_PERMISSION where FUNCTION_NAME=? and ROLE_NAME=?",aVector);
  }

  public int update(RoleAuthenticatedPermission aRoleAuthenticatedPermission) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleAuthenticatedPermission.getRoleName() );
    aVector.add( aRoleAuthenticatedPermission.getFunctionName() );
    aVector.add(aRoleAuthenticatedPermission.getFunctionName());
    aVector.add(aRoleAuthenticatedPermission.getRoleName());
    return SQLTools.getInstance().preparedUpdate(aConn,"update ROLE_AUTHENTICATED_PERMISSION set ROLE_NAME=?,FUNCTION_NAME=? where FUNCTION_NAME=? and ROLE_NAME=?" ,aVector);
   }

  public DataSet Query(RoleAuthenticatedPermission aRoleAuthenticatedPermission,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleAuthenticatedPermission!=null ){
      if(aRoleAuthenticatedPermission.getRoleName()!=null && !aRoleAuthenticatedPermission.getRoleName().equals("") )
        aSqlCondition.and("ROLE_NAME='" + aRoleAuthenticatedPermission.getRoleName()+ "'");
      if(aRoleAuthenticatedPermission.getFunctionName()!=null && !aRoleAuthenticatedPermission.getFunctionName().equals("") )
        aSqlCondition.and("FUNCTION_NAME='" + aRoleAuthenticatedPermission.getFunctionName()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleAuthenticatedPermission==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aRoleAuthenticatedPermission==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    RoleAuthenticatedPermission aTempRoleAuthenticatedPermission = null;
    while( aRS!=null && aRS.next()){
      aTempRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
      aTempRoleAuthenticatedPermission.setRoleName((String)aRS.getObject("ROLE_NAME"));
      aTempRoleAuthenticatedPermission.setFunctionName((String)aRS.getObject("FUNCTION_NAME"));
      aDS.addData(aTempRoleAuthenticatedPermission);
    }
    return aDS;
  }

  public DataSet pageQuery(RoleAuthenticatedPermission aRoleAuthenticatedPermission,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleAuthenticatedPermission!=null ){
      if(aRoleAuthenticatedPermission.getRoleName()!=null && !aRoleAuthenticatedPermission.getRoleName().equals("") )
        aSqlCondition.and("ROLE_NAME='" + aRoleAuthenticatedPermission.getRoleName()+ "'");
      if(aRoleAuthenticatedPermission.getFunctionName()!=null && !aRoleAuthenticatedPermission.getFunctionName().equals("") )
        aSqlCondition.and("FUNCTION_NAME='" + aRoleAuthenticatedPermission.getFunctionName()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleAuthenticatedPermission==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aRoleAuthenticatedPermission==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHENTICATED_PERMISSION where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    RoleAuthenticatedPermission aTempRoleAuthenticatedPermission = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempRoleAuthenticatedPermission = new RoleAuthenticatedPermission();
      aTempRoleAuthenticatedPermission.setRoleName((String)aRS.getObject("ROLE_NAME"));
      aTempRoleAuthenticatedPermission.setFunctionName((String)aRS.getObject("FUNCTION_NAME"));
      aDS.addData(aTempRoleAuthenticatedPermission);
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