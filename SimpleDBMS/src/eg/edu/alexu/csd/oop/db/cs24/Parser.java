package eg.edu.alexu.csd.oop.db.cs24;

import java.util.ArrayList;
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
		Pattern pattern = Pattern.compile("[A-za-z]*");
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
		
}
