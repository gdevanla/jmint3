// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PNC;


class Base
{

    public int x;

}

public class PNC3 extends MutantInjectionArtifacts.PNC.Base
{

    public PNC3()
    {
    }

    public PNC3( int y )
    {
        x = y;
    }

}
