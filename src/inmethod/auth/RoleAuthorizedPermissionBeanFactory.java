package inmethod.auth;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class RoleAuthorizedPermissionBeanFactory {

  private Connection aConn = null;
  private static RoleAuthorizedPermissionBeanFactory aBeanFactory = null;
  private RoleAuthorizedPermissionBeanFactory() { 
  }
  public static RoleAuthorizedPermissionBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new RoleAuthorizedPermissionBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(RoleAuthorizedPermission aRoleAuthorizedPermission) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleAuthorizedPermission.getFunctionRole() );
    aVector.add( aRoleAuthorizedPermission.getFunctionName() );
    aVector.add( aRoleAuthorizedPermission.getFunctionGroup() );
    aVector.add( aRoleAuthorizedPermission.getFunctionDelete() );
    aVector.add( aRoleAuthorizedPermission.getFunctionInsert() );
    aVector.add( aRoleAuthorizedPermission.getFunctionUpdate() );
    aVector.add( aRoleAuthorizedPermission.getFunctionQuery() );
    aVector.add( aRoleAuthorizedPermission.getFunctionVisible() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into ROLE_AUTHORIZED_PERMISSION (FUNCTION_ROLE,FUNCTION_NAME,FUNCTION_GROUP,FUNCTION_DELETE,FUNCTION_INSERT,FUNCTION_UPDATE,FUNCTION_QUERY,FUNCTION_VISIBLE)  values (?,?,?,?,?,?,?,?)",aVector);
  }

  public int delete(RoleAuthorizedPermission aRoleAuthorizedPermission) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aRoleAuthorizedPermission.getFunctionName());
    aVector.add(aRoleAuthorizedPermission.getFunctionRole());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from ROLE_AUTHORIZED_PERMISSION where FUNCTION_NAME=? and FUNCTION_ROLE=?",aVector);
  }

  public int update(RoleAuthorizedPermission aRoleAuthorizedPermission) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleAuthorizedPermission.getFunctionRole() );
    aVector.add( aRoleAuthorizedPermission.getFunctionName() );
    aVector.add( aRoleAuthorizedPermission.getFunctionGroup() );
    aVector.add( aRoleAuthorizedPermission.getFunctionDelete() );
    aVector.add( aRoleAuthorizedPermission.getFunctionInsert() );
    aVector.add( aRoleAuthorizedPermission.getFunctionUpdate() );
    aVector.add( aRoleAuthorizedPermission.getFunctionQuery() );
    aVector.add( aRoleAuthorizedPermission.getFunctionVisible() );
    aVector.add(aRoleAuthorizedPermission.getFunctionName());
    aVector.add(aRoleAuthorizedPermission.getFunctionRole());
    return SQLTools.getInstance().preparedUpdate(aConn,"update ROLE_AUTHORIZED_PERMISSION set FUNCTION_ROLE=?,FUNCTION_NAME=?,FUNCTION_GROUP=?,FUNCTION_DELETE=?,FUNCTION_INSERT=?,FUNCTION_UPDATE=?,FUNCTION_QUERY=?,FUNCTION_VISIBLE=? where FUNCTION_NAME=? and FUNCTION_ROLE=?" ,aVector);
   }

  public DataSet Query(RoleAuthorizedPermission aRoleAuthorizedPermission,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleAuthorizedPermission!=null ){
      if(aRoleAuthorizedPermission.getFunctionRole()!=null && !aRoleAuthorizedPermission.getFunctionRole().equals("") )
        aSqlCondition.and("FUNCTION_ROLE='" + aRoleAuthorizedPermission.getFunctionRole()+ "'");
      if(aRoleAuthorizedPermission.getFunctionName()!=null && !aRoleAuthorizedPermission.getFunctionName().equals("") )
        aSqlCondition.and("FUNCTION_NAME='" + aRoleAuthorizedPermission.getFunctionName()+ "'");
      if(aRoleAuthorizedPermission.getFunctionGroup()!=null && !aRoleAuthorizedPermission.getFunctionGroup().equals("") )
        aSqlCondition.and("FUNCTION_GROUP='" + aRoleAuthorizedPermission.getFunctionGroup()+ "'");
      if(aRoleAuthorizedPermission.getFunctionDelete()!=null && !aRoleAuthorizedPermission.getFunctionDelete().equals("") )
        aSqlCondition.and("FUNCTION_DELETE='" + aRoleAuthorizedPermission.getFunctionDelete()+ "'");
      if(aRoleAuthorizedPermission.getFunctionInsert()!=null && !aRoleAuthorizedPermission.getFunctionInsert().equals("") )
        aSqlCondition.and("FUNCTION_INSERT='" + aRoleAuthorizedPermission.getFunctionInsert()+ "'");
      if(aRoleAuthorizedPermission.getFunctionUpdate()!=null && !aRoleAuthorizedPermission.getFunctionUpdate().equals("") )
        aSqlCondition.and("FUNCTION_UPDATE='" + aRoleAuthorizedPermission.getFunctionUpdate()+ "'");
      if(aRoleAuthorizedPermission.getFunctionQuery()!=null && !aRoleAuthorizedPermission.getFunctionQuery().equals("") )
        aSqlCondition.and("FUNCTION_QUERY='" + aRoleAuthorizedPermission.getFunctionQuery()+ "'");
      if(aRoleAuthorizedPermission.getFunctionVisible()!=null && !aRoleAuthorizedPermission.getFunctionVisible().equals("") )
        aSqlCondition.and("FUNCTION_VISIBLE='" + aRoleAuthorizedPermission.getFunctionVisible()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleAuthorizedPermission==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHORIZED_PERMISSION order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHORIZED_PERMISSION where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aRoleAuthorizedPermission==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHORIZED_PERMISSION" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_AUTHORIZED_PERMISSION where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    RoleAuthorizedPermission aTempFunctionInfo = null;
    while( aRS!=null && aRS.next()){
      aTempFunctionInfo = new RoleAuthorizedPermission();
      aTempFunctionInfo.setFunctionRole((String)aRS.getObject("FUNCTION_ROLE"));
      aTempFunctionInfo.setFunctionName((String)aRS.getObject("FUNCTION_NAME"));
      aTempFunctionInfo.setFunctionGroup((String)aRS.getObject("FUNCTION_GROUP"));
      aTempFunctionInfo.setFunctionDelete((String)aRS.getObject("FUNCTION_DELETE"));
      aTempFunctionInfo.setFunctionInsert((String)aRS.getObject("FUNCTION_INSERT"));
      aTempFunctionInfo.setFunctionUpdate((String)aRS.getObject("FUNCTION_UPDATE"));
      aTempFunctionInfo.setFunctionQuery((String)aRS.getObject("FUNCTION_QUERY"));
      aTempFunctionInfo.setFunctionVisible((String)aRS.getObject("FUNCTION_VISIBLE"));
      aDS.addData(aTempFunctionInfo);
    }
    return aDS;
  }

  public DataSet pageQuery(RoleAuthorizedPermission aRoleAuthorizedPermission,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleAuthorizedPermission!=null ){
      if(aRoleAuthorizedPermission.getFunctionRole()!=null && !aRoleAuthorizedPermission.getFunctionRole().equals("") )
        aSqlCondition.and("FUNCTION_ROLE='" + aRoleAuthorizedPermission.getFunctionRole()+ "'");
      if(aRoleAuthorizedPermission.getFunctionName()!=null && !aRoleAuthorizedPermission.getFunctionName().equals("") )
        aSqlCondition.and("FUNCTION_NAME='" + aRoleAuthorizedPermission.getFunctionName()+ "'");
      if(aRoleAuthorizedPermission.getFunctionGroup()!=null && !aRoleAuthorizedPermission.getFunctionGroup().equals("") )
        aSqlCondition.and("FUNCTION_GROUP='" + aRoleAuthorizedPermission.getFunctionGroup()+ "'");
      if(aRoleAuthorizedPermission.getFunctionDelete()!=null && !aRoleAuthorizedPermission.getFunctionDelete().equals("") )
        aSqlCondition.and("FUNCTION_DELETE='" + aRoleAuthorizedPermission.getFunctionDelete()+ "'");
      if(aRoleAuthorizedPermission.getFunctionInsert()!=null && !aRoleAuthorizedPermission.getFunctionInsert().equals("") )
        aSqlCondition.and("FUNCTION_INSERT='" + aRoleAuthorizedPermission.getFunctionInsert()+ "'");
      if(aRoleAuthorizedPermission.getFunctionUpdate()!=null && !aRoleAuthorizedPermission.getFunctionUpdate().equals("") )
        aSqlCondition.and("FUNCTION_UPDATE='" + aRoleAuthorizedPermission.getFunctionUpdate()+ "'");
      if(aRoleAuthorizedPermission.getFunctionQuery()!=null && !aRoleAuthorizedPermission.getFunctionQuery().equals("") )
        aSqlCondition.and("FUNCTION_QUERY='" + aRoleAuthorizedPermission.getFunctionQuery()+ "'");
      if(aRoleAuthorizedPermission.getFunctionVisible()!=null && !aRoleAuthorizedPermission.getFunctionVisible().equals("") )
        aSqlCondition.and("FUNCTION_VISIBLE='" + aRoleAuthorizedPermission.getFunctionVisible()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleAuthorizedPermission==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHORIZED_PERMISSION order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHORIZED_PERMISSION where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aRoleAuthorizedPermission==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHORIZED_PERMISSION" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_AUTHORIZED_PERMISSION where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    RoleAuthorizedPermission aTempFunctionInfo = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempFunctionInfo = new RoleAuthorizedPermission();
      aTempFunctionInfo.setFunctionRole((String)aRS.getObject("FUNCTION_ROLE"));
      aTempFunctionInfo.setFunctionName((String)aRS.getObject("FUNCTION_NAME"));
      aTempFunctionInfo.setFunctionGroup((String)aRS.getObject("FUNCTION_GROUP"));
      aTempFunctionInfo.setFunctionDelete((String)aRS.getObject("FUNCTION_DELETE"));
      aTempFunctionInfo.setFunctionInsert((String)aRS.getObject("FUNCTION_INSERT"));
      aTempFunctionInfo.setFunctionUpdate((String)aRS.getObject("FUNCTION_UPDATE"));
      aTempFunctionInfo.setFunctionQuery((String)aRS.getObject("FUNCTION_QUERY"));
      aTempFunctionInfo.setFunctionVisible((String)aRS.getObject("FUNCTION_VISIBLE"));
      aDS.addData(aTempFunctionInfo);
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