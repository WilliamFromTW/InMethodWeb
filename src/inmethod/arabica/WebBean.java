package inmethod.arabica;

import java.util.Vector;

import java.sql.*;
import java.io.*;
import java.util.HashMap;

public class WebBean implements Coffee{
	
  public static String BEAN_LOCATION = "inmethod.arabica.WebBean";
  private Connection aConn = null;
  private Vector<String> aTableVector = null;
  private String strDirectory = null;
  private String strPackageName = null;
  private Vector<String> aCatalogName = null;
  private Vector<String> aTableDesc = null;

  public void  SumatraMandheling(){

  }
  public void setParameter(Object[] obj) throws Exception {
    if( obj.length!=6 ) throw new Exception("parameter must be 1: db Connection,2: Vector(n table name), 3: String(PackageName), 4: String(path name to create code), 5: CatalogName,6:TableDesc");
    setConnection((Connection)obj[0]);
    setTable( (Vector) obj[1] );
    setPackageName( (String) obj[2] );
    setDirectory( (String) obj[3] );
    setCatalogName( (Vector) obj[4] );
    setTableDesc( (Vector) obj[5] );
  }

  private void setTableDesc(Vector aVector) {
	  aTableDesc = aVector;
  }
  private void  setCatalogName(Vector aVector) {
	 this.aCatalogName = aVector;  
  }
  
  private void setConnection(Connection aConnection) throws Exception{
    this.aConn = aConnection;
  }

  private void setTable(Vector aTable) throws Exception{
    aTableVector = aTable;
  }

  private void setPackageName(String sPackagename) throws Exception{
    this.strPackageName = sPackagename;
  }

  private void setDirectory(String strDirectory) throws Exception{
    if( !strDirectory.endsWith("/") )
      this.strDirectory = strDirectory + "/";
    else
      this.strDirectory = strDirectory ;
  }

  public void genCode() throws Exception{
    try{
      buildBeanSourceCode();
      buildBeanManagerSourceCode();
      buildJspSourceCode();
      buildServletSourceCode();
      buildJsSourceCode();
      buildSql();
    }catch(Exception ex){ex.printStackTrace();};
  }






  private void buildSql() throws Exception{
	    if( aConn==null || aTableVector == null) throw new Exception("no connection or table vector");
	    File aFile = null;
	    for(int i = 0; i <aTableVector.size();i++){
	      if( strPackageName != null ){
	        aFile = new File( strDirectory + replace(strPackageName,".","/"));
	        aFile.mkdirs(); aFile = null;
	        aFile = new File( strDirectory + replace(strPackageName,".","/") + "/" + getFormattedName( (String)aTableVector.get(i) ) +".sql" );
	      }
	      else
	        aFile = new File( strDirectory + getFormattedName( (String)aTableVector.get(i) ) + ".sql");
	      FileOutputStream fos = new FileOutputStream(aFile);
	      fos.write( getSql().getBytes() );
	      fos.close();
	    }
  }

  /**
   * default role is admin
   * @return
 * @throws Exception 
   */
  private String getSql() throws Exception {
	  String sReturn="#default role is 'admin'";
	  String sTableName = null;
	  String sCatalogName = null;
	  String sTableDesc = null;
	  for(int i = 0; i <aTableVector.size();i++){
		  sTableName =  getFormattedName( aTableVector.get(i) );
		  sCatalogName = aCatalogName.get(i);
		  sTableDesc = aTableDesc.get(i);
		  //strPackageName = null; // for test
		   if( strPackageName != null ){
		    sReturn = sReturn+"\r\n"+ "insert into FUNCTION_INFO values('"+sTableName+"ControlServlet','"+replace(strPackageName,".","/")+"/"+sTableName+"Control.jsp','"+sTableDesc+"');\r\n" +
	                        "insert into ROLE_AUTHENTICATED_PERMISSION values('admin','"+sTableName+"ControlServlet');\r\n" +
	                        "insert into ROLE_AUTHORIZED_PERMISSION values('admin','"+sTableName+"ControlServlet','"+sCatalogName+"','Y','Y','Y','Y','Y');\r\n";
		   }else {
			    sReturn = sReturn+"\r\n"+ "insert into FUNCTION_INFO values('"+sTableName+"ControlServlet','"+sTableName+".jsp','"+sTableDesc+"');\r\n" +
                        "insert into ROLE_AUTHENTICATED_PERMISSION values('admin','"+sTableName+"ControlServlet');\r\n" +
                        "insert into ROLE_AUTHORIZED_PERMISSION values('admin','"+sTableName+"ControlServlet','"+sCatalogName+"','Y','Y','Y','Y','Y');\r\n";
		   }
	  }
	  System.out.println(sReturn);
	  return sReturn;
  }



  /**
   * Build Bean source code
   * @throws Exception
   */
  private void buildBeanSourceCode() throws Exception{
    if( aConn==null || aTableVector == null) throw new Exception("no connection or table vector");
    File aFile = null;
    for(int i = 0; i <aTableVector.size();i++){
      if( strPackageName != null ){
        aFile = new File( strDirectory + replace(strPackageName,".","/"));
        aFile.mkdirs(); aFile = null;
        aFile = new File( strDirectory + replace(strPackageName,".","/") + "/" + getFormattedName( (String)aTableVector.get(i) ) +".java" );
      }
      else
        aFile = new File( strDirectory + getFormattedName( (String)aTableVector.get(i) ) + ".java");
      FileOutputStream fos = new FileOutputStream(aFile);
      fos.write( ((String)(getBeanSource((String)aTableVector.get(i)).get("BeanSource"))).getBytes() );
      fos.close();
    }
  }

  /**
   * Build Bean Manager source code
   * @throws Exception
   */
  private void buildBeanManagerSourceCode() throws Exception{
    if( aConn==null || aTableVector == null) throw new Exception("no connection or table vector");

    for(int i = 0; i <aTableVector.size();i++){
    	File aFile = null;
    if( strPackageName != null ){
      aFile = new File( strDirectory + replace(strPackageName,".","/"));
      aFile.mkdirs(); aFile = null;
      aFile = new File( strDirectory + replace(strPackageName,".","/") + "/"+getFormattedName( (String)aTableVector.get(i) )+"BeanFactory.java" );
    }
    else
      aFile = new File( strDirectory + "/BeanFactory.java");

    FileOutputStream fos = new FileOutputStream(aFile);
    if( strPackageName!=null && !strPackageName.equals(".") && !strPackageName.equals("") )
      fos.write( ("package " + strPackageName + ";\n\n").getBytes() ) ;
    fos.write( ("import inmethod.commons.rdb.*;\n" +
                "import java.sql.*;\n" +
                "import java.util.*;\n\n" +
                "public class "+getFormattedName( (String)aTableVector.get(i) )+"BeanFactory {\n\n"+
                "  private Connection aConn = null;\n" +
                "  private static "+getFormattedName( (String)aTableVector.get(i) )+"BeanFactory aBeanFactory = null;\n" +
                "  private "+getFormattedName( (String)aTableVector.get(i) )+"BeanFactory() { \n" +
                "  }\n" +
                "  public static "+getFormattedName( (String)aTableVector.get(i) )+"BeanFactory getInstance(Connection aConn) {\n" +
                "    if (aBeanFactory == null) aBeanFactory = new "+getFormattedName( (String)aTableVector.get(i) )+"BeanFactory();\n" +
                "    aBeanFactory.setConnection(aConn);\n" +
                "    return aBeanFactory;\n" +
                "  };                \n" +
                "  public void setConnection(Connection aConn){\n" +
                "    this.aConn = aConn;\n" +
                "  }\n").getBytes() );

      fos.write( ((String)(getBeanManagerSource((String)aTableVector.get(i)).get("BeanManagerSource"))).getBytes() );
    
    fos.write(            ("\n  public long getTableTotalCount(String sTable){\n" +
                          "      if( sTable==null || sTable.equals(\"\") ) return -1;\n" +
                          "      ResultSet aRS = null;\n" +
                          "      try{\n" +
                          "        aRS = SQLTools.getInstance().Select(aConn, \"select count(*) as NUM from \" + sTable );\n" +
                          "        if( aRS!=null && aRS.next() )\n" +
                          "          return aRS.getLong(\"NUM\");\n" +
                          "        else\n" +
                          "          return -1;\n" +
                          "      }catch(Exception ex){\n" +
                          "        return -1;\n" +
                          "      }\n" +
                          "    }\n").getBytes());

    fos.write( ("\n}").getBytes() );
    fos.close();
    }
  }

