// This is a mutant program.
// Author : ysma                            `

package MutantInjectionArtifacts.OMR;


public class OMR2
{

    public  java.lang.String getVariable( java.lang.String s )
    {
        return "";
    }

    public  int getVariable( java.lang.String s, int x1 )
    {
        return (int) Math.random() + x1;
    }

    public  int getVariable( int x1, java.lang.String s )
    {
        return (int) Math.random();
    }

}
