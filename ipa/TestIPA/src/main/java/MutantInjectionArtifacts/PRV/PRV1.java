// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PRV;


public class PRV1
{

    MutantInjectionArtifacts.PRV.PRV3 prv6 = new MutantInjectionArtifacts.PRV.PRV3();

    public  void F1( MutantInjectionArtifacts.PRV.PRV3 prv31, MutantInjectionArtifacts.PRV.PRV3 prv32, MutantInjectionArtifacts.PRV.PRV3 prv33 )
    {
        MutantInjectionArtifacts.PRV.PRV2 prv2 = new MutantInjectionArtifacts.PRV.PRV2();
        //prv33 = prv31;
        prv2.updateObject( prv33 );
    }

}
