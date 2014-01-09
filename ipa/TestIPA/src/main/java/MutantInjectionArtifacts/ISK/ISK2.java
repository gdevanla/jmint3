// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.ISK;


class Base
{
    public int basex = (int) Math.random();
}

public class ISK2 extends MutantInjectionArtifacts.ISK.Base
{

    public int basex = (int) Math.random();

    public  int getSomeInstanceVar()
    {
        int zz = super.basex;
        return zz;
    }

}
