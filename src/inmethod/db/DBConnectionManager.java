package inmethod.db;

import inmethod.Global;
import inmethod.commons.rdb.JDBCConnection;
import inmethod.commons.rdb.MysqlConnection;
import inmethod.commons.rdb.OracleConnection;

public class DBConnectionManager {

	
	
	public static JDBCConnection getWebDBConnection() {
		if( Global.getInstance().getEnvirenment("DATABASE_TYPE").equalsIgnoreCase("MYSQL")) {
			  return new MysqlConnection(Global.getInstance().getEnvirenment("WEB_DB_HOST"),
					  Global.getInstance().getEnvirenment("WEB_DB_UID"), 
					  Global.getInstance().getEnvirenment("WEB_DB_PWD"),
					  Global.getInstance().getEnvirenment("WEB_DB"),
					  Global.getInstance().getEnvirenment("ENCODE"), new inmethod.commons.NumberGen.NumberGen());
			
		}else if( Global.getInstance().getEnvirenment("DATABASE_TYPE").equalsIgnoreCase("ORACLE") ){ 
			  return new OracleConnection(Global.getInstance().getEnvirenment("WEB_DB_HOST"),
					  Global.getInstance().getEnvirenment("WEB_DB"),
					  Global.getInstance().getEnvirenment("WEB_DB_UID"), 
					  Global.getInstance().getEnvirenment("WEB_DB_PWD"),
						 new inmethod.commons.NumberGen.NumberGen());	
		}
		
		return null;
	}

	
}
