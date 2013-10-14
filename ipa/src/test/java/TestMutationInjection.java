import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import jmint.*;
import jmint.mutants.javaish.*;
import jmint.mutants.progmistakes.EAM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import soot.*;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

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
        Options.v().setPhaseOption("jb", "use-original-names");
        Scene.v().setSootClassPath(
                Scene.v().getSootClassPath() + ":" + appSourcePath + ":" + jcePath);

    }

    private CustomIFDSSolver<?, InterproceduralCFG<Unit,SootMethod>> runIPA(String mainClass, String[] sootAppFiles){

        setSootOptions();

        if (!mainClass.equals("")){
            Options.v().set_main_class(mainClass);
        }

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

        if ( sootAppFiles.length == 0){
            String[] sootArguments = new String[]{"-process-dir", appSourcePath};
            soot.Main.main(sootArguments);
        }
        else
        {
            soot.Main.main(sootAppFiles);
        }

        //soot.Main.main(sootAppFiles);
        G.reset();

        return solverRef.get(0);
    }

   /* private void generateMutants(String mainClass, String[] sootAppFiles, final List<UseDefChain> udChains ){

        setSootOptions();
        Options.v().set_main_class(mainClass);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.mutantsinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
               // MutantGenerator generator = new MutantGenerator(udChains);
               // generator.generate();
            }
        }));
        soot.Main.main(sootAppFiles);
        G.reset();

    }
*/

    private void generateMutants(String mainClass, String[] sootAppFiles, final List<UseDefChain> udChains, Transform transformCallBack ){

        setSootOptions();
        if (!mainClass.equals("")){
            Options.v().set_main_class(mainClass);
        }

        PackManager.v().getPack("wjtp").add(transformCallBack);
        if ( sootAppFiles.length == 0){
            String[] sootArguments = new String[]{"-process-dir", appSourcePath};
            soot.Main.main(sootArguments);
        }
        else
        {
            soot.Main.main(sootAppFiles);
        }

        G.reset();

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
      //  assertEquals("i1 = 1000000 + i0", useDefChain.getDefStmt().toString());
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
        //assertEquals("i1 = 1000000 + i0", useDefChain.getDefStmt().toString());
        //Unit newUnit = mutantInjector.injectMutant();


        //System.out.println(newUnit);

    }

    @Test
    public void TestIfStmtCanMutate(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.IfStmt.Main",
                "MutantInjectionArtifacts.IfStmt.TestClass2_01",
                "MutantInjectionArtifacts.IfStmt.TestClass2_02"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IfStmt.Main", sootAppFiles);

        //solver.printFilteredResults();

        assertEquals(2, solver.udChains.size());
        /*jmint.UseDefChain useDefChain = solver.udChains.get(0);

        jmint.MutantInjector mutantInjector = new jmint.MutantInjector(useDefChain.getDefStmt());
        assertEquals("i1 = 1000000 + i0", useDefChain.getDefStmt().toString());
        assertEquals(true, mutantInjector.canMutate());
        */

    }

    @Test
    public void TestRefTypeCanMutate(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.RefType.Main",
                "MutantInjectionArtifacts.RefType.TestClass3_01",
                "MutantInjectionArtifacts.RefType.TestClass3_02"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.RefType.Main", sootAppFiles);

        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());

        MutantInjector mutantInjector = new MutantInjector(solver.udChains.get(0).getDefStmt());
        assertEquals(true, mutantInjector.canMutate());
    }

    @Test
    public void TestJTD1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.JTD.JTDTest1",
                "MutantInjectionArtifacts.JTD.JTD1",
                "MutantInjectionArtifacts.JTD.JTD2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.JTD.JTDTest1", sootAppFiles);

        //solver.printFilteredResults();

        assertEquals(1, solver.udChains.size());

        final JTD jtd = new JTD(solver.udChains.get(0));

        Transform x = (new Transform("wjtp.jtdinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                assertEquals(true, jtd.canInject());
                System.out.println(jtd.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.JTD.JTDTest1",sootAppFiles, solver.udChains, x);


        }

    @Test
    public void TestEAM1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.EAM.EAMTest1",
                "MutantInjectionArtifacts.EAM.EAM1",
                "MutantInjectionArtifacts.EAM.EAM2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.EAM.EAMTest1", sootAppFiles);

        //solver.printFilteredResults();
        //assertEquals(1, solver.udChains.size());
        //final EAM eam = new EAM(solver.udChains.get(0));

        EAM eam = new EAM(solver.udChains.get(0));
        List<BaseMutantInjector> injectors = new ArrayList<BaseMutantInjector>();
        injectors.add(eam);

        final MutantGenerator generator = new MutantGenerator(injectors);

        Transform x = (new Transform("wjtp.eaminjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                generator.generate();
                //System.out.println(generator.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.EAM.EAMTest1",sootAppFiles, solver.udChains, x);

        for (BaseMutantInjector injector:injectors){
            injector.printInfo();
        }
    }

    @Test
    public void TestJSC1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.JSC.JSCTest1",
                "MutantInjectionArtifacts.JSC.JSC1",
                "MutantInjectionArtifacts.JSC.JSC2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.JSC.JSCTest1", sootAppFiles);

        final JSC jsc1 = new JSC(solver.udChains.get(0));

        Transform x = (new Transform("wjtp.jscinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                assertEquals(true, jsc1.canInject());
                System.out.println(jsc1.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.JSC.JSCTest1",sootAppFiles, solver.udChains, x);

        final JSC jsc2 = new JSC(solver.udChains.get(1));

        Transform x2 = (new Transform("wjtp.jscinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                assertEquals(true, jsc2.canInject());
                System.out.println(jsc2.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.JSC.JSCTest1",sootAppFiles, solver.udChains, x2);
    }

    @Test
    public void TestJID1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.JID.JIDTest1",
                "MutantInjectionArtifacts.JID.JID1",
                "MutantInjectionArtifacts.JID.JID2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.JID.JIDTest1", sootAppFiles);

        final JID jid = new JID(solver.udChains.get(0));

        Transform x = (new Transform("wjtp.jidinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                assertEquals(true, jid.canInject());
                System.out.println(jid.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.JID.JIDTest1",sootAppFiles, solver.udChains, x);
    }

    @Test
    public void TestJDC1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.JDC.JDCTest1",
                "MutantInjectionArtifacts.JDC.JDC1",
                "MutantInjectionArtifacts.JDC.JDC2", "MutantInjectionArtifacts.JDC.JDC3"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.JDC.JDCTest1", sootAppFiles);

        final JDC jdc = new JDC(solver.udChains.get(0));

        Transform x = (new Transform("wjtp.jdcinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                assertEquals(true, jdc.canInject());
                System.out.println(jdc.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.JDC.JDCTest1",sootAppFiles, solver.udChains, x);
    }

    @Test
    public void TestJTI1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.JTI.JTITest1",
                "MutantInjectionArtifacts.JTI.JTI1",
                "MutantInjectionArtifacts.JTI.JTI2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.JTI.JTITest1", sootAppFiles);

        final JTI jti = new JTI(solver.udChains.get(0));

        Transform x = (new Transform("wjtp.jti1injector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                assertEquals(true, jti.canInject());
                System.out.println(jti.mutantLog());
            }
        }));

        generateMutants("MutantInjectionArtifacts.JTI.JTITest1",sootAppFiles, solver.udChains, x);
    }

    @Test
    public void TestBCEL(){

        appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/stage/mujava/mujava_bcel/classes";

        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("", new String[]{});

        //solver.printFilteredResults();
        //assertEquals(1, solver.udChains.size());
        //final EAM eam = new EAM(solver.udChains.get(0));


        final List<BaseMutantInjector> injectors = new ArrayList<BaseMutantInjector>();

        for ( UseDefChain udChain: solver.udChains){
            EAM eam = new EAM(udChain);
            injectors.add(eam);
        }

        final MutantGenerator generator = new MutantGenerator(injectors);

        Transform x = (new Transform("wjtp.eaminjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                generator.generate();
                //System.out.println(generator.mutantLog());
                for (BaseMutantInjector injector:injectors){
                    injector.printInfo();
                }
            }
        }));

        generateMutants("", new String[]{}, solver.udChains, x);

        for (BaseMutantInjector injector:injectors){
            injector.printInfo();
        }




    }


    @Test
    public void TestGeneratedClass(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.AssignStmts.Main",
                "MutantInjectionArtifacts.AssignStmts.TestClass1_01",
                "MutantInjectionArtifacts.AssignStmts.TestClass1_02"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.AssignStmts.Main", sootAppFiles);

        //generateMutants("MutantInjectionArtifacts.AssignStmts.Main",sootAppFiles, solver.udChains);


    }
}