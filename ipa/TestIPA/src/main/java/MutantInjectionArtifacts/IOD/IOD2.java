package MutantInjectionArtifacts.IOD;


class Base {
    public int getVariable() { return (int)Math.random();}
}


public class IOD2 extends Base {
    public IOD2(){
    }

    public int getSomeOtherVariable(){
        return -1;
    }

    @Override
    public int getVariable() {

        return (int)Math.random();
        //return new Base().getVariable();
    }

}
