// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IPC;


public class IPC1
{

    public  void F1()
    {
        MutantInjectionArtifacts.IPC.IPC2 t1_02 = new MutantInjectionArtifacts.IPC.IPC2( (int) Math.random() );
        //MutantInjectionArtifacts.IPC.IPC2 t1_02 = new MutantInjectionArtifacts.IPC.IPC2();

        int zz = t1_02.getSomeInstanceVar();
        System.out.println( zz );
    }

}
