package MutantInjectionArtifacts.OMD;

public class OMD2 {

    public int getVariable(int x){
        return (int)Math.random() + x;
    }

    public int getVariable() {

        return (int)Math.random();
        //return new Base().getVariable();
    }

}
