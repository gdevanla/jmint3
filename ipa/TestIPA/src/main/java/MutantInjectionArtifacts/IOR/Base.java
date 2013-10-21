package MutantInjectionArtifacts.IOR;

class Base {

    public int getSomeVariable(){
        int a = (int)Math.random();
        int b = (int)Math.random();
        int ab =  a +b;
        return ab;
    }

    public int getVariable() {
        int z = (int)Math.random();
        int y = getSomeVariable();
        return z + y;

    }
}
