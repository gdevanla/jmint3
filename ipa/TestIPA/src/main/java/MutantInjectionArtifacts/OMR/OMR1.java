// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.OMR;


public class OMR1
{

    public  void F1()
    {
        MutantInjectionArtifacts.OMR.OMR2 t1_02 = new MutantInjectionArtifacts.OMR.OMR2();
        int zz = t1_02.getVariable( (int) Math.random(), "adsfa" );
        System.out.println( zz );
    }

}
