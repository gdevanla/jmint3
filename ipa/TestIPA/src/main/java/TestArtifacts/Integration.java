package TestArtifacts;

public class Integration {
public static void main(String args[])
{
	int a,b,c,d,e,f,g;
	A a1 =new A();
	B b1=new B();
	boolean flagTest=true;
	b1.flag=true;
	a1.flag=false;
	//test 1
	/*a=a1.x;
	b=b1.y+5;
	d=a1.x+25;
	c=b+10;
	d=b1.y+95;
	e=11+20;
	c=d+87;
	f=a1.x+78;*/
	
	//test 2
    //a=a1.x;
	b=b1.y+5;
	d=b+25;
	c=d+10;
	//a1.x=b1.y;
	//f=c+29;
/*	f=21;//1
	c=f+19;//2
*/	//a=b1.process(c);//3

	//b1.y=a1.x+6;//4

    if(b1.flag && c >5)
	{
		
	a1.setSumOp(b1.getFlag(),b1.y,1);//5

	e=b1.process(a1.getResult());

	a1.DoOp(b1.getY(), b1.y);//6

	flagTest=b1.flag & a1.flag;

	a1.process();

	g=a1.x;

	b1.y=g;

	a1.setSumOp(flagTest,4,1);
	}


	/*int x,y,z;
	x=10;
	y=5;
	
	z=2;*/
	/*a1.setSumOp(b1.getFlag(),b1.process(),1);
	a1.DoOp(b1.getY(), 2);*/
	
}
}