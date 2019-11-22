package eg.edu.alexu.csd.oop.db.cs24;
 
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
 
public class ConditionParser {
 
    private static ConditionParser cp;
 
    Pattern mainPattern=Pattern.compile("");
 
    private ConditionParser() {
 
    }
 
    public static ConditionParser getInstance() {
        if(cp == null) {
            cp = new ConditionParser();
        }
        return cp;
    }
    private boolean isCompartor(Character checked){
        if(checked=='>'||checked=='<'||checked=='='){return true;}
        else return false;
    }
    public ArrayList<String> noregexparser(String input){
        ArrayList<String> output = new ArrayList<String>();
        int i=0,istart;
        while(i<input.length()) {
            while (i<input.length()&&input.charAt(i) == ' ') {
                i++;
            }
            if (i<input.length()&&(Character.isDigit(input.charAt(i)) || Character.isLetter(input.charAt(i))||input.charAt(i) =='\'')) {
                istart = i;
                while (!isCompartor(input.charAt(i)) && input.charAt(i) != ' ') {
                    if(input.charAt(i)=='\''){i++;
                        while(!(input.charAt(i) =='\'')){i++;}
                        i++;
                        output.add(input.substring(istart,i));
                        break;
                    }
                    if (i == input.length() - 1) {
                        output.add(input.substring(istart));
                        i++;
                        break;
                    }
                    i++;
                }
                if(i<input.length()) output.add(input.substring(istart, i));
            } else if(i<input.length()){
                istart = i;
                while (isCompartor(input.charAt(i))) {
                    if (i == input.length() - 1) {
                        output.add(input.substring(istart, i));
                        i++;
                        break;
                    }
                    i++;
 
                }
                if(i<input.length()) output.add(input.substring(istart, i));
            }
 
        }
        if(conditionChecker(output))return output;
        return null;
    }
 
    private boolean smallCondtioncheck(ArrayList<String> a){
        a.set(1,a.get(1).replace(" ",""));
        if(a.get(1).equals("<")||a.get(1).equals(">")||a.get(1).equals("=")||a.get(1).equals("<=")||a.get(1).equals(">=")){
            if(a.get(0)==null||a.get(2)==null){return false;}
            return true;
        }
        return false;
    }
    ArrayList<String> sublist(ArrayList<String> input,int start,int finish){
        ArrayList<String> op = new ArrayList<String>();
        while(start<finish){
            op.add(input.get(start));
            start++;
        }
        return op;
    }
 
    boolean conditionChecker(ArrayList<String> a){
        if(a.size()<3)return false;
        int i=0;
        while(i<a.size()){
            if(!smallCondtioncheck(sublist(a,i,i+3)))return false;
            if(i+3<a.size()&&!a.get(i+3).equals("and")&&!a.get(i+3).equals("or")){return false;}
            i=i+4;
 
        }
        return true;
 
    }

	public boolean evaluate(ArrayList<String> condition, ArrayList<String> reps) {
		
		for (int i = 0; i < reps.size(); i++) {
			condition.set((3 * i) + i, reps.get(i));
		}
		ArrayList<String> bigTest = new ArrayList<String>();
		int size = condition.size();
		for(int i = 0; i < (size + 1) / 4; i++) {
			if(singleCondEvaluation(condition.subList(0, 3))) {
				bigTest.add("true");
			}else {
				bigTest.add("false");
			}
			condition.remove(0); condition.remove(0); condition.remove(0);
			if(i != (((size + 1) / 4) - 1)){
				bigTest.add(condition.get(0));
				condition.remove(0);
			}
		}
		return finalEvaluation(bigTest);
	}
	
	private boolean singleCondEvaluation(List<String> condition) {
		String STRING_REGEX = "[A-Z]+";
		String INT_REGEX = "[0-9]+";
		if(Pattern.matches(STRING_REGEX, condition.get(0)) && Pattern.matches(STRING_REGEX, condition.get(2))) {
			if(!condition.get(0).equals(condition.get(2))) {
				if(condition.get(1).equals("<>")) {
					return true;
				}
				return ((condition.get(0).compareTo(condition.get(2)) < 0) && ((condition.get(1).equals("<")) || condition.get(1).equals("<="))) ||
						((condition.get(0).compareTo(condition.get(2)) > 0) && ((condition.get(1).equals(">")) || condition.get(1).equals(">=")));
				
			}else {
				return condition.get(1).equals("=");
			}
		}
		if(Pattern.matches(INT_REGEX, condition.get(0)) && Pattern.matches(INT_REGEX, condition.get(2))) {
			int num1 = Integer.parseInt(condition.get(0));
			int num2 = Integer.parseInt(condition.get(2));
			if(num1 != num2) {
				if(condition.get(1).equals("<>")) {
					return true;
				}
				return ((num1 > num2) && (condition.get(1).contentEquals(">") || condition.get(1).contentEquals(">="))) ||
						((num1 < num2) && (condition.get(1).contentEquals("<") || condition.get(1).contentEquals("<=")));
			}else {
				return condition.get(1).equals("=");
			}
		}
		return false;
	}
	
	private boolean finalEvaluation(ArrayList<String> bigTest) {
		if(bigTest.size() == 1) {
			return Boolean.parseBoolean(bigTest.get(0));
		}
		String s = evaluateBoolean(bigTest.subList(0, 3));
		bigTest.remove(0); bigTest.remove(0); bigTest.remove(0);
		bigTest.add(0, s);
		return finalEvaluation(bigTest);
	}
	
	private String evaluateBoolean(List<String> next) {
		if(next.get(1).equals("and")) {
			return Boolean.toString(Boolean.logicalAnd(Boolean.parseBoolean(next.get(0)), Boolean.parseBoolean(next.get(2))));
		}else if(next.get(1).equals("or")) {
			return Boolean.toString(Boolean.logicalOr(Boolean.parseBoolean(next.get(0)), Boolean.parseBoolean(next.get(2))));
		}
		return "";
	}

}
