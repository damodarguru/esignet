package pages;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;
import utils.BaseTestUtil;
import utils.BaseTestUtil.CapturedRequest;

/**
 * Field-level locators here (individualId/mobile-number/fullName/dob) are
 * only confirmed against the environment where KBI is actually enabled -
 * they are NOT sourced from oidc-ui's code, since the KBI form is rendered
 * at runtime by an external package (@mosip/json-form-builder) not vendored
 * in this repo. If a different environment/schema is targeted, these will
 * need re-verifying against the live DOM.
 */
public class KbiPage extends BasePage {

	public KbiPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "form-container")
	WebElement kbiFormContainer;

	@FindBy(id = "individualId")
	WebElement policyNumberField;

	@FindBy(id = "mobile-number")
	WebElement mobileNumberField;

	@FindBy(id = "fullName")
	WebElement fullNameField;

	@FindBy(css = "input.real-date-input")
	WebElement dobRealInput;

	@FindBy(css = "#form-container button")
	WebElement loginButton;

	public boolean isKbiFormDisplayed() {
		return isElementVisible(kbiFormContainer, "Verified KBI form is displayed");
	}

	public void enterPolicyNumber(String value) {
		enterText(policyNumberField, value, "Entered policy number in KBI form");
	}

	public void enterMobileNumber(String value) {
		enterText(mobileNumberField, value, "Entered mobile number in KBI form");
	}

	public void enterFullName(String value) {
		enterText(fullNameField, value, "Entered full name in KBI form");
	}

	/**
	 * The visible dob input is a readonly display bound to a date picker; the
	 * actual submitted value lives in a hidden input[type=date] sibling. Set
	 * that directly via JS and dispatch input/change so any listeners pick it
	 * up, mirroring how SignupFormDynamicFiller handles the signup DOB field.
	 */
	public void enterDob(String yyyyMMdd) {
		waitForElementVisible(dobRealInput);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].value=arguments[1];"
				+ "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));"
				+ "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", dobRealInput, yyyyMMdd);
	}

	/** Fills the 4 known KBI fields with plausible placeholder values. */
	public void fillKnownFieldsWithValidData() {
		enterPolicyNumber("123456789");
		enterMobileNumber("9876543210");
		enterFullName("Test User");
		enterDob("1990-01-01");
	}

	public boolean areKnownFieldsEmpty() {
		return policyNumberField.getAttribute("value").isEmpty() && mobileNumberField.getAttribute("value").isEmpty()
				&& fullNameField.getAttribute("value").isEmpty();
	}

	public boolean isLoginButtonEnabled() {
		return isButtonEnabled(loginButton, "Verified KBI login button state");
	}

	public void clickOnLoginButton() {
		clickOnElement(loginButton, "Clicked on KBI login button");
	}

	public String getLoginButtonText() {
		return getText(loginButton, "Verified KBI login button text");
	}

	public boolean isPageTextContains(String expectedText) {
		return driver.getPageSource().contains(expectedText);
	}

	/**
	 * Confirms the oauth-details request carrying the KBI schema was captured,
	 * sent no Authorization header, and completed successfully. Capture must
	 * have been started before driver.get(authorizeUrl) via the @kbiSchemaFetch
	 * tag hook in BaseTest, since the request fires on initial page load. The
	 * request itself is async (React useEffect), so this polls briefly rather
	 * than assuming it's already landed by the time the step runs.
	 */
	public boolean isKbiSchemaFetchUnauthenticatedAndSuccessful() {
		List<CapturedRequest> requests = BaseTestUtil.getCapturedKbiSchemaFetchRequests();
		long deadline = System.currentTimeMillis() + 10000;
		while ((requests == null || requests.isEmpty()) && System.currentTimeMillis() < deadline) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			requests = BaseTestUtil.getCapturedKbiSchemaFetchRequests();
		}
		if (requests == null || requests.isEmpty()) {
			return false;
		}
		return requests.stream().allMatch(request -> {
			boolean hasAuthHeader = request.headers.keySet().stream()
					.anyMatch(key -> key.equalsIgnoreCase("Authorization"));
			boolean successful = request.statusCode >= 200 && request.statusCode < 300;
			return !hasAuthHeader && successful;
		});
	}

}
