package MutantInjectionArtifacts.IPC;


public class IPC1 {

    public void F1(){
        IPC2 t1_02 = new IPC2((int)Math.random());
        int zz  = t1_02.getSomeInstanceVar();
        System.out.println(zz);

    }


}


