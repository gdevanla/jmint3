package jmint.mutants.Inheritance;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.Set;

//Currently, this is symmetric to  IOD. IOD is better in not inducing more compile errors.
public class IOR extends BaseMutantInjector {
    public IOR(UseDefChain udChain) {
        super(udChain);
    }

/*
    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if ( parent.getO2().equals(udChain.getDefMethod())){
            return null; //we will not rename the original callee method.
        }

        if (method.isAbstract())
            return null;

        Set<SootMethod> methods = Scene.v().getFastHierarchy().resolveAbstractDispatch(method.getDeclaringClass(), method);
        for ( SootMethod m:methods){
            System.out.println(m.getDeclaringClass());
            System.out.println("Method = " + method  + " can be renamed or duplicated.");
        }

        return null;
    }*/


     /*  public Unit findStatementInvokingGetter(SootMethod getterMethod){
        //TODO: Fingers crossed to see if this dumb version of callgraph is sufficient
        CallGraphBuilder callGraphBuilder = new CallGraphBuilder(DumbPointerAnalysis.v());
        callGraphBuilder.build();
        CallGraph callGraph = callGraphBuilder.getCallGraph();

        ExceptionalUnitGraph expUnitGraph = new ExceptionalUnitGraph(
                SUtil.getResolvedMethod(udChain.getUseMethod()).getActiveBody());



        Iterator<Edge> edgeList = callGraph.edgesOutOf(SUtil.getResolvedMethod(udChain.getUseMethod()));

        while(edgeList.hasNext()){
            Edge  e = edgeList.next();
            if (e.tgt().equivHashCode() == getterMethod.equivHashCode() &&
                    ){
                System.out.println("Returning unit=" +  e.srcUnit() + " calling getter " + getterMethod);
                return e.srcUnit();

            }
        }
        return null;
    }*/




}