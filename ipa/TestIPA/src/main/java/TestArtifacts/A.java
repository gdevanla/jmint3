package TestArtifacts;

public class A {
	int result;
	int x=1;
	boolean flag = false;

	public void DoOp(int a, int b) {
		if (flag) {
			result = a + b;
		} else {
			result = a - b;

		}
	}

	public int getResult() {
		return result;
	}
	
	public void process()
	{
		int y=10;
		invoke();
	}
	
	public void invoke()
	{
		B b=new B();
		b.process(x);
	}
	
	public void setSumOp(boolean f,int e,float g) {
		flag = f;
		int a,b;
		a=x+5;
		b=10;
		x=e+15;
		result=x+9;
	}

	public int doubleit(int a) {
		return a * 2;
	}
	
	public boolean getFlag()
	{
		return flag;
	}
}
