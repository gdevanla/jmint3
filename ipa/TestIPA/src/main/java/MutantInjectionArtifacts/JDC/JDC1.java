package MutantInjectionArtifacts.JDC;

public class JDC1 {

    public void F1(){
        JDC3 t7_03 = new JDC3();
        JDC2 t1_02 = new JDC2();
        t1_02.updateObject(t7_03);
       // System.out.println("Member object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


