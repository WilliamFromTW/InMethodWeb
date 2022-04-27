package inmethod.test;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;


import inmethod.db.DBConnectionManager;
import inmethod.commons.rdb.ResultSetToJson;
import inmethod.commons.util.JsonUtil;

/**
 * @BackEnd 
 * 測試-從servlet中抓取資料庫資料,然後回傳json字傳的範例
 * <pre>
 * URL: inmethod/test/testJsonFromDb (servlet 範例)
 * 
 * 功能: 查詢資料表FUNCTION_INFO資料
 * 參數: (R)FlowID=doQuery    
 * 回傳: 
 *  Html code (含FUNCTION_INFO資料轉成json格式)
 * </pre>
 *
 */
@WebServlet(name = "testJsonFromDb", urlPatterns = { "/test/testJsonFromDbServlet" })
public class testDbToJson extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// resp.setContentType("application/json; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Servlet testJsonFromDb</title>");
		out.println("</head>");
		out.println("<body>");
		
		try(Connection aConn = DBConnectionManager.getWebDBConnection().getConnection();Statement aStat = aConn.createStatement();) {
			ResultSet aResultSet = aStat.executeQuery("select * from FUNCTION_INFO");
			out.println("json=" + JsonUtil.ResultSetToJsonArrayString(aResultSet) + "</br>");
			aResultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
	}
}