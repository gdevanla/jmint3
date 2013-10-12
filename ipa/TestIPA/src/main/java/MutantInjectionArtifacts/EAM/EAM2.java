package MutantInjectionArtifacts.EAM;


class Base {
    public int getVariable() { return (int)Math.random();}
}



public class EAM2 {
    public EAM2(){
    }

    public int getSomeOtherVariable(){
        return -1;
    }

    public int getVariable() {

        return (int)Math.random();
        //return new Base().getVariable();
    }
}
