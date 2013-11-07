package jmint.mutants.Inheritance;
import jmint.*;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
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
import soot.tagkit.Tag;
import soot.toolkits.scalar.Pair;

public class IHI extends BaseMutantInjector {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public IHI(UseDefChain udChain) {
        super(udChain);
    }

    public static void writeMutantClass(InstanceFieldRef fieldRef, MutantHeader h){
        Pair<SootField, SootClass> f =  (Pair<SootField, SootClass>)h.originalDefStmt;

        System.out.println("Over here" + fieldRef.getField().getDeclaringClass());
        System.out.println(f.getO2());

        SootField field = new SootField(fieldRef.getField().getName(), fieldRef.getField().getType(), fieldRef.getField().getModifiers());

        SootClass klass = SUtil.getResolvedClass(fieldRef.getBase().getType().toString());
        try {
            klass.addField(field);
            MutantGenerator.write(f.getO2(), MutantsCode.IHI);
        }
        finally{
            klass.removeField(field);
        }


    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent) {

        Value base = fieldRef.getBase();
        SootField field = fieldRef.getField();

        //check if base type is different from declaring class type.

        logger.debug("Base = " + base.getType() + ":" + fieldRef.getField().getDeclaringClass());

        //only deal with public fields, courtesy soot!
        if ( (!SUtil.eqAsStr(base.getType(), fieldRef.getField().getDeclaringClass()))
                && fieldRef.getField().isPublic()){
            logger.debug(field.getSignature() + ": was part of parent class");
            MutantHeader header = new MutantHeader(udChain,
                    parent,
                    new Pair<SootField, SootClass>(field, (SootClass)SUtil.getResolvedClass(base.getType().toString())),
                    MutantsCode.IHI, fieldRef.getField().getDeclaration() + " is added.");
                    //"Field =" + field.getSignature() + " referred from parent =" +
                    //        fieldRef.getField().getDeclaringClass());
                if (!allMutants.containsKey(header.getKey())){
                    writeMutantClass(fieldRef, header);
                    allMutants.put(header.getKey(), header);
                }
            }
        return null;

    }

}