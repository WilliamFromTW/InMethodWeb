package inmethod.auth;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class UserRolesBeanFactory {

  private Connection aConn = null;
  private static UserRolesBeanFactory aBeanFactory = null;
  private UserRolesBeanFactory() { 
  }
  public static UserRolesBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new UserRolesBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(UserRoles aUserRoles) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aUserRoles.getUserName() );
    aVector.add( aUserRoles.getRoleName() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into USER_ROLES (USER_NAME,ROLE_NAME)  values (?,?)",aVector);
  }

  public int delete(UserRoles aUserRoles) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aUserRoles.getRoleName());
    aVector.add(aUserRoles.getUserName());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from USER_ROLES where ROLE_NAME=? and USER_NAME=?",aVector);
  }

  public int update(UserRoles aUserRoles) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aUserRoles.getUserName() );
    aVector.add( aUserRoles.getRoleName() );
    aVector.add(aUserRoles.getRoleName());
    aVector.add(aUserRoles.getUserName());
    return SQLTools.getInstance().preparedUpdate(aConn,"update USER_ROLES set USER_NAME=?,ROLE_NAME=? where ROLE_NAME=? and USER_NAME=?" ,aVector);
   }

  public DataSet Query(UserRoles aUserRoles,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aUserRoles!=null ){
      if(aUserRoles.getUserName()!=null && !aUserRoles.getUserName().equals("") )
        aSqlCondition.and("USER_NAME='" + aUserRoles.getUserName()+ "'");
      if(aUserRoles.getRoleName()!=null && !aUserRoles.getRoleName().equals("") )
        aSqlCondition.and("ROLE_NAME='" + aUserRoles.getRoleName()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aUserRoles==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from USER_ROLES order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from USER_ROLES where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aUserRoles==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from USER_ROLES" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from USER_ROLES where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    UserRoles aTempUserRoles = null;
    while( aRS!=null && aRS.next()){
      aTempUserRoles = new UserRoles();
      aTempUserRoles.setUserName((String)aRS.getObject("USER_NAME"));
      aTempUserRoles.setRoleName((String)aRS.getObject("ROLE_NAME"));
      aDS.addData(aTempUserRoles);
    }
    return aDS;
  }

  public DataSet pageQuery(UserRoles aUserRoles,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aUserRoles!=null ){
      if(aUserRoles.getUserName()!=null && !aUserRoles.getUserName().equals("") )
        aSqlCondition.and("USER_NAME='" + aUserRoles.getUserName()+ "'");
      if(aUserRoles.getRoleName()!=null && !aUserRoles.getRoleName().equals("") )
        aSqlCondition.and("ROLE_NAME='" + aUserRoles.getRoleName()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aUserRoles==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USER_ROLES order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USER_ROLES where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aUserRoles==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USER_ROLES" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USER_ROLES where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    UserRoles aTempUserRoles = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempUserRoles = new UserRoles();
      aTempUserRoles.setUserName((String)aRS.getObject("USER_NAME"));
      aTempUserRoles.setRoleName((String)aRS.getObject("ROLE_NAME"));
      aDS.addData(aTempUserRoles);
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