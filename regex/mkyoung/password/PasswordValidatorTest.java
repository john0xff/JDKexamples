package mkyoung.password;

import org.testng.Assert;
import org.testng.annotations.*;

/**
 * Password validator Testing
 * 
 * @author mkyong
 *
 */
public class PasswordValidatorTest
{

	private PasswordValidator passwordValidator;

	@BeforeClass
	public void initData()
	{
		passwordValidator = new PasswordValidator();
	}

	@DataProvider
	public Object[][] ValidPasswordProvider()
	{
		return new Object[][]
		{
		{ new String[]
		{ "mkyong1A@", "mkYOn12$", } } };
	}

	@DataProvider
	public Object[][] InvalidPasswordProvider()
	{
		return new Object[][]
		{
		{ new String[]
		{ "mY1A@", "mkyong12@", "mkyoNg12*", "mkyonG$$", "MKYONG12$" } } };
	}

	@Test(dataProvider = "ValidPasswordProvider")
	public void ValidPasswordTest(String[] password)
	{

		for (String temp : password)
		{
			boolean valid = passwordValidator.validate(temp);
			System.out.println("Password is valid : " + temp + " , " + valid);
			Assert.assertEquals(true, valid);
		}

	}

	@Test(dataProvider = "InvalidPasswordProvider", dependsOnMethods = "ValidPasswordTest")
	public void InValidPasswordTest(String[] password)
	{

		for (String temp : password)
		{
			boolean valid = passwordValidator.validate(temp);
			System.out.println("Password is valid : " + temp + " , " + valid);
			Assert.assertEquals(false, valid);
		}
	}
}