import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
        Vector<Instruction> instructions;
        if (Objects.equals(args[0], "evaluation")) {
            instructions = printParse(args[1]);
            int x = new Evaluator().eval(instructions);
            System.out.println("\nLe résultat du programme est : x = " + x);
        } else if (Objects.equals(args[0], "simplification")) {
            instructions = printParse(args[1]);
            Vector<Instruction> instructionsSimplify = new Simplificator().simplify(instructions);
            System.out.println("\nVoici le programme simplifié :");
            for (Instruction i : instructionsSimplify) {
                if (i instanceof Assign assign) {
                    System.out.println("La variable " + assign.lhs + " reçoit la valeur " + assign.rhs);
                } else {
                    AssignOperator assignOperator = (AssignOperator) i;
                    System.out.println("On effectue l'opération " + assignOperator.op + " aux valeurs " + assignOperator.t0 + " et " + assignOperator.t1 + " et on stocke le résultat dans " + assignOperator.lhs);
                }
            }
            int x = new Evaluator().eval(instructionsSimplify);
            System.out.println("\nEt le résultat du programme est : x = " + x);
        } else if (Objects.equals(args[0], "evaluationModulo")) {
            instructions = printParse(args[1]);
            int x = new EvaluatorModulo().evaluer(instructions, Integer.parseInt(args[2]));
            System.out.println("\nLe résultat du programme est : x = " + x);
        } else if (Objects.equals(args[0], "comparer")) {
            Vector<Instruction> instructions1 = printParse(args[1]);
            Vector<Instruction> instructions2 = printParse(args[2]);
            Comparator comparator = new Comparator();
            boolean x = comparator.comparer(instructions1, instructions2);
            System.out.println("\nLe résultat du programme est : " + (x ? "VRAI" : "FAUX"));
        } else {
            System.out.println("\nLes paramètres fournis ne sont pas les bons.\n");
        }
    }

    private static Vector<Instruction> printParse(String arg) {
        Vector<Instruction> instructions = Parser.parse(FileToString(arg));
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                System.out.println("La variable " + assign.lhs + " reçoit la valeur " + assign.rhs);
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                System.out.println("On effectue l'opération " + assignOperator.op + " aux valeurs " + assignOperator.t0 + " et " + assignOperator.t1 + " et on stocke le résultat dans " + assignOperator.lhs);
            }
        }
        return instructions;
    }

}
