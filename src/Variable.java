class Variable extends Value{
    String var;
    Variable(String s){
	var = s;
    }

    @Override
    public String toString(){
	return "Variable " + var;
    }   
}
