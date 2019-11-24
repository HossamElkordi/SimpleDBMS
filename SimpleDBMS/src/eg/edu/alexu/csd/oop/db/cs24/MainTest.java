package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTest {
//	static Parser a=Parser.getInstace();

	public static void main(String[] args) {
		MyCache cache = MyCache.getInstance();
		String path = System.getProperty("user.dir") + System.getProperty("file.separator") + "Databases" + System.getProperty("file.separator") + "test.xml";
		ArrayList<String> cols = new ArrayList<String>();
		cols.add("id, int");
		cols.add("name, varchar");
		Table table = new Table("group", cols);
		table.setPath(path);
		table.createXML();
		cache.addToCache(table);
		XML p = XML.getInstace();
		p.SaveTable(table, path);
		HashMap<String, String> record = new HashMap<String, String>();
		record.put("id", "24");
		record.put("name", "Hossam");
		table.addRecord(record);
		record.put("id", "10");
		record.put("name", "Ahmes");
		table.addRecord(record);
		record.put("id", "22");
		record.put("name", "Pierre");
		table.addRecord(record);
		record.put("id", "66");
		record.put("name", "Mina");
		table.addRecord(record);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				cache.clearCache();
				table.writeInFile();
			}
			
		}));
//		cache.clearCache();
//		ArrayList<String> condition = new ArrayList<String>();
//		condition.add("id"); condition.add(">"); condition.add("10");
//		Object[][]temp=table.SelectRecord(condition, path);
//		table.deleteRecord(condition);
		
		
//		ConditionParser cp = ConditionParser.getInstance();
//		ArrayList<String> condition = new ArrayList<String>();
//		ArrayList<String> reps = new ArrayList<String>();
//		condition.add("x"); condition.add("="); condition.add("x"); condition.add("or"); condition.add("y"); condition.add(">="); condition.add("7");
//		reps.add("y"); reps.add("7");
//		System.out.println(cp.evaluate(condition, reps));
	}

}
