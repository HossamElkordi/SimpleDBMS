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
        if(a.size()!=2) return null;
        else return a.get(1);

    }
    private ArrayList<String > fieldArraygetter(String in){
        in=in.replace(","," , ");
        in=in.replace("="," = ");
	    Pattern p=Pattern.compile("(['][^']*[']|[^'^\\s])*");
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
            if(a.get(i).equals("=")){if(i==0||i==a.size()-1){return null;}
                output.add(a.get(i-1)+a.get(i)+a.get(i+1));
                i=i+2;
            }
            i++;
        }
        return output;
    }
    private int listcount(String in,Character a){
	    int count=0,i=0;
	    while(i<in.length()){
	        if(in.charAt(i)==a)count++;
	        i++;
        }
	    return count;
    }

    private ArrayList<String > selectquery(String in){
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
        int i=1;
        while(i<a.size()){
            if(a.get(1).equals("*")){
                output.add("*");
                if(a.size()>2){return null;}
                else return output;
            }
            else{
                if(!a.get(i).equals(",")) {
                    output.add(a.get(i));
                }
                i++;
            }
        }
        return output;
    }



	public Map<String,Object> updateQueryParser(String input){
	    Pattern a=Pattern.compile(
	            "((update)|(UPDATE))[\\s]+[\\S]+[\\s]+((set)|(SET))[\\s]+[\\S]+[\\s]*[=][\\s]*(['][^']*[']|[^'])" +
                        "([\\s]*([,][\\s]+[\\S]+[\\s]*[=][\\s]*(['][^']*[']|[^']))*)[\\s]*(((WHERE)|(where))[^;]+)?"
        );
	    Matcher z=a.matcher(input.toLowerCase());
	    if(!z.matches())return null;
	    input=input.substring(0,input.length()-1);
	    ConditionParser cp=ConditionParser.getInstance();
        Map<String,Object> output=new HashMap<>();
        int i=0,conditioni,seti,updatei;
        updatei=input.toLowerCase().indexOf("update");
        if(input.toLowerCase().contains("where")){conditioni=input.toLowerCase().indexOf("where");}
        else conditioni=-1;
         seti=input.toLowerCase().indexOf("set");
        output.put("table",namegetter(input.substring(updatei,seti)));
        ArrayList<String> fieldarray=new ArrayList<>();
        if(conditioni!=-1){fieldarray=fieldArraygetter(input.substring(seti,conditioni));}
        else{fieldarray=fieldArraygetter(input.substring(seti));}
        while(i<fieldarray.size()){if(fieldarray==null)return null;
            output.put(fieldarray.get(i).substring(0,fieldarray.get(i).indexOf('=')).replace(" ",""),fieldarray.get(i).substring(fieldarray.get(i).indexOf('=')+1));
            i++;
        }
        if(conditioni==-1){output.put("condition",null);}
        else{if(cp.noregexparser(input.substring(conditioni+5).replace(";",""))==null){return null;}
            output.put("condition",cp.noregexparser(input.substring(conditioni+5).replace(";","")));}
        return output;



    }
    public Map<String,Object> selectQueryParser(String input){ConditionParser cp=ConditionParser.getInstance();
        Pattern a=Pattern.compile(
                "((select)|(SELECT))[\\s]+([*]|([^,\\s]+[\\s]*([,][\\s]*[^,\\s]+)*))[\\s]+((FROM)|(from))[\\s]+[\\S]+([\\s]+((WHERE)|(where))[^;]+)*"
        );
        Matcher z=a.matcher(input.toLowerCase());
        if(!z.matches())return null;
       input=input.substring(0,input.length()-1);
        Map<String,Object> output=new HashMap<>();
        int selecti,fromi,conditioni;
        selecti=input.toLowerCase().indexOf("select");

        fromi=input.toLowerCase().indexOf("from");
        if(!input.toLowerCase().contains("where")){conditioni=-1;}
        else{conditioni=input.toLowerCase().indexOf("where");}
        if(selectquery(input.substring(selecti,fromi))==null)return null;
        output.put("fields",selectquery(input.substring(selecti,fromi)));
        String tablename;
        if(conditioni==-1){
            tablename=namegetter(input.substring(fromi));
        }
        else{tablename=namegetter(input.substring(fromi,conditioni));}
        if(tablename==null)return null;
        else{output.put("table",tablename);}
        if(conditioni==-1){output.put("condition",null);}
        else{if(cp.noregexparser(input.substring(conditioni+5).replace(";",""))==null){return null;}
            output.put("condition",cp.noregexparser(input.substring(conditioni+5).replace(";","")));}
        return output;
    }

   public Map<String,Object> insertQueryParser(String input){
        input=input.replace(")"," ) ");
        input= input.replace("("," ( ");
        input=input.replace(","," , ");
        Pattern a=Pattern.compile(
                "(insert)[\\s]+(into)[\\s]+[\\S]+[\\s]+(([\\s]*[\\(][\\s]*[^\\s,\\(\\)]+([\\s]*[,][\\s]*[^,\\s\\(\\)]+)*" +
                        "[\\s]*[\\)][\\s]*(values)[\\s]*+[\\s]*[\\(][\\s]*(([^\\s,\\(\\)]+)|(['][^']+[']))([\\s]*[,][\\s]*" +
                        "(([^,\\s]+)|(['][^']+['])))*[\\s]*[\\)][\\s]*)|([\\s]*(values)[\\s]*+[\\s]*[\\(][\\s]*(([^\\s,\\(\\)]+)" +
                        "|(['][^']+[']))([\\s]*[,][\\s]*(([^,\\s\\(\\)]+)|(['][^']+['])))*[\\s]*[\\)][\\s]*))"
        );
        Matcher z=a.matcher(input.toLowerCase());
        if(!z.matches())return null;
        else{input=input.substring(0,input.length()-1);}
        input=input.replace(")"," ) ");
        input=input.replace("("," ( ");
        input=input.replace(","," , ");
        Map<String,Object> output=new HashMap<>();
        ArrayList<String> var=new ArrayList<>(),fields=new ArrayList<>();

        if(listcount(input,'(')==1){
                int i=input.indexOf("(");String temp=new String();
                while(i<input.indexOf(")")){
                    if(i==input.indexOf(")")-1){temp=temp+input.charAt(i);var.add(temp);}
                    else if(input.charAt(i)!=' '&&input.charAt(i)!=','){
                        temp=temp+input.charAt(i);
                        if(input.charAt(i)=='\''){i++;
                            while(input.charAt(i)!='\''){
                                temp=temp+input.charAt(i);i++;
                            }
                            temp=temp+'\'';i++;var.add(temp);temp="";
                        }
                    }
                    else if(input.charAt(i)==','){
                        var.add(temp);
                        temp= "";
                    }
                    i++;
                }
            for (int j=0;j<var.size();j++){
                output.put(Integer.toString(j),var.get(j));
            }

        }

        else if(listcount(input,'(')==2){String temp=new String();
            int i=input.lastIndexOf('(')+1;
            while(i<input.lastIndexOf(")")){
                if(i==input.lastIndexOf(")")-1){if(input.charAt(i)!=' ')temp=temp+input.charAt(i);var.add(temp);}
                else if(input.charAt(i)!=' '&&input.charAt(i)!=','){
                    temp=temp+input.charAt(i);
                    if(input.charAt(i)=='\''){i++;
                        while(input.charAt(i)!='\''){
                            temp=temp+input.charAt(i);i++;
                        }
                        temp=temp+'\'';i++;var.add(temp);temp="";
                    }
                }
                else if(input.charAt(i)==','){
                    if(!temp.equals(""))
                    var.add(temp);
                    temp= "";
                }
                i++;
            }
            i=input.indexOf("(")+1;
            temp="";
            while(i<input.indexOf(")")){
                if(i==input.indexOf(")")-1){if(input.charAt(i)!=' ')temp=temp+input.charAt(i);fields.add(temp);}
                else if(input.charAt(i)!=' '&&input.charAt(i)!=','){
                    temp=temp+input.charAt(i);

                }
                else if(input.charAt(i)==','){
                    if(!temp.equals(""))fields.add(temp);
                    temp= "";
                }
                i++;
            }
            if(var.size()!=fields.size()){return null;}
            else {
                for (int j=0;j<var.size();j++){
                    output.put(fields.get(j),var.get(j));
                }
            }
        }
       int i= input.toLowerCase().indexOf("into")+4;
        String temp="";
        while(input.charAt(i)==' '){i++;}
        while(!(input.charAt(i)==' ')){temp=temp+input.charAt(i);i++;}
        output.put("table",temp);
        output.put("condition",null);
        return output;


   }

   public Map<String,Object> deleteQueryParser(String input){ConditionParser cp=ConditionParser.getInstance();
       Pattern a=Pattern.compile("(delete)[\\s]+(from)[\\s]+[^\\s]+[\\s]*([\\s]+(where)[\\s]+[^;]+)?[\\s]*");
	   Map<String ,Object> output=new HashMap<>();
       Matcher z=a.matcher(input.toLowerCase());
       if(!z.matches())return null;
       input.replace(";","");
       int wherei,fromi;
       if(!(input.toLowerCase().contains("where"))){output.put("condition",null);wherei=-1;}
       else{wherei=input.toLowerCase().indexOf("where");
           output.put("condition",cp.noregexparser(input.substring(wherei+5).replace(";","")));
       }
       fromi=input.toLowerCase().indexOf("from");
       if(wherei==-1){
           output.put("table",input.substring(fromi+4).replace(" ","").replace(";",""));
       }
       else{output.put("table",input.substring(fromi+4,wherei).replace(" ",""));}
       return output;
   }

    public Map<String,Object> createdatabase(String input){
        input=input.trim();
        Map<String,Object> output=new HashMap<>();
        Pattern pattern = Pattern.compile("(create)[\\s]+(database)[\\s]+[\\w]+[\\s]*");
        Matcher matcher=pattern.matcher(input.toLowerCase());
        if(!matcher.matches())
            return null;
        input=input.substring(0,input.length()-1);
        input.trim();
        input=input.substring(6);
        input=input.trim();
        input=input.substring(8);
        input=input.trim();
        output.put("DataBaseName",input);
        return output;
    }
    public Map<String,Object> dropdatabase(String input){
        input=input.trim();
        Map<String,Object> output=new HashMap<>();
        Pattern pattern = Pattern.compile("(drop)[\\s]+(database)[\\s]+[\\w]+[\\s]*");
        Matcher matcher=pattern.matcher(input.toLowerCase());
        if(!matcher.matches())
            return null;
        input=input.substring(0,input.length()-1);
        input.trim();
        input=input.substring(4);
        input=input.trim();
        input=input.substring(8);
        input=input.trim();
        output.put("DataBaseName",input);
        return output;
    }
    public Map<String,Object> createtable(String input){
        input=input.trim();
        /*if(input.charAt(input.length()-1)!=';'){
            return null;
        }else{
            input=input.substring(0,input.length()-1);
        }*/
        int i=input.indexOf("(");
        String check=input.substring(0,i-1);
        check=check.trim();
        Map<String,Object> output=new HashMap<>();
        Pattern pattern = Pattern.compile("(create)[\\s]+(table)[\\s]+[\\w]+");
        Matcher matcher=pattern.matcher(check.toLowerCase());
        if(!matcher.matches())
            return null;
        check=check.substring(6);
        check=check.trim();
        check=check.substring(5);
        check=check.trim();
        output.put("tableName",check);
        String check2=input.substring(i+1,input.lastIndexOf(")")-1);
        check2=check2.trim();
        String[] arrOfStr = check2.split(",");
        int counter=0;
        for(int j=0;j<check2.length();j++)
        {
            if(check2.charAt(j)==',')
                counter++;
        }
        if(counter!=arrOfStr.length-1)
            return null;
        for(int j=0;j<arrOfStr.length;++j)
        {
            arrOfStr[j]=arrOfStr[j].trim();
            Pattern pattern1=Pattern.compile("[\\w]+[\\s]+(int|varchar)");
            Matcher matcher1=pattern1.matcher(arrOfStr[j].toLowerCase());
            if(!matcher.matches())
                return null;
            String Name=arrOfStr[j].substring(0,arrOfStr[j].indexOf(" ")-1);
            if(arrOfStr[j].toLowerCase().contains("int")){
                output.put(Name,"int");
            }else if(arrOfStr[j].toLowerCase().contains("varchar")){
                output.put(Name,"varchar");
            }
        }
        return output;
    }
    public Map<String,Object> droptable(String input){
        input=input.trim();
        Map<String,Object> output=new HashMap<>();
        Pattern pattern = Pattern.compile("(drop)[\\s]+(table)[\\s]+[\\w]+[\\s]*");
        Matcher matcher=pattern.matcher(input.toLowerCase());
        if(!matcher.matches())
            return null;
        input=input.substring(0,input.length()-1);
        input.trim();
        input=input.substring(4);
        input=input.trim();
        input=input.substring(5);
        input=input.trim();
        output.put("tableName",input);
        return output;
    }
		
}
