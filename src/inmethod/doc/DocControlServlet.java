package inmethod.doc;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.JsonArray;

import javax.servlet.annotation.WebServlet;
import java.sql.*;
import java.util.*;
import java.io.*;

import inmethod.I18N;
import inmethod.auth.*;
import inmethod.auth.inter_face.*;
import inmethod.backend.downloadDocument;
import inmethod.commons.rdb.*;
import inmethod.commons.util.*;
import inmethod.db.DBConnectionManager;

/** 
 * DocControlServlet資料維護API
 * @BackEnd
 * <pre>
 * URL: inmethod/doc/DocControlServlet
 * 
 * 功能: 查詢基本資料       
 * 參數: (R)FlowID=doQuery   
 * 回傳: 所有基本資料
 *  [
 *    {"Oid":"Data","Catalog":"Data","Subject":"Data","Message":"Data","UploadType":"Data","UploadName":"Data","UploadPath":"Data","UpdateDt":"Data"}
 *    {"Oid":"Data","Catalog":"Data","Subject":"Data","Message":"Data","UploadType":"Data","UploadName":"Data","UploadPath":"Data","UpdateDt":"Data"}
 *  ]
 *  
 * 功能: 刪除基本資料      
 * 參數: (R)FlowID=doDelete    
 *      (R)Oid=
 * 回傳:
 *      {"DeleteResult":"OK"} 成功       
 *      {"DeleteResult":"FAIL"} 失敗        
 *  
 * 功能: 新增基本資料       
 * 參數: (R)FlowID=doAdd    
 *      (R)Oid=
 *      (O)Oid=
 *      (O)Catalog=
 *      (O)Subject=
 *      (O)Message=
 *      (O)UploadType=
 *      (O)UploadName=
 *      (O)UploadPath=
 *      (O)UpdateDt=
 * 回傳:
 *      {"AddResult":"OK"} 成功       
 *      {"AddResult":"FAIL"} 失敗        
 * 
 * 功能: 修改基本資料       
 * 參數: (R)FlowID=doUpdate
 *      (R)Oid=
 *      (O)Oid=
 *      (O)Catalog=
 *      (O)Subject=
 *      (O)Message=
 *      (O)UploadType=
 *      (O)UploadName=
 *      (O)UploadPath=
 *      (O)UpdateDt=
 * 回傳:
 *      {"UpdateResult":"OK"} 成功       
 *      {"UpdateResult":"FAIL"} 失敗        
 *      
 * </pre>
 * 
 */