  /**
   * Build jsp page to query,insert,update,delete for specify table
   * @param sEncode
   * @throws Exception
   */
  private void buildJspSourceCode() throws Exception{
    if( aConn==null || aTableVector == null) throw new Exception("no connection or table vector");
    File aFile = null;
    for(int i = 0; i <aTableVector.size();i++){
      if( strPackageName != null ){
        aFile = new File( strDirectory + replace(strPackageName,".","/"));
        aFile.mkdirs(); aFile = null;
        aFile = new File( strDirectory + replace(strPackageName,".","/") + "/" + getFormattedName( (String)aTableVector.get(i) ) +"Control.jsp" );
      }
      else
        aFile = new File( strDirectory + getFormattedName( (String)aTableVector.get(i) ) + ".jsp");
      FileOutputStream fos = new FileOutputStream(aFile);
      fos.write( ((String)(getJspSource((String)aTableVector.get(i)).get("JspSource"))).getBytes() );
      fos.close();
    }
  }

  /**
   * Build Servlet to handle "query,insert,update,delete" for specify table
   * @param sEncode
   * @throws Exception
   */
  private void buildServletSourceCode() throws Exception{
    if( aConn==null || aTableVector == null) throw new Exception("no connection or table vector");
    File aFile = null;
    for(int i = 0; i <aTableVector.size();i++){
      if( strPackageName != null ){
        aFile = new File( strDirectory + replace(strPackageName,".","/"));
        aFile.mkdirs(); aFile = null;
        aFile = new File( strDirectory + replace(strPackageName,".","/") + "/" + getFormattedName( (String)aTableVector.get(i) ) +"ControlServlet.java" );
      }
      else
        aFile = new File( strDirectory + getFormattedName( (String)aTableVector.get(i) ) + ".ControlServlet.java");
      FileOutputStream fos = new FileOutputStream(aFile);
      fos.write( ((String)(getServletSource((String)aTableVector.get(i)).get("ServletSource"))).getBytes() );
      fos.close();
    }
  }

  /**
   * Build javascript to query,insert,update,delete for specify table
   * @param sEncode
   * @throws Exception
   */
  private void buildJsSourceCode() throws Exception{
    if( aConn==null || aTableVector == null) throw new Exception("no connection or table vector");
    File aFile = null;
    for(int i = 0; i <aTableVector.size();i++){
      if( strPackageName != null ){
        aFile = new File( strDirectory + replace(strPackageName,".","/"));
        aFile.mkdirs(); aFile = null;
        aFile = new File( strDirectory + replace(strPackageName,".","/") + "/" + getFormattedName( (String)aTableVector.get(i) ) +"Control.js" );
      }
      else
        aFile = new File( strDirectory + getFormattedName( (String)aTableVector.get(i) ) + ".jsp");
      FileOutputStream fos = new FileOutputStream(aFile);
      fos.write( ((String)(getJsSource((String)aTableVector.get(i)).get("JsSource"))).getBytes() );
      fos.close();
    }
  }
  
