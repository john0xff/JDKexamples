package reference.passByValue;

class Dog
{
	String dogsName;

	public Dog(String dogsName)
	{
		this.dogsName = dogsName;
	}

	public String getName()
	{
		return dogsName;
	}

	public void setName(String dogsName)
	{
		this.dogsName = dogsName;
	}
}

public class Test2
{
	public static void changeName(Dog dog)
	{
		dog = new Dog("aaa");
		dog.setName("New Name");
	}
	
	public static void main(String[] args)
	{
		// ---------- test1
		Dog dog = new Dog("Max");
		
		System.out.println(dog.getName());
		
		changeName(dog);
		
		System.out.println(dog.getName());
		
		
	}
}
