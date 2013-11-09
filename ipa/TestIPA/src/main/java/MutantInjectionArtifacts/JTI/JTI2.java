// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JTI;


public class JTI2
{

    public  void useLocalVariable( int x , int zz)
    {
        int y = 10;
        int z = x + y;
        System.out.println( "value of z =" + z );
    }

    public int getLocalVariable(int x, int y) {
        return (int)Math.random();
    }
}
