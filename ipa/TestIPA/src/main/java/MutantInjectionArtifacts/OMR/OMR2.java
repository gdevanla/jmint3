package MutantInjectionArtifacts.OMR;

public class OMR2 {

    public String getVariable(String s){
        //return (int)Math.random();
        return "";
    }

    public int getVariable(String s, int x1){
        return (int)Math.random() + x1;
    }

    public int getVariable(int x1, String s) {
        return (int)Math.random();
    }

}
