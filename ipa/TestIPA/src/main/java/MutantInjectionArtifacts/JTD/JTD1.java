// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JTD;


public class JTD1
{

    int x = (int) Math.random();

    private int y = 100;

    public  void F1( int x )
    {
        x = (int) Math.random();
        MutantInjectionArtifacts.JTD.JTD2 t1_02 = new MutantInjectionArtifacts.JTD.JTD2();
        t1_02.useLocalVariable( this.x );
    }

}
