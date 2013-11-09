// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JID;


public class JID2
{

    public int instancevar = (int) (((Math.random())*2000)+1);

    public static int staticVar;

    public JID2()
    {
    }

    public JID2( int x )
    {
        instancevar = (int) ((Math.random() + 1)  * 10);
    }

    public  int getSomeInstanceVar()
    {
        int zz = this.instancevar;
        return zz;
    }

    public  int getStaticVar()
    {
        return JID2.staticVar;
    }

}
