// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PNC;


public class PNC3 extends MutantInjectionArtifacts.PNC.Base
{

    public PNC3()
    {
       System.out.println("PMD3() called");
    }

    public  int something( int aaa )
    {
        System.out.println("Something in PMD3 called");
        return aaa + 1;
    }

}