@WebServlet(name = "DocControlServlet", urlPatterns = { "/inmethod/doc/DocControlServlet" })
public class DocControlServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private static String FUNCTION_NAME = null;
	private static final String FLOW_ID = "FlowID";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		doPost(req, resp);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType(CONTENT_TYPE);
		WebAuthentication aWebAuth = null;
		FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);		
		System.out.println("funcation="+FUNCTION_NAME);
		String sFlowID = request.getParameter(FLOW_ID);
		String sCatalog = request.getParameter("Catalog");
		String sUserID = null;
		PrintWriter out = response.getWriter();
		List<FileItem> multipart = null;
		if (ServletFileUpload.isMultipartContent(request)) {     
			System.out.println("is MultipartContent");
			ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
			upload.setHeaderEncoding("UTF-8");
			try {
				multipart = upload.parseRequest(request);
			      for (FileItem item : multipart) {
					   
				        if (item.isFormField()) {
				        	if(item.getFieldName().equalsIgnoreCase(FLOW_ID) ) {
				        		sFlowID = item.getString("UTF-8");
				        	}else if (item.getFieldName().equalsIgnoreCase("Catalog") ) {
				        		sCatalog =  item.getString("UTF-8");
				        	}		
				        }
				  }
				
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}      
		else
			System.out.println("is not MultipartContent");
				
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection() ) {
			try {
				aConn.setAutoCommit(false);
				aWebAuth = AuthFactory.getWebAuthentication(request, response); 
				sUserID = aWebAuth.getUserPrincipal();
				System.out.println("userid"+sUserID+",is login "+aWebAuth.isLogin(sUserID));
			
				if (sFlowID == null)
					return;
               else  if( sFlowID.equals("getDoc")){
				  // 不需要執行權限的功能,如html select option
            	  String sSqlCmd;
            		  
                  ResultSet aRS = null;
                  String sFieldName = null;
                  sSqlCmd ="select update_dt,oid,message,upload_name,upload_path from DOC where catalog=11   and update_dt<='"+ DateUtil.getDateStringWithFormat("yyyyMMdd")+"'   order by update_dt desc,oid desc";
          	      System.out.println("sql cmd="+sSqlCmd);
        	      
        	      aRS = SQLTools.getInstance().Select(aConn, sSqlCmd);
        	      
        	      sFieldName = "<th w-75 data-valign='middle' data-align='left' data-field='upload_name'>"+I18N.getValue(request, response, "FileName")+"</th>"+
	                                         "<th w-25 data-sortable='true' data-valign='middle' data-align='left' data-field='update_dt'>"+I18N.getValue(request, response,"Date")+"</th>" +
                                             "<th data-sortable='true' data-valign='middle' data-align='left' data-visible='false'  data-field='oid'>OID</th>"+
                                             "<th data-sortable='true' data-valign='middle' data-align='left' data-visible='false'  data-field='message'>message</th>"+
                                             "<th data-sortable='true' data-valign='middle' data-align='left' data-visible='false'  data-field='upload_path'>upload_path</th>";
        	      
        	      JsonArray aJA = JsonUtil.ResultSetToJsonArray(aRS);
        	      //或者 JsonArray aJA = JsonUtil  .ResultSetToJsonArraySkipTheSameColValue(aRS);
        	      System.out.println(  aJA.toString());
        	      String sMsg =   "{\"STATUS\":\"TRUE\",\"FIELD_NAME\":\""+sFieldName +"\",\"JSON\":" +  aJA.toString() + "}";
        	  	
            	  aConn.commit();
            	  out.print(sMsg);
           		  out.flush();
				  out.close();
            	  return;
				}else if( sFlowID.equals("getDoc2")){
					  // 不需要執行權限的功能,如html select option
	            	  String sSqlCmd;
	            		  
	                  ResultSet aRS = null;
	                  String sFieldName = null;
	                  sSqlCmd ="select update_dt,oid,message,upload_name,upload_path from DOC where catalog=12   and update_dt<='"+ DateUtil.getDateStringWithFormat("yyyyMMdd")+"'   order by update_dt desc,oid desc";
	          	      System.out.println("sql cmd="+sSqlCmd);
	        	      
	        	      aRS = SQLTools.getInstance().Select(aConn, sSqlCmd);
	        	      
	        	      sFieldName = "<th w-75 data-valign='middle' data-align='left' data-field='upload_name'>"+I18N.getValue(request, response, "FileName")+"</th>"+
                              "<th w-25 data-sortable='true' data-valign='middle' data-align='left' data-field='update_dt'>"+I18N.getValue(request, response,"Date")+"</th>" +
                              "<th data-sortable='true' data-valign='middle' data-align='left' data-visible='false'  data-field='oid'>OID</th>"+
                              "<th data-sortable='true' data-valign='middle' data-align='left' data-visible='false'  data-field='message'>message</th>"+
                              "<th data-sortable='true' data-valign='middle' data-align='left' data-visible='false'  data-field='upload_path'>upload_path</th>";
	        	      
	        	      JsonArray aJA = JsonUtil.ResultSetToJsonArray(aRS);
	        	      //或者 JsonArray aJA = JsonUtil  .ResultSetToJsonArraySkipTheSameColValue(aRS);
	        	      System.out.println(  aJA.toString());
	        	      String sMsg =   "{\"STATUS\":\"TRUE\",\"FIELD_NAME\":\""+sFieldName +"\",\"JSON\":" +  aJA.toString() + "}";
	        	  	
	            	  aConn.commit();
	            	  out.print(sMsg);
	           		  out.flush();
					  out.close();
	            	  return;
					}

				System.out.println("funcation="+FUNCTION_NAME);
				
				if (sUserID == null || !aWebAuth.isLogin(sUserID)) {
					response.setStatus(1000);
					out.println("{\"NoLogin\":\"FAIL\"}");
					out.flush();
					out.close();
					aConn.rollback();
					return;
				}	
				if( sCatalog!=null && !sCatalog.trim().equals("")  )
				   FUNCTION_NAME = FUNCTION_NAME +"?Catalog="+sCatalog;
				
					System.out.println("funcation="+FUNCTION_NAME);
				if (aWebAuth.getUserRoles(sUserID) != null
						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {
					response.setStatus(1000);
					out.println("{\"NoPermission\":\"FAIL\"}");
					out.flush();
					out.close();
					aConn.rollback();
					return;
				}
               RoleAuthorizedPermission aRoleAuthorizedPermission = aWebAuth.getAuthorizedFunctionInfo(sUserID, FUNCTION_NAME);
               if( aRoleAuthorizedPermission==null){ 
                 response.setStatus(1000);
                 out.println("{\"NoPermission\":\"FAIL\"}");
	              out.flush();
	              out.close();
	              aConn.rollback();
               }
			    else if (sFlowID.equalsIgnoreCase("doQuery")  && aRoleAuthorizedPermission.getFunctionQuery().equalsIgnoreCase("Y")) {
					DocBeanFactory aBF = DocBeanFactory.getInstance(aConn);
					Doc aDoc = null;
					if( sCatalog!=null && !sCatalog.trim().equals("")  ) {
						aDoc =  new Doc();
						aDoc.setCatalog( Integer.parseInt( sCatalog) );						
					}
					
					DataSet aDS = aBF.Query(aDoc, null);
					String sReturn = "";
					while (aDS != null && aDS.next()) {
						aDoc = (Doc) aDS.getData();
						sReturn = sReturn + aDoc.toJson() + ",";
					}
					System.out.println(sReturn);
					if (sReturn.length() == 0) {
						sReturn = "[]";
					} else {
						sReturn = "[" + sReturn.substring(0, sReturn.length() - 1) + "]";
					}
					out.println(sReturn);
				} else if (sFlowID.equalsIgnoreCase("doUpdate") && aRoleAuthorizedPermission.getFunctionUpdate().equalsIgnoreCase("Y")) {
					DocBeanFactory aBF = DocBeanFactory.getInstance(aConn);
					Doc aDoc = new Doc();
                    if( request.getParameter(Doc.FIELD_OID)!=null && !request.getParameter(Doc.FIELD_OID).equals("") ) 
                      aDoc.setOid( new Integer(request.getParameter(Doc.FIELD_OID)) );
                    if( request.getParameter(Doc.FIELD_CATALOG)!=null && !request.getParameter(Doc.FIELD_CATALOG).equals("") ) 
                      aDoc.setCatalog( new Integer(request.getParameter(Doc.FIELD_CATALOG)) );
                    aDoc.setMessage( request.getParameter(Doc.FIELD_MESSAGE) );
                    if( request.getParameter(Doc.FIELD_UPLOAD_TYPE)!=null && !request.getParameter(Doc.FIELD_UPLOAD_TYPE).equals("") ) 
                      aDoc.setUploadType( new Integer(request.getParameter(Doc.FIELD_UPLOAD_TYPE)) );
                    aDoc.setUploadName( request.getParameter(Doc.FIELD_UPLOAD_NAME) );
         //           aDoc.setUploadPath( request.getParameter(Doc.FIELD_UPLOAD_PATH) );
                    aDoc.setUpdateDt( request.getParameter(Doc.FIELD_UPDATE_DT) );

					try {
						if (aBF.update(aDoc) == 0) {
							response.setStatus(1000);
							out.println("{\"UpdateResult\":\"FAIL\"}");
                           aConn.rollback();
						} else {
							out.print("{\"UpdateResult\":\"OK\"}");
                           aConn.commit();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"UpdateResult\":\"FAIL\"}");
					    aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doAdd")&& aRoleAuthorizedPermission.getFunctionInsert() .equalsIgnoreCase("Y")) {
				
					DocBeanFactory aBF = DocBeanFactory.getInstance(aConn);
					
					Doc aDoc = new Doc();
					String sParameter = null;
					String sParameterValue = null;
				      for (FileItem item : multipart) {
						   
					        if (item.isFormField()) {
					      //  	System.out.println(item.getFieldName()+","+ item.getString("UTF-8") );
					        	sParameter = item.getFieldName();
					        	sParameterValue = item.getString("UTF-8");
					        	
					        	if(sParameter.equalsIgnoreCase(Doc.FIELD_OID) ) {
					        		if( sParameterValue!=null && !sParameterValue.equals("") )
					        			try {
					        			  aDoc.setOid(Integer.parseInt(sParameterValue));
					        			}catch(Exception ee) {
					        			}
					        	}else 	if(sParameter.equalsIgnoreCase(Doc.FIELD_CATALOG) ) {
					        		if( sParameterValue!=null && !sParameterValue.equals("") )
					        			aDoc.setCatalog(Integer.parseInt(sParameterValue));
					        	}else 	if(sParameter.equalsIgnoreCase(Doc.FIELD_UPLOAD_TYPE) ) {
					        		if( sParameterValue!=null && !sParameterValue.equals("") )
					        			aDoc.setUploadType(Integer.parseInt(sParameterValue));
					        	}else 	if(sParameter.equalsIgnoreCase(Doc.FIELD_MESSAGE) ) {
					        		if( sParameterValue!=null && !sParameterValue.equals("") )
					        			aDoc.setMessage(sParameterValue);
					        	}else 	if(sParameter.equalsIgnoreCase(Doc.FIELD_UPLOAD_NAME) ) {
					        		if( sParameterValue!=null && !sParameterValue.equals("") )
					        			aDoc.setUploadName(sParameterValue);
					        	}else 	if(sParameter.equalsIgnoreCase(Doc.FIELD_UPDATE_DT) ) {
					        		if( sParameterValue!=null && !sParameterValue.equals("") )
					        			aDoc.setUpdateDt(sParameterValue);
					        	}
					        		
					        	
					        }else {
					        	
					            DiskFileItemFactory factory = new DiskFileItemFactory();

			                    // maximum size that will be stored in memory
			                    factory.setSizeThreshold(1000000);

			                    // Location to save data that is larger than maxMemSize.
			                    factory.setRepository(new File(DocGlobal.DOC_DIR ));

			                    // Create a new file upload handler
			                    ServletFileUpload upload = new ServletFileUpload(factory);

			                    // maximum file size to be uploaded.
			                    upload.setSizeMax( 1000000 );

			                    try { 
			               
			                             // Get the uploaded file parameters
			                             String fieldName = item.getFieldName();
			                         	
			                             String filePath = item.getName();
			                             String contentType = item.getContentType();
			                             boolean isInMemory = item.isInMemory();
			                             long sizeInBytes = item.getSize();
			                            File filess = null;
			                            Long longTime =   Calendar.getInstance().getTimeInMillis() ;
			                            String sFIleID = longTime + filePath.substring(filePath .lastIndexOf("."));
			                            String sUploadPath = DocGlobal.DOC_DIR  + sFIleID;
			                             downloadDocument.writeDownloadInfo( sFIleID,  FUNCTION_NAME,aDoc.getUploadName(),sUploadPath);
			                             // Write the file
		                            	 filess = new File(sUploadPath) ;
		                            	 aDoc.setUploadPath(sUploadPath);
			                             item.write( filess ) ;
			                       } catch(Exception ex) {
			                          System.out.println(ex);
			                       }					        	
					        	
					        }
					  }
					
                    
					try {
						if (aBF.insert(aDoc) == 0) {
							response.setStatus(1000);
							out.println("{\"AddResult\":\"FAIL\"}");
                           aConn.rollback();
						} else {
							out.print("{\"AddResult\":\"OK\"}");
                           aConn.commit();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"AddResult\":\"FAIL\"}");
                       aConn.rollback();
					}
				} else if (sFlowID.equalsIgnoreCase("doDelete") && aRoleAuthorizedPermission.getFunctionDelete().equalsIgnoreCase("Y")) {
					DocBeanFactory aBF = DocBeanFactory.getInstance(aConn);
					Doc aDoc = new Doc();
                    aDoc.setOid(Integer.parseInt( request.getParameter(Doc.FIELD_OID)) );

					try {
						if (aBF.delete(aDoc) == 0) {
							response.setStatus(1000);
							out.println("{\"DeleteResult\":\"FAIL\"}");
                           aConn.rollback();
						} else {
							out.print("{\"DeleteResult\":\"OK\"}");
                           aConn.commit();
						}
					} catch (Exception ee) {
						response.setStatus(1000);
						out.println("{\"DeleteResult\":\"FAIL\"}");
                       aConn.rollback();
					}
				}
               
				out.flush();
				out.close();
				return;
			} catch (Exception ex) {
				aConn.rollback();
				ex.printStackTrace();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
