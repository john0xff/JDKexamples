package inheritance.abstractImpl2;

public class MainAbst2
{
	
	
	public static void main(String[] args)
	{
		PersonAbstract2 person = new PersonExtended2();
		
		person.setPersonName("bart ext 2");
		
		System.out.println(person.getPersonName());
		
	}

}
