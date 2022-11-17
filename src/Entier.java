class Entier extends Value {
    final Integer x;

    Entier(Integer s) {
        x = s;
    }

    @Override
    public String toString() {
        return "Entier " + x.toString();
    }
}
