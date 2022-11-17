import java.util.HashMap;
import java.util.Vector;

public class Evaluator {

    final HashMap<String, Integer> variables = new HashMap<>();

    int eval(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                variables.put(assign.lhs, ((Entier) assign.rhs).x);
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value;
                int t1Value;
                if (assignOperator.t0 instanceof Variable) {
                    String t0 = ((Variable) assignOperator.t0).var;
                    if (variables.containsKey(t0)) {
                        t0Value = variables.get(t0);
                    } else {
                        throw new Exception("La variable t0 n'existe pas");
                    }
                } else {
                    t0Value = ((Entier) assignOperator.t0).x;
                }
                if (assignOperator.t1 instanceof Variable) {
                    String t1 = ((Variable) assignOperator.t1).var;
                    if (variables.containsKey(t1)) {
                        t1Value = variables.get(t1);
                    } else {
                        throw new Exception("La variable t1 n'existe pas");
                    }
                } else {
                    t1Value = ((Entier) assignOperator.t1).x;
                }
                switch (assignOperator.op.charAt(0)) {
                    case '+' -> variables.put(assignOperator.lhs, t0Value + t1Value);
                    case '-' -> variables.put(assignOperator.lhs, t0Value - t1Value);
                    case '*' -> variables.put(assignOperator.lhs, t0Value * t1Value);
                    default -> throw new Exception("Erreur dans l'opérateur");
                }
            }
        }
        return variables.getOrDefault("x", 0);
    }
}
