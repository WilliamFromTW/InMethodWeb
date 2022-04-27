package inmethod.news;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class NewsBeanFactory {

  private Connection aConn = null;
  private static NewsBeanFactory aBeanFactory = null;
  private NewsBeanFactory() { 
  }
  public static NewsBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new NewsBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(News aNews) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aNews.getOid() );
    aVector.add( aNews.getCatalog() );
    aVector.add( aNews.getSubject() );
    aVector.add( aNews.getMessage() );
    aVector.add( aNews.getUploadType() );
    aVector.add( aNews.getUploadName() );
    aVector.add( aNews.getUploadPath() );
    aVector.add( aNews.getUpdateDt() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into NEWS (OID,CATALOG,SUBJECT,MESSAGE,UPLOAD_TYPE,UPLOAD_NAME,UPLOAD_PATH,UPDATE_DT)  values (?,?,?,?,?,?,?,?)",aVector);
  }

  public int delete(News aNews) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aNews.getOid());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from NEWS where OID=?",aVector);
  }

  public int update(News aNews) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aNews.getOid() );
    aVector.add( aNews.getCatalog() );
    aVector.add( aNews.getSubject() );
    aVector.add( aNews.getMessage() );
    aVector.add( aNews.getUploadType() );
    aVector.add( aNews.getUploadName() );
    //aVector.add( aNews.getUploadPath() );
    aVector.add( aNews.getUpdateDt() );
    aVector.add(aNews.getOid());
    return SQLTools.getInstance().preparedUpdate(aConn,"update NEWS set OID=?,CATALOG=?,SUBJECT=?,MESSAGE=?,UPLOAD_TYPE=?,UPLOAD_NAME=?,UPDATE_DT=? where OID=?" ,aVector);
   }

  public DataSet Query(News aNews,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aNews!=null ){
      if(aNews.getOid()!=null && !aNews.getOid().equals("") )
        aSqlCondition.and("OID='" + aNews.getOid()+ "'");
      if(aNews.getCatalog()!=null && !aNews.getCatalog().equals("") )
        aSqlCondition.and("CATALOG='" + aNews.getCatalog()+ "'");
      if(aNews.getSubject()!=null && !aNews.getSubject().equals("") )
        aSqlCondition.and("SUBJECT='" + aNews.getSubject()+ "'");
      if(aNews.getMessage()!=null && !aNews.getMessage().equals("") )
        aSqlCondition.and("MESSAGE='" + aNews.getMessage()+ "'");
      if(aNews.getUploadType()!=null && !aNews.getUploadType().equals("") )
        aSqlCondition.and("UPLOAD_TYPE='" + aNews.getUploadType()+ "'");
      if(aNews.getUploadName()!=null && !aNews.getUploadName().equals("") )
        aSqlCondition.and("UPLOAD_NAME='" + aNews.getUploadName()+ "'");
      if(aNews.getUploadPath()!=null && !aNews.getUploadPath().equals("") )
        aSqlCondition.and("UPLOAD_PATH='" + aNews.getUploadPath()+ "'");
      if(aNews.getUpdateDt()!=null && !aNews.getUpdateDt().equals("") )
        aSqlCondition.and("UPDATE_DT='" + aNews.getUpdateDt()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aNews==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from NEWS order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from NEWS where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aNews==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from NEWS" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from NEWS where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    News aTempNews = null;
    while( aRS!=null && aRS.next()){
      aTempNews = new News();
        aTempNews.setOid( new Integer(aRS.getString("OID")));
        aTempNews.setCatalog( new Integer(aRS.getString("CATALOG")));
      aTempNews.setSubject((String)aRS.getObject("SUBJECT"));
      aTempNews.setMessage((String)aRS.getObject("MESSAGE"));
      if( aRS.getObject("UPLOAD_TYPE")!=null) 
        aTempNews.setUploadType( new Integer(aRS.getString("UPLOAD_TYPE")));
      aTempNews.setUploadName((String)aRS.getObject("UPLOAD_NAME"));
      aTempNews.setUploadPath((String)aRS.getObject("UPLOAD_PATH"));
      aTempNews.setUpdateDt((String)aRS.getObject("UPDATE_DT"));
      aDS.addData(aTempNews);
    }
    return aDS;
  }

  public DataSet pageQuery(News aNews,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aNews!=null ){
      if(aNews.getOid()!=null && !aNews.getOid().equals("") )
        aSqlCondition.and("OID='" + aNews.getOid()+ "'");
      if(aNews.getCatalog()!=null && !aNews.getCatalog().equals("") )
        aSqlCondition.and("CATALOG='" + aNews.getCatalog()+ "'");
      if(aNews.getSubject()!=null && !aNews.getSubject().equals("") )
        aSqlCondition.and("SUBJECT='" + aNews.getSubject()+ "'");
      if(aNews.getMessage()!=null && !aNews.getMessage().equals("") )
        aSqlCondition.and("MESSAGE='" + aNews.getMessage()+ "'");
      if(aNews.getUploadType()!=null && !aNews.getUploadType().equals("") )
        aSqlCondition.and("UPLOAD_TYPE='" + aNews.getUploadType()+ "'");
      if(aNews.getUploadName()!=null && !aNews.getUploadName().equals("") )
        aSqlCondition.and("UPLOAD_NAME='" + aNews.getUploadName()+ "'");
      if(aNews.getUploadPath()!=null && !aNews.getUploadPath().equals("") )
        aSqlCondition.and("UPLOAD_PATH='" + aNews.getUploadPath()+ "'");
      if(aNews.getUpdateDt()!=null && !aNews.getUpdateDt().equals("") )
        aSqlCondition.and("UPDATE_DT='" + aNews.getUpdateDt()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aNews==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from NEWS order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from NEWS where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aNews==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from NEWS" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from NEWS where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    News aTempNews = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempNews = new News();
        aTempNews.setOid( new Integer(aRS.getString("OID")));
        aTempNews.setCatalog( new Integer(aRS.getString("CATALOG")));
      aTempNews.setSubject((String)aRS.getObject("SUBJECT"));
      aTempNews.setMessage((String)aRS.getObject("MESSAGE"));
        aTempNews.setUploadType( new Integer(aRS.getString("UPLOAD_TYPE")));
      aTempNews.setUploadName((String)aRS.getObject("UPLOAD_NAME"));
      aTempNews.setUploadPath((String)aRS.getObject("UPLOAD_PATH"));
      aTempNews.setUpdateDt((String)aRS.getObject("UPDATE_DT"));
      aDS.addData(aTempNews);
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