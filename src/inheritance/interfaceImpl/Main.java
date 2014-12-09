package inheritance.interfaceImpl;

public class Main
{
	
	
	public static void main(String[] args)
	{
		Person person = new PersonImpl();
		person.setPersonName("bart");
		
		System.out.println(person.getPersonName());
		
	}

}
