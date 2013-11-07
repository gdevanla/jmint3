// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PMD;


public class PMD3 extends Base
{

    public PMD3()
    {
       System.out.println("PMD3() called");
    }

    public  int something( int aaa )
    {
        System.out.println("Something in PMD3 called");
        return aaa + 1;
    }

}
