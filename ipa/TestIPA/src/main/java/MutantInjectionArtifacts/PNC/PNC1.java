// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PNC;


public class PNC1
{

    public  void F1()
    {
        MutantInjectionArtifacts.PNC.Base t7_03 = new MutantInjectionArtifacts.PNC.Base( (int) Math.random() );
        MutantInjectionArtifacts.PNC.PNC2 t1_02 = new MutantInjectionArtifacts.PNC.PNC2();
        t1_02.updateObject( t7_03 );
    }

}
