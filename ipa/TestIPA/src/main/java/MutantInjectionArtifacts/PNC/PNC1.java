package MutantInjectionArtifacts.PNC;

public class PNC1 {

    public void F1(){
        Base t7_03 = new PNC3();
        PNC2 t1_02 = new PNC2();
        t1_02.updateObject(t7_03);
       // System.out.println("Member object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


