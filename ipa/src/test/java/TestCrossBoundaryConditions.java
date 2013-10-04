import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import jmint.CustomIFDSSolver;
import jmint.UseDefChain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import soot.*;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

import java.util.ArrayList;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TestCrossBoundaryConditions {

    String jcePath = "/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/lib/jce.jar";
    String appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/myprojects/jMint_paper/jmint3/ipa/TestIPA/target/classes/";
    //String appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/stage/ipa/TestIPA/target/classes";

    public String[] getSootArgsByTestNum(int testNum, int numTestClasses){

        String mainClass = "TestArtifacts.Test%d.TestIntg%d";
        String testClass1 = "TestArtifacts.Test%d.TestClass%d_01";
        String testClass2 = "TestArtifacts.Test%d.TestClass%d";

        Options.v().set_no_bodies_for_excluded(true);
        ArrayList<String> exclude_list = new ArrayList<String>();
        exclude_list.add("java.");
        Options.v().set_exclude(exclude_list);
        Options.v().set_whole_program(true);
        Options.v().set_output_format(Options.output_format_J);
        Options.v().set_main_class(String.format(mainClass, testNum, testNum));

        String[] files = new String[numTestClasses+1];
        files[0] = String.format(mainClass, testNum, testNum);
        for (int i = 1; i < files.length; i++) {
            files[i] = String.format("TestArtifacts.Test%d.TestClass%d_0%d", testNum, testNum, i);
        }

        return files;
    }

    private CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> runIPA(int testNum, int numTestClasses){
        String[] sootArgs = getSootArgsByTestNum(testNum, numTestClasses);
        Options.v().set_keep_line_number(true);
        Scene.v().setSootClassPath(
                Scene.v().getSootClassPath() + ":" + appSourcePath + ":" + jcePath);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                IFDSTabulationProblem<Unit,?,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem = new IFDSReachingDefinitions(new JimpleBasedInterproceduralCFG());
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = new CustomIFDSSolver(problem, true);

                solver.solve();
                solverRef.add(solver);
                //solver.printFilteredResults();

            }
        }));
        soot.Main.main(sootArgs);
        G.reset();

        return solverRef.get(0);
    }


    @Test
    public void Test1(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(1,2);
        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());
        UseDefChain useDefChain = solver.udChains.get(0);
        assertEquals(useDefChain.getDefMethod().toString(), "<TestArtifacts.Test1.TestClass1_01: void F1()>");
        assertEquals(useDefChain.getDefStmt().toString(), "b0 = 100");

        assertEquals(useDefChain.getUseMethod().toString(), "<TestArtifacts.Test1.TestClass1_02: void useLocalVariable(int)>");
        assertEquals(useDefChain.getUseValue().toString(), "@parameter0: int");
        assertEquals(useDefChain.getUseUnit().toString(), "i0 := @parameter0: int");

    }

    @Test
    public void Test2(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(2,2);
        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());
        UseDefChain useDefChain = solver.udChains.get(0);
        assertEquals(useDefChain.getDefMethod().toString(), "<TestArtifacts.Test2.TestClass2_01: void F1()>");
        assertEquals(useDefChain.getDefStmt().toString(), "$i0 = r0.<TestArtifacts.Test2.TestClass2_01: int x>");

        assertEquals(useDefChain.getUseMethod().toString(), "<TestArtifacts.Test2.TestClass2_02: void useLocalVariable(int)>");
        assertEquals(useDefChain.getUseValue().toString(), "@parameter0: int");
        assertEquals(useDefChain.getUseUnit().toString(), "i0 := @parameter0: int");

    }


    @Test
    public void Test3(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(3,2);
        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());
        UseDefChain useDefChain1 =  solver.udChains.get(0);

        //chain2
        assertEquals(useDefChain1.getDefMethod().toString(), "<TestArtifacts.Test3.TestClass3_02: void useMemberInstance()>");
        assertEquals(useDefChain1.getDefStmt().toString(), "$i2 = $r1.<TestArtifacts.Test3.TestClass3_01: int x>");

        assertEquals(useDefChain1.getUseMethod().toString(),  "<TestArtifacts.Test3.TestClass3_02: void useMemberInstance()>");
        assertEquals(useDefChain1.getUseValue().toString(), "$i2");
        assertEquals(useDefChain1.getUseUnit().toString(),  "i1 = $i2 + b0");

    }

    @Test
    public void Test4(){

        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(3,2);
        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());
        UseDefChain useDefChain1 =  solver.udChains.get(0);

        //chain2
        assertEquals(useDefChain1.getDefMethod().toString(), "<TestArtifacts.Test3.TestClass3_02: void useMemberInstance()>");
        assertEquals(useDefChain1.getDefStmt().toString(), "$i2 = $r1.<TestArtifacts.Test3.TestClass3_01: int x>");

        assertEquals(useDefChain1.getUseMethod().toString(),  "<TestArtifacts.Test3.TestClass3_02: void useMemberInstance()>");
        assertEquals(useDefChain1.getUseValue().toString(), "$i2");
        assertEquals(useDefChain1.getUseUnit().toString(),  "i1 = $i2 + b0");

    }

    @Test
    public void Test5(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(5,2);
        //solver.printFilteredResults();

        UseDefChain useDefChain = solver.udChains.get(0);

        assertEquals(useDefChain.getDefMethod().toString(), "<TestArtifacts.Test5.TestClass5_02: int getSomeValue()>");
        assertEquals(useDefChain.getDefStmt().toString(), "i0 = (int) $d0");

        assertEquals(useDefChain.getUseMethod().toString(), "<TestArtifacts.Test5.TestClass5_01: void F1()>");
        assertEquals(useDefChain.getUseValue().toString(), "$i0");
        assertEquals(useDefChain.getUseUnit().toString(), "r0.<TestArtifacts.Test5.TestClass5_01: int x> = $i0");


    }

    @Test
    public void Test6(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(6,2);
        //solver.printFilteredResults();

        UseDefChain useDefChain = solver.udChains.get(0);

        assertEquals(useDefChain.getDefMethod().toString(), "<TestArtifacts.Test6.TestClass6_02: int getSomeValue()>");
        assertEquals(useDefChain.getDefStmt().toString(), "i0 = (int) $d0");

        assertEquals(useDefChain.getUseMethod().toString(), "<TestArtifacts.Test6.TestClass6_01: void F1()>");
        assertEquals(useDefChain.getUseValue().toString(), "i0");
        assertEquals(useDefChain.getUseUnit().toString(), "i1 = i0 + 1010");
    }


    @Test
    public void Test7(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(7,3);
        //solver.printFilteredResults();
        assertEquals(2, solver.udChains.size());
        for ( UseDefChain ud:solver.udChains){
            if ( ud.getUseValue().toString().equals(":@parameter0: TestArtifacts.Test8.TestClass8_03")){
                assertEquals(ud.getUseMethod().toString(),
                        "<TestArtifacts.Test8.TestClass8_02: void updateObject(TestArtifacts.Test8.TestClass8_03)>" );
                assertEquals(ud.getDefMethod().toString(),
                        "<TestArtifacts.Test8.TestClass8_01: void F1()>");

                assertEquals(ud.getDefStmt().toString(), "<TestArtifacts.Test8.TestClass8_01: void F1()>");
                assertEquals(ud.getUseUnit().toString(), "r1 := @parameter0: TestArtifacts.Test8.TestClass8_03");

            }
            else
            {
                //other condition is routine JInstanceRef within same method
            }
        }


    }

    @Test
    public void Test8(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(8,3);
        //solver.printFilteredResults();

        assertEquals(2, solver.udChains.size());

        for ( UseDefChain ud:solver.udChains){
            if ( ud.getUseValue().toString().equals(":@parameter0: TestArtifacts.Test8.TestClass8_03")){
                assertEquals(ud.getUseMethod().toString(),
                        "<TestArtifacts.Test8.TestClass8_02: void updateObject(TestArtifacts.Test8.TestClass8_03)>" );
                assertEquals(ud.getDefMethod().toString(),
                        "<TestArtifacts.Test8.TestClass8_01: void F1()>");

                assertEquals(ud.getDefStmt().toString(), "<TestArtifacts.Test8.TestClass8_01: void F1()>");
                assertEquals(ud.getUseUnit().toString(), "r1 := @parameter0: TestArtifacts.Test8.TestClass8_03");

            }
            else
            {
               //other condition is routine JInstanceRef within same method
            }
        }
    }

    @Test
    public void Test10(){
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA(10,2);
        //Check if definitions within if conditions are picked up
        assertEquals(2, solver.udChains.size());

    }

    //@Test
    public void template(){

        int testNum = 2;
        String[] sootArgs = getSootArgsByTestNum(testNum, 2);
        Options.v().set_keep_line_number(true);
        Scene.v().setSootClassPath(
                Scene.v().getSootClassPath() + ":" + appSourcePath + ":" + jcePath);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {

                IFDSTabulationProblem<Unit,?,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem = new IFDSReachingDefinitions(new JimpleBasedInterproceduralCFG());

                @SuppressWarnings({ "rawtypes", "unchecked" })
                CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = new CustomIFDSSolver(problem, true);
                solver.solve();

                //solver.printFilteredResults();
                UseDefChain udChain = solver.udChains.get(0);

            }
        }));

        soot.Main.main(sootArgs);
        G.reset();

    }
}