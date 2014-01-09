// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.OMR;


public class OMR2
{

    public  java.lang.String getVariable( java.lang.String s )
    {
        return "";
    }

    public  int getVariable( java.lang.String s, int x )
    {
        System.out.println("getVariable with (String, int) called");
        return (int) Math.random() + x;
    }

    public  int getVariable(int x1, java.lang.String s )
    {
        System.out.println("getVariable with (int, String) called");
        return (int) Math.random();
    }

}
