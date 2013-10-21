package MutantInjectionArtifacts.IOR;

public class IOR2 extends Base {
    public IOR2(){
    }

    //@Override
    public int getSomeVariable(int x){
        return -1;
    }

    public int getVariable(int x){
        int z = (int)Math.random();
        return z;
    }

}
