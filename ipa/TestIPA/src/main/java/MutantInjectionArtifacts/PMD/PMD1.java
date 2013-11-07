// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PMD;


public class PMD1
{

    public  void F1()
    {
        PMD3 t7_03 = new PMD3();
        PMD2 t1_02 = new PMD2();
        t1_02.updateObject( t7_03 );
    }

}
