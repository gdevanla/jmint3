// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IOP;


class Base
{

    public  int setVariable( int y )
    {
        return (int) Math.random();
    }

}

public class IOP2 extends MutantInjectionArtifacts.IOP.Base
{

    public IOP2()
    {
    }

    public  int getSomeOtherVariable()
    {
        return -1;
    }

    public  int setVariable( int yyy )
    {
        int ann = super.setVariable( 10101 );
        int zzz = (int) Math.random();
        return zzz;
    }

}
