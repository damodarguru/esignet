package pages;

import base.BasePage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginOptionsPage extends BasePage {

	public LoginOptionsPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "signup-url-button")
	WebElement loginButton;

	@FindBy(xpath = "//img[@class='brand-logo']")
	WebElement brandLogo;

	@FindBy(id = "login_with_walletname")
	WebElement loginWithInji;

	@FindBy(id = "language_selection")
	WebElement languageDropdown;

	@FindBy(id = "hi1")
	WebElement hindiLanguage;

	@FindBy(id = "login_with_otp")
	WebElement loginWithOtpBtn;

	@FindBy(id = "login_with_bio")
	WebElement loginWithBiometricBtn;

	@FindBy(id = "login_with_walletname")
	WebElement loginWithInjiBtn;

	@FindBy(id = "login_with_pwd")
	WebElement loginWithPasswordBtn;

	@FindBy(id = "login_with_pin")
	WebElement loginWithPinBtn;

	@FindBy(id = "login_with_kbi")
	WebElement loginWithKbiBtn;

	@FindBy(id = "show-more-options")
	List<WebElement> moreWaysToSignIn;

	@FindBy(id = "mobile")
	WebElement mobileNumberOption;

	@FindBy(id = "nrc")
	WebElement nrcIdOption;

	@FindBy(id = "vid")
	WebElement vidOption;

	@FindBy(id = "email")
	WebElement emailOption;

	@FindBy(id = "back-button")
	WebElement backButton;

	@FindBy(id = "login-header")
	WebElement loginHeader;

	@FindBy(id = "login-subheader")
	WebElement loginSubHeader;

	@FindBy(xpath = "//div[contains(@class,'font-semibold') and contains(@class,'mx-2')]")
	WebElement selectPreferredIdHeader;

	@FindBy(id = "get_otp")
	WebElement getOtpButton;

	@FindBy(xpath = "//button[@id='mobile' and contains(@class,'selected_login_id')]")
	WebElement mobileSelected;

	@FindBy(id = "Otp_login_dropdown_button")
	WebElement prefixNumberField;

	@FindBy(id = "mobile_login_dropdown_button")
	WebElement passwordScreenMobilePrefixDropdownButton;

	@FindBy(id = "KHM")
	WebElement khmCountryCode;

	@FindBy(id = "IND")
	WebElement indCountryCode;

	@FindBy(id = "otp_verify_input")
	WebElement otpInputField;

	@FindBy(xpath = "//div[contains(@class,'header my-2')]")
	WebElement attentionScreen;

	@FindBy(id = "cancel-button")
	WebElement attentionCancelButton;

	@FindBy(id = "discontinue-button")
	WebElement attentionDiscontinueButton;

	@FindBy(id = "Otp_vid")
	WebElement vidField;

	@FindBy(id = "error-banner-message")
	WebElement invalidIndividualIdErrorMessage;

	@FindBy(id = "Otp_email")
	WebElement emailField;

	@FindBy(id = "Password_vid")
	WebElement vidPasswordField;

	@FindBy(id = "verify_password")
	WebElement passwordLoginButton;

	public boolean isLogoDisplayed() {
		return isElementVisible(brandLogo, "Verified is logo displayed");
	}

	public void clickOnLoginWithInji() {
		clickOnElement(loginWithInji, "Clicked on login with inji");
	}

	public boolean isLanguageDropdownDisplayed() {
		return isElementVisible(languageDropdown, "Verified language dropdown is visible");
	}

	public void clickOnLanguageDropdown() {
		clickOnElement(languageDropdown, "Clicked on language dropdown");
	}

	public void clickOnHindiLanguage() {
		clickOnElement(hindiLanguage, "Selected hindi language from dropdown");
	}

	public boolean isSelectedLanguageDisplayed() {
		return isElementVisible(loginWithOtpBtn, "Verified selected language displayed");
	}

	public boolean isLoginWithBiometicDisplayed() {
		return isElementVisible(loginWithBiometricBtn, "Verified login with biometric button is displayed");
	}

	public boolean isLoginWithInjiDisplayed() {
		return isElementVisible(loginWithInjiBtn, "Verified login with inji button is displayed");
	}

	public boolean isLoginWithPasswordDisplayed() {
		return isElementVisible(loginWithPasswordBtn, "Verified login with password button is displayed");
	}

	public List<WebElement> getLoginOptions() {
		List<WebElement> options = new ArrayList<>();
		options.add(loginWithOtpBtn);
		options.add(loginWithBiometricBtn);
		options.add(loginWithInjiBtn);
		options.add(loginWithPasswordBtn);
		options.add(loginWithPinBtn);
		options.add(loginWithKbiBtn);
		return options;
	}

	public boolean isMoreWaysToSignInOptionDisplayed() {
		return !moreWaysToSignIn.isEmpty() && moreWaysToSignIn.get(0).isDisplayed();
	}

	public Map<String, WebElement> getAcrToElementMap() {
		Map<String, WebElement> map = new HashMap<>();
		map.put("PWD", loginWithPasswordBtn);
		map.put("OTP", loginWithOtpBtn);
		map.put("BIO", loginWithBiometricBtn);
		map.put("WLA", loginWithInjiBtn);
		map.put("PIN", loginWithPinBtn);
		map.put("KBI", loginWithKbiBtn);
		return map;
	}

	public void selectLanguage(String language) {
		WebElement langOption = driver
				.findElement(By.xpath("//div[@role='menuitem' and normalize-space()='" + language + "']"));

		langOption.click();
	}

	public boolean isUILanguageChanged(String text) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.textToBePresentInElement(loginButton, text));
		return loginButton.getText().contains(text);
	}

	public WebElement getLoginWithOtpButton() {
		return loginWithOtpBtn;
	}

	public boolean isMobileNumberOptionDisplayed() {
		return isElementVisible(mobileNumberOption, "Verified mobile number option is displayed for authentication");
	}

	public void clickOnMobileNumberOption() {
		clickOnElement(mobileNumberOption, "Clicked on mobile number option");
	}

	public void clickOnPasswordScreenMobilePrefixDropdownButton() {
		clickOnElement(passwordScreenMobilePrefixDropdownButton,
				"Clicked on mobile prefix dropdown button on the password login screen");
	}

	/**
	 * Types text into the Mobile Number identifier field on the password login
	 * screen. Reuses the mobileNumberOption locator (id="mobile") - once the
	 * mobile option is selected, oidc-ui replaces that selection button with
	 * the identifier text input carrying the same id (see Password.js), so the
	 * same @FindBy proxy re-resolves to the input.
	 */
	public void typeIntoMobileNumberFieldOnPasswordScreen(String text) {
		mobileNumberOption.sendKeys(text);
	}

	public String getMobileNumberFieldValueOnPasswordScreen() {
		return mobileNumberOption.getAttribute("value");
	}

	public boolean isNrcIdOptionDisplayed() {
		return isElementVisible(nrcIdOption, "Verified nrc option is displayed for authentication");
	}

	public boolean isVidOptionDisplayed() {
		return isElementVisible(vidOption, "Verified vid option is displayed for authentication");
	}

	public boolean isEmailOptionDisplayed() {
		return isElementVisible(emailOption, "Verified email option is displayed for authentication");
	}

	/**
	 * Counts how many of the known login-ID-option buttons (mobile, nrc, vid,
	 * email) are currently rendered. Uses the non-waiting isElementDisplayed()
	 * check (not isMobileNumberOptionDisplayed() etc., which wait for
	 * visibility and would throw rather than return false for an option that
	 * is legitimately absent from the configured set).
	 */
	public int getDisplayedLoginIdOptionsCount() {
		int count = 0;
		if (isElementDisplayed(mobileNumberOption)) {
			count++;
		}
		if (isElementDisplayed(nrcIdOption)) {
			count++;
		}
		if (isElementDisplayed(vidOption)) {
			count++;
		}
		if (isElementDisplayed(emailOption)) {
			count++;
		}
		return count;
	}

	public boolean isBackButtonDisplayed() {
		return isElementVisible(backButton, "Verified back button is visible for return to Select a preferred mode");
	}

	public void clickOnBackButton() {
		clickOnElement(backButton, "Clicked on back button");
	}

	public void clickOnLoginWithBiometric() {
		clickOnElement(loginWithBiometricBtn, "Clicked on login with biometrics");
	}

	public void clickOnLoginWithPassword() {
		clickOnElement(loginWithPasswordBtn, "Clicked on login with password");
	}

	public void clickOnLoginWithKbi() {
		clickOnElement(loginWithKbiBtn, "Clicked on login with KBI");
	}

	public boolean isGetOtpButtonEnabled() {
		return isButtonEnabled(getOtpButton, "Verified get otp button is enabled");
	}

	public boolean isMobileNumberSelected() {
		return isElementVisible(mobileSelected, "Verified mobile number seleted in authentication screen");
	}

	public boolean isKhmCountryCodePrefixDisplayed() {
		return isElementVisible(khmCountryCode, "Verified khm country code prefix is displayed");
	}

	public boolean isIndCountryCodePrefixDisplayed() {
		return isElementVisible(indCountryCode, "Verified ind country code prefix is displayed");
	}

	public void clickOnPrefixNumberFieldButton() {
		clickOnElement(prefixNumberField, "Clicked on Prefix Number Field button");
	}

	public void clickOnIndCountryCodePrefix() {
		clickOnElement(indCountryCode, "Clicked on ind country code prefix button");
	}

	public void clickOnKhmCountryCodePrefix() {
		clickOnElement(khmCountryCode, "Clicked on khm country code prefix button");
	}

	public boolean isOtpInputFieldIsDisplayed() {
		return isElementVisible(otpInputField, "Verified otp input field is displayed");
	}

	public boolean isAttentionScreenIsDisplayed() {
		return isElementVisible(attentionScreen, "Verified attention screen is displayed");
	}

	public void clickOnAttentionCancelButton() {
		clickOnElement(attentionCancelButton, "Clicked on attention cancel button");
	}

	public void clickOnAttentionDiscontinueButton() {
		clickOnElement(attentionDiscontinueButton, "Clicked on attention discontinue button");
	}

	public void clickOnVidOptionButton() {
		clickOnElement(vidOption, "Clicked on vid option button");
	}

	public boolean isInvalidIndividualIdErrorMessageIsDisplayed() {
		return isElementVisible(invalidIndividualIdErrorMessage,
				"Verified invalid individual id error message is displayed");
	}

	public void enterVid(String vid) {
		vidField.clear();
		enterText(vidField, vid, "Entered vid in vid field");
	}

	public void clickOnEmailOptionButton() {
		clickOnElement(emailOption, "Clicked on email option button");
	}

	public void enterEmail(String email) {
		emailField.clear();
		enterText(emailField, email, "Entered email in email field");
	}

	/**
	 * Types the individual ID into the VID field on the password login screen.
	 * Reuses the vidOption locator (id="vid") - once the vid option is selected,
	 * oidc-ui replaces that selection button with the identifier text input
	 * carrying the same id (see Password.js), same pattern as
	 * typeIntoMobileNumberFieldOnPasswordScreen.
	 */
	public void typeIntoVidFieldOnPasswordScreen(String vid) {
		vidOption.sendKeys(vid);
	}

	public void enterPasswordForVidLogin(String password) {
		enterText(vidPasswordField, password, "Entered password on the vid password login screen");
	}

	public void clickOnPasswordLoginButton() {
		clickOnElement(passwordLoginButton, "Clicked on login button in password screen");
	}

}