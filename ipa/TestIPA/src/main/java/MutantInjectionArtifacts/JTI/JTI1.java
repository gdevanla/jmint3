// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JTI;


public class JTI1
{

    int x = 10;

    private int y = 100;

    public  void F1( int x )
    {
        int y = ((int)Math.random())*3;
        //this.y = ((int)Math.random())*3;
        int z = x + y;
        MutantInjectionArtifacts.JTI.JTI2 t1_02 = new MutantInjectionArtifacts.JTI.JTI2();
        int xxx = t1_02.getLocalVariable( z, y );
        t1_02.useLocalVariable( x, y );

    }

}
