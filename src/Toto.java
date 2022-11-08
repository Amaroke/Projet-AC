import java.util.*;

import java.io.FileInputStream;
import java.io.File;




public class Toto{

    public static String FileToString(String filename){
	try{
	    
	File file = new File(filename);
	FileInputStream f = new FileInputStream(file);
	byte[] data = new byte[(int) file.length()];
	f.read(data);
	f.close();
	
	return new String(data, "UTF-8");
	}
	 catch(Exception ex)
	     {
		 ex.printStackTrace();
		 return null;
	     }       
    }

    public static void main(String[] args){
	// String s = "x=1;y=x*2;z=x-y;x=y+z";
	Vector<Instruction> x = Parser.parse(FileToString("src/prog1"));
	for(Instruction i: x){
	    if (i instanceof Assign){
		Assign a = (Assign) i;
		System.out.println("La variable " + a.lhs + " reçoit la valeur " + a.rhs);
	    }
	    else{		
		AssignOperator a = (AssignOperator) i;
		System.out.println("On effectue l'opération " + a.op + " aux valeurs " + a.t0  + " et " + a.t1 + " et on stocke le résultat dans " + a.lhs);
	    }

	    
	}
    }

}
