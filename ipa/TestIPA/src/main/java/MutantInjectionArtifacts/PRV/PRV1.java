package MutantInjectionArtifacts.PRV;

public class PRV1 {

    public void F1(){
        PRV3 prv3 = new PRV3();
        PRV4 prv4 = new PRV4();

        PRV2 prv2 = new PRV2();

        prv2.updateObject(prv3);
       // System.out.println("Member object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


