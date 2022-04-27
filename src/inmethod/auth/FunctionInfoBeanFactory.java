package inmethod.auth;

import inmethod.commons.rdb.*;
import inmethod.db.DBConnectionManager;

import java.sql.*;
import java.util.*;

public class FunctionInfoBeanFactory {

	private Connection aConn = null;
	private String sEncode = "UTF-8";
	private static FunctionInfoBeanFactory aBeanFacade = null;

	private FunctionInfoBeanFactory() {
	}

	public static FunctionInfoBeanFactory getInstance(Connection aConn) {
		if (aBeanFacade == null)
			aBeanFacade = new FunctionInfoBeanFactory();
		aBeanFacade.setConnection(aConn);
		return aBeanFacade;
	}

	public void setConnection(Connection aConn) {
		this.aConn = aConn;
	}



	public int insert(FunctionInfo aFunctionInfo) throws Exception {
		Vector aVector = new Vector();
		aVector.add(aFunctionInfo.getFunctionName());
		aVector.add(aFunctionInfo.getFunctionUrl());
		aVector.add(aFunctionInfo.getFunctionDesc());

		return SQLTools.getInstance().preparedInsert(aConn, "insert into FUNCTION_INFO values (?,?,?)", aVector);
	}

	public int delete(FunctionInfo aFunctionInfo) throws Exception {
		Vector aVector = new Vector();
		aVector.add(aFunctionInfo.getFunctionName());
		return SQLTools.getInstance().preparedDelete(aConn, "delete from FUNCTION_INFO where function_name=?", aVector);
	}

	public int update(FunctionInfo aFunctionInfo) throws Exception {
		Vector aVector = new Vector();
		aVector.add(aFunctionInfo.getFunctionName());
		aVector.add(aFunctionInfo.getFunctionUrl());
		aVector.add(aFunctionInfo.getFunctionDesc());
		aVector.add(aFunctionInfo.getFunctionName());
		//System.out.println(aFunctionInfo.getFunctionName()+aFunctionInfo.getFunctionUrl());
		return SQLTools.getInstance().preparedUpdate(aConn,
				"update FUNCTION_INFO set function_name=?,function_url=?,function_desc=? where function_name=?",
				aVector);
	}

	public DataSet Query(FunctionInfo aFunctionInfo, String sOrderBy) throws Exception {
		SQLCondition aSqlCondition = new SQLCondition();
		if (aFunctionInfo != null) {
			if (aFunctionInfo.getFunctionName() != null && !aFunctionInfo.getFunctionName().equals(""))
				aSqlCondition.and("function_name='" + aFunctionInfo.getFunctionName() + "'");
			if (aFunctionInfo.getFunctionUrl() != null && !aFunctionInfo.getFunctionUrl().equals(""))
				aSqlCondition.and("function_url='" + aFunctionInfo.getFunctionUrl() + "'");
			if (aFunctionInfo.getFunctionDesc() != null && !aFunctionInfo.getFunctionDesc().equals(""))
				aSqlCondition.and("function_desc='" + aFunctionInfo.getFunctionDesc() + "'");
		}
		ResultSet aRS = null;
		DataSet aDS = new DataSet();
		FunctionInfo aTempFunctionInfo = null;

		if (sOrderBy != null && !sOrderBy.equals("")) {
			if (aFunctionInfo == null || aSqlCondition.toString() == null)
				aRS = SQLTools.getInstance().Select(aConn, "select * from FUNCTION_INFO order by " + sOrderBy);
			else
				aRS = SQLTools.getInstance().Select(aConn,
						"select * from FUNCTION_INFO where " + aSqlCondition.toString() + " order by " + sOrderBy);
		} else {
			if (aFunctionInfo == null || aSqlCondition.toString() == null)
				aRS = SQLTools.getInstance().Select(aConn, "select * from FUNCTION_INFO");
			else
				aRS = SQLTools.getInstance().Select(aConn,
						"select * from FUNCTION_LIST where " + aSqlCondition.toString());
		}
		while (aRS != null && aRS.next()) {
			aTempFunctionInfo = new FunctionInfo();
			aTempFunctionInfo.setFunctionName((String) aRS.getObject("function_name"));
			aTempFunctionInfo.setFunctionUrl((String) aRS.getObject("function_url"));
			aTempFunctionInfo.setFunctionDesc((String) aRS.getObject("function_desc"));
			aDS.addData(aTempFunctionInfo);
		}
		try {
				aRS.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}

		return aDS;
	}

	public DataSet pageQuery(FunctionInfo aFunctionInfo, String sOrderBy, long lRowsBegin, long lPageRows)
			throws Exception {
		SQLCondition aSqlCondition = new SQLCondition();
		if (aFunctionInfo != null) {
			if (aFunctionInfo.getFunctionName() != null && !aFunctionInfo.getFunctionName().equals(""))
				aSqlCondition.and("function_name='" + aFunctionInfo.getFunctionName() + "'");
			if (aFunctionInfo.getFunctionUrl() != null && !aFunctionInfo.getFunctionUrl().equals(""))
				aSqlCondition.and("function_url='" + aFunctionInfo.getFunctionUrl() + "'");
			if (aFunctionInfo.getFunctionDesc() != null && !aFunctionInfo.getFunctionDesc().equals(""))
				aSqlCondition.and("function_desc='" + aFunctionInfo.getFunctionDesc() + "'");
		}
		long i = 0;
		ResultSet aRS = null;
		
		if (sOrderBy != null && !sOrderBy.equals("")) {
			if (aFunctionInfo == null || aSqlCondition.toString() == null)
				aRS = SQLTools.getInstance().PageSelect(aConn, "select * from FUNCTION_INFO order by " + sOrderBy,
						lRowsBegin, lPageRows);
			else
				aRS = SQLTools.getInstance().PageSelect(aConn,
						"select * from FUNCTION_LIST where " + aSqlCondition.toString() + " order by " + sOrderBy,
						lRowsBegin, lPageRows);
		} else {
			if (aFunctionInfo == null || aSqlCondition.toString() == null)
				aRS = SQLTools.getInstance().PageSelect(aConn, "select * from FUNCTION_INFO", lRowsBegin, lPageRows);
			else
				aRS = SQLTools.getInstance().PageSelect(aConn,
						"select * from FUNCTION_LIST where " + aSqlCondition.toString(), lRowsBegin, lPageRows);
		}
		DataSet aDS = new DataSet();
		FunctionInfo aTempFunctionInfo = null;
		while (aRS != null && aRS.next()) {
			i++;
			if (i > lPageRows)
				break;
			aTempFunctionInfo = new FunctionInfo();
			aTempFunctionInfo.setFunctionName((String) aRS.getObject("function_name"));
			aTempFunctionInfo.setFunctionUrl((String) aRS.getObject("function_url"));
			aTempFunctionInfo.setFunctionDesc((String) aRS.getObject("function_desc"));
			aDS.addData(aTempFunctionInfo);
		}
		try {
				aRS.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}

		return aDS;
	}



	public long getTableTotalCount(String sTable) {
		if (sTable == null || sTable.equals(""))
			return -1;
		long iReturn = -1;
		ResultSet aRS = null;
		try  {
			aRS = SQLTools.getInstance().Select(aConn, "select count(*) as NUM from " + sTable);
			if (aRS != null && aRS.next())
				iReturn = aRS.getLong("NUM");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			aRS.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return iReturn;
	}

}