  /**
   * a table will convert to a bean manager source code
   * @param strTableName
   * @return HashMap aHashMap.get("BeanManagerSource") to get Source Code
   * @throws Exception
   */
  private HashMap getBeanManagerSource(String strTableName) throws Exception{

    if( aConn==null || strTableName==null) throw new Exception("no connection�Btable vector or printwriter");
    HashMap aHM = new HashMap();
    DatabaseMetaData metaData = aConn.getMetaData();
    ResultSet aRset = metaData.getColumns(null, null, strTableName, null);
    ResultSet aKeyRset = metaData.getPrimaryKeys(null,null,strTableName);
    String strBeanSource = "";
    String strAttribute = "";
    // insert
    String strInsertMethod = "";
    String strInsertField = "";
    String strInsertField2 = " (";
    String strInsertPara = null;
    // delete
    String strDeleteMethod = "";
    String strDeleteField = "";
    String strDeletePara = null;
    // query
    String strQueryMethod = "";
    String strQueryField1 = "";
    String strQueryField2 = "";
    String strQueryPara = null;
    // Pagequery
    String strPageQueryMethod = "";
    String strPageQueryField1 = "";
    String strPageQueryField2 = "";
    String strPageQueryPara = null;
    // update
    String strUpdateMethod = "";
    String strUpdateField = "";
    String strUpdatePara1 = null;
    String strUpdatePara2 = null;
    // Total Count method
    String strTotalCountMethod = "";

    String strClassName = getFormattedName(strTableName);
    int iSqlType = 0;
    String strObjectType = null;

    // got the filed
    while( aRset!=null && aRset.next() ){
      iSqlType = aRset.getInt("DATA_TYPE");
      System.out.println( "field name="+aRset.getString("COLUMN_NAME")+",type="+getDataType(iSqlType));
      strObjectType = getDataType(iSqlType);
      strInsertField += "    aVector.add( a" + strClassName + ".get" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "() );\n";
      if( strInsertField2.equals(" ("))
        strInsertField2 += aRset.getString("COLUMN_NAME");
      else
        strInsertField2 += "," + aRset.getString("COLUMN_NAME");

      strUpdateField += "    aVector.add( a" + strClassName + ".get" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "() );\n";
      if(getDataType( aRset.getInt("DATA_TYPE")).equals("Long") ||getDataType( aRset.getInt("DATA_TYPE")).equals("Integer")||getDataType( aRset.getInt("DATA_TYPE")).equals("Float")||getDataType( aRset.getInt("DATA_TYPE")).equals("Double") ){
        strQueryField2 +=  "        aTemp" + strClassName + ".set" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "( new " + getDataType( aRset.getInt("DATA_TYPE") ) + "(aRS.getString(\"" + aRset.getString("COLUMN_NAME").toUpperCase() + "\")));\n";
        strPageQueryField2 +=   "        aTemp" + strClassName + ".set" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "( new " + getDataType( aRset.getInt("DATA_TYPE") ) + "(aRS.getString(\"" + aRset.getString("COLUMN_NAME").toUpperCase() + "\")));\n";
      }else{
        strQueryField2 +="      aTemp" + strClassName + ".set" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "((" + getDataType( aRset.getInt("DATA_TYPE") ) + ")aRS.getObject(\"" + aRset.getString("COLUMN_NAME").toUpperCase() + "\"));\n";
        strPageQueryField2 +="      aTemp" + strClassName + ".set" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "((" + getDataType( aRset.getInt("DATA_TYPE") ) + ")aRS.getObject(\"" + aRset.getString("COLUMN_NAME").toUpperCase() + "\"));\n";
      }    	  
      strQueryField1 += "      if(a" + strClassName + ".get" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "()!=null && !a" + strClassName + ".get" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "().equals(\"\") )\n" +
                        "        aSqlCondition.and(\"" + aRset.getString("COLUMN_NAME").toUpperCase() + "='\" + a" +strClassName + ".get" + getFormattedName( aRset.getString("COLUMN_NAME") ) + "()+ \"'\");\n";

      if( strUpdatePara1 == null )
        strUpdatePara1 = aRset.getString("COLUMN_NAME").toUpperCase() + "=?";
      else
        strUpdatePara1 += "," + aRset.getString("COLUMN_NAME").toUpperCase() + "=?";
      if( strInsertPara==null ) strInsertPara = "?";
      else strInsertPara += ",?";
    }
    strInsertField2 += ") ";
    // got the PK
    while( aKeyRset!= null && aKeyRset.next() ){
      strDeleteField += "    aVector.add(a" + strClassName + ".get" + getFormattedName( aKeyRset.getString("COLUMN_NAME") ) + "());\n";
      strUpdateField += "    aVector.add(a" + strClassName + ".get" + getFormattedName( aKeyRset.getString("COLUMN_NAME") ) + "());\n";
//      strQueryField1 += "    aVector.add(a" + strClassName + ".get" + getFormattedName( aKeyRset.getString("COLUMN_NAME") ) + "());\n";
      strPageQueryField1 += "    aVector.add(a" + strClassName + ".get" + getFormattedName( aKeyRset.getString("COLUMN_NAME") ) + "());\n";
      if( strDeletePara == null ){
        strDeletePara = aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
       // strQueryPara = aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
        strUpdatePara2 = aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
      }
      else{
        strDeletePara += " and " + aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
      //  strQueryPara += " and " + aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
        strPageQueryPara += " and " + aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
        strUpdatePara2 += " and " + aKeyRset.getString("COLUMN_NAME").toUpperCase() + "=?";
      }
    }

    strInsertMethod = "\n  public int insert(" + strClassName + " a" + strClassName + ") throws Exception{\n" +
                      "    Vector aVector = new Vector();\n" + strInsertField +
                      "    return SQLTools.getInstance().preparedInsert(aConn,\"insert into " + strTableName + strInsertField2 + " values (" +
                      strInsertPara + ")\",aVector);\n  }\n";

    if( strDeletePara!=null)
      strDeleteMethod = "\n  public int delete(" + strClassName + " a" + strClassName + ") throws Exception{\n" +
                        "    Vector aVector = new Vector();\n" + strDeleteField +
                        "    return SQLTools.getInstance().preparedDelete(aConn,\"delete from " + strTableName + " where " + strDeletePara + "\",aVector);\n" +
                        "  }\n";
    else
      strDeleteMethod = "\n  public int delete(" + strClassName + " a" + strClassName + ") throws Exception{\n/*" +
                        "    Vector aVector = new Vector();\n" + strDeleteField +
                        "    return SQLTools.getInstance().preparedDelete(aConn,\"delete from " + strTableName + " where ???\",aVector);\n*/\n    return -1;\n" +
                        "  }\n";

    if( strUpdatePara2 != null )
      strUpdateMethod = "\n  public int update(" + strClassName + " a" + strClassName + ") throws Exception{\n" +
                        "    Vector aVector = new Vector();\n" + strUpdateField +
                        "    return SQLTools.getInstance().preparedUpdate(aConn,\"update " + strTableName + " set " + strUpdatePara1 + " where " + strUpdatePara2 + "\" ,aVector);\n " +
                        "  }\n";
    else
      strUpdateMethod = "\n  public int update(" + strClassName + " a" + strClassName + ") throws Exception{\n/*" +
                        "    Vector aVector = new Vector();\n" + strUpdateField +
                        "    return SQLTools.getInstance().preparedUpdate(aConn,\"update test set ??? + where  ???  \" ,aVector);\n*/\n    return -1;\n " +
                        "  }\n";
      strQueryMethod = "\n  public DataSet Query(" + strClassName + " a" + strClassName + ",String sOrderBy) throws Exception{\n"+
                       "    SQLCondition aSqlCondition = new SQLCondition();\n" +
                       "    if( a" + strClassName + "!=null ){\n" +
                        strQueryField1 +
                       "    }\n" +
                       "    ResultSet aRS = null;\n" +
                       "    if( sOrderBy!=null && !sOrderBy.equals(\"\") ){\n" +
                       "      if( a" + strClassName + "==null || aSqlCondition.toString()==null )\n" +
                       "        aRS = SQLTools.getInstance().Select(aConn, \"select * from " + strTableName + " order by \" + sOrderBy );\n" +
                       "      else\n" +
                       "        aRS = SQLTools.getInstance().Select(aConn, \"select * from " + strTableName + " where \"+aSqlCondition.toString() + \"" + " order by \" + sOrderBy );\n" +
                       "    }\n" +
                       "    else{ \n" +
                       "      if( a" + strClassName + "==null || aSqlCondition.toString()==null)\n" +
                       "        aRS = SQLTools.getInstance().Select(aConn, \"select * from " + strTableName + "\" );\n" +
                       "      else\n" +
                       "        aRS = SQLTools.getInstance().Select(aConn, \"select * from " + strTableName + " where \"+aSqlCondition.toString()" + ");\n" +
                       "    }\n" +
                       "    DataSet aDS = new DataSet();\n    " +
                       strClassName + " aTemp" + strClassName + " = null;\n" +
                       "    while( aRS!=null && aRS.next()){\n" +
                       "      aTemp" + strClassName + " = new " + strClassName + "();\n" +
                       strQueryField2 +
                       "      aDS.addData(aTemp" + strClassName + ");\n    }\n" +
                       "    return aDS;\n" + "  }\n";
      strPageQueryMethod = "\n  public DataSet pageQuery(" + strClassName + " a" + strClassName + ",String sOrderBy,long lRowsBegin,long lPageRows) throws Exception{\n"+
                       "    SQLCondition aSqlCondition = new SQLCondition();\n" +
                       "    if( a" + strClassName + "!=null ){\n" +
                        strQueryField1 +
                       "    }\n" +
                       "    long i = 0;\n" +
                       "    ResultSet aRS = null;\n" +
                       "    if( sOrderBy!=null && !sOrderBy.equals(\"\") ){\n" +
                       "      if( a" + strClassName + "==null || aSqlCondition.toString()==null )\n" +
                       "        aRS = SQLTools.getInstance().PageSelect(aConn, \"select * from " + strTableName + " order by \" + sOrderBy ,lRowsBegin,lPageRows);\n" +
                       "      else\n" +
                       "        aRS = SQLTools.getInstance().PageSelect(aConn, \"select * from " + strTableName + " where \"+aSqlCondition.toString() + \"" + " order by \" + sOrderBy ,lRowsBegin,lPageRows);\n" +
                       "    }\n" +
                       "    else{ \n" +
                       "      if( a" + strClassName + "==null || aSqlCondition.toString()==null)\n" +
                       "        aRS = SQLTools.getInstance().PageSelect(aConn, \"select * from " + strTableName + "\" ,lRowsBegin,lPageRows);\n" +
                       "      else\n" +
                       "        aRS = SQLTools.getInstance().PageSelect(aConn, \"select * from " + strTableName + " where \"+aSqlCondition.toString()" + ",lRowsBegin,lPageRows);\n" +
                       "    }\n" +
                       "    DataSet aDS = new DataSet();\n    " +
                       strClassName + " aTemp" + strClassName + " = null;\n" +
                       "    while( aRS!=null && aRS.next()){\n" +
                       "      i++;\n" +
                       "      if(i>lPageRows ) break;\n" +
                       "      aTemp" + strClassName + " = new " + strClassName + "();\n" +
                       strQueryField2 +
                       "      aDS.addData(aTemp" + strClassName + ");\n    }\n" +
                       "    return aDS;\n" + "  }\n";
    strTotalCountMethod = "\n  public long getTableTotalCount(String sTable){\n" +
                          "      if( sTable==null || sTable.equals(\"\") ) return -1;\n" +
                          "      ResultSet aRS = null;\n" +
                          "      try{\n" +
                          "        aRS = SQLTools.getInstance().Select(aConn, \"select count(*) as NUM from \" + sTable );\n" +
                          "        if( aRS!=null && aRS.next() )\n" +
                          "          return aRS.getLong(\"NUM\");\n" +
                          "        else\n" +
                          "          return -1;\n" +
                          "      }catch(Exception ex){\n" +
                          "        return -1;\n" +
                          "      }\n" +
                          "    }\n";

    aRset.close();
    aKeyRset.close();
    aHM.put("BeanManagerSource",strBeanSource + strInsertMethod + strDeleteMethod + strUpdateMethod + strQueryMethod + strPageQueryMethod );
    return aHM;
  }

