package MutantInjectionArtifacts.PPD;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 10/28/13
 * Time: 6:13 AM
 * To change this template use File | Settings | File Templates.
 */
class Base
{

    public int x;

    public  int getFromChild( Child child )
    {
        return (int) Math.random();
    }

}
