package eg.edu.alexu.csd.oop.db.cs24;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class MyDatabase implements Database {
	
	private final String dbsPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "Databases";
	
	private String dbName = "";
	private String tableName = "";
	private ArrayList<String> condition;
	private Table table;
	private XML xmlParser = XML.getInstace();
	private HashMap<String, String> colVals;

	public MyDatabase() {
		File dir = new File(dbsPath);
		dir.mkdirs();
	}
	
	public String createDatabase(String databaseName, boolean dropIfExists) {
		return null;
	}
	
	public boolean executeStructureQuery(String query) throws SQLException {
		return false;
	}

	public Object[][] executeQuery(String query) throws SQLException {
		return null;
	}

	public int executeUpdateQuery(String query) throws SQLException {
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void addMapDecomposer(HashMap<String, Object> map) throws SQLException {
		if(!this.tableName.equals(map.get("table").toString())) {
			this.table = xmlParser.LoadTable(this.dbsPath + System.getProperty("file.separator") + this.dbName + map.get("table").toString());
			if(this.table == null) {
				JOptionPane.showMessageDialog(null, "Table doesn't exist!");
				return;
			}
			this.tableName = map.get("table").toString();
			map.remove("table");
		}
		this.condition = (ArrayList<String>) map.get("condition");
		map.remove("condition");
		
		Set<?> set = map.entrySet();
		Iterator<?> iter = set.iterator();
		int count = 0;
		boolean check = false;
		
		while (iter.hasNext()) {
			Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
			if(table.getColumnByName(m.getKey()) == null) {
				if(map.size() == table.getColumns().size()) {
					
				}else {
					throw new SQLException();
				}
			}else {
				for(int i = 0; ((i < table.getColumns().size()) && !check); i++) {
					if(map.get(table.getColumns().get(i).getName()) == null) {
						throw new SQLException();
					}
				}
				check = true;
			}
			
			
		}
	}
	
}
