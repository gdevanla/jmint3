// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.IHI;


class IHIParent1
{

    public int xx;

}

class IHDUnRelatedClass
{

    public int y;

}

public class IHI1 extends MutantInjectionArtifacts.IHI.IHIParent1
{

    public  void F1()
    {
        MutantInjectionArtifacts.IHI.IHDUnRelatedClass c = new MutantInjectionArtifacts.IHI.IHDUnRelatedClass();
        int y = c.y;
        int z = (new MutantInjectionArtifacts.IHI.B()).x;
        MutantInjectionArtifacts.IHI.IHI2 t1_02 = new MutantInjectionArtifacts.IHI.IHI2();
        t1_02.useLocalVariable( this.xx + z );
    }

}
