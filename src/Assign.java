class Assign extends Instruction {
    // x = 10
    final String lhs; // x
    final Value rhs; // 10

    Assign(String s, Value x) {
        lhs = s;
        rhs = x;
    }
}
