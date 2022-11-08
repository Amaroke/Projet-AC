import java.util.regex.*;
import java.util.*;

public class Parser{
    
    static Vector<Instruction> parse(String s2){
	String s = s2.replaceAll(" ", "");
	String[] instructions = s.split("[\n;]");
	Vector<Instruction> v = new Vector<Instruction>();
	for (int i = 0; i < instructions.length; i++)
	    {
		String[] split = instructions[i].split("=");
		String lhs = split[0];
		String rhs = split[1];
		if (rhs.contains("*") || rhs.contains("+") || rhs.contains("-")){
		    String operator = "";
		    if (rhs.contains("*")) operator = "*";
		    if (rhs.contains("+")) operator = "+";
		    if (rhs.contains("-")) operator = "-";
		    String[] terms = rhs.split(Pattern.quote(operator));
		    Value t0, t1;
		    if (terms[0].matches("[a-z]*"))  t0 = new Variable(terms[0]); else t0 = new Entier(Integer.parseInt(terms[0]));
		    if (terms[1].matches("[a-z]*"))  t1 = new Variable(terms[1]); else t1 = new Entier(Integer.parseInt(terms[1]));
		    Instruction t = new AssignOperator(lhs,operator, t0, t1);
		    v.add(t);
		}
		else{
		    Value t0;
		    if (rhs.matches("[a-z]*"))  t0 = new Variable(rhs); else t0 = new Entier(Integer.parseInt(rhs));
		    Instruction t = new Assign(lhs,t0);
		    v.add(t);
		}
	    }
	return v;
    }


}
