package pages;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.BaseTestUtil;
import utils.ClaimsUtil;
import utils.EsignetConfigManager;
import utils.LanguageUtil;

public class ConsentPage extends BasePage {

	public ConsentPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "login_with_otp")
	WebElement loginWithOtpButton;

	@FindBy(xpath = "//img[@class='brand-logo']")
	WebElement brandLogo;

	@FindBy(xpath = "//img[@class='brand-logo']/following-sibling::img")
	WebElement esignetLogo;

	@FindBy(xpath = "//*[text()='Network Error!']")
	WebElement networkErrorHeader;

	@FindBy(xpath = "//*[text()='Please check your internet connection and try again.']")
	WebElement networkErrorSubHeader;

	@FindBy(id = "language_selection")
	WebElement languageSelection;

	@FindBy(id = "Otp_IND")
	WebElement mobileNumberField;

	@FindBy(id = "get_otp")
	WebElement getOtpButton;

	@FindBy(xpath = "//div[@class='pincode-input-container']/input")
	List<WebElement> otpInputFields;

	@FindBy(id = "verify_otp")
	WebElement verifyOtpButton;

	@FindBy(id = "navbar-header")
	WebElement proceedToAttentionScreen;

	@FindBy(id = "proceed-button")
	WebElement proceedButtonInAttentionPage;

	@FindBy(xpath = "//button[contains(@class,'inline-flex items-center justify-center')][2]")
	WebElement proceedButton;

	@FindBy(id = "mock-identity-verifier")
	WebElement eKycServiceProvider;

	@FindBy(id = "proceed-preview-button")
	WebElement proceedButtonInServiceProviderPage;

	@FindBy(id = "consent-button")
	WebElement termsAndConditionCheckBox;

	@FindBy(id = "proceed-tnc-button")
	WebElement proceedBtnInTandCPage;

	@FindBy(id = "proceed-preview-button")
	WebElement proceedBtnInCameraPreviewPage;

	@FindBy(id = "language_selection")
	WebElement languageDropdown;

	@FindBy(id = "ar2")
	WebElement arabicLanguage;

	@FindBy(xpath = "//div[@class='h-screen']")
	WebElement rootContainer;

	@FindBy(xpath = "//label[@for='voluntary_claims']")
	WebElement voluntaryClaimsMasterToggle;

	@FindBy(xpath = "//input[@type='checkbox' and contains(@class, 'sr-only peer')]")
	List<WebElement> voluntaryClaimsSubToggles;

	@FindBy(xpath = "//input[@id='voluntary_claims']")
	WebElement voluntaryClaimsMasterCheckbox;

	@FindBy(xpath = "(//div[@class='divide-y'])[1]//li//div[contains(@class,'justify-start')]//label")
	List<WebElement> mandatoryClaimsElements;

	@FindBy(xpath = "(//div[@class='divide-y'])[2]/ul/li")
	List<WebElement> voluntaryClaimsElements;

	@FindBy(xpath = "//button[@id='essential_claims_tooltip']/following::div[@class='divide-y'][1]//label")
	private List<WebElement> essentialClaims;

	@FindBy(xpath = "//button[@id='voluntary_claims_tooltip']/following::div[@class='divide-y'][1]//label")
	private List<WebElement> voluntaryClaims;

	@FindBy(id = "continue")
	WebElement allowButtonInConsentScreen;

	@FindBy(xpath = "//p[@class='font-bold consent-timer-text']")
	WebElement consentTimer;

	@FindBy(xpath = "//div[@role='menuitem']")
	List<WebElement> languageDropdownItems;

	@FindBy(id = "continue")
	WebElement allowButton;

	@FindBy(xpath = "//div[@class=' css-1dimb5e-singleValue']")
	WebElement selectedLanguageDropdown;

	@FindBy(xpath = "//button[contains(@class,'flex items-center px-4')]")
	WebElement profileDropdown;

	@FindBy(xpath = "(//div[@class='font-semibold'])[1]")
	WebElement essentialClaimsHeader;

	@FindBy(xpath = "(//div[@class='divide-y'])[1]")
	WebElement essentialClaimsList;

	@FindBy(xpath = "//p[@class='text-[#4E4E4E] font-semibold']")
	WebElement actionMessage;

	@FindBy(id = "login-header")
	WebElement loginTitle;

	@FindBy(id = "login-subheader")
	WebElement loginSubTitle;

	@FindBy(xpath = "//h1[@class='text-base leading-5 font-sans font-medium my-2']")
	WebElement selectPreferredModeHeader;

	@FindBy(xpath = "//div[@class='inline mx-2 font-semibold my-3']")
	WebElement selectPreferredIdHeader;
	@FindBy(xpath = "//div[@class='header my-2']")
	WebElement headerInConsentUpdateProfileScreen;

	@FindBy(xpath = "//p[@class='sub-header m-0 mt-1 md:mx-5 md:mb-1 md:mt-3']")
	WebElement subHeaderInConsentUpdateProfileScreen;

	@FindBy(xpath = "(//div[@class='font-semibold mb-1'])[1]")
	WebElement essentialClaimHeaderInConsentUpdateProfileScreen;

	@FindBy(xpath = "(//div[@class='font-semibold mb-1'])[2]")
	WebElement voluntaryClaimHeaderInConsentUpdateProfileScreen;

	@FindBy(xpath = "(//*[name()='svg' and contains(@class,'cursor-pointer')])[1]")
	WebElement essentialInfoIconInConsentUpdateProfileScreen;

	@FindBy(xpath = "(//*[name()='svg' and contains(@class,'cursor-pointer')])[2]")
	WebElement voluntaryInfoIconInConsentUpdateProfileScreen;

	@FindBy(id = "cancel-button")
	WebElement cancelButtonInConsentUpdateProfileScreen;

	@FindBy(xpath = "(//div[@class='divide-y'])[2]")
	WebElement voluntaryClaimsList;

	@FindBy(xpath = "//span[@class='available-claim']")
	WebElement availableClaimsStatus;

	@FindBy(xpath = "//span[@class='not-available-claim']")
	WebElement notAvailableClaimStatus;

	/**
	 * Locates the "(Verified)"/"(Not Verified)" <em> that ClaimDetails.js
	 * (oidc-ui) renders as a sibling of the claim's label <span>, keyed off the
	 * claim's exact display label (e.g. "Email Address" for the email claim -
	 * see oidc-ui/public/locales/en.json's consentDetails section).
	 */
	private WebElement getClaimVerifiedStatusElement(String claimLabel) {
		return driver.findElement(By.xpath("//label[span[text()='" + claimLabel + "']]/em"));
	}

	/**
	 * Confirms the "Consent to Update Profile" screen shows "(Verified)" - not
	 * "(Not Verified)" - next to the given claim's label.
	 */
	public boolean isClaimShownAsVerified(String claimLabel) {
		WebElement statusElement = getClaimVerifiedStatusElement(claimLabel);
		waitForElementVisible(statusElement);
		String text = statusElement.getText().trim();
		return text.equalsIgnoreCase("(Verified)");
	}

	/**
	 * Confirms the "Consent to Update Profile" screen shows "(Not Verified)"
	 * next to the given claim's label.
	 */
	public boolean isClaimShownAsNotVerified(String claimLabel) {
		WebElement statusElement = getClaimVerifiedStatusElement(claimLabel);
		waitForElementVisible(statusElement);
		String text = statusElement.getText().trim();
		return text.equalsIgnoreCase("(Not Verified)");
	}

	@FindBy(xpath = "(//p[@class='mb-1'])[1]")
	WebElement infoIconMeassage;

	@FindBy(xpath = "//div[@class='message mx-0 px-2 mt-2 md:mx-5']")
	WebElement messageAboveProceedBtn;

	@FindBy(xpath = "//div[@class='relative text-center text-dark font-semibold text-xl text-[#2B3840] mt-9']")
	WebElement attentionHeaderInWarningPopup;

	@FindBy(xpath = "//p[@class='text-base text-[#707070]']")
	WebElement subHeaderInWarningPopup;

	@FindBy(id = "stay-button")
	WebElement stayButtonInConsentUpdateProfileScreen;

	@FindBy(id = "discontinue-button")
	WebElement discontinueButtonInConsentUpdateProfileScreen;

	public void clickOnLoginWithOtp() {
		clickOnElement(loginWithOtpButton, "Clicked on login with Otp button");
	}

	public void enterRegisteredMobileNumber(String number) {
		mobileNumberField.clear();
		enterText(mobileNumberField, number, "Entered registered mobile number");
	}

	public void clickOnGetOtp() {
		clickOnElement(getOtpButton, "Clicked on get otp button");
	}

	public String getCurrentLanguage() {
		return languageSelection.getText().trim();
	}

	public void enterOtp(String otp) {
		if (otp.length() > otpInputFields.size()) {
			throw new IllegalArgumentException(
					"OTP length " + otp.length() + " exceeds rendered inputs " + otpInputFields.size());
		}
		for (WebElement field : otpInputFields) {
			field.click();
			field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			field.sendKeys(Keys.BACK_SPACE);
		}
		for (int i = 0; i < otp.length(); i++) {
			WebElement field = otpInputFields.get(i);
			field.click();
			field.sendKeys(String.valueOf(otp.charAt(i)));
		}
	}

	public void clickOnVerifyButton() {
		clickOnElement(verifyOtpButton, "Clicked on verify otp button");
	}

	public boolean isOnAttentionScreen() {
		return isElementVisible(proceedToAttentionScreen, "Verified user is on attention screen");
	}

	public void clickOnProceedButtonInAttentionPage() {
		clickOnElement(proceedButtonInAttentionPage, "Clicked on Procced button in attention screen");
	}

	public void clickOnProceedButton() {
		clickOnElement(proceedButton, "Clicked on proceed button");
	}

	public void clickOnMockIdentifyVerifier() {
		clickOnElement(eKycServiceProvider, "Selected the ekyc provider");
	}

	public boolean isOnEkycProviderListScreen() {
		return isElementVisible(eKycServiceProvider, "Verified user is on the eKYC provider list screen");
	}

	public void clickOnProceedButtonInServiceProviderPage() {
		clickOnElement(proceedButtonInServiceProviderPage, "clicked on proceed button in ekyc screen");
	}

	public void checkTermsAndCondition() {
		if (!termsAndConditionCheckBox.isSelected()) {
			clickOnElement(termsAndConditionCheckBox, "Selected the terms and condition checkbox");
		}
	}

	public void clickOnProceedButtonInTermsAndConditionPage() {
		clickOnElement(proceedBtnInTandCPage, "Clicked on proceed button in terms and condition screen");
	}

	public void clickOnProceedButtonInCameraPreviewPage() {
		clickWhenClickable(proceedBtnInCameraPreviewPage);
	}

	public void waitUntilLivenessCheckCompletes() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
		wait.until(ExpectedConditions.visibilityOf(allowButtonInConsentScreen));
	}

	private void clickWhenClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		WebElement stableElement = wait
				.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(element)));
		stableElement.click();
	}

	public boolean isConsentScreenVisible() {
		return isElementVisible(allowButton, "Verified is navigated to consent scrren");
	}

	public boolean isVoluntaryClaimsMasterToggleVisible() {
		return voluntaryClaimsElements.size() > 1
				&& isElementVisible(voluntaryClaimsMasterToggle, "Verified voluntary claims master toggle button");
	}

	public WebElement getVoluntaryClaimsMasterToggle() {
		return voluntaryClaimsMasterToggle;
	}

	public List<WebElement> getVoluntaryClaimsSubToggles() {
		return voluntaryClaimsSubToggles;
	}

	public void enableVoluntaryClaimsMasterToggle() {
		if (!voluntaryClaimsMasterToggle.isSelected()) {
			clickOnElement(voluntaryClaimsMasterToggle, "Enabled the voluntary claims master toggle button");
		}
	}

	public void disableVoluntaryClaimsMasterToggle() {
		clickOnElement(voluntaryClaimsMasterToggle, "Disabled the voluntary claims master toggle button ");
	}

	public boolean isVoluntaryClaimsMasterToggleSelected() {
		return voluntaryClaimsMasterCheckbox.isSelected();
	}

	public String getVoluntaryClaimsTooltipText() {
		return getTooltipText(By.id("voluntary_claims_tooltip"), By.xpath("//div[contains(@class,'react-tooltip')]"));
	}

	public void toggleVoluntaryClaim(String claimName, boolean enable) {
		String normalized = ClaimsUtil.normalizeClaim(claimName);
		By labelLocator = By.xpath("//label[@for='" + normalized + "']");
		By inputLocator = By.id(normalized);
		WebElement label = driver.findElement(labelLocator);
		WebElement checkbox = driver.findElement(inputLocator);
		if (checkbox.isSelected() != enable) {
			label.click();
		}
	}

	public boolean areEssentialClaimsPresent() {
		return !essentialClaims.isEmpty();
	}

	public boolean areVoluntaryClaimsPresent() {
		return !voluntaryClaims.isEmpty();
	}

	public void clickOnAllowBtnInConsentScreen() {
		clickOnElement(allowButtonInConsentScreen, "Clicked on allow button in consent screen");
	}

	public boolean isRedirectedToRelyingPartyLandingPage() {
		String relyingPartyHost = URI.create(EsignetConfigManager.getproperty("baseurl")).getHost();
		String currentHost = URI.create(driver.getCurrentUrl()).getHost();
		return relyingPartyHost.equalsIgnoreCase(currentHost);
	}

	private static final String PREPARE_SIGNUP_REDIRECT_ENDPOINT = "prepare-signup-redirect";
	private static final String RESPONSE_TIMEOUT_ERROR_CODE = "IDA-RST-008";
	private static final String RESPONSE_TIMEOUT_MESSAGE = "The request took too long to process. Please try again later.";
	private static final Duration RELYING_PARTY_REDIRECT_WAIT = Duration.ofSeconds(30);

	/**
	 * Mocks the API call fired when clicking Proceed on the "Consent to Update
	 * Profile" (Attention) screen, so it fails with the backend's documented
	 * response-timeout error code instead of depending on an actual slow/hanging
	 * backend. Must be called before that Proceed click.
	 */
	public void mockPrepareSignupRedirectAsResponseTimeout() {
		String jsonBody = "{\"responseTime\":\"" + Instant.now() + "\",\"response\":null,\"errors\":[{\"errorCode\":\""
				+ RESPONSE_TIMEOUT_ERROR_CODE + "\",\"errorMessage\":\"Request timed out\"}]}";
		BaseTestUtil.mockApiErrorResponse(driver, PREPARE_SIGNUP_REDIRECT_ENDPOINT, 200, jsonBody);
	}

	/**
	 * Confirms the app redirected back to the relying party carrying the mocked
	 * response-timeout error, with the relying party's own rendition of that
	 * error message on screen - mirrors LivenessCheckPage.waitForRedirectWithVerificationIncompleteError().
	 */
	public boolean isResponseTimeoutErrorDisplayedOnRelyingParty() {
		WebDriverWait wait = new WebDriverWait(driver, RELYING_PARTY_REDIRECT_WAIT);
		try {
			wait.until(d -> d.getCurrentUrl().contains("error=" + RESPONSE_TIMEOUT_ERROR_CODE));
		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}

		String pageText = driver.findElement(By.tagName("body")).getText();
		return pageText.contains(RESPONSE_TIMEOUT_MESSAGE);
	}

	public boolean isLanguageDropdownDisplayed() {
		return isElementVisible(languageDropdown, "Verified language dropdown is visible");
	}

	public void clickOnLanguageDropdown() {
		clickOnElement(languageDropdown, "Clicked on language dropdown");
	}

	public void clickOnArabicLanguage() {
		clickOnElement(arabicLanguage, "Selected arabic language from dropdown");
	}

	public void selectLanguage(String langCode) {
		String displayName = LanguageUtil.getDisplayName(langCode);
		WebElement language = driver.findElement(By.xpath("//div[text()='" + displayName + "']"));
		clickOnElement(language, "Selected " + displayName + " language");
	}

	public String getPageDirection() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.attributeToBe(rootContainer, "dir", "rtl"));
		return rootContainer.getAttribute("dir");
	}

	public int getConsentTimerSeconds() {
		String timerValue = consentTimer.getText().trim();
		String secondsPart = timerValue.split(":")[1];
		int seconds = Integer.parseInt(secondsPart);
		return seconds;
	}

	private static final String TRANSACTION_TIMEOUT_ERROR_CODE = "transaction_timeout";
	private static final Duration CONSENT_TRANSACTION_TIMEOUT_WAIT = Duration.ofSeconds(75);

	/**
	 * The consent screen timer (~55s, per the existing 55sec-start check) is
	 * client-side (Consent.js): once it hits zero it automatically fires
	 * redirectOnError('transaction_timeout', ...) and navigates away - no Allow
	 * click is needed or possible by that point, since the redirect has already
	 * happened. This waits out that real timer rather than trying to force a
	 * click race against it.
	 */
	public boolean waitForTransactionTimeoutRedirect() {
		WebDriverWait wait = new WebDriverWait(driver, CONSENT_TRANSACTION_TIMEOUT_WAIT);
		try {
			wait.until(d -> d.getCurrentUrl().contains("error=" + TRANSACTION_TIMEOUT_ERROR_CODE));
		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
		return isRedirectedToRelyingPartyLandingPage();
	}

	public String getSelectedLanguageFromDropdown() {
		return selectedLanguageDropdown.getText().trim();
	}

	public void clickOnProfileDropdown() {
		clickOnElement(profileDropdown, "Clicked on profile dropdown");
	}

	public List<String> getDisplayedClaims() {
		List<String> claims = new ArrayList<>();
		List<WebElement> claimElements = driver.findElements(By.xpath("//a[contains(@class,'px-4 py-2 text-sm')]"));
		for (WebElement element : claimElements) {
			claims.add(element.getText().trim());
		}
		List<WebElement> profileElements = driver.findElements(By.xpath("//img[@class='h-12 w-12 ml-3 mr-3']"));
		if (!profileElements.isEmpty() && profileElements.get(0).isDisplayed()) {
			claims.add("Profile");
		}

		return claims;
	}

	public boolean isEssentialClaimsHeaderDisplayed() {
		return isElementVisible(essentialClaimsHeader, "Verified essential claims header is visible");
	}

	public boolean isEssentialClaimsListDisplayed() {
		return isElementVisible(essentialClaimsList, "Verified essential claims list is visible");
	}

	public boolean isActionMessageDisplayed() {
		return isElementVisible(actionMessage, "Verified action message is displayed");
	}

	public String getActionMessageText() {
		return actionMessage.getText().trim();
	}

	public String getBrandLogoAltText() {
		return brandLogo.getAttribute("alt");
	}

	public String getEsignetLogoAltText() {
		return esignetLogo.getAttribute("alt");
	}

	public boolean isNetworkErrorScreenDisplayed() {
		return isElementVisible(networkErrorHeader, "Verified network error header is displayed")
				&& isElementVisible(networkErrorSubHeader, "Verified network error subheader is displayed");
	}

	/**
	 * Fast, non-waiting presence check for the "should NOT be displayed" case -
	 * unlike isLanguageDropdownDisplayed() (which waits for visibility and is
	 * meant for the positive case), this returns false immediately instead of
	 * blocking for the full timeout when the element genuinely isn't there.
	 */
	public boolean isLanguageSelectionElementPresent() {
		return isElementDisplayed(languageSelection);
	}

	public boolean isTimerDisplayed() {
		return isElementVisible(consentTimer, "Verified timer is displayed");
	}

	public boolean isVerifyOtpButtonEnabled() {
		return isButtonEnabled(verifyOtpButton, "Verified otp verification button is enabled");
	}

	public boolean isLoginWithOtpDisplayed(String expectedText) {
		return isElementDisplayed(loginWithOtpButton) && loginWithOtpButton.getText().trim().startsWith(expectedText);
	}

	public boolean isLoginTitleDisplayed() {
		return isElementDisplayed(loginTitle);
	}

	public boolean isLoginSubTitleDisplayed() {
		return isElementDisplayed(loginSubTitle);
	}

	public String getLoginTitleText() {
		return loginTitle.getText().trim();
	}

	public String getLoginSubTitleText() {
		return loginSubTitle.getText().trim();
	}

	public String getSelectPreferredModeHeaderText() {
		return selectPreferredModeHeader.getText().trim();
	}

	public String getSelectPreferredIdHeaderText() {
		return selectPreferredIdHeader.getText().trim();
	}

	public boolean isHeaderInConsentUpdateProfileScreenVisible() {
		return isElementVisible(headerInConsentUpdateProfileScreen, "Verified header in consent update profile screen");
	}

	public boolean isSubHeaderInConsentUpdateProfileScreenVisible() {
		return isElementVisible(subHeaderInConsentUpdateProfileScreen,
				"Verified sub header in consent update profile screen");
	}

	public boolean isEssentialClaimsHeaderInConsentUpdateProfileScreenVisible() {
		return isElementVisible(essentialClaimHeaderInConsentUpdateProfileScreen,
				"Verified essential claims header in consent update profile screen");
	}

	public boolean isVoluntaryClaimsHeaderInConsentUpdateProfileScreenVisible() {
		return isElementVisible(voluntaryClaimHeaderInConsentUpdateProfileScreen,
				"Verified voluntary claims header in consent update profile screen");
	}

	public boolean isInfoIconInConsentUpdateProfileScreenVisible() {
		return isElementVisible(essentialInfoIconInConsentUpdateProfileScreen,
				"Verified info icon in consent update profile screen");
	}

	public boolean isProceedButtonInConsentUpdateProfileScreenVisible() {
		return isElementVisible(proceedButtonInAttentionPage,
				"Verified procced button in consent update profile screen");
	}

	public boolean isCancelButtonInConsentUpdateProfileScreenVisible() {
		return isElementVisible(cancelButtonInConsentUpdateProfileScreen,
				"Verified cancel button in consent update profile screen");
	}

	public boolean isEssentialClaimListInConsentUpdateProfileScreenVisible() {
		return isElementVisible(essentialClaimsList, "Verified essential claim list in consent update profile screen");
	}

	public boolean isVoluntaryClaimListInConsentUpdateProfileScreenVisible() {
		return isElementVisible(voluntaryClaimsList, "Verified voluntary claim list in consent update profile screen");
	}

	public boolean isAvailableClaimStausDisplayed() {
		return isElementVisible(availableClaimsStatus,
				"Verified available claim status in consent update profile screen");
	}

	public boolean isNotAvailableClaimStausDisplayed() {
		return isElementVisible(notAvailableClaimStatus,
				"Verified not available claim status in consent update profile screen");
	}

	public void clickOnEssentialInfoIcon() {
		clickOnElement(essentialInfoIconInConsentUpdateProfileScreen,
				"Clicked on Info icon in consent update profile screen");
	}

	public void clickOnVoluntaryInfoIcon() {
		clickOnElement(voluntaryInfoIconInConsentUpdateProfileScreen,
				"Clicked on Info icon in consent update profile screen");
	}

	public void clickOnAttentionHeader() {
		clickOnElement(headerInConsentUpdateProfileScreen, "Clicked on header in consent update profile screen");
	}

	public boolean isEssentialClaimInformationDisplayed() {
		return isElementVisible(infoIconMeassage, "Verified essential claim information is displayed");
	}

	public boolean isVoluntaryClaimInformationDisplayed() {
		return isElementVisible(infoIconMeassage, "Verified voluntary claim information is displayed");
	}

	public boolean isMessageAboveProceedButtonDisplayed() {
		return isElementVisible(messageAboveProceedBtn,
				"Verified message above the proceed button in consent update profile screen");
	}

	public void clickOnCancelButtonInUpdateProfilePage() {
		clickOnElement(cancelButtonInConsentUpdateProfileScreen,
				"Clicked on cancel button in consent update profile screen");
	}

	public boolean isAttentionWarningPopupDisplayed() {
		return isElementVisible(attentionHeaderInWarningPopup, "Verified header in warning popup");
	}

	public boolean isSubHeaderInWarningPopupDisplayed() {
		return isElementVisible(subHeaderInWarningPopup, "Verified sub header in warning popup");
	}

	public boolean isStayButtonInWarningPopupScreenDisplayed() {
		return isElementVisible(stayButtonInConsentUpdateProfileScreen,
				"Verified stay button in warning popup is displayed");
	}

	public void clickOnStayButton() {
		clickOnElement(stayButtonInConsentUpdateProfileScreen, "Clicked on stay button");
	}

	public boolean isDiscontinueButtonInWarningPopupScreenDisplayed() {
		return isElementVisible(discontinueButtonInConsentUpdateProfileScreen,
				"Verified discontinue button in warning popup is displayed");
	}

	public void clickOnDiscontinueButton() {
		clickOnElement(discontinueButtonInConsentUpdateProfileScreen, "Clicked on discontinue button");
	}
}