package TestArtifacts;

public class B {

	boolean flag= false;
	int y=3;
	public int process(int y1)
	{
		int a1=4;
		y=a1+37;
		A a=new A();
		boolean r=true;
		flag=a.getFlag();
		if(r)
		{
			flag=false;
		}
		else
		{
			flag=true;
		}
		
		int x=y*47;
		return y1;
	}
	
	public int getY()
	{
		return y;
	}
	
	public boolean getFlag()
	{
		return flag;
	}
}
