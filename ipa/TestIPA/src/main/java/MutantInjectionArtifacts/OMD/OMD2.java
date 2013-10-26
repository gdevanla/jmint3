package MutantInjectionArtifacts.OMD;

class A{

}

class B extends A{

}

public class OMD2 {

    public int getVariable(A a){
        return (int)Math.random() + 1;
    }

    public int getVariable(B b) {

        return (int)Math.random();
        //return new Base().getVariable();
    }

    public int getVariable1(int a){
        return (int)Math.random() + 1;
    }

    public int getVariable1(float b) {

        return (int)Math.random();
        //return new Base().getVariable();
    }

    public int getVariable1(double b) {

        return (int)Math.random();
        //return new Base().getVariable();
    }





}
