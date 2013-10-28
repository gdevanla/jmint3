// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.EAM;


class Base
{

    public  int getVariable()
    {
        return (int) Math.random();
    }

}

public class EAM2
{

    public EAM2()
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

    public  void testTryCatch()
    {
        int x = (int) Math.random();
        try {
            int y = (int) Math.random();
            System.out.print( y + x );
        } catch ( java.lang.Exception ex ) {
            System.out.println( "Exception" );
        } finally 
{
            System.out.println( "Finally" );
        }
    }

}
