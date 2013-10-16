package jmint;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
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
    public SootClass generateMutant( InterfaceInvokeExpr expr, Pair<Stmt, Host> parent);
    public SootClass generateMutant( SpecialInvokeExpr expr, Pair<Stmt, Host> parent);
    public SootClass generateMutant( VirtualInvokeExpr expr, Pair<Stmt, Host> parent);
    public SootClass generateMutant( StaticInvokeExpr expr, Pair<Stmt, Host> parent);
    public SootClass generateMutant( NewExpr expr, Pair<Stmt, Host> parent);
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent);
    public SootClass generateMutant( StaticFieldRef fieldRef, Pair<Stmt, Host> parent);
    public SootClass generateMutant(BinopExpr expr, Pair<Stmt, Host> parent);

    boolean canInject();

    String mutantLog();

    SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent);

}
