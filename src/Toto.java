import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Vector;


public class Toto {

    public static String FileToString(String filename) {
        try {

            File file = new File(filename);
            FileInputStream f = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            f.read(data);
            f.close();

            return new String(data, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        boolean printOn = true;
        Vector<Instruction> instructions = Parser.parse(FileToString("src/prog1"));
        instructions = new Simplificator().simplify(instructions);
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                if (printOn) {
                    System.out.println("La variable " + assign.lhs + " reçoit la valeur " + assign.rhs);
                }
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                if (printOn) {
                    System.out.println("On effectue l'opération " + assignOperator.op + " aux valeurs " + assignOperator.t0 + " et " + assignOperator.t1 + " et on stocke le résultat dans " + assignOperator.lhs);
                }
            }
        }
       int x = new Evaluator().eval(instructions);
       System.out.println("\nLe résultat du programme est : x = " + x);
    }

}
