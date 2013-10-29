// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PPD;


import soot.jimple.GroupIntPair;


class Base
{

    public int x;

    public  int getFromChild( MutantInjectionArtifacts.PPD.Child child )
    {
        return (int) Math.random();
    }

}

public class PPD3 extends MutantInjectionArtifacts.PPD.Base
{

    public PPD3()
    {
    }

    public PPD3( int y )
    {
        x = y;
    }

}
