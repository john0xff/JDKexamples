package passByValue2;


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

public class Test3
{
	public static void changeName(Dog dog)
	{
		dog.setName("test2"); // test3.Dog@3b95a09c
		dog = new Dog("test3"); // test3.Dog@1a93a7ca
		dog.setName("test4");
	}
	
	public static void main(String[] args)
	{
		// ---------- test1
		Dog dog = new Dog("Max"); // test3.Dog@3b95a09c
		System.out.println(dog.hashCode());
		System.out.println(dog.getName());
		
		changeName(dog);
		
		System.out.println(dog.getName()); // test3.Dog@3b95a09c
		
		// ---------- test2
		
	}
}
