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
	private HashMap<String, String> colVals = new HashMap<String, String>();
	private MyCache cache;

	public MyDatabase() {
		File dir = new File(dbsPath);
		dir.mkdirs();
		cache = MyCache.getInstance();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				cache.clearCache();
				table.writeInFile();
			}
			
		}));
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
	private void addMapDecomposer(HashMap<String, Object> map) {
		getBasicFromMap(map);
		map.remove("table");
		map.remove("condition");
		
		if(table.getColumns().size() != map.size()) {
			this.colVals = null;
			return;
		}
		if (map.size() == table.getColumns().size()) {
			if (map.get("" + 0) == null) {
				Set<?> set = map.entrySet();
				Iterator<?> iter = set.iterator();
				while(iter.hasNext()) {
					Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
					if(!columnMatchValue(this.table.getColumnByName(m.getKey()), m.getValue())) {
						this.colVals.clear();
						return;
					}
					this.colVals.put(m.getKey(), m.getValue());
				}
			}else {
				for (int i = 0; i < map.size(); i++) {
					if(!columnMatchValue(this.table.getColumns().get(i), map.get("" + i).toString())) {
						this.colVals.clear();
						return;
					}
					this.colVals.put(this.table.getColumns().get(i).getName(), map.get("" + i).toString());
				}
			}
		}else {
			this.colVals.clear();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateMapDecomposer(HashMap<String, Object> map) {
		getBasicFromMap(map);
		map.remove("table");
		map.remove("condition");
		
		Set<?> set = map.entrySet();
		Iterator<?> iter = set.iterator();
		while(iter.hasNext()) {
			Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
			if(!columnMatchValue(this.table.getColumnByName(m.getKey()), m.getValue())) {
				this.colVals.clear();
				return;
			}
			this.colVals.put(m.getKey(), m.getValue());
		}
	}
	
	private void deleteMapDecomposer(HashMap<String, Object> map) {
		getTableFromMap(map);
		map.remove("table");
		map.remove("condition");
		this.colVals.clear();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> selectMapDecomposer(HashMap<String, Object> map) {
		getBasicFromMap(map);
		ArrayList<String> colNames = (ArrayList<String>)map.get("fields");
		if((colNames.size() == 1) && (colNames.get(0).equals("*"))) {
			colNames.clear();
			for (int i = 0; i < this.table.getColumns().size(); i++) {
				colNames.add(this.table.getColumns().get(i).getName());
			}
		}else if((colNames.size() == 1) && (!colNames.get(0).equals("*"))) {
			colNames.clear();
		}else {
			for (int i = 0; i < colNames.size(); i++) {
				if(this.table.getColumnByName(colNames.get(i)) == null) {
					colNames.clear();
					return colNames;
				}
			}
		}
		return colNames;
	}

	@SuppressWarnings("unchecked")
	private void getBasicFromMap(HashMap<String, Object> map) {
		getTableFromMap(map);
		this.condition = (ArrayList<String>)map.get("condition");
	}

	private void getTableFromMap(HashMap<String, Object> map) {
		if(!map.get("table").toString().equals(this.table.getName())) {
			cache.addToCache(table);
			table = cache.retrieveFromCache(map.get("table").toString());
			if(table == null) {
				this.tableName = map.get("condition").toString();
				cache.addToCache(xmlParser.LoadTable(dbsPath + System.getProperty("file.separator") + dbName + System.getProperty("file.separator") + tableName + ".xml"));
				table = cache.retrieveFromCache(map.get("table").toString());
			}
		}
	}
	
	private boolean columnMatchValue(Column<?> col, String val) {
		if(col.getType().getSimpleName().equals("Integer")) {
			return !val.contains("\'");
		}
		if(col.getType().getSimpleName().equals("String")) {
			return val.contains("\'");
		}
		return false;
	}
	
}
