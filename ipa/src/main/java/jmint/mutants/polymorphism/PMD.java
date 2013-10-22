package jmint.mutants.polymorphism;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class PMD extends BaseMutantInjector {
    public PMD(UseDefChain udChain) {
        super(udChain);
    }

    //PMD   == PNC
    // look for new expressions in all reaching definitions
    // of  type has super class and invokes default constructor.

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {

        if ( !SUtil.isTypeIncludedInAnalysis(
                expr.getBaseType())) return null;

        //check if default constructor is being invoked.
        Unit u = SUtil.getUnitInvokingDefaultConstructor(
                parent.getO1().getDefBoxes().get(0).getValue(),
                (SootMethod) parent.getO2());

        if (u==null){return null;} //no default constructor invocation

        //check left-right op types.
        if (! parent.getO1().getDefBoxes().get(0).getValue().getType()
                .equals(expr.getType())){
            return null;
        }

        SootClass klass = SUtil.getResolvedClass(parent.getO1().getDefBoxes().get(0).getValue().getType().toString());
        if ( klass.hasSuperclass()) {
            MutantHeader header = new MutantHeader(udChain, parent, parent,
                    MutantsCode.PNC, "SuperClass is=" + klass.getSuperclass());
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }

        }

        return null;
    }

    // PPD
    // Look for all method invovations in use-method.
    // for each method invocation, loop around arguments and see
    // if the parameters can be substituted with more generic types.

    // PRV
    // Look at simple assignments and replace the ones with compatible classes.

}