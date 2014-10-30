package hashCode;


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
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}

public class HashCode
{
	public static void changeName(Dog dog)
	{
		dog.setName("test2"); // test3.Dog@3b95a09c
		System.out.println(dog.hashCode()); // 366712642
		
		dog = new Dog("test3"); // test3.Dog@1a93a7ca
		System.out.println(dog.hashCode()); // 1829164700
		
		dog.setName("test4");
		System.out.println(dog.hashCode()); // 1829164700
	}
	
	public static void main(String[] args)
	{
		// ---------- test1
		Dog dog = new Dog("Max"); // test3.Dog@3b95a09c
		System.out.println(dog.hashCode()); // hashCode.Dog@3b95a09c = 366712642
		System.out.println(dog.getName()); // Max
		
		changeName(dog);
		
		System.out.println(dog.getName()); // test3.Dog@3b95a09c 
		
		// ---------- test2
		System.out.println(dog.hashCode()); // hashCode.Dog@3b95a09c = 366712642
		
		Dog dog2 = new Dog("Frodo");
		System.out.println(dog2.hashCode()); // hashCode.Dog@3d82c5f3 = 2018699554
		
		Dog dog3 = new Dog("Max");
		System.out.println(dog3.hashCode()); // hashCode.Dog@2b05039f = 1311053135
		System.out.println(dog3.hashCode());
		
		System.out.println(dog.hashCode()); 
	}
}
