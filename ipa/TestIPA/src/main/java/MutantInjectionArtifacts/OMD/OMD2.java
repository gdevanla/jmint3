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

    public  int getVariable( MutantInjectionArtifacts.OMD.A a, float f ) throws NoSuchFieldException {
        System.out.println("A called");
        if ( f < 0){
            throw new NoSuchFieldException("dfs");
        }
        return (int) Math.random() + 1;

    }

    public  int getVariable( MutantInjectionArtifacts.OMD.B b , int a)
    {
        return (int) Math.random();
    }

    public  int getVariable1( int a )
    {
        System.out.println("getVariable1 of int called");
        return (int) Math.random() + 1;
    }

    public  int getVariable1( float a )
    {
        System.out.println("getVariable1 of float called");
        return (int) Math.random();
    }

    public  int getVariable1( double b ) throws Exception
    {
        System.out.println("getVariable1 of double called");
        return (int) Math.random();
    }

}
