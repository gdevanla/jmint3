import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import soot.SootClass;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 10/4/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IMutantInjector {

    //public SootClass generateMutant(UseDefChain udChain);
    public SootClass generateMutant( InterfaceInvokeExpr expr);
    public SootClass generateMutant( SpecialInvokeExpr expr);
    public SootClass generateMutant( VirtualInvokeExpr expr);
    public SootClass generateMutant( StaticInvokeExpr expr);
    public SootClass generateMutant( BinopExpr expr);
    public SootClass generateMutant( NewExpr expr);
    public SootClass generateMutant(InstanceFieldRef fieldRef);
    public SootClass generateMutant( StaticFieldRef fieldRef);


}
