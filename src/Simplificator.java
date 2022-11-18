import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class Simplificator {

    Vector<Instruction> instructions = new Vector<Instruction>();
    final HashMap<String, Integer> variables = new HashMap<>();

    Vector<Instruction> simplify(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                if(((Entier) assign.rhs).x == 1  || ((Entier) assign.rhs).x == 0){
                    this.instructions.add(i);
                    variables.put(assign.lhs, ((Entier) assign.rhs).x);
                } else {
                    this.instructions.add(i);
                    variables.put(assign.lhs, -1);
                }
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value;
                int t1Value;
                if (assignOperator.t0 instanceof Variable) {
                    String t0 = ((Variable) assignOperator.t0).var;
                    if (variables.containsKey(t0)) {
                        t0Value = this.variables.get(t0);
                    } else {
                        System.out.println(t0);
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
                boolean x_inc_op_1_1_op_inc = ((t0Value != 0 && t0Value != 1) && t1Value == 1) || ((t1Value != 0 && t1Value != 1) && t0Value == 1);
                System.out.println("test:" + assignOperator.op.charAt(0));
                switch (assignOperator.op.charAt(0)) {
                    case '+': {
                        if (t0Value == t1Value && t0Value == 0) { // X = 0 + 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(i);
                        } else if ((t0Value == 0 && t1Value == 1) || (t0Value == 1 && t1Value == 0)) { // X = 0 + 1 OU X = 1 + 0
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(i);
                        } else if (x_inc_op_1_1_op_inc) { // X = inc + 1 OU 1 + inc
                            variables.put(assignOperator.lhs, -1);
                            System.out.println("3");
                        } else if (t0Value != 0 && t0Value != 1 && t1Value != 0) { // X = inc + inc
                            variables.put(assignOperator.lhs, -1);
                            System.out.println("4");
                        } else if (t0Value != 0 && t0Value != 1 || t1Value != 1) { // X = inc + 0 OU 0 + inc
                            variables.put(assignOperator.lhs, -1);
                            System.out.println("5");
                        }
                        break;
                    }
                    case '-': {
                        if(t0Value == t1Value && t0Value == 0){ // X = 0 - 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(i);
                            System.out.println("6");
                        } else if(t0Value == t1Value && t0Value == 1){ // X = 1 - 1
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(i);
                            System.out.println("7");
                        } else if((t0Value == 0 && t1Value == 1)|| (t0Value == 1 && t1Value == 0)) { // X = 0 - 1 OU X = 1 - 0
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(i);
                            System.out.println("8");
                        } else if(x_inc_op_1_1_op_inc) { // X = inc - 1
                            variables.put(assignOperator.lhs, -1);
                        } else if(t0Value != 0 && t0Value != 1 && t1Value != 0) { // X = inc - inc
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(i);
                            System.out.println("9");
                        } else if (t0Value != 0 || t1Value != 1) { // X = inc - 0 OU 0 - inc
                            variables.put(assignOperator.lhs, -1);
                            System.out.println("10");
                        }
                        break;
                    }
                    case '*': {
                        if(t0Value == t1Value && t0Value == 0){ // X = 0 * 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(i);
                            System.out.println("11");
                        } else if((t0Value == 0 && t1Value == 1)|| (t0Value == 1 && t1Value == 0)) { // X = 0 * 1 OU X = 1 * 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(i);
                            System.out.println("12");
                        } else if(x_inc_op_1_1_op_inc) { // X = inc * 1 OU 1 * inc
                            variables.put(assignOperator.lhs, -1);
                            System.out.println("13");
                        } else if(t0Value != 0 && t0Value != 1 && t1Value != 0) { // X = inc * inc
                            variables.put(assignOperator.lhs, -1);
                            System.out.println("14");
                        } else if ((t0Value != 0 && t0Value != 1 && t1Value == 0) || (t1Value != 0 && t1Value != 1 && t0Value == 0)) { // X = inc * 0 OU 0 * inc
                            System.out.println("15");
                            if(Objects.equals(assignOperator.lhs, "x")){
                                this.instructions.clear();
                                variables.put(assignOperator.lhs, 0);
                                this.instructions.add(new Assign("x", new Entier(0)));
                            } else {
                                variables.put(assignOperator.lhs, 0);
                                this.instructions.add(i);
                            }
                        }
                        break;
                    }
                    default : throw new Exception("Erreur dans l'opérateur");
                }
            }
        }
        return this.instructions;
    }

}
