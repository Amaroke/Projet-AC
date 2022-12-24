import java.util.Vector;

public class Comparator {

    boolean comparer(Vector<Instruction> instructions1, Vector<Instruction> instructions2) throws Exception {
        EvaluatorModulo evaluator = new EvaluatorModulo();
        int p = RandomPrime.randomPrime();
        return evaluator.evaluer(instructions2, p) == evaluator.evaluer(instructions1, p);
    }

}
