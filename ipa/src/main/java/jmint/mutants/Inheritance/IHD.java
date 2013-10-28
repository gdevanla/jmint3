package jmint.mutants.Inheritance;

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

public class IHD extends BaseMutantInjector {

    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public IHD(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent) {

        Value base = fieldRef.getBase();
        SootField field = fieldRef.getField();

        //check if declaring class same as base.
        //does superclass have similar member
        //then yes.

        logger.debug("Base = " + base.getType() + ":" + fieldRef.getField().getDeclaringClass());

        if (!field.isPublic()) { return null; } //we don't support protected, courtesy muJava

        if (SUtil.eqAsStr(base.getType(), fieldRef.getField().getDeclaringClass())
                && field.getDeclaringClass().hasSuperclass()){

            logger.debug(field.getSignature() + ": was part of original base.");
            SootClass superKlass = getSuperClassWithSameField(field.getDeclaringClass().getSuperclass(), field);

            if ( superKlass!=null){

                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        new Pair<SootField, SootClass>(field, (SootClass)fieldRef.getField().getDeclaringClass())
                        ,
                        MutantsCode.IHD, field.getDeclaration().toString() + " is deleted.") ;
                        //"Field =" + field.getSignature() + " also declared in parent =" +
                        //superKlass + " as " + superKlass.getField(field.getSubSignature()).getSignature() + ", isPublic=" +
                        //superKlass.getField(field.getSubSignature()).isPublic());
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;

    }

    private SootClass getSuperClassWithSameField(SootClass declaringClass, SootField field) {
        logger.debug("Looking up " + field.getSignature() + "in class=" + declaringClass);
        if (declaringClass.declaresField(field.getSubSignature())){
            SootField declaredField = declaringClass.getField(field.getSubSignature());
            if (declaredField.isPublic() )
            //declaredField.isProtected()) not supported by muJava,
            // so we ignore that as well
            {
                return declaringClass;
            }
            return null; //once private always private. Ignore no-modifier declarations for now.
        }
        if (!declaringClass.hasSuperclass())
                return null;
        return getSuperClassWithSameField(declaringClass.getSuperclass(), field);
    }


}