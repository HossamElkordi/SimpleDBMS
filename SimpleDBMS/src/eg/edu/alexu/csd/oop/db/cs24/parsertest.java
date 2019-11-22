package eg.edu.alexu.csd.oop.db.cs24;

import java.util.HashMap;
import java.util.Map;

public class parsertest {
    static Parser a=Parser.getInstace();
    public static void main(String[] args) {
        Map<String,Object> z=new HashMap<>();
        z=a.deleteQueryParser("delete from Customers where x<5 ;");
        System.out.println("dady");
    }
}
