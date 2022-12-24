import java.util.HashMap;
import java.util.Vector;

public class EvaluatorModulo {

    final HashMap<String, Integer> variables = new HashMap<>();

    public int evaluer(Vector<Instruction> instructions, int p) throws Exception {
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
                    case '*' -> variables.put(assignOperator.lhs, multiply(t0Value, t1Value, p));
                    default -> throw new Exception("Erreur dans l'opérateur");
                }
            }
        }
        return variables.getOrDefault("x", 0);
    }

    private int multiply(int x, int y, int p) {
        int result = 0;
        while (y != 0) {
            if ((y & 1) != 0) result = (result + x) % p;
            y >>= 1;
            x = (2 * x) % p;
        }
        return result;
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