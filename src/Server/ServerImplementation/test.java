package Server.ServerImplementation;

public class test {

	public static void main(String ...strings)
	  {
		
		DCMS_backup obj = new DCMS_backup();
		
		obj.backup("test.txt", "FirstLine");
		obj.backup("test.txt", "Line2");
		
	  }
	
}
