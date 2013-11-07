// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IHD;


public class IHD1 extends MutantInjectionArtifacts.IHD.IHDParent1
{

    public int x = 10;

    public  void F1()
    {
        MutantInjectionArtifacts.IHD.IHDUnRelatedClass c = new MutantInjectionArtifacts.IHD.IHDUnRelatedClass();
        int y = c.y;
        int z = (new MutantInjectionArtifacts.IHD.B()).bx;
        MutantInjectionArtifacts.IHD.IHD2 t1_02 = new MutantInjectionArtifacts.IHD.IHD2();
        t1_02.useLocalVariable( this.x + z );
    }

}
