// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PMD;


public class PMD2
{

    public  int getSomeValue()
    {
        return 100;
    }

    public  void updateObject( Base t7_03 )
    {
        int y = (int) Math.random();
        t7_03.x = y;
    }

}
