// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IOD;


class Base
{

    public  int getVariable()
    {
        return (int) Math.random();
    }

}

public class IOD2 extends MutantInjectionArtifacts.IOD.Base
{

    public IOD2()
    {
    }

    public  int getSomeOtherVariable()
    {
        return -1;
    }

    public  int getVariable()
    {
        return (int) Math.random();
    }

}
