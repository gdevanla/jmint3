// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.OMR;


public class OMR1
{

    public  void F1()
    {
        MutantInjectionArtifacts.OMR.OMR2 t1_02 = new MutantInjectionArtifacts.OMR.OMR2();
        int xx = (int) Math.random();
        int zz = t1_02.getVariable( xx, "adsfa" );
        int xy = zz * xx;
        System.out.println( zz );
    }

}