  /**
   * a table will convert to a bean source code
   * @param strTableName table name
   * @return HashMap aHashMap.get("BeanSource") to get SourceCode
   * @throws Exception if no table exists
   *
   */
  private HashMap getBeanSource(String strTableName) throws Exception{
      if( aConn==null || strTableName==null) throw new Exception("no connection�Btable vector or printwriter");
      HashMap aHM = new HashMap();
      DatabaseMetaData metaData = aConn.getMetaData();
      ResultSet aRset = metaData.getColumns(null, null, strTableName, null);
      String strBeanSource = null;
      String sToJson = "  public String toJson(){\n    String sJson =  \"\";\n";
      String strAttribute = "\n";
      String strPublicAttribute = "\n";
      String strMethod = "\n  public int getFieldCount(){\n    return iFieldCount;\n  }\n";
      String strFieldName = "";
      int iFieldCount = 0;
      String strObjectType = null;
      int iSqlType = 0;
      if( strPackageName != null ) strBeanSource = "package " + strPackageName + ";\n\nimport inmethod.commons.util.HTMLConverter;\n\npublic class " + getFormattedName(strTableName) + " {\n\n";
      else strBeanSource = "import inmethod.commons.util.HTMLConverter;\n\npublic class " + getFormattedName(strTableName) + " {\n\n";

      while( aRset!=null && aRset.next() ){
         iFieldCount ++;
         iSqlType = aRset.getInt("DATA_TYPE");
         strObjectType = getDataType(iSqlType);
         strFieldName = getFormattedName( aRset.getString("COLUMN_NAME") );
         strPublicAttribute += "  public static final String FIELD_" + aRset.getString("COLUMN_NAME").toUpperCase() + " = \"" + strFieldName + "\";\n";
         strAttribute += "  private " + strObjectType + " a" + strFieldName + " = null;\n";
         strMethod += "\n  public void set" + strFieldName + "(" + strObjectType + " param){\n    a" + strFieldName + " = param;\n  }\n";
         strMethod += "\n  public " + strObjectType + " get" + strFieldName + "(){\n    return a" + strFieldName + ";\n  }\n";
         if( strObjectType.equalsIgnoreCase("String") || strObjectType.equalsIgnoreCase("Date") )
           sToJson = sToJson +"    if(a" + strFieldName+"!=null)\n      sJson = sJson + \"\\\""+ strFieldName+"\\\":\"+\"\\\"\"+HTMLConverter.nl2br(a" + strFieldName+")+\"\\\",\";\n";
         else
           sToJson = sToJson +"    if(a" + strFieldName+"!=null)\n      sJson = sJson + \"\\\""+ strFieldName+"\\\":\"+\"\\\"\"+a" + strFieldName+"+\"\\\",\";\n";
      }
      
      sToJson = sToJson +"\n    sJson = \"{\"+sJson.substring(0,sJson.length()-1)+\"}\";\n    return sJson;\n  }\n";
      aRset.close();
      strAttribute += "  private int iFieldCount = " + iFieldCount + ";\n";
      aHM.put("BeanSource",strBeanSource + strPublicAttribute + "\n" + strAttribute + strMethod + "\n" + sToJson + "\n}");
      return aHM;
  }

  /**
   * Table will convert to a jsp source code to do "insert , update ,delete ,query"
   * @param strTableName
   * @return HashMap aHashMap.get("JspSource") to get Source Code
   * @throws Exception
   */
  private HashMap getJspSource(String strTableName) throws Exception{

    if( aConn==null || strTableName==null) throw new Exception("no connection�Btable vector or printwriter");
    HashMap<String,String> aHM = new HashMap<String,String>();
    String strClassName = getFormattedName(strTableName);
    String strJspSource = "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>\n\n" +
    		"<%@ include file=\"/global.jsp\"%>\n"+
    		"<%\n"+
    		"/*\n"+
    		"<jsp:include page=\"/inmethod/index.jsp\"></jsp:include>\n"+
    		"*/\n"+
    		"%>\n"+
    		"<%=getBaseHead(request, response)%>\n"+
    		"<jsp:include page=\"/inmethod/index.jsp\"></jsp:include>\n\n\n\n"+
    		"<%\n"+
    		"   if (!isLogin(request, response)) {\n"+
    		"%>\n"+
    		"<script>\n"+
    		"   window.open(\"inmethod/index.jsp\", \"_self\", \"\", \"\");\n"+
    		"</script>\n\n"+
    		"<%\n"+
    		"   } else {\n"+
    		"%>\n\n\n\n"+
    		"<script>\n"+
    		"  window.jQuery || document.write('<%=addSlashes(getJavaScript())%>')\n"+
    		"</script>\n"+
    		"<div class=\"function-title  h3 d-flex justify-content-center\" ></div>\n"+
    		"<table class=\"Div"+strClassName+"Control\"> </table>\n" +
    		"<div id=\"change"+strClassName+"Status\"  style=\"display: none;\" ></div>\n\n" +
    		"<link type=\"text/css\" rel=\"stylesheet\" href=\"jsgrid/jsgrid.min.css\" />\n" +
    		"<link type=\"text/css\" rel=\"stylesheet\" href=\"jsgrid/jsgrid-theme.min.css\" />\n" +
    		"<script type=\"text/javascript\" src=\"jsgrid/jsgrid.min.js\"></script>\n\n" +
    		"<script src=\""+strPackageName.replace(".","/")+"/"+strClassName+"Control.js\"></script>\n\n"+
             "<%\n"+
             "   }\n"+
             "%>\n";
    aHM.put("JspSource",strJspSource);
    return aHM;
  }


