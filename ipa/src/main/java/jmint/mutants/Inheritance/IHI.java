package jmint.mutants.Inheritance;
import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootField;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.spark.ondemand.pautil.SootUtil;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class IHI extends BaseMutantInjector {
    public IHI(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent) {

        Value base = fieldRef.getBase();
        SootField field = fieldRef.getField();

        //check if base type is different from declaring class type.

        System.out.println("Base = " + base.getType() + ":" + fieldRef.getField().getDeclaringClass());

        if (!SUtil.eqAsStr(base.getType(), fieldRef.getField().getDeclaringClass())){
            System.out.println(field.getSignature() + ": was part of parent class");
            MutantHeader header = new MutantHeader(udChain,
                    parent,
                    new Pair<SootField, SootClass>(field, (SootClass)SUtil.getResolvedClass(base.getType().toString())),
                    MutantsCode.IHI,
                    "Field =" + field.getSignature() + " referred from parent =" +
                            fieldRef.getField().getDeclaringClass());
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        return null;

    }

}