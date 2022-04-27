package inmethod.auth;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class RoleListBeanFactory {

  private Connection aConn = null;
  private static RoleListBeanFactory aBeanFactory = null;
  private RoleListBeanFactory() { 
  }
  public static RoleListBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new RoleListBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(RoleList aRoleList) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleList.getRoleName() );
    aVector.add( aRoleList.getRoleDesc() );
    aVector.add( aRoleList.getRoleCatalogId() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into ROLE_LIST (ROLE_NAME,ROLE_DESC,ROLE_CATALOG_ID)  values (?,?,?)",aVector);
  }

  public int delete(RoleList aRoleList) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aRoleList.getRoleName());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from ROLE_LIST where ROLE_NAME=?",aVector);
  }

  public int update(RoleList aRoleList) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleList.getRoleName() );
    aVector.add( aRoleList.getRoleDesc() );
    aVector.add( aRoleList.getRoleCatalogId() );
    aVector.add(aRoleList.getRoleName());
    return SQLTools.getInstance().preparedUpdate(aConn,"update ROLE_LIST set ROLE_NAME=?,ROLE_DESC=?,ROLE_CATALOG_ID=? where ROLE_NAME=?" ,aVector);
   }

  public DataSet Query(RoleList aRoleList,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleList!=null ){
      if(aRoleList.getRoleName()!=null && !aRoleList.getRoleName().equals("") )
        aSqlCondition.and("ROLE_NAME='" + aRoleList.getRoleName()+ "'");
      if(aRoleList.getRoleDesc()!=null && !aRoleList.getRoleDesc().equals("") )
        aSqlCondition.and("ROLE_DESC='" + aRoleList.getRoleDesc()+ "'");
      if(aRoleList.getRoleCatalogId()!=null && !aRoleList.getRoleCatalogId().equals("") )
        aSqlCondition.and("ROLE_CATALOG_ID='" + aRoleList.getRoleCatalogId()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleList==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_LIST order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_LIST where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aRoleList==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_LIST" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_LIST where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    RoleList aTempRoleList = null;
    while( aRS!=null && aRS.next()){
      aTempRoleList = new RoleList();
      aTempRoleList.setRoleName((String)aRS.getObject("ROLE_NAME"));
      aTempRoleList.setRoleDesc((String)aRS.getObject("ROLE_DESC"));
      aTempRoleList.setRoleCatalogId((String)aRS.getObject("ROLE_CATALOG_ID"));
      aDS.addData(aTempRoleList);
    }
    return aDS;
  }

  public DataSet pageQuery(RoleList aRoleList,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleList!=null ){
      if(aRoleList.getRoleName()!=null && !aRoleList.getRoleName().equals("") )
        aSqlCondition.and("ROLE_NAME='" + aRoleList.getRoleName()+ "'");
      if(aRoleList.getRoleDesc()!=null && !aRoleList.getRoleDesc().equals("") )
        aSqlCondition.and("ROLE_DESC='" + aRoleList.getRoleDesc()+ "'");
      if(aRoleList.getRoleCatalogId()!=null && !aRoleList.getRoleCatalogId().equals("") )
        aSqlCondition.and("ROLE_CATALOG_ID='" + aRoleList.getRoleCatalogId()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleList==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_LIST order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_LIST where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aRoleList==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_LIST" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_LIST where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    RoleList aTempRoleList = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempRoleList = new RoleList();
      aTempRoleList.setRoleName((String)aRS.getObject("ROLE_NAME"));
      aTempRoleList.setRoleDesc((String)aRS.getObject("ROLE_DESC"));
      aTempRoleList.setRoleCatalogId((String)aRS.getObject("ROLE_CATALOG_ID"));
      aDS.addData(aTempRoleList);
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