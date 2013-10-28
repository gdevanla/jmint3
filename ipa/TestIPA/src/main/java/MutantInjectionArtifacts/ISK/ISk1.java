// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.ISK;


public class ISK1
{

    public  void F1()
    {
        MutantInjectionArtifacts.ISK.ISK2 t1_02 = new MutantInjectionArtifacts.ISK.ISK2();
        int zz = t1_02.getSomeInstanceVar();
        System.out.println( zz );
    }

}
