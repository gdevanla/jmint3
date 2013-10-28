// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IPC;


class Base
{

    public Base( java.lang.String s )
    {
    }

}

public class IPC2 extends MutantInjectionArtifacts.IPC.Base
{

    public int instancevar = (int) Math.random();

    public static int staticVar;

    public IPC2( int x )
    {
        super( "Test" );
        instancevar = (int) Math.random() + 1;
    }

    public  int getSomeInstanceVar()
    {
        int zz = this.instancevar;
        return zz;
    }

    public  int getStaticVar()
    {
        return IPC2.staticVar;
    }

}
