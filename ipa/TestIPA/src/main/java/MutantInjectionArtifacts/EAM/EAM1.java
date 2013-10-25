// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.EAM;


public class EAM1
{

    public  void F1()
    {
        MutantInjectionArtifacts.EAM.EAM2 t1_02 = new MutantInjectionArtifacts.EAM.EAM2();
        int zz = t1_02.getVariable();
        System.out.println( zz );
    }

}
