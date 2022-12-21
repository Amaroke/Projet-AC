import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class Simplificator {
    final Vector<Instruction> instructions = new Vector<>();
    final HashMap<String, Integer> variables = new HashMap<>();

    Vector<Instruction> simplify(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            // On ajoute toutes les asignations.
            if (i instanceof Assign assign) {
                if (assign.rhs instanceof Variable) {
                    this.instructions.add(i);
                    variables.put(assign.lhs, variables.get(((Variable) assign.rhs).var));
                } else {
                    if (((Entier) assign.rhs).x == 1 || ((Entier) assign.rhs).x == 0) {
                        this.instructions.add(i);
                        variables.put(assign.lhs, ((Entier) assign.rhs).x);
                    } else {
                        this.instructions.add(i);
                        variables.put(assign.lhs, -1);
                    }
                }
            } else { // On ajoute les opérations utiles.
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value = getValueT(assignOperator.t0);
                int t1Value = getValueT(assignOperator.t1);
                boolean t0Equal0 = (t0Value == 0);
                boolean t1Equal0 = (t1Value == 0);
                boolean t0Equal1 = (t0Value == 1);
                boolean t1Equal1 = (t1Value == 1);
                boolean t0Andt1Equal0 = t0Equal0 && t1Equal0;
                boolean t0Ort1Equal0 = !t0Andt1Equal0 && (t0Equal0 || t1Equal0);
                boolean t0Andt1Equal1 = t0Equal1 && t1Equal1;
                boolean t0Ort1Equal1 = !t0Andt1Equal1 && (t0Equal1 || t1Equal1);
                switch (assignOperator.op.charAt(0)) {
                    case '+' -> {
                        if (t0Andt1Equal0) { // X = 0 + 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(0)));
                        } else if (t0Ort1Equal0 && t0Ort1Equal1) { // X = 0 + 1 OR X = 1 + 0
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(1)));
                        } else if (t0Ort1Equal0) { // X = inc + 0 OR X = 0 + inc
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(t1Value)));
                        } else if (t0Andt1Equal1) { // X = 1 + 1
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(new AssignOperator(assignOperator.lhs, "+", new Entier(1), new Entier(1)));
                        } else { // X = inc + inc OR X = inc + 1 OR 1 + inc
                            variables.put(assignOperator.lhs, -1);
                            if (t0Equal1) {
                                this.instructions.add(new AssignOperator(assignOperator.lhs, "+", new Entier(1), new Entier(t1Value)));
                            } else {
                                this.instructions.add(new AssignOperator(assignOperator.lhs, "+", new Entier(t0Value), new Entier(1)));
                            }
                        }
                    }
                    case '-' -> {
                        if (t0Andt1Equal0) { // X = 0 - 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(0)));
                        } else if (t0Equal1 && t1Equal0) { // X = 1 - 0
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(1)));
                        } else if (t0Value == t1Value && t0Value == 1) { // X = 1 - 1
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(0)));
                        } else { // X = 0 - 1 OR X = inc - 1 OR inc - inc OR inc - 0
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(i);
                        }
                    }
                    case '*' -> {
                        if (t0Equal0 || t1Equal0) { // X = 0 * 0 OR X = 1 * 0 OR X = 0 * 1 OR X = inc * 0 OR X = 0 * inc
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(0)));
                        } else if (t0Equal1 && t1Equal1) { // X = 1 * 1
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(new Assign(assignOperator.lhs, new Entier(1)));
                        } else if (t0Equal1 || t1Equal1) { // X = 1 * inc OR X = inc * 1
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(i);
                        } else { // X = inc * 1 OR 1 * inc OR X = inc * inc
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(i);
                        }
                    }
                    default -> throw new Exception("Erreur dans l'opérateur");
                }
            }
        }

        // On supprime toutes les instructions qui sont après la dernière affectation de x.
        int lastXPosition = -1;
        for (int i = 0; i < this.instructions.size(); i++) {
            Instruction instructionI = this.instructions.get(i);
            String instructionVarI = (instructionI instanceof Assign) ? ((Assign) instructionI).lhs : ((AssignOperator) instructionI).lhs;
            if (instructionVarI.equals("x")) {
                lastXPosition = i;
            }
        }
        if (lastXPosition == -1) {
            throw new Exception("Pas d'affectation de x dans le programme");
        }
        while (lastXPosition + 1 != this.instructions.size()) {
            this.instructions.remove(lastXPosition + 1);
        }

        // On supprime ensuite toutes les instructions inutiles (qui ne sont pas utiles à la dernière affectation de x).
        boolean instructionsClear = false;
        while (!instructionsClear) {
            int i;
            int instructionSize = this.instructions.size() - 1;
            for (i = 0; i < instructionSize; i++) {
                boolean instructionIToDelete = true;
                Instruction instructionI = this.instructions.get(i);
                String instructionVarI = (instructionI instanceof Assign) ? ((Assign) instructionI).lhs : ((AssignOperator) instructionI).lhs;
                for (int j = i + 1; j < this.instructions.size(); j++) {
                    Instruction instructionJ = this.instructions.get(j);
                    String t0Var = (instructionJ instanceof AssignOperator) ? (((AssignOperator) instructionJ).t0 instanceof Variable) ? ((Variable) ((AssignOperator) instructionJ).t0).var : "" : (instructionJ instanceof Assign) ? (((Assign) instructionJ).rhs instanceof Variable) ? ((Variable) ((Assign) instructionJ).rhs).var : "" : "";
                    String t1Var = (instructionJ instanceof AssignOperator) ? (((AssignOperator) instructionJ).t1 instanceof Variable) ? ((Variable) ((AssignOperator) instructionJ).t1).var : "" : (instructionJ instanceof Assign) ? (((Assign) instructionJ).rhs instanceof Variable) ? ((Variable) ((Assign) instructionJ).rhs).var : "" : "";
                    if ((Objects.equals(instructionVarI, t0Var)) || (Objects.equals(instructionVarI, t1Var))) {
                        instructionIToDelete = false;
                        break;
                    }
                }
                if (instructionIToDelete) {
                    this.instructions.remove(i);
                    break;
                }
            }
            instructionsClear = (i == instructionSize);
        }
        return this.instructions;
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
