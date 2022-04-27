package inmethod.backend;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.URLEncoder;

import inmethod.auth.AuthFactory;
import inmethod.commons.rdb.SQLTools;
import inmethod.db.DBConnectionManager;

/**
 * downloadDocument(下載檔案) API.
 * 
 * @BackEnd
 * <pre>
 *   URL: downloadDocument
 * 
 *   功能: 下載檔案
 *   參數: 
 *        (R)FileName 
 *   回傳:
 *        檔案下載或是無任何資料輸出
 * </pre>       
 */
@WebServlet(name = "downloadDocument", urlPatterns = { "/downloadDocument" })
public class downloadDocument extends HttpServlet {
	private static final String ENCODE = "UTF-8";
	private static final String CONTENT_TYPE = "text/html; charset=" + ENCODE;
	private static final String FLOW_ID = "FlowID";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		doPost(req, resp);
	}

	/**
	 * read paramter "FileName" and output download information
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		String sLoginUserID = AuthFactory.getLoginUserID(request, response);
		FileInputStream istr = null;
		ServletOutputStream ostr = null;
		String sFileID = request.getParameter("FileID");
        if( sFileID==null || sFileID.equals("")) return;
		DownloadInfo aDI = getDownloadInfoByFildID(sFileID);
		if( aDI==null ) return ;
		String sFilePath = aDI.getFilePath();
		
		if (sFilePath == null || sFilePath.equals(""))
			return;
		try {
			response.setHeader("Content-Disposition", "inline;filename=\"" + URLEncoder.encode( aDI.getFileName() + sFilePath.substring(sFilePath .lastIndexOf(".")) , "UTF-8")+  "\"");
			if (sFilePath.toLowerCase().indexOf("txt") != -1) {
				response.setContentType("text/plain");
			} else if (sFilePath.toLowerCase().indexOf("doc") != -1||sFilePath.toLowerCase().indexOf("docx") != -1) {
				response.setContentType("application/msword");
			} else if (sFilePath.toLowerCase().indexOf("xls") != -1||sFilePath.toLowerCase().indexOf("xlsx") != -1) {
				response.setContentType("application/vnd.ms-excel");
			} else if (sFilePath.toLowerCase().indexOf("pdf") != -1) {
				response.setContentType("application/pdf");
			} else if (sFilePath.toLowerCase().indexOf("ppt") != -1 || sFilePath.toLowerCase().indexOf("pptx") != -1) {
				response.setContentType("application/ppt");
			} else if (sFilePath.toLowerCase().indexOf("odt") != -1) {
				response.setContentType("application/vnd.oasis.opendocument.text");
			} else if (sFilePath.toLowerCase().indexOf("ods") != -1) {
				response.setContentType("application/vnd.oasis.opendocument.spreadsheet");
			} else if (sFilePath.toLowerCase().indexOf("odp") != -1) {
				response.setContentType("application/vnd.oasis.opendocument.presentation");
			} else {
				response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode( aDI.getFileName() + sFilePath.substring(sFilePath .lastIndexOf(".")) , "UTF-8")+  "\"");
				response.setContentType("application/octet-stream");
			}
			try {
				Thread.sleep(1000);
			}catch(Exception ee) {
				ee.printStackTrace();
			}
			if( sLoginUserID==null)
				writeDownloadLog(BackEndGlobal.getIpAddress(request),sFileID);
			else
			writeDownloadLog(sLoginUserID,sFileID);
			//System.out.println("file path ="+sFilePath+ ", file name="+aDI.getFileName() );
			response.setCharacterEncoding("UTF-8");
			istr = new FileInputStream(sFilePath);
			ostr = response.getOutputStream();

			int curByte = -1;
			while ((curByte = istr.read()) != -1)
				ostr.write(curByte);
			ostr.flush();

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
			try {
				if (istr != null)
					istr.close();
				if (ostr != null)
					ostr.close();
			} catch (Exception ex) {
				System.out.println("Major Error Releasing Streams: " + ex.toString());
			}
		}
		try {
			response.flushBuffer();
		} catch (Exception ex) {
			System.out.println("Error flushing the Response: " + ex.toString());
		}

	}
	
	private DownloadInfo getDownloadInfoByFildID(String sFileID) {
		
      try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection();) {
	     String sSqlCmd = "select * from DOWNLOAD_INFO where FILE_ID='"+sFileID+"'";
	     System.out.println(sSqlCmd);
	     DownloadInfo aDI = new DownloadInfo();
	     
         ResultSet aRS = SQLTools.getInstance().Select(aConn,sSqlCmd);
         if( aRS!=null && aRS.next()) {
           aDI.setFileId(sFileID);
           aDI.setPrgId(aRS.getString("PRG_ID"));
           aDI.setFileName(aRS.getString("FILE_NAME"));
           aDI.setFilePath(aRS.getString("FILE_PATH"));
           aDI.setCreateTimestamp(aRS.getString("CREATE_TIMESTAMP"));
               
           return aDI;
       }
         return null;
	  }catch(Exception ee) {
		  ee.printStackTrace();
	  }		
      return null;
	}
	
	public static void writeDownloadLog(String sUserID,String sFileID) {
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection();) {
			String sSqlCmd = "insert into DOWNLOAD_LOG values ('"+sUserID+"','"+sFileID+"','"+ Calendar.getInstance().getTimeInMillis()  +"')";
			System.out.println(sSqlCmd);
			SQLTools.getInstance().Update(aConn,sSqlCmd);
		}catch(Exception ee) {
		  ee.printStackTrace();
		}		
		
	}

	public static void  writeDownloadInfo(String sCustomID,String sPrgID,String sFileName,String sFilePath) throws Exception{
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection();) {
			String sSqlCmd = "insert into DOWNLOAD_INFO values ('"+sCustomID+"','"+sPrgID+"','"+sFileName+"','"+sFilePath+"','"+ Calendar.getInstance().getTimeInMillis()  +"')";
			System.out.println(sSqlCmd);
			SQLTools.getInstance().Update(aConn, sSqlCmd);
		}catch(Exception ee) {
		  ee.printStackTrace();
		  throw new Exception("");
		}
	}
	
	public static String writeDownloadInfo(String sPrgID,String sFilePath) throws Exception{
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection();) {
			String sFileID = UUID.randomUUID().toString();
			
			String sSqlCmd = "insert into DOWNLOAD_INFO values ('"+sFileID+"','"+sPrgID+"','"+sFilePath.substring(sFilePath.lastIndexOf("/")+1,sFilePath.lastIndexOf(".")   ) +"','"  +sFilePath+"','"+ Calendar.getInstance().getTimeInMillis()  +"')";
			System.out.println(sSqlCmd);
			SQLTools.getInstance().Update(aConn,sSqlCmd);
            return sFileID;
		}catch(Exception ee) {
		  ee.printStackTrace();
		  throw new Exception("");
		}
	}
	
	
}