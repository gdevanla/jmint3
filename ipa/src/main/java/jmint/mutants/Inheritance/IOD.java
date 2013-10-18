package jmint.mutants.Inheritance;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IOD extends BaseMutantInjector {
    public IOD(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        if (method.getName().equals("<init>") || method.getName().equals("<clint>"))
            return null; // we will deal with this as part of JDC.

        if (method.getDeclaringClass().hasSuperclass()){
           SootClass parentKlass = IsMethodAnOveride(method, method.getDeclaringClass().getSuperclass());
           if (parentKlass != null){
               System.out.println(method + " was overriden in parent class " + parentKlass);

               MutantHeader header = new MutantHeader(udChain,
                       parent,
                       new Pair<SootMethod, SootClass>(method, method.getDeclaringClass()),
                       MutantsCode.IOD,
                       "Method =" + method.getSignature() + " also declared in parent =" + parentKlass);

               if (!allMutants.containsKey(header.getKey())){
                   allMutants.put(header.getKey(), header);
               }
           }
        }

        return null;
    }

    private SootClass IsMethodAnOveride(SootMethod method, SootClass declaringClass) {

        System.out.println("Looking up " + method.getSubSignature() + "in class=" + declaringClass);
        NumberedString ns = Scene.v().getSubSigNumberer().find(method.getSubSignature());
        System.out.println("NumberedString:" + ns.getString() + ":" + ns.getNumber());

        if (!SUtil.isClassIncludedInAnalysis(declaringClass)){
            return  null;
        }

        if (declaringClass.isAbstract())
            return null;

        if (declaringClass.declaresMethod( method.getNumberedSubSignature())){
            SootMethod sootMethod = declaringClass.getMethod(method.getSubSignature());
            if ((sootMethod.isPublic() || sootMethod.isProtected()) && (!sootMethod.isAbstract()))
            {
                return declaringClass;
            }
            else{
                return null; //once private always private. Ignore no-modifier declarations for now.
            }
        }

        if (!declaringClass.hasSuperclass())
            return null;

        return IsMethodAnOveride(method, declaringClass.getSuperclass());
    }

    //soot uses NumberedString to search for strings in an hashmap. But, since NumberedString does not
    //implement equals, the hashcodes produced are different. Therefore, the getMethod does not work always.
    private SootMethod searchForMethodInClassMyOwnWay(SootMethod method, SootClass declaringClass){
                    return null;
    }


}