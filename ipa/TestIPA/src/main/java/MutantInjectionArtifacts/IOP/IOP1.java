// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IOP;


public class IOP1
{

    public  void F1()
    {
        MutantInjectionArtifacts.IOP.IOP2 t1_02 = new MutantInjectionArtifacts.IOP.IOP2();
        int zz = t1_02.setVariable( 999991212 );
        System.out.println( zz );
    }

}
