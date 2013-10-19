package MutantInjectionArtifacts.IOP;

public class IOP1 {

    public void F1(){
        IOP2 t1_02 = new IOP2();
        int zz = t1_02.getVariable();
        System.out.println(zz);
    }
}


