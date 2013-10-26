package MutantInjectionArtifacts.PRV;

public class PRV1 {

    PRV3 prv6 = new PRV3();

    public void F1(PRV3 prv31, PRV3 prv32, PRV3 prv33){
        /*PRV3 prv31 = new PRV3();
        PRV3 prv32 = new PRV3();
        PRV3 prv33 = new PRV3();*/

        PRV2 prv2 = new PRV2();

        prv33 = prv31;

        prv2.updateObject(prv33);
       // System.out.println("Member object instance t7_03 was tainted by an external call = " + t7_03.x);
    }
}


