package inmethod.auth;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class RoleCatalogBeanFactory {

  private Connection aConn = null;
  private static RoleCatalogBeanFactory aBeanFactory = null;
  private RoleCatalogBeanFactory() { 
  }
  public static RoleCatalogBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new RoleCatalogBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(RoleCatalog aRoleCatalog) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleCatalog.getRoleCatalogId() );
    aVector.add( aRoleCatalog.getRoleCatalogDesc() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into ROLE_CATALOG (ROLE_CATALOG_ID,ROLE_CATALOG_DESC)  values (?,?)",aVector);
  }

  public int delete(RoleCatalog aRoleCatalog) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aRoleCatalog.getRoleCatalogId());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from ROLE_CATALOG where ROLE_CATALOG_ID=?",aVector);
  }

  public int update(RoleCatalog aRoleCatalog) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aRoleCatalog.getRoleCatalogId() );
    aVector.add( aRoleCatalog.getRoleCatalogDesc() );
    aVector.add(aRoleCatalog.getRoleCatalogId());
    return SQLTools.getInstance().preparedUpdate(aConn,"update ROLE_CATALOG set ROLE_CATALOG_ID=?,ROLE_CATALOG_DESC=? where ROLE_CATALOG_ID=?" ,aVector);
   }

  public DataSet Query(RoleCatalog aRoleCatalog,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleCatalog!=null ){
      if(aRoleCatalog.getRoleCatalogId()!=null && !aRoleCatalog.getRoleCatalogId().equals("") )
        aSqlCondition.and("ROLE_CATALOG_ID='" + aRoleCatalog.getRoleCatalogId()+ "'");
      if(aRoleCatalog.getRoleCatalogDesc()!=null && !aRoleCatalog.getRoleCatalogDesc().equals("") )
        aSqlCondition.and("ROLE_CATALOG_DESC='" + aRoleCatalog.getRoleCatalogDesc()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleCatalog==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_CATALOG order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_CATALOG where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aRoleCatalog==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_CATALOG" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from ROLE_CATALOG where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    RoleCatalog aTempRoleCatalog = null;
    while( aRS!=null && aRS.next()){
      aTempRoleCatalog = new RoleCatalog();
      aTempRoleCatalog.setRoleCatalogId((String)aRS.getObject("ROLE_CATALOG_ID"));
      aTempRoleCatalog.setRoleCatalogDesc((String)aRS.getObject("ROLE_CATALOG_DESC"));
      aDS.addData(aTempRoleCatalog);
    }
    return aDS;
  }

  public DataSet pageQuery(RoleCatalog aRoleCatalog,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aRoleCatalog!=null ){
      if(aRoleCatalog.getRoleCatalogId()!=null && !aRoleCatalog.getRoleCatalogId().equals("") )
        aSqlCondition.and("ROLE_CATALOG_ID='" + aRoleCatalog.getRoleCatalogId()+ "'");
      if(aRoleCatalog.getRoleCatalogDesc()!=null && !aRoleCatalog.getRoleCatalogDesc().equals("") )
        aSqlCondition.and("ROLE_CATALOG_DESC='" + aRoleCatalog.getRoleCatalogDesc()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aRoleCatalog==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_CATALOG order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_CATALOG where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aRoleCatalog==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_CATALOG" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from ROLE_CATALOG where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    RoleCatalog aTempRoleCatalog = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempRoleCatalog = new RoleCatalog();
      aTempRoleCatalog.setRoleCatalogId((String)aRS.getObject("ROLE_CATALOG_ID"));
      aTempRoleCatalog.setRoleCatalogDesc((String)aRS.getObject("ROLE_CATALOG_DESC"));
      aDS.addData(aTempRoleCatalog);
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