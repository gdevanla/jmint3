package jmint;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.toolkits.scalar.Pair;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 10/4/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IMutantInjector {

    //public SootClass generateMutant(jmint.UseDefChain udChain);
    public SootClass generateMutant( InterfaceInvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant( SpecialInvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant( VirtualInvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant( StaticInvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant( BinopExpr expr, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant( NewExpr expr, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<DefinitionStmt, SootMethod> parent);
    public SootClass generateMutant( StaticFieldRef fieldRef, Pair<DefinitionStmt, SootMethod> parent);


    boolean canInject();

    String mutantLog();

    SootClass generateMutant(AssignStmt stmt, Pair<DefinitionStmt, SootMethod> parent);

}
