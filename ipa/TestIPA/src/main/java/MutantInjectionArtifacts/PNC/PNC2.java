// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PNC;


public class PNC2
{

    public  int getSomeValue()
    {
        return 100;
    }

    public  void updateObject( MutantInjectionArtifacts.PNC.Base t7_03 )
    {
        int y = (int) Math.random();
        t7_03.x = y;
    }

}
