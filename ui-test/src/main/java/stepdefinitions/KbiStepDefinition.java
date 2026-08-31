package stepdefinitions;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import base.BaseTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.KbiPage;
import pages.LoginOptionsPage;

public class KbiStepDefinition {

	public WebDriver driver;
	LoginOptionsPage loginOptionsPage;
	KbiPage kbiPage;

	public KbiStepDefinition(BaseTest baseTest) {
		this.driver = baseTest.getDriver();
		loginOptionsPage = new LoginOptionsPage(driver);
		kbiPage = new KbiPage(driver);
	}

	@When("user clicks on Login with KBI")
	public void userClicksOnLoginWithKbi() {
		loginOptionsPage.clickOnLoginWithKbi();
	}

	@Then("verify KBI form is displayed")
	public void verifyKbiFormIsDisplayed() {
		Assert.assertTrue(kbiPage.isKbiFormDisplayed(), "KBI form is not displayed");
	}

	@Then("verify the KBI schema fetch request required no authentication")
	public void verifyKbiSchemaFetchRequestUnauthenticated() {
		Assert.assertTrue(kbiPage.isKbiSchemaFetchUnauthenticatedAndSuccessful(),
				"KBI schema fetch request either required authentication or did not complete successfully");
	}

	@Then("verify KBI login button is disabled")
	public void verifyKbiLoginButtonIsDisabled() {
		Assert.assertFalse(kbiPage.isLoginButtonEnabled(), "KBI login button is enabled when mandatory fields are empty");
	}

	@And("user fills all mandatory fields in the KBI form")
	public void userFillsAllMandatoryFieldsInKbiForm() {
		kbiPage.fillKnownFieldsWithValidData();
	}

	@Then("verify KBI login button is enabled")
	public void verifyKbiLoginButtonIsEnabled() {
		Assert.assertTrue(kbiPage.isLoginButtonEnabled(), "KBI login button is not enabled when mandatory fields are filled");
	}

	@And("user clicks on the KBI login button")
	public void userClicksOnKbiLoginButton() {
		kbiPage.clickOnLoginButton();
	}

	@Then("verify KBI authentication is successful")
	public void verifyKbiAuthenticationIsSuccessful() {
		Assert.assertFalse(kbiPage.isKbiFormDisplayed(), "User is still on the KBI form after submitting valid details");
	}

	@Then("verify KBI page content is displayed in default English language")
	public void verifyKbiPageContentInDefaultLanguage() {
		Assert.assertTrue(kbiPage.isPageTextContains("Please fill the below details"),
				"KBI page subheading is not displayed in the default English language");
	}

	@Then("verify KBI login button text is displayed in default English language")
	public void verifyKbiLoginButtonTextInDefaultLanguage() {
		Assert.assertEquals(kbiPage.getLoginButtonText().trim(), "Login",
				"KBI login button text is not displayed in the default English language");
	}

	@Then("verify KBI form fields are empty")
	public void verifyKbiFormFieldsAreEmpty() {
		Assert.assertTrue(kbiPage.areKnownFieldsEmpty(), "KBI form fields were not reset to a fresh entry after reload");
	}

}
