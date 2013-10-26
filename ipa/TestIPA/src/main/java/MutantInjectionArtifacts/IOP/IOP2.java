package MutantInjectionArtifacts.IOP;


class Base {
    public int getVariable(int y) { return (int)Math.random();}
}


public class IOP2 extends Base {
    public IOP2(){
    }

    public int getSomeOtherVariable(){
        return -1;
    }

    public int getVariable(int yyy) {
        int ann = super.getVariable(10101);
        int  zzz = (int)Math.random();
        return  zzz;
        //return new Base().getVariable();
    }


}
