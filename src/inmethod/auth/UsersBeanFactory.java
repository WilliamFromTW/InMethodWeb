package inmethod.auth;

import inmethod.Global;
import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class UsersBeanFactory {

  private Connection aConn = null;
  private static UsersBeanFactory aBeanFactory = null;
  private UsersBeanFactory() { 
  }
  public static UsersBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new UsersBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(Users aUsers) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aUsers.getUserName() );
    aVector.add( aUsers.getUserPass() );
    aVector.add( aUsers.getUserValidate() );
    aVector.add( aUsers.getUserDesc() );
    int iReturn = SQLTools.getInstance().preparedInsert(aConn,"insert into USERS (USER_NAME,USER_PASS,USER_VALIDATE,USER_DESC)  values (?,?,?,?)",aVector);;
	Global.refreshAuthenticatedFunctionInfoList();
    return iReturn; 
  }

  public int delete(Users aUsers) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aUsers.getUserName());
    int iReturn =  SQLTools.getInstance().preparedDelete(aConn,"delete from USERS where USER_NAME=?",aVector);
	Global.refreshAuthenticatedFunctionInfoList();
    return iReturn; 
  }

  public int update(Users aUsers) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aUsers.getUserName() );
    aVector.add( aUsers.getUserPass() );
    aVector.add( aUsers.getUserValidate() );
    aVector.add( aUsers.getUserDesc() );
    aVector.add(aUsers.getUserName());
    int iReturn = SQLTools.getInstance().preparedUpdate(aConn,"update USERS set USER_NAME=?,USER_PASS=?,USER_VALIDATE=?,USER_DESC=? where USER_NAME=?" ,aVector);
    Global.refreshAuthenticatedFunctionInfoList();
    return iReturn; 
   }

  public DataSet Query(Users aUsers,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aUsers!=null ){
      if(aUsers.getUserName()!=null && !aUsers.getUserName().equals("") )
        aSqlCondition.and("USER_NAME='" + aUsers.getUserName()+ "'");
      if(aUsers.getUserPass()!=null && !aUsers.getUserPass().equals("") )
        aSqlCondition.and("USER_PASS='" + aUsers.getUserPass()+ "'");
      if(aUsers.getUserValidate()!=null && !aUsers.getUserValidate().equals("") )
        aSqlCondition.and("USER_VALIDATE='" + aUsers.getUserValidate()+ "'");
      if(aUsers.getUserDesc()!=null && !aUsers.getUserDesc().equals("") )
        aSqlCondition.and("USER_DESC='" + aUsers.getUserDesc()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aUsers==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from USERS order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from USERS where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aUsers==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from USERS" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from USERS where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    Users aTempUsers = null;
    while( aRS!=null && aRS.next()){
      aTempUsers = new Users();
      aTempUsers.setUserName((String)aRS.getObject("USER_NAME"));
      aTempUsers.setUserPass((String)aRS.getObject("USER_PASS"));
      aTempUsers.setUserValidate((String)aRS.getObject("USER_VALIDATE"));
      aTempUsers.setUserDesc((String)aRS.getObject("USER_DESC"));
      //System.out.println("USER_NAME"+(String)aRS.getObject("USER_NAME"));
      aDS.addData(aTempUsers);
    }
    return aDS;
  }

  public DataSet pageQuery(Users aUsers,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aUsers!=null ){
      if(aUsers.getUserName()!=null && !aUsers.getUserName().equals("") )
        aSqlCondition.and("USER_NAME='" + aUsers.getUserName()+ "'");
      if(aUsers.getUserPass()!=null && !aUsers.getUserPass().equals("") )
        aSqlCondition.and("USER_PASS='" + aUsers.getUserPass()+ "'");
      if(aUsers.getUserValidate()!=null && !aUsers.getUserValidate().equals("") )
        aSqlCondition.and("USER_VALIDATE='" + aUsers.getUserValidate()+ "'");
      if(aUsers.getUserDesc()!=null && !aUsers.getUserDesc().equals("") )
        aSqlCondition.and("USER_DESC='" + aUsers.getUserDesc()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aUsers==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USERS order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USERS where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aUsers==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USERS" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from USERS where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    Users aTempUsers = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempUsers = new Users();
      aTempUsers.setUserName((String)aRS.getObject("USER_NAME"));
      aTempUsers.setUserPass((String)aRS.getObject("USER_PASS"));
      aTempUsers.setUserValidate((String)aRS.getObject("USER_VALIDATE"));
      aTempUsers.setUserDesc((String)aRS.getObject("USER_DESC"));
      aDS.addData(aTempUsers);
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