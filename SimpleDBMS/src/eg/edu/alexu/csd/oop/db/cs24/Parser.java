package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private static Parser parser;
	
	private Parser() {
		
	}
	
	public static Parser getInstace() {
		if(parser == null) {
			parser = new Parser();
		}
		return parser;
	}

	//for typechecker
	//-1 invalid
	//0 create database
	//1 create table
	//2 drop database
	//3 drop table
	//4 select
	//5 update
	//6 delete
	//7 insert

	public int typechecker(String input){
		Pattern pattern = Pattern.compile("[A-Za-z]*");
		String value;
		ArrayList<String> a=new ArrayList<>();
		Matcher matcher=pattern.matcher(input);
		while (matcher.find()) {String j=matcher.group();
			if(!j.equals("")){
				a.add(j);
			}

		}
		if(a.size()<2){return -1;}
		else if(a.get(0).toLowerCase().equals("create")){
			if(a.get(1).toLowerCase().equals("database")){return 0;}
			else if(a.get(1).toLowerCase().equals("table")){return 1;}
		}
		else if(a.get(0).toLowerCase().equals("drop")){
			if(a.get(1).toLowerCase().equals("database")){return 2;}
			else if(a.get(1).toLowerCase().equals("table")){return 3;}
		}
		else if(a.get(0).toLowerCase().equals("select")){return 4;}
		else if(a.get(0).toLowerCase().equals("update")){return 5;}
		else if(a.get(0).toLowerCase().equals("delete")){return 6;}
		else if(a.get(0).toLowerCase().equals("insert")){return 7;}

		return -1;

	}
	private String namegetter(String in){
	    Pattern p=Pattern.compile("[^\\s]*");
	    ArrayList<String> a=new ArrayList<>();
	    Matcher matcher=p.matcher(in);
        while (matcher.find()) {String j=matcher.group();
            if(!j.equals("")){
                a.add(j);
            }

        }
        if(a.size()<2) return null;
        else return a.get(1);

    }
    ArrayList<String > fieldArraygetter(String in){
        in=in.replace(","," , ");
	    Pattern p=Pattern.compile("[^\\s]*");
        ArrayList<String> a=new ArrayList<>();
        Matcher matcher=p.matcher(in);
        while (matcher.find()) {String j=matcher.group();
            if(!j.equals("")){
                a.add(j);
            }

        }
        ArrayList<String> output=new ArrayList<>();
        int i=0;
        while (i<a.size()){
            if(a.get(i).equals("=")){
                output.add(a.get(i-1)+a.get(i)+a.get(i+1));
                i=i+2;
            }
            i++;
        }
        return output;
    }


	public Map<String,Object> updateQueryParser(String input){ConditionParser cp=ConditionParser.getInstance();
        Map<String,Object> output=new HashMap<>();
        int i=0,conditioni,seti,updatei;
        updatei=input.toLowerCase().indexOf("update");
        if(input.toLowerCase().contains("where")){conditioni=input.toLowerCase().indexOf("where");}
        else conditioni=-1;
        if(input.toLowerCase().contains("set")){ seti=input.toLowerCase().indexOf("set");}
        else return null;
        output.put("table",namegetter(input.substring(updatei,seti)));
        ArrayList<String> fieldarray=new ArrayList<>();
        if(conditioni!=-1){fieldarray=fieldArraygetter(input.substring(seti,conditioni));}
        else{fieldarray=fieldArraygetter(input.substring(seti));}
        while(i<fieldarray.size()){
            output.put(fieldarray.get(i).substring(0,fieldarray.get(i).indexOf('=')).replace(" ",""),fieldarray.get(i).substring(fieldarray.get(i).indexOf('=')));
            i++;
        }
        if(conditioni==-1){output.put("condition",null);}
        else{output.put("condition",cp.noregexparser(input.substring(conditioni+5)));}
        return output;



    }
		
}
