class Variable extends Value {
    final String var;

    Variable(String s) {
        var = s;
    }

    @Override
    public String toString() {
        return "Variable " + var;
    }
}
