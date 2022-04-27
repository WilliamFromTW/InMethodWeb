package inmethod.doc;

import inmethod.commons.rdb.*;
import java.sql.*;
import java.util.*;

public class DocBeanFactory {

  private Connection aConn = null;
  private static DocBeanFactory aBeanFactory = null;
  private DocBeanFactory() { 
  }
  public static DocBeanFactory getInstance(Connection aConn) {
    if (aBeanFactory == null) aBeanFactory = new DocBeanFactory();
    aBeanFactory.setConnection(aConn);
    return aBeanFactory;
  };                
  public void setConnection(Connection aConn){
    this.aConn = aConn;
  }

  public int insert(Doc aDoc) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aDoc.getOid() );
    aVector.add( aDoc.getCatalog() );
    aVector.add( aDoc.getMessage() );
    aVector.add( aDoc.getUploadType() );
    aVector.add( aDoc.getUploadName() );
    aVector.add( aDoc.getUploadPath() );
    aVector.add( aDoc.getUpdateDt() );
    return SQLTools.getInstance().preparedInsert(aConn,"insert into DOC (OID,CATALOG,MESSAGE,UPLOAD_TYPE,UPLOAD_NAME,UPLOAD_PATH,UPDATE_DT)  values (?,?,?,?,?,?,?)",aVector);
  }

  public int delete(Doc aDoc) throws Exception{
    Vector aVector = new Vector();
    aVector.add(aDoc.getOid());
    return SQLTools.getInstance().preparedDelete(aConn,"delete from DOC where OID=?",aVector);
  }

  public int update(Doc aDoc) throws Exception{
    Vector aVector = new Vector();
    aVector.add( aDoc.getOid() );
    aVector.add( aDoc.getCatalog() );
    aVector.add( aDoc.getMessage() );
    aVector.add( aDoc.getUploadType() );
    aVector.add( aDoc.getUploadName() );
    //aVector.add( aDoc.getUploadPath() );
    aVector.add( aDoc.getUpdateDt() );
    aVector.add(aDoc.getOid());
    return SQLTools.getInstance().preparedUpdate(aConn,"update DOC set OID=?,CATALOG=?,MESSAGE=?,UPLOAD_TYPE=?,UPLOAD_NAME=?,UPDATE_DT=? where OID=?" ,aVector);
   }

  public DataSet Query(Doc aDoc,String sOrderBy) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aDoc!=null ){
      if(aDoc.getOid()!=null && !aDoc.getOid().equals("") )
        aSqlCondition.and("OID='" + aDoc.getOid()+ "'");
      if(aDoc.getCatalog()!=null && !aDoc.getCatalog().equals("") )
        aSqlCondition.and("CATALOG='" + aDoc.getCatalog()+ "'");
      if(aDoc.getMessage()!=null && !aDoc.getMessage().equals("") )
        aSqlCondition.and("MESSAGE='" + aDoc.getMessage()+ "'");
      if(aDoc.getUploadType()!=null && !aDoc.getUploadType().equals("") )
        aSqlCondition.and("UPLOAD_TYPE='" + aDoc.getUploadType()+ "'");
      if(aDoc.getUploadName()!=null && !aDoc.getUploadName().equals("") )
        aSqlCondition.and("UPLOAD_NAME='" + aDoc.getUploadName()+ "'");
      if(aDoc.getUploadPath()!=null && !aDoc.getUploadPath().equals("") )
        aSqlCondition.and("UPLOAD_PATH='" + aDoc.getUploadPath()+ "'");
      if(aDoc.getUpdateDt()!=null && !aDoc.getUpdateDt().equals("") )
        aSqlCondition.and("UPDATE_DT='" + aDoc.getUpdateDt()+ "'");
    }
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aDoc==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().Select(aConn, "select * from DOC order by " + sOrderBy );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from DOC where "+aSqlCondition.toString() + " order by " + sOrderBy );
    }
    else{ 
      if( aDoc==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().Select(aConn, "select * from DOC" );
      else
        aRS = SQLTools.getInstance().Select(aConn, "select * from DOC where "+aSqlCondition.toString());
    }
    DataSet aDS = new DataSet();
    Doc aTempDOC = null;
    while( aRS!=null && aRS.next()){
      aTempDOC = new Doc();
        aTempDOC.setOid( new Integer(aRS.getString("OID")));
        aTempDOC.setCatalog( new Integer(aRS.getString("CATALOG")));
      aTempDOC.setMessage((String)aRS.getObject("MESSAGE"));
      if( aRS.getObject("UPLOAD_TYPE")!=null) 
        aTempDOC.setUploadType( new Integer(aRS.getString("UPLOAD_TYPE")));
      aTempDOC.setUploadName((String)aRS.getObject("UPLOAD_NAME"));
      aTempDOC.setUploadPath((String)aRS.getObject("UPLOAD_PATH"));
      aTempDOC.setUpdateDt((String)aRS.getObject("UPDATE_DT"));
      aDS.addData(aTempDOC);
    }
    return aDS;
  }

  public DataSet pageQuery(Doc aDoc,String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{
    SQLCondition aSqlCondition = new SQLCondition();
    if( aDoc!=null ){
      if(aDoc.getOid()!=null && !aDoc.getOid().equals("") )
        aSqlCondition.and("OID='" + aDoc.getOid()+ "'");
      if(aDoc.getCatalog()!=null && !aDoc.getCatalog().equals("") )
        aSqlCondition.and("CATALOG='" + aDoc.getCatalog()+ "'");
      if(aDoc.getMessage()!=null && !aDoc.getMessage().equals("") )
        aSqlCondition.and("MESSAGE='" + aDoc.getMessage()+ "'");
      if(aDoc.getUploadType()!=null && !aDoc.getUploadType().equals("") )
        aSqlCondition.and("UPLOAD_TYPE='" + aDoc.getUploadType()+ "'");
      if(aDoc.getUploadName()!=null && !aDoc.getUploadName().equals("") )
        aSqlCondition.and("UPLOAD_NAME='" + aDoc.getUploadName()+ "'");
      if(aDoc.getUploadPath()!=null && !aDoc.getUploadPath().equals("") )
        aSqlCondition.and("UPLOAD_PATH='" + aDoc.getUploadPath()+ "'");
      if(aDoc.getUpdateDt()!=null && !aDoc.getUpdateDt().equals("") )
        aSqlCondition.and("UPDATE_DT='" + aDoc.getUpdateDt()+ "'");
    }
    long i = 0;
    ResultSet aRS = null;
    if( sOrderBy!=null && !sOrderBy.equals("") ){
      if( aDoc==null || aSqlCondition.toString()==null )
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DOC order by " + sOrderBy ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DOC where "+aSqlCondition.toString() + " order by " + sOrderBy ,lRowsBegin,lPageRows);
    }
    else{ 
      if( aDoc==null || aSqlCondition.toString()==null)
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DOC" ,lRowsBegin,lPageRows);
      else
        aRS = SQLTools.getInstance().PageSelect(aConn, "select * from DOC where "+aSqlCondition.toString(),lRowsBegin,lPageRows);
    }
    DataSet aDS = new DataSet();
    Doc aTempDOC = null;
    while( aRS!=null && aRS.next()){
      i++;
      if(i>lPageRows ) break;
      aTempDOC = new Doc();
        aTempDOC.setOid( new Integer(aRS.getString("OID")));
        aTempDOC.setCatalog( new Integer(aRS.getString("CATALOG")));
      aTempDOC.setMessage((String)aRS.getObject("MESSAGE"));
        aTempDOC.setUploadType( new Integer(aRS.getString("UPLOAD_TYPE")));
      aTempDOC.setUploadName((String)aRS.getObject("UPLOAD_NAME"));
      aTempDOC.setUploadPath((String)aRS.getObject("UPLOAD_PATH"));
      aTempDOC.setUpdateDt((String)aRS.getObject("UPDATE_DT"));
      aDS.addData(aTempDOC);
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