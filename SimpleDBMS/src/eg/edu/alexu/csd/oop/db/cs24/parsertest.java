package eg.edu.alexu.csd.oop.db.cs24;

import java.util.HashMap;
import java.util.Map;

public class parsertest {
    static Parser a=Parser.getInstace();
    public static void main(String[] args) {
        Map<String,Object> z=new HashMap<>();
        z=a.updateQueryParser("UPDATE table_name8 SET column_name1='11111111', COLUMN_NAME2=22222222, column_name3='333333333' WHERE coLUmn_NAME3='VALUE3'");
        System.out.println("dady");
    }
}
