package jmint.mutants.Inheritance;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class ISK extends BaseMutantInjector {

    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public ISK(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent) {

        Value base = fieldRef.getBase();
        SootField field = fieldRef.getField();
        SootMethod parentMethod = (SootMethod)parent.getO2();

        if (!((SootMethod) parent.getO2()).getDeclaringClass().hasSuperclass()){
            return null;
        }

        if(!parentMethod.getDeclaringClass().declaresField(field.getSubSignature())){
            return null;
        }

        logger.debug("Base = " + base.getType() + ":" + fieldRef.getField().getDeclaringClass());
        if (base.toString().equals("this")){
            if (field.getDeclaringClass().equals(parentMethod.getDeclaringClass().getSuperclass())){

                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        parent,
                        MutantsCode.ISD, //substitutes ISK for parity with muJava.
                        parent.toString());
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;

    }



}