  /**
   * Table will convert to Servlet source code to handle "insert , update ,delete ,query"
   * @param strTableName
   * @param aConnection  "your connection string"
   * @return HashMap aHashMap.get("JspSource") to get Source Code
   * @throws Exception
   */
  private HashMap getServletSource(String strTableName) throws Exception{

    if( aConn==null || strTableName==null) throw new Exception("no connection�Btable vector or printwriter");
    HashMap aHM = new HashMap();
    DatabaseMetaData metaData = aConn.getMetaData();
    ResultSet aRset = metaData.getColumns(null, null, strTableName, null);
    ResultSet aKeyRset = metaData.getPrimaryKeys(null,null,strTableName);
    // variable
    String strClassName = getFormattedName(strTableName);
    String strImportPackageName="";
    String strImportPackageNameDotStart="";
    String strPackageNameSlash= "";
    String strAddParameter = "";
    String strDeleteParameter = "";
    String strUpdateParameter = "";
    String strBackEndQueryParameter = "";
    String strBackEndRequiredParameter = "";
    String strBackEndOptionalParameter = "";

    if( strPackageName != null ){
      strImportPackageName = "package " + strPackageName + ";\n";
      strPackageNameSlash =  strPackageName.replace(".", "/");
    }
    String strServletSource="";
    int iSqlType = 0;
    String strObjectType = null;
    HashMap<String ,Integer>  aColumnTyeHM = new HashMap<String, Integer>();
    // got the filed 
    while( aRset!=null && aRset.next() ){
      iSqlType = aRset.getInt("DATA_TYPE");
      strObjectType = getDataType(iSqlType);
      String sTemp = getFormattedName( aRset.getString("COLUMN_NAME") );     
      aColumnTyeHM.put( sTemp, iSqlType);
      String sTemp2 =  ".FIELD_" + aRset.getString("COLUMN_NAME").toUpperCase();
      
      if( strObjectType.equalsIgnoreCase("String") || strObjectType.equalsIgnoreCase("Date") ){
        strAddParameter +=
          "                    a" + strClassName + ".set" + sTemp + "( request.getParameter(" + strClassName+sTemp2 + ") );\n";
        strUpdateParameter +=
          "                    a" + strClassName + ".set" + sTemp + "( request.getParameter(" + strClassName+sTemp2 + ") );\n";
      }
      else{
        strAddParameter +=
          "                    if( request.getParameter(" + strClassName+sTemp2 + ")!=null && !request.getParameter(" + strClassName+sTemp2 + ").equals(\"\") ) \n" +
          "                      a" + strClassName + ".set" + sTemp + "( new " + strObjectType + "(request.getParameter(" +strClassName+ sTemp2 + ")) );\n";
        strUpdateParameter +=
          "                    if( request.getParameter(" + strClassName+sTemp2 + ")!=null && !request.getParameter(" + strClassName+sTemp2 + ").equals(\"\") ) \n" +
          "                      a" + strClassName + ".set" + sTemp + "( new " + strObjectType + "(request.getParameter(" +strClassName+ sTemp2 + ")) );\n";
      }
      strBackEndOptionalParameter = strBackEndOptionalParameter + "\n *      (O)"+sTemp+"=";
      strBackEndQueryParameter += "\""+sTemp+"\":\"Data\",";
    }
    strBackEndQueryParameter = " *    {"+strBackEndQueryParameter.substring(0,strBackEndQueryParameter.length()-1) +"}";
    strBackEndQueryParameter = strBackEndQueryParameter +"\n" +strBackEndQueryParameter;
    int i=0;
    
    while( aKeyRset!= null && aKeyRset.next() ){
    	i++;
        String sTemp = getFormattedName( aKeyRset.getString("COLUMN_NAME") );
        String sTemp2 =  ".FIELD_" + aKeyRset.getString("COLUMN_NAME").toUpperCase();
        iSqlType = aColumnTyeHM.get(sTemp);
        strObjectType = getDataType(iSqlType);
        if( strObjectType.equalsIgnoreCase("String") || strObjectType.equalsIgnoreCase("Date") ){
            strDeleteParameter +=
                    "                    a" + strClassName + ".set" + sTemp + "( request.getParameter(" + strClassName+sTemp2 + ") );\n";
        	
        }else {
            strDeleteParameter +=
                    "                    a" + strClassName + ".set" + sTemp + "("+ strObjectType +".parseInt( request.getParameter(" + strClassName+sTemp2 + ")) );\n";
        }
        strBackEndRequiredParameter = strBackEndRequiredParameter + "\n *      (R)"+sTemp+"=";
    }
    
    strServletSource =
strImportPackageName + "\n" +
"import javax.servlet.*;\n" +
"import javax.servlet.http.*;\n" +
"import javax.servlet.annotation.WebServlet;\n"+
"import java.sql.*;\n" +
"import java.util.*;\n" +
"import java.io.*;\n" +
"import inmethod.auth.*;\n"+
 "import inmethod.auth.inter_face.*;\n"+
"import inmethod.commons.rdb.*;\n" +
"import inmethod.commons.util.*;\n" +
"import inmethod.db.DBConnectionManager;\n\n"+
"/** \n" +
" * "+strClassName+"ControlServlet資料維護API\n" +
" * @BackEnd\n" +
" * <pre>\n" +
" * URL: "+strPackageNameSlash+"/"+strClassName +"ControlServlet\n" +
" * \n"+    
" * 功能: 查詢基本資料       \n" +
" * 參數: (R)FlowID=doQuery   \n" +
" * 回傳: 所有基本資料\n" +
" *  [\n" +strBackEndQueryParameter + "\n" +
" *  ]\n" +
" *  \n" +
" * 功能: 刪除基本資料      \n" +
" * 參數: (R)FlowID=doDelete    " +
strBackEndRequiredParameter + "\n" +
" * 回傳:\n" +
" *      {\"DeleteResult\":\"OK\"} 成功       \n" +
" *      {\"DeleteResult\":\"FAIL\"} 失敗        \n" +
" *  \n" +
" * 功能: 新增基本資料       \n" +
" * 參數: (R)FlowID=doAdd    " +
strBackEndRequiredParameter + 
strBackEndOptionalParameter + "\n"+
" * 回傳:\n" +
" *      {\"AddResult\":\"OK\"} 成功       \n" +
" *      {\"AddResult\":\"FAIL\"} 失敗        \n" +
" * \n" +
" * 功能: 修改基本資料       \n" +
" * 參數: (R)FlowID=doUpdate" +
strBackEndRequiredParameter + 
strBackEndOptionalParameter + "\n"+
" * 回傳:\n" +
" *      {\"UpdateResult\":\"OK\"} 成功       \n" +
" *      {\"UpdateResult\":\"FAIL\"} 失敗        \n" +
" *      \n" +
" * </pre>\n" +
" * \n" +
" */\n"+
"@WebServlet(name = \""+strClassName + "ControlServlet\", urlPatterns = { \"/"+strPackageNameSlash+"/"+strClassName + "ControlServlet\" })\n"+
"public class "+ strClassName + "ControlServlet extends HttpServlet {\n" +
"	private static final String CONTENT_TYPE = \"text/html; charset=UTF-8\";\n" +
"	private static String FUNCTION_NAME = null;\n" +
"	private static final String FLOW_ID = \"FlowID\";\n" +
"	protected void doGet(HttpServletRequest req, HttpServletResponse resp)\n" +
"			throws ServletException, java.io.IOException {\n" +
"		doPost(req, resp);\n" +
"	}\n" +
"	protected void doPost(HttpServletRequest request, HttpServletResponse response)\n" +
"			throws ServletException, java.io.IOException {\n" +
"		request.setCharacterEncoding(\"UTF-8\");\n" +
"		response.setContentType(CONTENT_TYPE);\n" +
"		WebAuthentication aWebAuth = null;\n" +
"		FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf(\"/\") + 1);\n" +
"		String sFlowID = request.getParameter(FLOW_ID);\n" +
"		String sUserID = null;\n" +
"		PrintWriter out = response.getWriter();\n" +
"		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection() ) {\n" +
"			try {\n" +
"				aConn.setAutoCommit(false);\n" +
"				aWebAuth = AuthFactory.getWebAuthentication(request, response); \n" +
"				sUserID = aWebAuth.getUserPrincipal();\n" +
"				if (sUserID == null || !aWebAuth.isLogin(sUserID)) {\n" +
"					response.setStatus(1000);\n" +
"					out.println(\"{\\\"NoLogin\\\":\\\"FAIL\\\"}\");\n" +
"					out.flush();\n" +
"					out.close();\n" +
"					aConn.rollback();\n" +
"					return;\n" +
"				}\n" +
"				if (sFlowID == null)\n" +
"					return;\n" +
"               else {\n" +
"					// 不需要執行權限的功能,如html select option\n" +
"				}\n" +
"				if (aWebAuth.getUserRoles(sUserID) != null\n" +
"						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {\n" +
"					response.setStatus(1000);\n" +
"					out.println(\"{\\\"NoPermission\\\":\\\"FAIL\\\"}\");\n" +
"					out.flush();\n" +
"					out.close();\n" +
"					aConn.rollback();\n" +
"					return;\n" +
"				}\n" +
"               RoleAuthorizedPermission aRoleAuthorizedPermission = aWebAuth.getAuthorizedFunctionInfo(sUserID, FUNCTION_NAME);\n"+
"               if( aRoleAuthorizedPermission==null){ \n"+
"                 response.setStatus(1000);\n"+
"                 out.println(\"{\\\"NoPermission\\\":\\\"FAIL\\\"}\");\n"+
"	              out.flush();\n"+
"	              out.close();\n"+
"	              aConn.rollback();\n"+	
"               }\n"+ 
"			    else if (sFlowID.equalsIgnoreCase(\"doQuery\")  && aRoleAuthorizedPermission.getFunctionQuery().equalsIgnoreCase(\"Y\")) {\n" +
"					"+ strClassName + "BeanFactory aBF = "+ strClassName + "BeanFactory.getInstance(aConn);\n" +
"					"+ strClassName + " a"+ strClassName + " = null;\n" +
"					DataSet aDS = aBF.Query(a"+ strClassName + ", null);\n" +
"					String sReturn = \"\";\n" +
"					while (aDS != null && aDS.next()) {\n" +
"						a"+ strClassName + " = ("+ strClassName + ") aDS.getData();\n" +
"						sReturn = sReturn + a"+ strClassName + ".toJson() + \",\";\n" +
"					}\n" +
"					if (sReturn.length() == 0) {\n" +
"						sReturn = \"[]\";\n" +
"					} else {\n" +
"						sReturn = \"[\" + sReturn.substring(0, sReturn.length() - 1) + \"]\";\n" +
"					}\n" +
"					out.println(sReturn);\n" +
"				} else if (sFlowID.equalsIgnoreCase(\"doUpdate\") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase(\"Y\")) {\n" +
"					"+ strClassName + "BeanFactory aBF = "+ strClassName + "BeanFactory.getInstance(aConn);\n" +
"					"+ strClassName + " a"+ strClassName + " = new "+ strClassName + "();\n" +
                    strUpdateParameter +"\n"+
"					try {\n" +
"						if (aBF.update(a"+ strClassName + ") == 0) {\n" +
"							response.setStatus(1000);\n" +
"							out.println(\"{\\\"UpdateResult\\\":\\\"FAIL\\\"}\");\n" +
"                           aConn.rollback();\n" +
"						} else {\n" +
"							out.print(\"{\\\"UpdateResult\\\":\\\"OK\\\"}\");\n" +
"                           aConn.commit();\n" +
"						}\n" +
"					} catch (Exception ee) {\n" +
"						response.setStatus(1000);\n" +
"						out.println(\"{\\\"UpdateResult\\\":\\\"FAIL\\\"}\");\n" +
"					    aConn.rollback();\n" +
"					}\n" +
"				} else if (sFlowID.equalsIgnoreCase(\"doAdd\")&& aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase(\"Y\")) {\n" +
"					"+ strClassName + "BeanFactory aBF = "+ strClassName + "BeanFactory.getInstance(aConn);\n" +
"					"+ strClassName + " a"+ strClassName + " = new "+ strClassName + "();\n" +
                    strAddParameter + "\n"+
"					try {\n" +
"						if (aBF.insert(a"+ strClassName + ") == 0) {\n" +
"							response.setStatus(1000);\n" +
"							out.println(\"{\\\"AddResult\\\":\\\"FAIL\\\"}\");\n" +
"                           aConn.rollback();\n" +
"						} else {\n" +
"							out.print(\"{\\\"AddResult\\\":\\\"OK\\\"}\");\n" +
"                           aConn.commit();\n" +
"						}\n" +
"					} catch (Exception ee) {\n" +
"						response.setStatus(1000);\n" +
"						out.println(\"{\\\"AddResult\\\":\\\"FAIL\\\"}\");\n" +
"                       aConn.rollback();\n" +
"					}\n" +
"				} else if (sFlowID.equalsIgnoreCase(\"doDelete\") && aRoleAuthorizedPermission.getFunctionDelete().equalsIgnoreCase(\"Y\")) {\n" +
"					"+ strClassName + "BeanFactory aBF = "+ strClassName + "BeanFactory.getInstance(aConn);\n" +
"					"+ strClassName + " a"+ strClassName + " = new "+ strClassName + "();\n" +
                    strDeleteParameter + "\n"+
"					try {\n" +
"						if (aBF.delete(a"+ strClassName + ") == 0) {\n" +
"							response.setStatus(1000);\n" +
"							out.println(\"{\\\"DeleteResult\\\":\\\"FAIL\\\"}\");\n" +
"                           aConn.rollback();\n" +
"						} else {\n" +
"							out.print(\"{\\\"DeleteResult\\\":\\\"OK\\\"}\");\n" +
"                           aConn.commit();\n" +
"						}\n" +
"					} catch (Exception ee) {\n" +
"						response.setStatus(1000);\n" +
"						out.println(\"{\\\"DeleteResult\\\":\\\"FAIL\\\"}\");\n" +
"                       aConn.rollback();\n" +
"					}\n" +
"				}\n"+
"				out.flush();\n" +
"				out.close();\n" +
"				return;\n" +
"			} catch (Exception ex) {\n" +
"				aConn.rollback();\n" +
"				ex.printStackTrace();\n" +
"			}\n" +
"		} catch (Exception ee) {\n" +
"			ee.printStackTrace();\n" +
"		}\n" +
"	}\n" +
"}\n";
    aRset.close();
    aKeyRset.close();
    aHM.put("ServletSource",strServletSource);
    return aHM;
  }

