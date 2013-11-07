// This is a mutant program.
// Author : ysma

package MutantInjectionArtifacts.JTD;


public class JTD1
{

    int x = (int) (Math.random()*100);

    private int y = 100;

    public  void F1()
    {
        int x = (int) (Math.random()*100);
        System.out.println("Local x= "+x + " Instance x = " + this.x);
        try{
            MutantInjectionArtifacts.JTD.JTD2 t1_02 = new MutantInjectionArtifacts.JTD.JTD2();
            t1_02.useLocalVariable( this.x );
        }
        catch(Exception ex){
            return;
        }
    }

}
