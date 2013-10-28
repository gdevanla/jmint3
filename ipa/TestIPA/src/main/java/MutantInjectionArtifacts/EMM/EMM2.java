// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.EMM;


public class EMM2
{

    int y = 0;

    public EMM2()
    {
    }

    public  int setVariable2( int x )
    {
        y = (int) Math.random();
        return y;
    }

    public  int setVariable( int x )
    {
        y = (int) Math.random();
        return y;
    }

    public  int getVariable( int aaa )
    {
        return (int) Math.random();
    }

}
