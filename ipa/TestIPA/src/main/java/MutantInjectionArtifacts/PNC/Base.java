package MutantInjectionArtifacts.PNC;

class Base
{
    public int x;

    public Base(int y){
        x = (int)Math.random();
    }

    public int doSomethingElse(){
        return (int)Math.random();
    }

}
