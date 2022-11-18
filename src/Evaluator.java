import java.util.HashMap;
import java.util.Vector;

public class Evaluator {

    final HashMap<String, Integer> variables = new HashMap<>();

    int eval(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                if (assign.rhs instanceof Variable) {
                    variables.put(assign.lhs, variables.get(((Variable) assign.rhs).var));
                } else {
                    variables.put(assign.lhs, ((Entier) assign.rhs).x);
                }
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value = getValueT(assignOperator.t0);
                int t1Value = getValueT(assignOperator.t1);
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

    public int getValueT(Value t) throws Exception {
        if (t instanceof Variable) {
            String stringT = ((Variable) t).var;
            if (variables.containsKey(stringT)) {
                return variables.get(stringT);
            } else {
                throw new Exception("La variable " + stringT + " n'existe pas");
            }
        } else {
            return ((Entier) t).x;
        }
    }
}
