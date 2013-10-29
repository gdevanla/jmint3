// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JTI;


public class JTI1
{

    int x = 10;

    private int y = 100;

    public  void F1( int x )
    {
        int z = x;
        MutantInjectionArtifacts.JTI.JTI2 t1_02 = new MutantInjectionArtifacts.JTI.JTI2();
        t1_02.useLocalVariable( z );
    }

}
