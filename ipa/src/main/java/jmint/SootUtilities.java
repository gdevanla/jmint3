package jmint;


import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.util.Chain;

public class SootUtilities {

    public static boolean isTypeIncludedInAnalysis(Value r){
        for ( String s:Configuration.packageUnderTest){
            if ( r.getType().toString().startsWith(s))
                return true;
        }
        return false;
    }

    public static boolean isSpecialInvokeExprOnValue(Value r, Unit u){
        if ( (u instanceof InvokeStmt))
        {
            if (((InvokeStmt)u).getInvokeExpr() instanceof SpecialInvokeExpr){
                SpecialInvokeExpr expr = (SpecialInvokeExpr) ((InvokeStmt)u).getInvokeExpr();

                if (expr.getArgCount() == 0 &&
                        expr.getBase().hashCode() == r.hashCode()) {//soot bug circumvented.
                    return true;
                }
            }
        }
        return false;
    }

    public static Unit getUnitInvokingDefaultConstructor(Value r, SootMethod method){
        for (Unit u:getResolvedMethod(method).getActiveBody().getUnits()){
            if (isSpecialInvokeExprOnValue(r, u)) { return u; }
        }
        return null;
    }

    public static SootMethod getResolvedMethod(String methodName) {
        //Assume Soot scene is currently active
        SootClass klass = Scene.v().forceResolve(methodName,
                SootClass.SIGNATURES);
        return klass.getMethodByName(methodName);
    }

    public static SootMethod getResolvedMethod(SootMethod method) {
        //Assume Soot scene is currently active
        SootClass klass = Scene.v().forceResolve(method.getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        return klass.getMethodByName(method.getName());
    }

    public static SootClass getResolvedClass(String className) {
        //Assume Soot scene is currently active
        SootClass klass = Scene.v().forceResolve(className,
                SootClass.SIGNATURES);
        return klass;
    }

    public static SootClass getResolvedClass(SootMethod method) {
        //Assume Soot scene is currently active
        return getResolvedClass(method.getDeclaringClass().getName());
    }

    public static boolean doesClassHasMember(SootClass klass, String name, Type type) {

        //TODO: Need to handle super class
        for (SootField fields:klass.getFields()){
            if ( fields.getName().equals(name) &&
                    !Modifier.isStatic(fields.getModifiers())
                    && fields.getType().toString().equals(fields.getType().toString())){
                return true;
            }
        }


        return false;
    }
}
