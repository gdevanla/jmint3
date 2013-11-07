package MutantInjectionArtifacts.PPD;

public class Base
{

    public int x;

    public  int getFromChild( Child child )
    {
        return (int) Math.random();
    }

}
