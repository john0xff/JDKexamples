package inheritance.interfaceImpl;

public class PersonImpl implements Person
{
	private String personName;
	
	@Override
	public void setPersonName(String personName)
	{
		this.personName = personName;
	}

	@Override
	public String getPersonName()
	{
		return this.personName;
	}

}
