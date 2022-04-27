package inmethod.backend.template;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import inmethod.db.DBConnectionManager;
import inmethod.auth.AuthFactory;
import inmethod.auth.inter_face.*;
import inmethod.commons.rdb.ResultSetToJson;

/**
 * 測試-從servlet收到資料,然後回傳json字傳的範例
 * 
 * @BackEnd
 * <pre>
 * 
 * URL: inmethod/test/templateServlet (servlet 範例)
 * 
 * 功能: 查詢
 * 參數: (R)FlowID=doQuery    
 * 回傳: 
 *     {"doQuery":"OK"}
 *
 * 功能: 更新
 * 參數: (R)FlowID=Update  
 *      (O)FunctionName=xxx  (O)代表可有可無之參數
 * 回傳: 
 *     {"doUpdate":"OK"}
 *   
 *   
 *   
 * 回傳: 
 *     {"NoLogin":"FAIL"}  // 未登入  
 *     {"NoPermission":"FAIL"}  // 無權限
 * </pre>
 */
@WebServlet(name = "templateServlet", urlPatterns = { "/inmethod/test/templateServlet" })
public class templateServlet extends HttpServlet {
	
	private static final String CONTENT_TYPE = "text/html; charset=" + inmethod.Global.getInstance().getEnvirenment("ENCODE");
	private static String FUNCTION_NAME = null;
	private static final String FLOW_ID = "FlowID";

	// Initialize global variables
	public void init() throws ServletException {
	}

	// Process the HTTP Get request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(inmethod.Global.getInstance().getEnvirenment("ENCODE"));
		response.setContentType(CONTENT_TYPE);
		WebAuthentication aWebAuth = null;
		FUNCTION_NAME = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);

		String sFlowID = request.getParameter(FLOW_ID);
		String sUserID = null;
		PrintWriter out = response.getWriter();
		try (Connection aConn = DBConnectionManager.getWebDBConnection().getConnection()) {
			try {
				aConn.setAutoCommit(false);
				aWebAuth = AuthFactory.getWebAuthentication(request, response);
				sUserID = aWebAuth.getUserPrincipal();

				if (sUserID == null || !aWebAuth.isLogin(sUserID)) {
					out.println("{\"NoLogin\":\"FAIL\"}");
				}

				if (aWebAuth.getUserRoles(sUserID) != null
						&& aWebAuth.hasPermission(aWebAuth.getUserRoles(sUserID), FUNCTION_NAME)) {
					out.println("{\"NoPermission\":\"FAIL\"}");
				}

				if (sFlowID == null)
					return;

				if (sFlowID.equalsIgnoreCase("doQuery")) {

					// 當FlowID等於doQuery做甚麼事情
					out.println("{\"doQuery\":\"OK\"}");
					
				} else if (sFlowID.equalsIgnoreCase("doUpdate")) {
					
					// 當FlowID等於doUpdate
					out.println("{\"doUpdate\":\"OK\"}");
					
				} else {
					
					// 當FlowID不等於 doQuery , doUpdate 

				}

				aConn.commit();
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