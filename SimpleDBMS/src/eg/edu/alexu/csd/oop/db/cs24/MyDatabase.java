package eg.edu.alexu.csd.oop.db.cs24;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

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
	private Parser parser=Parser.getInstace();
	private boolean Drop;

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
		Drop=dropIfExists;
		dbName=databaseName;
		File NewDatabase =new File(dbsPath+System.getProperty("file.separator")+dbName);
		if(!NewDatabase.exists()||Drop)
		{
			if(NewDatabase.exists())
				delete(NewDatabase);
			NewDatabase.mkdirs();
		}
		return dbsPath+System.getProperty("file.separator")+dbName;
	}
	
	public boolean executeStructureQuery(String query) throws SQLException {//the one that deals with create and drop
		HashMap<String,Object> map;
		int TypeChecker=parser.typechecker(query.toLowerCase());
		if(TypeChecker==-1){throw new SQLException("syntax error");}
		else if(TypeChecker==0){//create database
			map= (HashMap<String, Object>) parser.createdatabase(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			//at this point the query is correct and tha map contains the database's name
			if(createDatabase(map.get("DataBaseName").toString(), true).equals(dbsPath+System.getProperty("file.separator")+dbName)) {
				return true;
			}
//			dbName= (String) map.get("DataBaseName");
//			File NewDatabase =new File(dbsPath+System.getProperty("file.separator")+dbName);
//			if(!NewDatabase.exists()||Drop)
//			{
//				NewDatabase.mkdirs();
//				return true;
//			}

		}
		else if(TypeChecker==1){//create table
			map= (HashMap<String, Object>) parser.createtable(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			//at this point the query is correct and tha map contains the table's name and field/type (key,value pair)
			ArrayList<String>columns=new ArrayList<>();
			tableName=(String)map.get("tableName");
			map.remove("tableName");
            Iterator<Entry<String, Object>> it = map.entrySet().iterator();
			while(it.hasNext())
			{
					Map.Entry<String, Object> pair = (Map.Entry<String, Object>)it.next();
					columns.add( pair.getKey()+ ", "+pair.getValue());
			}
			File NewTable =new File(dbsPath+System.getProperty("file.separator")+dbName+System.getProperty("file.separator")+tableName+".xml");
			if(dbName!="")
			{
				if(!NewTable.exists())
				{
					table=new Table(tableName,columns);
					table.setPath(dbsPath+System.getProperty("file.separator")+dbName+System.getProperty("file.separator")+tableName+".xml");
					table.createXML();
					cache.addToCache(table);
					return true;
				}
				else
					return false;
					/*table=xmlParser.LoadTable(dbsPath+System.getProperty("file.separator")+dbName+System.getProperty("file.separator")+tableName);
					cache.retrieveFromCache(tableName);
					cache.addToCache(table);
					return true;*/
			}
			throw new SQLException("No Table found");
		}
		else if(TypeChecker==2){//drop database
			map= (HashMap<String, Object>) parser.dropdatabase(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			//at this point the query is correct and tha map contains the database's name(you also need to check if a database with this name exists)
			File database=new File(dbsPath+System.getProperty("file.separator")+dbName);
			if(database.exists())
			{
				delete(database);
				return true;
			}
		}
		else if(TypeChecker==3){//drop table
			map= (HashMap<String, Object>) parser.droptable(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			//at this point the query is correct and tha map contains the table's name(you also need to check if a database with this name exists)
			File Table =new File(dbsPath+System.getProperty("file.separator")+dbName+System.getProperty("file.separator")+map.get("tableName")+".xml");
			File DTD =new File(dbsPath+System.getProperty("file.separator")+dbName+System.getProperty("file.separator")+map.get("tableName")+".dtd");
			if(Table.exists()&&DTD.exists())
			{
				delete(Table);
				delete(DTD);
				cache.retrieveFromCache((String) map.get("tableName"));
				return true;
			}
		}
		return false;
	}

	public Object[][] executeQuery(String query) throws SQLException {//the one that deals with select
		HashMap<String,Object> map=new HashMap<>();
		map=(HashMap<String, Object>) parser.selectQueryParser(query.toLowerCase());
		if(map==null){throw new SQLException("syntax error");}
		ArrayList<String> names = selectMapDecomposer(map);
		if(names.size()==0){throw new SQLException("syntax error");}
		//at this point map contains 1)table==>(String)tablename  2)fields==>(Arraylist<Strings> contains the field to be shown or * if all the fields are to be show
		//3)condition==>Arraylist<String>condition that contains the conditions or null if there isn't any
		//(you also need to check if a database with this name exists)

		if(table != null)
		{
			return table.SelectRecord(this.condition,names);
		}
		throw new SQLException("Table doesn't exist");
	}

	public int executeUpdateQuery(String query) throws SQLException {//the one that deals with update insert and delete
		HashMap<String ,Object>map;
		int updatedRows = 0;
		int TypeChecker=parser.typechecker(query.toLowerCase());
		if(TypeChecker==-1){throw new SQLException("syntax error");}
		else if(TypeChecker==5){
			map=(HashMap<String, Object>) parser.updateQueryParser(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			updateMapDecomposer(map);
			if(colVals.size()==0){throw new SQLException("syntax error");}
			//at this point you know that the update query has no errors ask hossam to know what is stored where
			//(you also need to check if a database with this name exists)
			if((table != null) && (colVals.size() != 0))
			{
				updatedRows = table.updateRecord(this.colVals, this.condition);
			}
		}
		else if(TypeChecker==6){
			map=(HashMap<String, Object>) parser.deleteQueryParser(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			deleteMapDecomposer(map);
			if(colVals.size()==0){throw new SQLException("syntax error");}
			//at this point you know that the delete query has no errors ask hossam to know what is stored where
			//(you also need to check if a database with this name exists)
			if((table != null) && (colVals.size() == 0))
			{
				updatedRows = table.deleteRecord(this.condition);
			}
		}
		else if (TypeChecker==7){
			map=(HashMap<String, Object>) parser.insertQueryParser(query.toLowerCase());
			if(map==null){throw new SQLException("syntax error");}
			addMapDecomposer(map);
			if(colVals.size()==0){throw new SQLException("syntax error");}
			//at this point you know that the insert query has no errors ask hossam to know what is stored where
			//(you also need to check if a database with this name exists)
			if((table != null) && (colVals.size() != 0))
			{
				updatedRows = table.addRecord(this.colVals);;
			}
		}
		return updatedRows;
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
	private void updateMapDecomposer(HashMap<String, Object> map) throws SQLException {
		getBasicFromMap(map);
		map.remove("table");
		map.remove("condition");
		
		Set<?> set = map.entrySet();
		Iterator<?> iter = set.iterator();
		if(table==null)
			throw new SQLException("Null table");
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
		map.remove("table");
		map.remove("condition");
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
				this.tableName = map.get("table").toString();
				Table t = xmlParser.LoadTable(dbsPath + System.getProperty("file.separator") + dbName + System.getProperty("file.separator") + tableName + ".xml");
				if(t != null) {
					cache.addToCache(t);
					table = cache.retrieveFromCache(map.get("table").toString());
				}else {
					table = t;
				}
			}
		}
	}

	private boolean columnMatchValue(Column<?> col, String val) {
		// corrected
		if(col == null) {
			return false;
		}
		if(col.getType().getSimpleName().equals("Integer")) {
			return !val.contains("\'");
		}
		if(col.getType().getSimpleName().equals("String")) {
			return val.contains("\'");
		}
		return false;
	}

	public static void delete(File file) {

		if(file.isDirectory()){
			if(file.list().length==0)
				file.delete();
			else{

				//list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);

					//recursive delete
					delete(fileDelete);
				}

				//check the directory again, if empty then delete it
				if(file.list().length==0)
					file.delete();
			}

		}
		else
			//if file, then delete it
			file.delete();
	}
}
