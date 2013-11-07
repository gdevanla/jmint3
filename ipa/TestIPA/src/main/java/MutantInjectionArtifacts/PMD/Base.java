// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PMD;


class Base
{

    public int x;

    public Base(  )
    {
      System.out.println("base() called");
    }

    public  int doSomethingElse()
    {
        System.out.println("DOSomething in base called");
        return (int) Math.random();
    }

}
