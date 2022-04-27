package inmethod.backend.example;

import inmethod.Global;
import inmethod.auth.AuthFactory;
import inmethod.auth.inter_face.WebAuthentication;
import inmethod.backend.BackEndGlobal;
import inmethod.backend.downloadDocument;
import inmethod.db.DBConnectionManager;
import inmethod.jakarta.jasper.JasperDesignManager;
import inmethod.jakarta.jasper.JasperReportManager;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

/**
 * 程式基本資料API.
 * 
 * @BackEnd
 * 
 *          <pre>
 *   URL: GenEmployeePdf
 * 
 *   功能:  網行放行明細表 From 財務
 *   參數: 
 *        (R)sUserID: 帳號

 *   回傳:
 *        {"STATUS":"TRUE|FALSE","MSG":"訊息","URL":""}
 *          </pre>
 */
@SuppressWarnings("serial")
@WebServlet(name = "GenEmployeePdf", urlPatterns = { "/inmethod/backend/example/GenEmployeePdf" })
public class GenEmployeePdf extends HttpServlet {
	
	private static final String CONTENT_TYPE = "text/html; charset="
			+ inmethod.Global.getInstance().getEnvirenment("ENCODE");
	private static String FUNCTION_NAME = "GenEmployeePdf";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		request.setCharacterEncoding(inmethod.Global.getInstance().getEnvirenment("ENCODE"));
		response.setContentType(CONTENT_TYPE);
		WebAuthentication aWebAuth = null;
        
		String sLoginUserID = null;
		PrintWriter out = response.getWriter();
		// System.out.println(FUNCTION_NAME);
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			try {
				aConn.setAutoCommit(true);
				aWebAuth = AuthFactory.getWebAuthentication(request, response);
				sLoginUserID = aWebAuth.getUserPrincipal();
				if (sLoginUserID == null || !aWebAuth.isLogin(sLoginUserID)) {
					out.println("{\"NoLogin\":\"FAIL\"}");
					out.flush();
					out.close();
					return;
				}
				
				/*
				Vector<String> aUserRoles=aWebAuth.getUserRoles(sUserID);
				for(String aUserRole:aUserRoles) {
					System.out.println("Role : "+aUserRole);
				}
				*/
				if (aWebAuth.getUserRoles(sLoginUserID) != null
						&& !aWebAuth.hasPermission(aWebAuth.getUserRoles(sLoginUserID), FUNCTION_NAME)) {
					out.println("{\"NoPermission\":\"FAIL\"}");
					out.flush();
					out.close();
					return;
				}
				//System.out.println("has permission : "+aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME));

				String sUserID = request.getParameter("UserID");
				String sReturn = buildPDF(sUserID);

				
				out.println(sReturn);
				out.flush();
				out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}		
	
	}

	public static void main(String ar[]) {
		try {

			String sUserID = "admin";
			System.out.println(new GenEmployeePdf().buildPDF(sUserID));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String buildPDF(String sUserID) {

		/**
		 * sReportName.jrxml  ,  sReportName.jasper,  sReportName.pdf 
		 */
		String sReportName = FUNCTION_NAME;
		
		String sReturnPDF = sReportName+ "-" +  Global.getInstance().getCurrentDateString()+ ".pdf";
        String sJasperReport = sReportName +".jasper";
        
		try (Connection aConn = inmethod.db.DBConnectionManager.getWebDBConnection().getConnection();) {

			
			JasperDesignManager aJasperDesignManager = new JasperDesignManager(BackEndGlobal.FILE_TEMPLATE+"jasper/" +sReportName+".jrxml");

			/**
			 * setup image
			 */
			aJasperDesignManager.setImagePath("KafeiouImage", BackEndGlobal.FILE_TEMPLATE+"jasper/cropped-kafeiou_logo2.png");
			
			aJasperDesignManager.addPdfCustomFont();
			aJasperDesignManager.compileJasperReport(BackEndGlobal.FILE_TEMPLATE + sJasperReport);
			JasperReportManager aJasperReportManager = new JasperReportManager(
					BackEndGlobal.FILE_TEMPLATE + sJasperReport, BackEndGlobal.FILE_TEMP + sReturnPDF);
			
			/**
			 * set connection
			 */
			aJasperReportManager.setConnection(aConn);
			
			/**
			 * set parameter
			 */
			aJasperReportManager.addParameter("UserID", sUserID);
			
			aJasperReportManager.buildPDF();
			String sFileID = downloadDocument.writeDownloadInfo(FUNCTION_NAME,BackEndGlobal.FILE_TEMP+sReturnPDF);
		    System.out.println("finished! msg = "+genMsg("TRUE", "download file id is "+sFileID,sFileID));
		    return genMsg("TRUE",sFileID ,sFileID);
			
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return sReturnPDF;
	}
	
	/**
	 * 
	 * @param sStatus "TRUE" , "FALSE"
	 * @param sMsg  
	 * @param sFileID download FILE UUID
	 * @return
	 */
	private String  genMsg(String sStatus,String sMsg,String sFileID) {		
	  return	"{\"STATUS\":\""+sStatus+"\",\"MSG\":\"" + sMsg + "\",\"URL\":\""+sFileID+"\"}";
	}

}