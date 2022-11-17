import java.util.ArrayList;
import java.util.Vector;

public class Evaluator {

    final ArrayList<Assign> variables = new ArrayList<>();

    int eval(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                variables.add(assign);
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value;
                int t1Value;
                if (assignOperator.t0 instanceof Variable) {
                    if (variableExist(((Variable) assignOperator.t0).var)) {
                        t0Value = ((Entier) getVariable(((Variable) assignOperator.t0).var).rhs).x;
                    } else {
                        throw new Exception("La variable t0 n'existe pas");
                    }
                } else {
                    t0Value = ((Entier) assignOperator.t0).x;
                }
                if (assignOperator.t1 instanceof Variable) {
                    if (variableExist(((Variable) assignOperator.t1).var)) {
                        t1Value = ((Entier) getVariable(((Variable) assignOperator.t1).var).rhs).x;
                    } else {
                        throw new Exception("La variable t1 n'existe pas");
                    }
                } else {
                    t1Value = ((Entier) assignOperator.t1).x;
                }
                if (!variableExist(assignOperator.lhs)) {
                    switch (assignOperator.op.charAt(0)) {
                        case '+' -> {
                            Assign assign = new Assign(assignOperator.lhs, new Entier(t0Value + t1Value));
                            variables.add(assign);
                        }
                        case '-' -> {
                            Assign assign = new Assign(assignOperator.lhs, new Entier(t0Value - t1Value));
                            variables.add(assign);
                        }
                        case '*' -> {
                            Assign assign = new Assign(assignOperator.lhs, new Entier(t0Value * t1Value));
                            variables.add(assign);
                        }
                        default -> throw new Exception("Erreur dans l'opérateur");
                    }
                } else {
                    switch (assignOperator.op.charAt(0)) {
                        case '+' -> getVariable(assignOperator.lhs).rhs = new Entier(t0Value + t1Value);
                        case '-' -> getVariable(assignOperator.lhs).rhs = new Entier(t0Value - t1Value);
                        case '*' -> getVariable(assignOperator.lhs).rhs = new Entier(t0Value * t1Value);
                        default -> throw new Exception("Erreur dans l'opérateur");
                    }
                }
            }
        }
        return variableExist("x") ? ((Entier) getVariable("x").rhs).x : 0;
    }

    boolean variableExist(String lhs) {
        for (Assign variable : variables) {
            if (lhs.equals(variable.lhs)) {
                return true;
            }
        }
        return false;
    }

    public Assign getVariable(String lhs) throws Exception {
        for (Assign variable : variables) {
            if (lhs.equals(variable.lhs)) {
                return variable;
            }
        }
        throw new Exception("Variable non présente");
    }

}
