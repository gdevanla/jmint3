// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IOD;


class Base
{
    protected  int getVariable()
    {
        return (int) Math.random();
    }

    protected int getVariableForParam(int x){
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

    protected  int getVariable()
    {
        return (int) Math.random();
    }

    protected int getVariableForParam(int x){
        return x + (int) Math.random();
    }

}
