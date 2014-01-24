package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class JDC extends BaseMutantInjector {

    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public JDC(UseDefChain udChain) {
        super(udChain);
    }

    public String getText(Type type)
    {
        return String.format("public %s() is deleted.", type.toString().substring(type.toString().lastIndexOf(".")+1));
    }

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {

        if ( !SUtil.isTypeIncludedInAnalysis(
                expr.getBaseType())) return null;

        Unit u = SUtil.getUnitInvokingDefaultConstructor(
                parent.getO1().getDefBoxes().get(0).getValue(),
                (SootMethod) parent.getO2());

        Type baseType = parent.getO1().getDefBoxes().get(0).getValue().getType();
        SootClass klass = SUtil.getResolvedClass(baseType.toString());

        if ( u != null ){
            //TODO: Right now store the SpecialInvoke in DefStmt. Later this can be
            //replaced with more generic reference to the constructor.
            MutantHeader header = new MutantHeader(udChain, parent,
                    new Pair<SootMethod, SootClass>(getDefaultConstructor(klass), klass),
                    MutantsCode.JDC, getText(baseType));
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }

    private SootMethod getDefaultConstructor(SootClass declaringClass) {
        SootClass klass = SUtil.getResolvedClass(declaringClass.getName());
        for(SootMethod m:klass.getMethods()){
            if (m.getName().contains("<init>")
                    && m.getParameterCount() == 0){
                return m;
            }
        }

        logger.error("Default constructor not found for = " + declaringClass);
        return null;
    }

}