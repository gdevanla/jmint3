import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import jmint.*;
import jmint.mutants.Inheritance.IHD;
import jmint.mutants.Inheritance.IHI;
import jmint.mutants.Inheritance.IOD;
import jmint.mutants.MutantsCode;
import jmint.mutants.javaish.*;
import jmint.mutants.progmistakes.EAM;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

import java.util.*;

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

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.JID});
        generateMutants("MutantInjectionArtifacts.JID.JIDTest1", sootAppFiles, null,x);

        assertEquals(2, BaseMutantInjector.allMutants.size());  //one from default constructor and other from defined constructor.
        MutantHeader header =  BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next());
        assertEquals("5", header.lineNoOriginalStmt);


    }

    @Test
    public void TestJDC1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.JDC.JDCTest1",
                "MutantInjectionArtifacts.JDC.JDC1",
                "MutantInjectionArtifacts.JDC.JDC2", "MutantInjectionArtifacts.JDC.JDC3"};

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();
        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.JDC});
        generateMutants("MutantInjectionArtifacts.JDC.JDCTest1", sootAppFiles, null,x);

        assertEquals(1, BaseMutantInjector.allMutants.size());
        MutantHeader header =  BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next());
        assertEquals("6", header.lineNoOriginalStmt);


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
    public void TestIHD1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.IHD.IHDTest1",
                "MutantInjectionArtifacts.IHD.IHD1",
                "MutantInjectionArtifacts.IHD.IHD2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IHD.IHDTest1", sootAppFiles);

        final IHD ihd1 = new IHD(solver.udChains.get(0));
        final IHD ihd2 = new IHD(solver.udChains.get(1));

        final List<BaseMutantInjector> injectors = new ArrayList<BaseMutantInjector>();

        Set<UseDefChain> uniqueUDChain = new HashSet<UseDefChain>();
        injectors.add(ihd1);
        injectors.add(ihd2);

        final MutantGenerator generator = new MutantGenerator(injectors);

        Transform x = (new Transform("wjtp.ihdinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                generator.generate();
                injectors.get(0).printMutantKeys();
                System.out.println(BaseMutantInjector.allMutants.size());
            }
        }));

        generateMutants("MutantInjectionArtifacts.IHD.IHDTest1", sootAppFiles, solver.udChains, x);
        assertEquals(2, BaseMutantInjector.allMutants.size());

    }


    @Test
    public void TestIHI1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.IHI.IHITest1",
                "MutantInjectionArtifacts.IHI.IHI1",
                "MutantInjectionArtifacts.IHI.IHI2"};
        CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IHI.IHITest1", sootAppFiles);

        final IHI ihi1 = new IHI(solver.udChains.get(0));
        final IHI ihi2 = new IHI(solver.udChains.get(1));

        final List<BaseMutantInjector> injectors = new ArrayList<BaseMutantInjector>();

        Set<UseDefChain> uniqueUDChain = new HashSet<UseDefChain>();
        injectors.add(ihi1);
        injectors.add(ihi2);

        final MutantGenerator generator = new MutantGenerator(injectors);

        Transform x = (new Transform("wjtp.ihdinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                generator.generate();
                injectors.get(0).printMutantKeys();
                System.out.println(BaseMutantInjector.allMutants.size());
            }
        }));

        generateMutants("MutantInjectionArtifacts.IHI.IHITest1", sootAppFiles, solver.udChains, x);
        assertEquals(2, BaseMutantInjector.allMutants.size());

    }

    private Transform getTransformForJMint(final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef,
                                   final MutantsCode[] mutants){
        Transform x = (new Transform("wjtp.iodinjector", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
                IFDSTabulationProblem<Unit,?,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem = new IFDSReachingDefinitions(new JimpleBasedInterproceduralCFG());
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = new CustomIFDSSolver(problem, true);
                solver.solve();
                solverRef.add(solver);

                final List<BaseMutantInjector> injectors = new ArrayList<BaseMutantInjector>();
                for (UseDefChain udChain:solver.udChains){
                    for(MutantsCode mutantCode:mutants){
                        injectors.add(BaseMutantInjector.getMutantInjector(mutantCode, udChain));
                    }
                }
                //final BaseMutantInjector ihi1 = new IOD(solver.udChains.get(0));

                //Set<UseDefChain> uniqueUDChain = new HashSet<UseDefChain>();
                //injectors.add(ihi1);

                MutantGenerator generator = new MutantGenerator(injectors);
                generator.generate();
                injectors.get(0).printMutantKeys();
                System.out.println(BaseMutantInjector.allMutants.size());
            }
        }));

        return x;
    }

    @After
    public void tearDown(){
        BaseMutantInjector.allMutants.clear();
    }

    @Test
    public void TestIOD1(){

        String[] sootAppFiles = { "MutantInjectionArtifacts.IOD.IODTest1",
                "MutantInjectionArtifacts.IOD.IOD1",
                "MutantInjectionArtifacts.IOD.IOD2"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOD.IODTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.IOD});
        generateMutants("MutantInjectionArtifacts.IOD.IODTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());
        assertEquals(BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next()).originalDefStmt.getO1().toString(),
                "<MutantInjectionArtifacts.IOD.IOD2: int getVariable()>");
        assertEquals(BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next()).originalDefStmt.getO2().toString(),
                "MutantInjectionArtifacts.IOD.IOD2");

    }

    @Test
    public void TestIOP1(){

        BaseMutantInjector.allMutants.clear();

        String[] sootAppFiles = { "MutantInjectionArtifacts.IOP.IOPTest1",
                "MutantInjectionArtifacts.IOP.IOP1",
                "MutantInjectionArtifacts.IOP.IOP2"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.IOP});
        generateMutants("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());
        assertEquals("SootClass=[MutantInjectionArtifacts.IOP.IOP2]:MutantCode=[IOP]:LineNo=[18]",
                BaseMutantInjector.allMutants.keySet().iterator().next());

    }

    @Ignore("IOR is kind of symmetric to IOD and therefore we will skip this for the time being")
    @Test
    public void TestIOR1(){

        BaseMutantInjector.allMutants.clear();

        String[] sootAppFiles = { "MutantInjectionArtifacts.IOR.IORTest1",
                "MutantInjectionArtifacts.IOR.IOR1",
                "MutantInjectionArtifacts.IOR.IOR2", "MutantInjectionArtifacts.IOR.Base"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.IOR});
        generateMutants("MutantInjectionArtifacts.IOR.IORTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());

    }

    @Test
    public void TestIPC1(){

        BaseMutantInjector.allMutants.clear();

        String[] sootAppFiles = { "MutantInjectionArtifacts.IPC.IPCTest1",
                "MutantInjectionArtifacts.IPC.IPC1",
                "MutantInjectionArtifacts.IPC.IPC2"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.IPC});
        generateMutants("MutantInjectionArtifacts.IPC.IPCTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());
        assertEquals("Pair specialinvoke this.<MutantInjectionArtifacts.IPC.Base: void <init>(java.lang.String)>(\"Test\"),<MutantInjectionArtifacts.IPC.IPC2: void <init>(int)>",
                BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next()).originalDefStmt.toString());

    }

    @Test
    public void TestISK1(){

        BaseMutantInjector.allMutants.clear();

        String[] sootAppFiles = { "MutantInjectionArtifacts.ISK.ISKTest1",
                "MutantInjectionArtifacts.ISK.ISK1",
                "MutantInjectionArtifacts.ISK.ISK2"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.ISK});
        generateMutants("MutantInjectionArtifacts.ISK.ISKTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());

        assertEquals("Pair zz = this.<MutantInjectionArtifacts.ISK.Base: int basex>,<MutantInjectionArtifacts.ISK.ISK2: int getSomeInstanceVar()>",
                BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next()).originalDefStmt.toString());

    }


    @Test
    public void TestOMD1(){

        BaseMutantInjector.allMutants.clear();

        String[] sootAppFiles = { "MutantInjectionArtifacts.OMD.OMDTest1",
                "MutantInjectionArtifacts.OMD.OMD1",
                "MutantInjectionArtifacts.OMD.OMD2"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.OMD});
        generateMutants("MutantInjectionArtifacts.OMD.OMDTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());

        //assertEquals("Pair zz = this.<MutantInjectionArtifacts.ISK.Base: int basex>,<MutantInjectionArtifacts.ISK.ISK2: int getSomeInstanceVar()>",
        //        BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next()).originalDefStmt.toString());

    }

    @Test
    public void TestOMR1(){

        BaseMutantInjector.allMutants.clear();

        String[] sootAppFiles = { "MutantInjectionArtifacts.OMR.OMRTest1",
                "MutantInjectionArtifacts.OMR.OMR1",
                "MutantInjectionArtifacts.OMR.OMR2"};
        //CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = runIPA("MutantInjectionArtifacts.IOP.IOPTest1", sootAppFiles);

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();

        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.OMR});
        generateMutants("MutantInjectionArtifacts.OMR.OMRTest1", sootAppFiles, null,x);
        assertEquals(1, BaseMutantInjector.allMutants.size());

        assertEquals("Pair zz = virtualinvoke t1_02.<MutantInjectionArtifacts.OMR.OMR2: int getVariable(int,java.lang.String)>($i0, \"fsda\"),<MutantInjectionArtifacts.OMR.OMR1: void F1()>",
                BaseMutantInjector.allMutants.get(BaseMutantInjector.allMutants.keySet().iterator().next()).originalDefStmt.toString());

    }


    @Test
    public void TestBCEL(){

        appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/stage/mujava/mujava_bcel/classes";

        final ArrayList<CustomIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>>> solverRef = new ArrayList<CustomIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>>>();
        Transform x = getTransformForJMint(solverRef, new MutantsCode[]{MutantsCode.OMR});
        generateMutants("", new String[]{}, null, x);

    }


    @Test
    public void TestTryCatch(){

        Transform x = (new Transform("jtp.transformer", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map map) {

               // if (!body.getMethod().getName().equals("testTryCatch1"))
               //     return;

                SootClass thrwCls = Scene.v().getSootClass("java.lang.Throwable");
                PatchingChain<Unit> pchain = body.getUnits();

                Stmt sFirstNonId = (Stmt)pchain.getFirst();
                Stmt sLast = (Stmt) pchain.getLast();
                Stmt sGotoLast = Jimple.v().newGotoStmt(sLast);

                Local lException1 = new JimpleLocal("ex1", RefType.v(thrwCls));
                body.getLocals().add(lException1);
                Stmt sCatch = Jimple.v().newIdentityStmt(lException1, Jimple.v().newCaughtExceptionRef());

                pchain.add(sCatch);
                pchain.add(sGotoLast);


                // insert trap (catch)
                body.getTraps().add(Jimple.v().newTrap(thrwCls, sFirstNonId, sGotoLast, sCatch));

            }
        } ));

        PackManager.v().getPack("jtp").add(x);


        setSootOptions();
        Options.v().set_output_format(Options.output_format_J);
        Options.v().set_main_class("TestIPA.A");
        soot.Main.main(new String[]{"TestIPA.A"});
        G.reset();


        setSootOptions();
        Options.v().set_output_format(Options.output_format_c);
        Options.v().set_main_class("TestIPA.A");
        soot.Main.main(new String[]{"TestIPA.A"});
        G.reset();



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