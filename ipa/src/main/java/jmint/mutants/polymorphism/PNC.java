package jmint.mutants.polymorphism;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PNC extends BaseMutantInjector {
    public PNC(UseDefChain udChain) {
        super(udChain);
    }

    //PNC   == PMD
    // look for new expressions in all reaching definitions
    // if left type = right type, and right type has sub-classes with default constructor
    // may be extended to other compatible constructors

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



}