import soot.SootClass;
import soot.jimple.*;

/**
 * Created with IntelliJ IDEA.
 * User: gdevanla
 * Date: 10/4/13
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class BaseMutantInjector implements IMutantInjector {

    private final UseDefChain udChain;

    public BaseMutantInjector(UseDefChain udChain){
        this.udChain = udChain;
    }

    @Override
    public SootClass generateMutant(InterfaceInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(SpecialInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(VirtualInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(BinopExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(NewExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticFieldRef fieldRef) {
        return udChain.getDefMethod().getDeclaringClass();
    }
}
