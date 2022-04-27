package inmethod.arabica;

import java.util.Vector;

import inmethod.arabica.CoffeeMachine;
import inmethod.commons.rdb.MariadbConnection;

public class CodeGeneration {

	public static void main(String ar[]) {
		try {
			java.sql.Connection aConn = null;

			// 步驟1: 取得資料庫連線
			aConn = new MariadbConnection("localhost", "root", "onepizza", "WEB", "utf-8").getConnection();

			// 步驟2: 設定相關資料
			Vector aTableList = new Vector();
			aTableList.add("DOWNLOAD_INFO");
			aTableList.add("DOWNLOAD_LOG");
			
			Vector aCatalogName = new Vector<String>();
			aCatalogName.add("人事-公告");
			aCatalogName.add("人事-2公告");

			Vector aTableDesc = new Vector<String>();
			aTableDesc.add("公告");
			aTableDesc.add("公告2");

			
			// 步驟3: 設定程式路徑
			String strPackage = "inmethod.news";

			// 步驟4: 檔案產生存放路徑
			String strDirectory = "/opt";

			// 步驟5: 設定參數 (不需更改)
			Object[] obj = new Object[6];
			obj[0] = aConn; // parameter Connection
			obj[1] = aTableList; // Table Name List
			obj[2] = strPackage; // default package
			obj[3] = strDirectory;
			obj[4] = aCatalogName;
			obj[5] = aTableDesc;

			// 步驟6: 取得咖啡機 (不需更改)
			CoffeeMachine aCoffeeMachine = CoffeeMachine.getCoffeeMachineInstance();

			// 步驟7: 選擇咖啡豆 (目前只提供威廉專用咖啡豆)
			aCoffeeMachine.selectCoffeeBean(WebBean.BEAN_LOCATION);

			// 步驟8: 煮咖啡 (不需更改)
			aCoffeeMachine.makeCoffee(obj);

			// 步驟9: 喝咖啡 (不需更改)
			aCoffeeMachine.drinkCoffee();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
