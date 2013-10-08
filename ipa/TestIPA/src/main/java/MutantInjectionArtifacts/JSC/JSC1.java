package MutantInjectionArtifacts.JSC;


public class JSC1 {

    int x = 10;

    public void F1(){
        JSC2 t1_02 = new JSC2();
        int zz  = t1_02.getSomeInstanceVar();
        System.out.println(zz);

    }

    public void F2(){
        JSC2 t1_02 = new JSC2();
        int zz  = t1_02.getStaticVar();
        System.out.println(zz);

    }


}


