package TestArtifacts.Test10;

/*
   Test case when local variable in one class
   is used in external class
 */
public class TestIntg10
{
    public static void main(String[] args){
        TestClass10_01 t = new TestClass10_01();
        t.F1();
    }

    public void testConditional(){

        int x = (int)Math.random();
        int y = (int)Math.random();
        int z = x > y ? x:y;
        int w = x > y ? x+100:y*200;

        boolean v = x > y;
    }
}
