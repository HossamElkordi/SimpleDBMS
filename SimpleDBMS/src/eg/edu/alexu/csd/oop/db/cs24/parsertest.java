package eg.edu.alexu.csd.oop.db.cs24;

import java.util.HashMap;
import java.util.Map;

public class parsertest {
    static Parser a=Parser.getInstace();
    public static void main(String[] args) {
        Map<String,Object> z=new HashMap<>();
        z=a.insertQueryParser("INSERT INTO table_name3 VALUES ('value1', 3,'value3')");
        System.out.println("dady");
    }
}
