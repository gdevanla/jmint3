// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.OMD;


class A
{
}

class B extends MutantInjectionArtifacts.OMD.A
{
}

public class OMD2
{

    public  int getVariable( MutantInjectionArtifacts.OMD.A a )
    {
        return (int) Math.random() + 1;
    }

    public  int getVariable( MutantInjectionArtifacts.OMD.B b )
    {
        return (int) Math.random();
    }

    public  int getVariable1( int a )
    {
        return (int) Math.random() + 1;
    }

    public  int getVariable1(float a)
    {
        return (int) Math.random();
    }

    /*public int getVariable1(int a){
        return (int)Math.random();
    } */

    public  int getVariable1( double b)
    {
        return (int) Math.random();
    }

}
