package jmint;


import com.google.common.collect.ConcurrentHashMultiset;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.util.Chain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

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

    public static Set<Unit> findUnitInvokingOverloadedMethods(UseDefChain udChain, SootMethod method){
        Set<Unit> mutableUnits = new HashSet<Unit>();
        //assert(udChain.useValue instanceof Local);
        if (!(udChain.useValue instanceof Local)){
            System.out.println("useValue other than Local found=" + udChain.useUnit + ":" + udChain.useValue.getClass());
            return mutableUnits;
        }

        Local l = (Local) udChain.useValue;
        PatchingChain<Unit> units = SUtil.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        PatchingChain<Unit> units2 = SUtil.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        SimpleLocalDefs localDefs = new SimpleLocalDefs(
                new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                        udChain.getUseMethod()).getActiveBody()));

        for (Unit u:units){
            // I hope this is not bad. Unit.equals Soot is mysteriously missing
            if ( u.equals(udChain.useUnit)
                    && SUtil.DoesUnitUseThisLocalAsString(u, l)){
                Local equivLocal = SUtil.getEquivalentLocal(u, l);
                if ( localDefs.hasDefsAt(equivLocal, u)){
                    for (Unit def:localDefs.getDefsOfAt(equivLocal, u)){
                        if (def instanceof JAssignStmt &&
                                ((JAssignStmt) def).containsInvokeExpr() &&
                                SUtil.isThisMethodOverloaded((((JAssignStmt) def).getInvokeExpr()))){
                            if (! mutableUnits.contains(def)){
                                mutableUnits.add(def);
                            }
                            else
                            {
                                //System.out.println(def);
                            }
                        }
                    }
                }
            }
        }
        return mutableUnits;

    }

    private static boolean isThisMethodOverloaded(InvokeExpr invokeExpr) {
        SootClass klass = SUtil.getResolvedClass(invokeExpr.getMethod().getDeclaringClass().toString());
        SootMethod method = invokeExpr.getMethod();

        for(SootMethod m : klass.getMethods()){
            if (m.getName().equals(method.getName()) && !m.getSubSignature().equals(method.getSubSignature())){
                return true; //we have found atleast one overloaded method
            }
        }
        return false;
    }

    public static Multiset<Type> getTypesInMethod(SootMethod method){
        Multiset<Type> argumentTypes = HashMultiset.create();
        for (Type t:method.getParameterTypes()){
            argumentTypes.add(t);
        }
        return argumentTypes;
    }

    public static boolean isTypeListASubset(Multiset<Type> t, Multiset<Type> ts  ){

        //TODO: Check for base/sub class relationships.
        for (Type type:ts){
            if (!t.contains(type) || ts.count(type) != t.count(type)){
                return false;
            }
        }
        return true;

    }

    public static boolean isAlternateMethodAvail(InvokeExpr expr){

        SootMethod method = expr.getMethod();
        SootClass klass = expr.getMethod().getDeclaringClass();

        Multiset<Type> origTypes = getTypesInMethod(expr.getMethod());

        for (SootMethod m:klass.getMethods()){
            Multiset<Type> types = getTypesInMethod(m);
            if ( !m.equals(method)
                    && m.getReturnType().equals(method.getReturnType())
                    && m.getName().equals(method.getName())
                    && isTypeListASubset(origTypes, types)){
                System.out.println("Overloaded method=" + m + "can be substituted for " + method);
                return true;
            }
        }
        return false;
    }

    public static Set<SootClass> getSubClassesWithDefaultConstructor(Type type) {
        Set<SootClass> classes =  new HashSet<SootClass>();
        Collection<SootClass> subClasses = Scene.v().getFastHierarchy().
                getSubclassesOf(SUtil.getResolvedClass(type.toString()));

        //check if atleast one subClass has a default constructor
        for (SootClass c:subClasses){
            for (SootMethod m:c.getMethods()){
                if ( m.getName().equals("<init>") && m.getParameterCount() == 0){
                    classes.add(c);
                }
            }
        }

        return classes;
    }


    public static Set<Unit> findAssignStmtsWithInvoke(UseDefChain udChain, SootMethod getterMethod){
        Set<Unit> mutableUnits = new HashSet<Unit>();
        //assert(udChain.useValue instanceof Local);
        if (!(udChain.useValue instanceof Local)){
            System.out.println("useValue other than Local found=" + udChain.useUnit + ":" + udChain.useValue.getClass());
            return mutableUnits;
        }

        Local l = (Local) udChain.useValue;
        PatchingChain<Unit> units = SUtil.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        PatchingChain<Unit> units2 = SUtil.getResolvedMethod(
                udChain.getUseMethod()).getActiveBody().getUnits();

        SimpleLocalDefs localDefs = new SimpleLocalDefs(
                new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                        udChain.getUseMethod()).getActiveBody()));

        for (Unit u:units){
            if ( u.equals(udChain.useUnit)
                    && SUtil.DoesUnitUseThisLocalAsString(u, l)){
                //Local equivLocal = SUtil.getEquivalentLocal(u, l);
                if ( localDefs.hasDefsAt(l, u)){
                    for (Unit def:localDefs.getDefsOfAt(l, u)){
                        if (def instanceof JAssignStmt &&
                                ((JAssignStmt) def).containsInvokeExpr() &&
                                areParametersGeneralizable(((JAssignStmt) def).getInvokeExpr())){

                            if (! mutableUnits.contains(def)){
                                mutableUnits.add(def);
                            }
                            else
                            {
                                //System.out.println(def);
                            }
                        }
                    }
                }
            }
        }
        return mutableUnits;

    }

    public static boolean areParametersGeneralizable(InvokeExpr invokeExpr) {
        if (!(invokeExpr instanceof JVirtualInvokeExpr)){
            return false;
        }

        JVirtualInvokeExpr expr = (JVirtualInvokeExpr) invokeExpr;
        SootClass klass = SUtil.getResolvedClass(expr.getBase().getType().toString());
        SootMethod method = expr.getMethod();
        List<Type> parameterTypes = method.getParameterTypes();

        for (Type t:parameterTypes){
            SootClass c = SUtil.getResolvedClass(t.toString());
            if (c.hasSuperclass()) return true;
        }

        return false;
    }


    public static Set<Pair<Type, Type>> getGeneralizableParameters(InvokeExpr invokeExpr) {
        Set<Pair<Type, Type>> params = new HashSet<Pair<Type, Type>>();
        if (!(invokeExpr instanceof JVirtualInvokeExpr)){
            return params;
        }

        JVirtualInvokeExpr expr = (JVirtualInvokeExpr) invokeExpr;
        SootClass klass = SUtil.getResolvedClass(expr.getBase().getType().toString());
        SootMethod method = expr.getMethod();
        List<Type> parameterTypes = method.getParameterTypes();

        for (Type t:parameterTypes){
            SootClass c = SUtil.getResolvedClass(t.toString());
            if (c.hasSuperclass())
            {
             params.add(new Pair<Type, Type>(c.getType(), c.getSuperclass().getType()));
            }
        }

        return params;
    }


    public static boolean isTypeIncludedInAnalysis(Type t) {
        for ( String s:Configuration.packageUnderTest){
            if ( t.toString().startsWith(s))
                return true;
        }
        return false;
    }

    public static Set<Type> getAllTypesInLocal(Chain<Local> locals, boolean filterByAnalyzedPackage) {
        Set<Type> types = new HashSet<Type>();

        for(Local l:locals){
            if (filterByAnalyzedPackage && !SUtil.isTypeIncludedInAnalysis(l.getType())) continue;
            else types.add(l.getType());

        }
        return types;

    }

    public static boolean doTypesShareParentClass(Type t1, Type t2) {

        SootClass c1 = SUtil.getResolvedClass(t1.toString());
        SootClass c2 = SUtil.getResolvedClass(t2.toString());

        if (!c1.hasSuperclass()) return false;
        if (!c2.hasSuperclass()) return false;
        if (c1.equals(c2)) return false;

        if ( c1.getSuperclass().equals(c2.getSuperclass())){
            return true;
        }

        return false;
    }

    public static boolean areOtherSetterMethodsAvailable(SootClass declaringClass, SootMethod excludeMethod) {
        List<SootMethod> methods = declaringClass.getMethods();

        Multiset<Type> typesInExcludeMethod = HashMultiset.create();
        typesInExcludeMethod.addAll(excludeMethod.getParameterTypes());

        for (SootMethod method: methods){
            Multiset<Type> typesInMethod = HashMultiset.create();
            typesInMethod.addAll(method.getParameterTypes());

            if (method.getName().startsWith("set")
                    && !method.getName().equals(excludeMethod.getName()) && !method.isAbstract()
                    && method.getReturnType().toString().equals(excludeMethod.getReturnType().toString())
                    && isTypeListASubset(typesInExcludeMethod, typesInMethod)){
                return true;
            }
        }

        return false;
    }
}


