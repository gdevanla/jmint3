package MutantInjectionArtifacts.EMM;

public class EMM2 {
    int y = 0;
    public EMM2(){
    }


    public int setVariable2(int x){
        y = (int)Math.random();
        return y;
    }

    public int setVariable(int x) {
        y = (int)Math.random();
        return y;
        //return new Base().getVariable();
    }

    public int getVariable(){
        return (int)Math.random();
    }



}
