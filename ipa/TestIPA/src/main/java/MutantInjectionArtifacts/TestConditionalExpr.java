package MutantInjectionArtifacts;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 9/19/13
 * Time: 5:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestConditionalExpr {
        public void testConditional(){

            int x = (int)Math.random();
            int y = (int)Math.random();
            int z = x > y ? x:y;
            int w = x > y ? x+100:y*200;
        }
}
