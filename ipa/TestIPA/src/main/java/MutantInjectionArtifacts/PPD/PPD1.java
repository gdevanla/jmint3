package MutantInjectionArtifacts.PPD;

public class PPD1 {

    public void F1(){
        Base t7_03 = new PPD3();
        int x = t7_03.getFromChild(new Child());
        System.out.println(x);
       // System.out.println("Member object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


