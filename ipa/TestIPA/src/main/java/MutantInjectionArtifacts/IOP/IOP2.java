package MutantInjectionArtifacts.IOP;


class Base {
    public int getVariable() { return (int)Math.random();}
}


public class IOP2 extends Base {
    public IOP2(){
    }

    public int getSomeOtherVariable(){
        return -1;
    }

    public int getVariable() {
        int anothermember1 = super.getVariable();
        int  zzz = (int)Math.random();
        return  zzz + anothermember1;
        //return new Base().getVariable();
    }


}
