import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
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

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 9/19/13
 * Time: 9:29 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JUnit4.class)
public class TestMutationInjection {

    String jcePath = "/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/lib/jce.jar";
    String appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/myprojects/jMint_paper/jmint3/ipa/TestIPA/target/classes/";
    //String appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/stage/ipa/TestIPA/target/classes";

    public void setSootOptions(){

        Options.v().set_no_bodies_for_excluded(true);
        ArrayList<String> exclude_list = new ArrayList<String>();
        exclude_list.add("java.");
        Options.v().set_exclude(exclude_list);
        Options.v().set_whole_program(true);
        Options.v().set_output_format(Options.output_format_J);
        Options.v().set_keep_line_number(true);
        Scene.v().setSootClassPath(
                Scene.v().getSootClassPath() + ":" + appSourcePath + ":" + jcePath);

    }

    private CustomIFDSSolver<?, InterproceduralCFG<Unit,SootMethod>> runIPA(String mainClass, String[] sootAppFiles){

        setSootOptions();

        Options.v().set_main_class(mainClass);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                IFDSTabulationProblem<Unit,?,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem = new IFDSReachingDefinitions(new JimpleBasedInterproceduralCFG());
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = new CustomIFDSSolver(problem, true);

                solver.solve();
                solverRef.add(solver);
                solver.printFilteredResults();

            }
        }));
        soot.Main.main(sootAppFiles);
        G.reset();

        return solverRef.get(0);
    }


    @Test
    public void TestAssignStmtCanMutate(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.AssignStmts.Main",
                "MutantInjectionArtifacts.AssignStmts.TestClass1_01",
                "MutantInjectionArtifacts.AssignStmts.TestClass1_02"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.AssignStmts.Main", sootAppFiles);

        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());
        UseDefChain useDefChain = solver.udChains.get(0);

        MutantInjector mutantInjector = new MutantInjector(useDefChain.getDefStmt());
        assertEquals("i1 = 1000000 + i0", useDefChain.getDefStmt().toString());
        assertEquals(true, mutantInjector.canMutate());

    }

    @Test
    public void TestAssignStmtMutated(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.AssignStmts.Main",
                "MutantInjectionArtifacts.AssignStmts.TestClass1_01",
                "MutantInjectionArtifacts.AssignStmts.TestClass1_02"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.AssignStmts.Main", sootAppFiles);

        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());
        UseDefChain useDefChain = solver.udChains.get(0);

        MutantInjector mutantInjector = new MutantInjector(useDefChain.getDefStmt());
        assertEquals("i1 = 1000000 + i0", useDefChain.getDefStmt().toString());
        Unit newUnit = mutantInjector.injectMutant();
        System.out.println(newUnit);

    }

    @Test
    public void TestIfStmtCanMutate(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.IfStmt.Main",
                "MutantInjectionArtifacts.IfStmt.TestClass2_01",
                "MutantInjectionArtifacts.IfStmt.TestClass2_02"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IfStmt.Main", sootAppFiles);

        //solver.printFilteredResults();

        assertEquals(2, solver.udChains.size());
        /*UseDefChain useDefChain = solver.udChains.get(0);

        MutantInjector mutantInjector = new MutantInjector(useDefChain.getDefStmt());
        assertEquals("i1 = 1000000 + i0", useDefChain.getDefStmt().toString());
        assertEquals(true, mutantInjector.canMutate());
        */

    }
}