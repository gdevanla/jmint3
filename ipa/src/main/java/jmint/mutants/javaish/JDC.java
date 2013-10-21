package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class JDC extends BaseMutantInjector {
    public JDC(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {

        if ( !SUtil.isTypeIncludedInAnalysis(
                expr.getBaseType())) return null;

        Unit u = SUtil.getUnitInvokingDefaultConstructor(
                parent.getO1().getDefBoxes().get(0).getValue(),
                (SootMethod) parent.getO2());

        if ( u != null ){
            //TODO: Right now store the SpecialInvoke in DefStmt. Later this can be
            //replaced with more generic reference to the constructor.
            MutantHeader header = new MutantHeader(udChain, parent, new Pair<Stmt, Host>((InvokeStmt)u, udChain.getUseMethod()), MutantsCode.JDC);
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }


}