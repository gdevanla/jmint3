package MutantInjectionArtifacts.JID;


public class JID1 {

    int x = 10;
    String s = "";

    public JID1(){
        x = 100;
        s = "test";
    }

    public JID1(int zz){
        x = zz;
    }

    public void F1(){
        JID2 t1_02 = new JID2();
        int zz  = t1_02.getSomeInstanceVar();
        System.out.println(zz);

    }

    public void F2(){
        JID2 t1_02 = new JID2();
        int zz  = t1_02.getStaticVar();
        System.out.println(zz);
    }

}


