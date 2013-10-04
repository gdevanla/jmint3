package jmint;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import soot.*;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Core {

    public static void main(String[] args) {

        String jcePath = "/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/lib/jce.jar";
        String appSourcePath = "/Users/gdevanla/Dropbox/private/se_research/stage/ipa/TestIPA/src/main/java";
        String[] sootArgs = {"-no-bodies-for-excluded","-x", "java", "-w", "-f" , "j", "TestIPA.A"};

        Options.v().set_keep_line_number(true);
        Scene.v().setSootClassPath(
                Scene.v().getSootClassPath() + ":" + appSourcePath + ":" + jcePath);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
            protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {

                IFDSTabulationProblem<Unit,?,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem = new IFDSReachingDefinitions(new JimpleBasedInterproceduralCFG());

                @SuppressWarnings({ "rawtypes", "unchecked" })
                JimpleIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = new JimpleIFDSSolver(problem, true);
                solver.solve();
            }
        }));

        soot.Main.main(sootArgs);
    }

}
