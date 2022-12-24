class AssignOperator extends Instruction {
    // z = x * y
    final String lhs; // z
    final String op; // *
    final Value t0; // x
    final Value t1; // y

    AssignOperator(String s, String ope, Value x, Value y) {
        lhs = s;
        op = ope;
        t0 = x;
        t1 = y;
    }
}
