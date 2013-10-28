// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.PPD;


public class PPD1
{

    public  void F1()
    {
        MutantInjectionArtifacts.PPD.Base t7_03 = new MutantInjectionArtifacts.PPD.PPD3();
        int x = t7_03.getFromChild( new MutantInjectionArtifacts.PPD.Child() );
        System.out.println( x );
    }

}
