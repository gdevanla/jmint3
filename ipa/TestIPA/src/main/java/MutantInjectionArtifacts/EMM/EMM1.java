// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.EMM;


public class EMM1
{

    public  void F1()
    {
        MutantInjectionArtifacts.EMM.EMM2 t1_02 = new MutantInjectionArtifacts.EMM.EMM2();
        t1_02.setVariable( 10121210 );
        int zz = t1_02.getVariable( 12121 );
        System.out.println( zz );
    }

}
