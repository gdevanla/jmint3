// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IOD;


public class IOD1
{

    public  void F1()
    {
        MutantInjectionArtifacts.IOD.IOD2 t1_02 = new MutantInjectionArtifacts.IOD.IOD2();
        int zz = t1_02.getVariable();
        System.out.println( zz );
    }

}
