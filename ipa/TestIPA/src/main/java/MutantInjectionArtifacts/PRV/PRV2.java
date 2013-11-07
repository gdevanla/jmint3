// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PRV;


public class PRV2
{

    public PRV2()
    {
    }

    public PRV2( int x )
    {

    }

    public  void updateObject( MutantInjectionArtifacts.PRV.Base t7_03 )
    {
        int y = (int) Math.random();
        t7_03.x = y;

    }

}