  /**
   * Table will convert to a jsp source code to do "insert , update ,delete ,query"
   * @param strTableName
   * @return HashMap aHashMap.get("JspSource") to get Source Code
   * @throws Exception
   */
  private HashMap getJsSource(String strTableName) throws Exception{

    if( aConn==null || strTableName==null) throw new Exception("no connection�Btable vector or printwriter");
    HashMap<String,String> aHM = new HashMap<String,String>();
    DatabaseMetaData metaData = aConn.getMetaData();
    ResultSet aRset = metaData.getColumns(null, null, strTableName, null);
    ResultSet aKeyRset = metaData.getPrimaryKeys(null,null,strTableName);
    String strClassName = getFormattedName(strTableName);
    String sUrlPath = strPackageName.replace(".","/");
    int iFieldCount = 0;
    String strObjectType = null;
    int iSqlType = 0;

    String sFilter = null;
    String sDeleteParameter = null;
    String sAddParameter = null;
    String sUpdateParameter = null;
    
    
    String sField = "";
    HashMap<String,String> aKeySetHashMap = new HashMap<String,String>();

    while( aKeyRset!= null && aKeyRset.next() ){
    	aKeySetHashMap.put(aKeyRset.getString("COLUMN_NAME"), aKeyRset.getString("COLUMN_NAME"));
    }

    while( aRset!=null && aRset.next() ){
          iSqlType = aRset.getInt("DATA_TYPE");
          strObjectType = getDataType(iSqlType);
          String sTemp = getFormattedName(aRset.getString("COLUMN_NAME"));
   	      
   	      if( sFilter == null)
   	    	sFilter = "                        return  (!filter."+sTemp+" || client."+sTemp+".indexOf(filter."+sTemp+") > -1)\n" ;
   	      else
   	    	sFilter = sFilter + "                         && (!filter."+sTemp+" || client."+sTemp+".indexOf(filter."+sTemp+") > -1)\n" ;
   	      
          if( sAddParameter == null)
          	sAddParameter = "                        \""+sTemp+"\":ItemData."+sTemp +"\n";
          else
          	sAddParameter = sAddParameter.substring(0, sAddParameter.length()-1)+",\n"+ "                        \""+sTemp+"\":ItemData."+sTemp +"\n";
          
          if( sUpdateParameter == null)
          	sUpdateParameter = "                        \""+sTemp+"\":ItemData."+sTemp +"\n";
          else
          	sUpdateParameter = sUpdateParameter.substring(0, sUpdateParameter.length()-1)+",\n"+ "                        \""+sTemp+"\":ItemData."+sTemp +"\n";
          
       	 if( aKeySetHashMap.containsKey(aRset.getString("COLUMN_NAME")) )
             sField = sField + 	"		        { name: \""+sTemp+"\",title: \"title_"+aRset.getString("COLUMN_NAME")+"\",css: \"jsgrid\", type: \"text\",width: 30,editing:false},\n" ;
       	 else
             sField = sField + 	"		        { name: \""+sTemp+"\",title: \"title_"+aRset.getString("COLUMN_NAME")+"\",css: \"jsgrid\", type: \"text\",width: 30},\n" ;
    }     
    sFilter = sFilter.substring(0,sFilter.length()-1) + ";\n";
    
    for (String key : aKeySetHashMap.keySet()) {
    	
        if( sDeleteParameter == null)
        	sDeleteParameter = "                        \""+getFormattedName(aKeySetHashMap.get(key)) +"\":ItemData."+getFormattedName(aKeySetHashMap.get(key)) +"\n";
        else
        	sDeleteParameter = sDeleteParameter.substring(0, sDeleteParameter.length()-1)+",\n"+ "                        \""+getFormattedName(aKeySetHashMap.get(key))+"\":ItemData."+getFormattedName(aKeySetHashMap.get(key)) +"\n";
    }
    
    String strJsSource = "var myDivClassName=\"Div"+strClassName+"Control\";\n" +
            "var myDivStatusIDName=\"change"+strClassName+"Status\";\n"+
    		"var sFunctionName = \""+strClassName+"ControlServlet\";\n" +
    		"var sFunctionUrl = \""+sUrlPath+"/\"+sFunctionName;\n\n" +
    		"if(typeof FunctionDivTableIDName !== \"undefined\"  && FunctionDivTableIDName!=null && FunctionDivTableIDName!=\"\"){\n" +
    		"	myDivClassName = FunctionDivTableIDName; \n" +
    		"}\n" +
    		"function show"+strClassName+"Message(message,color){\n"+
    		"  if(typeof FunctionDivStatusIDName !== \"undefined\"  && FunctionDivStatusIDName!=null && FunctionDivStatusIDName!=\"\"){\n\n"+
    		"    showMessage(message,color);\n" +
    		"  }\n"+
    		"  else{\n"+
    		"	  if( color==\"green\"){\n"+
    		"	    $('#'+myDivStatusIDName).removeClass().addClass(\"alert alert-success\");\n"+
    		"	    $('#'+myDivStatusIDName).html(message).slideDown(1000).fadeOut(3000);\n"+
    		"	  }else if(color=='red'){\n"+
    		"	    $('#'+myDivStatusIDName).removeClass().addClass(\"alert alert-danger\");\n"+
    		"	    $('#'+myDivStatusIDName).html(message).slideDown(1000).fadeOut(3000);\n\n"+
    		"	  }\n\n"+  
    		"  }\n"+	  
    		"};\n\n" +
    		"$(function(){\n\n" +
    		"var lastPrevItem;\n\n" +
    		"// 修改標題 \n" +
    		"$(\".function-title\").html(sFunctionName);\n"+
    		"$.when(\n"+
    		"  $.getJSON(\"inmethod/auth/AuthenticationControlServlet\", {\"FlowID\" : \"getAuthorizedFunctionInfo\",\"FunctionName\":sFunctionName})\n"+
    		").done( function(jsonAuthorized){\n"+    		
    		"$.getJSON(sFunctionUrl, {\"FlowID\" : \"doQuery\"}, function(d) {\n" +
    		"		$('.'+myDivClassName).jsGrid({\n" +
    		"		    width: \"100%\",\n" +
    		"		    height: \"50%\",\n" +
    		"		    inserting: JSON.parse(jsonAuthorized.Insert),\n" +
    		"		    filtering: true,\n" +
    		"		    editing: JSON.parse(jsonAuthorized.Update),\n" +
    		"		    sorting: true,\n" +
    		"	        autoload: true,\n" +
    		"	        paging: true,\n" +
    		"	        pageSize: 15,\n" +
    		"	        pageButtonCount: 5,\n" +
    		"	        pagerFormat: \"Currnet: {pageIndex} &nbsp;&nbsp; {first} {prev} {pages} {next} {last} &nbsp;&nbsp; ALL: {pageCount} Page\",\n" +
    		"	        pagePrevText: \"<\",\n" +
    		"	        pageNextText: \">\",\n" +
    		"	        pageFirstText: \"<<\",\n" +
    		"	        pageLastText: \">>\",\n" +
    		"	        pageNavigatorNextText: \"&#8230;\",\n" +
    		"	        pageNavigatorPrevText: \"&#8230;\",\n" +
    		"	        noDataContent: \"No Data\",\n" +
    		"	        updateOnResize: true,\n" +
    		"		    data: d,\n" +
    		"           deleteConfirm: function(item) { \n" +
            "             return \"Are you sure?\";\n" +
            "           }, \n" +	        "           onItemUpdating: function(grid){\n" +
            "             lastPrevItem = grid.previousItem;\n" +
            "           },\n" +		    
    		"		    controller: {\n" +
    		"		        loadData: function(filter) {\n" +
    		"		            return $.grep(d, function(client) {\n" +
    		                       sFilter +
    		"		            });\n" +
    		"		        },\n\n" +
    		"		        deleteItem: function(ItemData) {\n" +
    		"                   var result = $.Deferred();\n\n"+
    		"		        	$.getJSON(sFunctionUrl, {\n" +
    		"		        		\"FlowID\" : \"doDelete\",\n" +
    		sDeleteParameter +
    		"		        			}, function(d) {\n" +
    		"		        				if(typeof d.DeleteResult !== \"undefined\"){\n" +
    		"		        				  if( d.DeleteResult==\"OK\"){\n" +
    		"                                   result.resolve(ItemData);\n" +   
    		"                                   show"+strClassName+"Message(\"Delete Success\",\"green\");\n"+
    		"		        				  }\n" +
    		"		        				  else{\n" +
    		"                                   result.reject(new Error(\"delete failed\"));\n" +
    		"                                   show"+strClassName+"Message(\"Delete Fail\",\"red\");\n"+
    		"		        				  }\n" +
    		"		        				}else{\n" +
    		"                                 result.reject(new Error(\"delete failed\"));\n" +
    		"                                 show"+strClassName+"Message(\"Delete Fail\",\"red\");\n"+
    		"		        				}\n" +
    		"		        		})\n" +
    		"		             .fail(function(s) {\n" +
    		"                        result.reject(new Error(s.responseText));\n" +
    		"                        show"+strClassName+"Message(\"Delete Fail\",\"red\");\n"+
    		"		             });\n" +
		    "                    return result.promise();\n" +
    		"		        },		        \n" +
    		"		        insertItem: function(ItemData) {\n" +
    		"                   var result = $.Deferred();\n\n"+
    		"		        	$.getJSON(sFunctionUrl, {\n" +
    		"		        		\"FlowID\" : \"doAdd\",\n" +
    		sAddParameter +
    		"		        			}, function(d) {\n\n" +
    		"		        				if(typeof d.AddResult !== \"undefined\"){\n" +
    		"		        				  if( d.AddResult==\"OK\"){\n" +
    		"                                   result.resolve(ItemData);\n" +   
    		"                                   show"+strClassName+"Message(\"Insert Success\",\"green\");\n"+
    		"		        				  }\n" +
    		"		        				  else{\n" +
    		"                                   result.reject(new Error(\"add failed\"));\n" +
    		"                                   show"+strClassName+"Message(\"Insert Fail\",\"red\");\n"+
    		"		        				  }\n" +
    		"		        				}else{\n" +
    		"                                 result.reject(new Error(\"add failed\"));\n" +
    		"                                 show"+strClassName+"Message(\"Insert Fail\",\"red\");\n"+
    		"		        				}\n" +
    		"		        		})\n" +
    		"		             .fail(function(s) {\n" +
    		"                        result.reject(new Error(s.responseText));\n" +
    		"                        show"+strClassName+"Message(\"Insert Fail\",\"red\");\n"+
    		"		             });\n" +
		    "                    return result.promise();\n" +
    		"		        },\n" +
    		"		        updateItem: function(ItemData) {\n" +
    		"                   var result = $.Deferred();\n\n"+
    		"		        	$.getJSON(sFunctionUrl, {\n" +
    		"		        		\"FlowID\" : \"doUpdate\",\n" +
    		sUpdateParameter +
    		"		        			}, function(d) {\n\n" +
    		"		        				if(typeof d.UpdateResult !== \"undefined\"){\n" +
    		"		        				  if( d.UpdateResult==\"OK\"){\n" +
    		"                                   result.resolve(ItemData);\n" +   
    		"                                   show"+strClassName+"Message(\"Update Success\",\"green\");\n"+
    		"		        				  }\n" +
    		"		        				  else{\n" +
    		"                                   result.resolve(lastPrevItem);\n" +
    		"                                   show"+strClassName+"Message(\"Update Fail\",\"red\");\n"+
    		"		        				  }\n" +
    		"		        				}else{\n" +
    		"                                 result.resolve(lastPrevItem);\n" +
    		"                                 show"+strClassName+"Message(\"Update Fail\",\"red\");\n"+
    		"		        				}\n" +
    		"		        		})\n" +
    		"		             .fail(function(s) {\n" +
    		"                        result.resolve(lastPrevItem);\n" +
    		"                        show"+strClassName+"Message(\"Update Fail\",\"red\");\n"+
    		"		             });\n" +
		    "                    return result.promise();\n" +
    		"		        }\n" +
    		"		    },\n\n" +
    		"		    fields: [\n" +
    		sField +
    		"		        { type: \"control\",width: 10,editButton: JSON.parse(jsonAuthorized.Update),deleteButton: JSON.parse(jsonAuthorized.Delete)}\n" +
    		"		    ]\n" +
    		"		});\n\n" +
    		"	}\n" +
    		").fail(function(d){\n"+
    		"    if(d.responseJSON.NoLogin==\"FAIL\"){\n" +
    		"       showMessage(\"No Login!\",\"red\");\n\n" +
    		"    }else if(d.responseJSON.NoPermission==\"FAIL\"){\n"+
    		"       showMessage(\"No Permission!\",\"red\");\n"+
            "    };\n"+
    		"});			\n\n" +
    		"})\n"+
	        "})\n";


    aHM.put("JsSource",strJsSource);
    return aHM;
  }

