// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PNC;


public class PNC3 extends MutantInjectionArtifacts.PNC.Base
{

    public PNC3( int y )
    {
        super( y );
    }

    public  int something( int aaa )
    {
        return aaa + 1;
    }

}
