// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.EAM;


public class EAM1
{

    public  void F1()
    {
        MutantInjectionArtifacts.EAM.EAM2 t1_02 = new MutantInjectionArtifacts.EAM.EAM2();
        int zz = t1_02.getVariable();
        System.out.println( zz );
    }

    public void F2(){
        //NOTE: This method not supported by jMint yet, since it calls interfaceinvoke
        BaseInterface t1_02 = new BaseEAM();
        int zz = t1_02.getSomeVariable2();
        System.out.println( zz );

        String message = "Test message";
        System.out.println(message);
    }

}
