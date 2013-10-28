// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JID;


public class JID1
{

    int x = 10;

    java.lang.String s = "";

    public JID1()
    {
        x = 100;
        s = "test";
    }

    public JID1( int zz )
    {
        x = zz;
    }

    public  void F1()
    {
        MutantInjectionArtifacts.JID.JID2 t1_02 = new MutantInjectionArtifacts.JID.JID2();
        int zz = t1_02.getSomeInstanceVar();
        System.out.println( zz );
    }

    public  void F2()
    {
        MutantInjectionArtifacts.JID.JID2 t1_02 = new MutantInjectionArtifacts.JID.JID2();
        int zz = t1_02.getStaticVar();
        System.out.println( zz );
    }

}
