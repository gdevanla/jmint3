// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.OMD;


public class OMD1
{

    public  void F1()
    {
        MutantInjectionArtifacts.OMD.OMD2 t1_02 = new MutantInjectionArtifacts.OMD.OMD2();
        int zz = t1_02.getVariable( new MutantInjectionArtifacts.OMD.A() );
        System.out.println( zz );
    }

}
