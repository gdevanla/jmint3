package jmint;


import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.tagkit.Tag;
import soot.util.Chain;

import java.util.List;

public class SUtil {

    public static boolean isTypeIncludedInAnalysis(RefType r){
        for ( String s:Configuration.packageUnderTest){
            if ( r.toString().startsWith(s))
                return true;
        }
        return false;
    }

    public static boolean eqAsStr(Object o1, Object o2){
       return o1.toString().equals(o2.toString());
    }

    public static boolean isClassIncludedInAnalysis(SootClass r){
        for ( String s:Configuration.packageUnderTest){
            if (r.getPackageName().startsWith(s)){
                return true;
            }
        }
        return false;
    }


    public static boolean isSpecialInvokeExprOnValue(Value r, Unit u){
        if ( (u instanceof InvokeStmt))
        {
            if (((InvokeStmt)u).getInvokeExpr() instanceof SpecialInvokeExpr){
                SpecialInvokeExpr expr = (SpecialInvokeExpr) ((InvokeStmt)u).getInvokeExpr();

                if (expr.getBase().equals(r)){
                    return true;
                }
            }
        }
        return false;
    }



    public static Unit getUnitInvokingDefaultConstructor(Value r, SootMethod method){
        for (Unit u:getResolvedMethod(method).getActiveBody().getUnits()){
            if (isSpecialInvokeExprOnValue(r, u))
            {
                if ( isUnitInvokingDefaultConstructor(u)){
                    return u;
                }
            }
        }
        return null;
    }

    private static boolean isUnitInvokingDefaultConstructor(Unit u) {
        return ((InvokeStmt)u).getInvokeExpr().getArgCount() == 0;
    }

    public static Unit getUnitInvokingAnConstructor(Value r, SootMethod method){
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
        //System.out.println(method.getName() + ":" + method.getSignature() + ":" + method.getSubSignature());
        return klass.getMethod(method.getSubSignature());
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

    /* This method is used to get around Unit in Soot not implementing hashcode or equals method
    properly.
     */
    public static boolean AreTheseObjectEqualAsStrings(Object o1, Object o2) {
        if (o1.toString().equals(o2.toString())) return true;
        else return false;
    }

    //Careful using this. Use it if you know the calls are scoped to the same methods
    //where l and u belong to.
    public static boolean DoesUnitUseThisLocalAsString(Unit u, Local l) {
        List<ValueBox> boxes = u.getUseBoxes();
        for (ValueBox b:boxes){
            if (b.getValue().toString().equals(l.toString())) return true;
        }
        return false;
    }

    public static Local getEquivalentLocal(Unit u, Local l) {
        List<ValueBox> boxes = u.getUseAndDefBoxes();
        for (ValueBox b:boxes){
            if (b.getValue().hashCode() == l.hashCode()){
                return (Local)b.getValue();
            }
        }
        return null;

    }

    public static boolean isThisMethodInvoked(JAssignStmt def, SootMethod getterMethod) {
        return def.containsInvokeExpr() &&
                ((InvokeExpr)def.getRightOp()).getMethod().getSignature().equals(getterMethod.getSignature());
    }

    public static boolean areOtherGetterMethodsAvailable(SootClass declaringClass, SootMethod excludeMethod) {
        List<SootMethod> methods = declaringClass.getMethods();
        for (SootMethod method: methods){
            if (method.getParameterCount() == 0 &&
                    method.getName().startsWith("get")
                    && !method.getName().equals(excludeMethod.getName()) && !method.isAbstract()
                    && method.getReturnType().toString().equals(excludeMethod.getReturnType().toString())){
                return true;
            }
        }

        return false;
    }

    public static String getTagOrDefaultValue(Tag tag, String defaultValue){
        if (tag == null)
            return defaultValue;
        else
            return tag.toString();
    }


    public static Unit getSpecialInvokeToBaseClassNonDefaultConstructor(SootMethod initMethod) {
        PatchingChain<Unit> units = initMethod.getActiveBody().getUnits();

        for ( Unit u:units){
            if (u instanceof InvokeStmt){
                if (((InvokeStmt)u).getInvokeExpr() instanceof SpecialInvokeExpr){
                    SpecialInvokeExpr expr =  (SpecialInvokeExpr)((InvokeStmt)u).getInvokeExpr();
                    if (expr.getMethod().getDeclaringClass().equals(initMethod.getDeclaringClass().getSuperclass())
                            && expr.getMethod().getName().contains("<init>") && expr.getArgCount() > 0){
                        return u;
                    }
                }
            }
        }

        return null;
    }
}