  /**
   * convert Name to Uppercase name  , ex: fieLd1_next -> Field1Next ,  fIELD => Field.
   * @param sName
   * @return String
   * @throws Exception
   */
  private String getFormattedName(String sName) throws Exception{
    if( sName == null || sName.length()<1) throw new Exception("no file name or filename is blank");

    StringBuffer sSB = new StringBuffer(sName.toLowerCase());
    int i=0,j;
    while( (i=sSB.toString().indexOf("_",i))!=-1){
      i++;
      sSB.replace(i,i+1,sSB.toString().substring(i,i+1).toUpperCase());
      sSB.replace(i-1,i,"");
    }
    String sReturn = sSB.toString().substring(0,1).toUpperCase() + sSB.toString().substring(1);
    return sReturn;

  }

  /**
   * replace some small string to another string
   * @param strOrigString original string
   * @param strCharBeReplaced will be replaced
   * @param strCharReplace new char
   * @return String
   */
  private String replace(String strOrigString,String strCharBeReplaced,String strCharReplace){
    StringBuffer sSB = new StringBuffer(strOrigString);
    int i=0,j;
    while( (i=strOrigString.indexOf(strCharBeReplaced,i))!=-1){
      i++;
      sSB.replace(i-1,i+strCharBeReplaced.length()-1,strCharReplace);
    }
    return sSB.toString();

  }

  /**
   * convert iSqlType to String
   * @param iSqlType
   * @return String
   */
  private String getDataType(int iSqlType){
    String strObjectType = null;
    switch(iSqlType){
      // suppose bigint, integer , tinyint to be Integer
      case java.sql.Types.BIGINT:
        strObjectType= "Long";
        break;
      case java.sql.Types.INTEGER:
      case java.sql.Types.TINYINT:
        strObjectType = "Integer";
        break;
      // float
      case java.sql.Types.FLOAT:
        strObjectType = "Float";
        break;
      // double, decimal convert to Double
      case java.sql.Types.DOUBLE:
      case java.sql.Types.DECIMAL:
        strObjectType = "Double";
        break;
      // char,varbinary,date,varchar to String
      case java.sql.Types.CHAR:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.VARCHAR:
        strObjectType = "String";
        break;
      case java.sql.Types.DATE:
        strObjectType = "Date";
        break;
      case java.sql.Types.TIMESTAMP:
        strObjectType = "String";
        break;
      default:
        strObjectType = "String";
        break;
    }
    return strObjectType;
  }

}