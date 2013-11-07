// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.EAM;

interface BaseInterface{
    public int getSomeVariable2();
    public int getSomeVariable3();
}


class BaseEAM implements BaseInterface {

    public  int getVariable()
    {
        return (int) Math.random();
    }

    @Override
    public int getSomeVariable2() {
         return (int)Math.random();
    }

    @Override
    public int getSomeVariable3() {
        return (int)Math.random();
    }
}

public class EAM2
{

    public EAM2()
    {
    }

    public  int getSomeOtherVariable()
    {
        System.out.println("GetSomeOtherVariable");
        return -1;
    }

    public  int getVariable()
    {
        System.out.println("GetVariable");
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
