// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PPD;


public class PPD2
{

    public  int getSomeValue()
    {
        return 100;
    }

    public  void updateObject( MutantInjectionArtifacts.PPD.PPD3 t7_03 )
    {
        int y = (int) Math.random();
        t7_03.x = y;
    }

}
