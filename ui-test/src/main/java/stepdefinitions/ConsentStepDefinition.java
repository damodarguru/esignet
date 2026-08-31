package stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.log4j.Logger;

import base.BasePage;
import base.BaseTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.mosip.testrig.apirig.utils.NotificationListener;
import pages.ConsentPage;
import pages.LoginOptionsPage;
import pages.SignUpPage;
import pages.SignupFormDynamicFiller;
import utils.BaseTestUtil;
import utils.BaseTestUtil.CapturedRequest;
import utils.ClaimsUtil;
import utils.EsignetUtil;
import utils.EsignetUtil.RegisteredDetails;
import utils.ResourceBundleLoader;


public class ConsentStepDefinition {

	public WebDriver driver;
	private static final Logger logger = Logger.getLogger(ConsentStepDefinition.class);
	LoginOptionsPage loginOptionsPage;
	SignUpPage signUpPage;
	SignupFormDynamicFiller formFiller;
	ConsentPage consentPage;

	public ConsentStepDefinition(BaseTest baseTest) {
		this.driver = baseTest.getDriver();
		loginOptionsPage = new LoginOptionsPage(driver);
		signUpPage = new SignUpPage(driver);
		formFiller = new SignupFormDynamicFiller(driver);
		consentPage = new ConsentPage(driver);
	}

	@Given("user directly navigates to sign-up portal URL")
	public void userLaunchesSignupPortal() {
		signUpPage.navigateToSignupPortal();
	}

	@When("user clicks on Register button")
	public void userClicksOnRegisterButton() {
		signUpPage.clickOnRegisterButton();
	}

	private String lastGeneratedIdentifier;
	@Then("user enters mobile_number in the mobile number field")
	public void userEnterValidMobileNumber() {
		String fieldId = EsignetUtil.getIdentifierFieldId();
		String regex = EsignetUtil.getRegexForField(fieldId);
		String value = EsignetUtil.generateValueFromRegex(regex, 9);
		lastGeneratedIdentifier = value;
		RegisteredDetails.setMobileNumber(value);
		signUpPage.enterMobileNumber(value);
	}

	@Then("user clicks on the Continue button")
	public void userClickOnContinueButton() {
		signUpPage.clickOnContinueButton();
	}

	@When("user enters the OTP")
	public void userEnterOtp() {
		String number = EsignetUtil.normalizeIdentifierForOtp(lastGeneratedIdentifier);
		signUpPage.enterOtp(NotificationListener.getOtp(number));
	}

	@Then("user clicks on the Verify OTP button")
	public void userClicksOnVerifyOtpButton() {
		signUpPage.clickOnVerifyOtpButton();
	}

	@When("user click on Continue button in Success Screen")
	public void clickOnContinueButtonInSucessScreen() {
		signUpPage.clickOnContinueButtonInSucessScreen();
	}

	@When("user fills the signup form using UI specification")
	public void userFillsSignupFormUsingUiSpecification() throws Exception {
		Map<String, Map<String, Object>> uiSpecFields = EsignetUtil.getUiSpecFields();
		formFiller.fillFormFromUiSpec(uiSpecFields);
	}

	@When("user clicks on Continue button in Setup Account Page")
	public void userClicksOnContinueButtonInSetpuAccountPage() {
		signUpPage.clickOnSetupAccountContinueButton();
	}

	@Then("verify that success screen is displayed")
	public void verifyThenSuccessMessageDisplayed() {
		Assert.assertTrue(signUpPage.isAccountCreatedSuccessfullyMessageDisplayed(),
				"Success message is not displayed");
	}

	@Then("user navigates back in the browser from the signup form and a leave site prompt should appear")
	public void userNavigatesBackFromSignupFormExpectingPrompt() {
		signUpPage.navigateBackExpectingLeaveSitePrompt();
	}

	@Then("user cancels the leave site prompt on the signup form")
	public void userCancelsLeaveSitePromptOnSignupForm() {
		signUpPage.dismissAlert();
	}

	@Then("user confirms the leave site prompt on the signup form")
	public void userConfirmsLeaveSitePromptOnSignupForm() {
		signUpPage.acceptAlert();
	}

	@Then("verify user is retained on the setup account page")
	public void verifyUserRetainedOnSetupAccountPage() {
		Assert.assertTrue(signUpPage.isSetupAccountPageDisplayed(),
				"User is not retained on the setup account page after cancelling the leave site prompt");
	}

	@Then("verify user is no longer on the setup account page")
	public void verifyUserNoLongerOnSetupAccountPage() {
		Assert.assertFalse(signUpPage.isSetupAccountPageDisplayed(),
				"User is still on the setup account page after confirming to leave");
	}

	private String cameraDeviceIdBeforeFlip;

	@When("user opens the photo capture camera")
	public void userOpensPhotoCaptureCamera() {
		signUpPage.clickOnUploadPhoto();
	}

	@When("user notes the active camera device")
	public void userNotesActiveCameraDevice() {
		cameraDeviceIdBeforeFlip = signUpPage.getActivePhotoCaptureDeviceId();
	}

	@When("user clicks on the flip camera button")
	public void userClicksOnFlipCameraButton() {
		signUpPage.clickOnFlipCameraButton();
	}

	@Then("verify the camera view has flipped to a different camera")
	public void verifyCameraViewHasFlipped() {
		String deviceIdAfterFlip = signUpPage.getActivePhotoCaptureDeviceId();
		Assert.assertNotNull(cameraDeviceIdBeforeFlip, "No active camera device detected before flipping");
		Assert.assertNotNull(deviceIdAfterFlip, "No active camera device detected after flipping");
		Assert.assertNotEquals(deviceIdAfterFlip, cameraDeviceIdBeforeFlip,
				"Camera view did not flip to a different camera device");
	}

	@Then("verify the tooltip message for photo icon is displayed with guidance text")
	public void verifyTooltipMessageForPhotoIcon() {
		String actualTooltip = signUpPage.getPhotoIconTooltipText();
		assertFalse(actualTooltip.trim().isEmpty());
	}

	@Then("verify camera permission state on signup is {string}")
	public void verifyCameraPermissionStateOnSignup(String expectedState) {
		Assert.assertEquals(signUpPage.getCameraPermissionState(), expectedState,
				"Camera permission state did not match expected value");
	}

	@Then("verify camera access denied message is displayed on signup photo capture")
	public void verifyCameraAccessDeniedMessageDisplayedOnSignup() {
		Assert.assertTrue(signUpPage.isCameraAccessDeniedMessageDisplayed(),
				"Camera access denied message is not displayed on the photo capture screen");
	}

	@Then("verify fallback upload option is displayed on signup photo capture")
	public void verifyFallbackUploadOptionDisplayedOnSignup() {
		Assert.assertTrue(signUpPage.isFallbackUploadOptionDisplayed(),
				"Fallback upload option is not displayed after camera access was denied");
	}

	@When("user uploads a photo file for the face photo instead of using the camera")
	public void userUploadsPhotoFileInsteadOfUsingCamera() throws Exception {
		signUpPage.uploadPhotoFromFallbackInput();
	}

	@When("user fills the remaining signup form fields using UI specification")
	public void userFillsRemainingSignupFormFieldsUsingUiSpecification() throws Exception {
		Map<String, Map<String, Object>> uiSpecFields = EsignetUtil.getUiSpecFields();
		formFiller.fillFormFromUiSpec(uiSpecFields, true);
	}

	@Given("user starts monitoring the registration submission request")
	public void userStartsMonitoringRegistrationSubmissionRequest() {
		signUpPage.startCapturingRegistrationSubmission();
	}

	@Then("verify the registration request was submitted as multipart form-data and processed successfully")
	public void verifyRegistrationRequestSubmittedAsMultipartFormData() {
		Assert.assertTrue(signUpPage.isRegistrationSubmissionMultipartAndSuccessful(),
				"Registration request was not submitted as multipart/form-data or was not processed successfully");
	}

	@Then("verify the signup ui schema fetch request required no authentication")
	public void verifySignupUiSchemaFetchRequestRequiredNoAuthentication() {
		Assert.assertTrue(signUpPage.isUiSpecFetchUnauthenticatedAndSuccessful(),
				"Signup UI schema fetch request either required authentication or did not complete successfully");
	}

	@Then("verify the signup ui schema contains no sensitive information")
	public void verifySignupUiSchemaContainsNoSensitiveInformation() {
		List<String> findings = EsignetUtil.findSensitiveDataInSignupUiSchema();
		Assert.assertTrue(findings.isEmpty(), "Signup UI schema contains sensitive/unexpected content: " + findings);
	}

	@Then("verify the signup ui schema does not specify a default language")
	public void verifySignupUiSchemaDoesNotSpecifyDefaultLanguage() {
		Assert.assertTrue(EsignetUtil.isDefaultLanguageUnspecifiedInSignupSchema(),
				"Signup UI schema's language config unexpectedly specifies a default language");
	}

	@When("user clears the signup language preference and reloads")
	public void userClearsSignupLanguagePreferenceAndReloads() {
		signUpPage.clearStoredLanguagePreferenceAndReload();
	}

	@Then("verify the signup form defaults to English when no language preference is set")
	public void verifySignupFormDefaultsToEnglish() {
		Assert.assertEquals(signUpPage.getSignupLanguageFromLocalStorage(), "en",
				"Signup form did not default to English when no language preference was set");
	}

	@When("user's internet connection is disconnected during signup")
	public void userInternetConnectionIsDisconnectedDuringSignup() {
		signUpPage.setNetworkOffline(true);
	}

	private List<CapturedRequest> uiSpecRequestsAfterReconnect;

	@When("user's internet connection is restored during signup")
	public void userInternetConnectionIsRestoredDuringSignup() {
		uiSpecRequestsAfterReconnect = BaseTestUtil.captureRequests(driver, "ui-spec");
		signUpPage.setNetworkOffline(false);
	}

	@Then("verify the network error message is displayed on signup")
	public void verifyNetworkErrorMessageDisplayedOnSignup() {
		Assert.assertTrue(signUpPage.isNetworkErrorBannerDisplayed(),
				"Network error message (\"Your network connection dropped, please check your internet connection.\") is not displayed on signup");
	}

	@Then("verify the network error message is no longer displayed on signup")
	public void verifyNetworkErrorMessageGoneOnSignup() {
		Assert.assertTrue(signUpPage.waitUntilNetworkErrorBannerHidden(),
				"Network error message is still displayed on signup after the connection was restored");
	}

	@Then("verify the signup ui schema is freshly re-fetched after reconnecting")
	public void verifySignupUiSchemaFreshlyRefetchedAfterReconnecting() {
		long deadline = System.currentTimeMillis() + 8000;
		while ((uiSpecRequestsAfterReconnect == null || uiSpecRequestsAfterReconnect.isEmpty())
				&& System.currentTimeMillis() < deadline) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		Assert.assertFalse(uiSpecRequestsAfterReconnect == null || uiSpecRequestsAfterReconnect.isEmpty(),
				"Signup UI did not re-fetch the schema from the API after the network connection was restored");
	}

	private String expectedDefaultLang;

	@Then("user click on Login with Otp")
	public void clickOnLoginWithOtp() {
		expectedDefaultLang = consentPage.getCurrentLanguage();
		consentPage.clickOnLoginWithOtp();
	}

	@Then("user enters Registered mobile number into the mobile number field")
	public void userEntersRegisteredMobileNumber() {
		String registeredNumber = RegisteredDetails.getMobileNumber();
		consentPage.enterRegisteredMobileNumber(registeredNumber);
	}

	@Then("user click on get otp button")
	public void userClickOnGetOtpBtn() {
		consentPage.clickOnGetOtp();
	}

	@Then("user enters the correct otp")
	public void userEnterCorrectOtp() {
		String mobile = RegisteredDetails.getMobileNumber();
		consentPage.enterOtp(NotificationListener.getOtp(mobile));
	}

	@Then("click on verify Otp button")
	public void userClickOnVerifyOtpBtn() {
		consentPage.clickOnVerifyButton();
	}

	@Then("verify consent should ask user to proceed in attention page")
	public void userGoesToAttentionScreen() {
		Assert.assertTrue(consentPage.isOnAttentionScreen(), "User didn't navigated to attention page");
	}

	@Then("clicks on proceed button in attention page")
	public void clickOnProceedButtonInAttentionPage() {
		consentPage.clickOnProceedButtonInAttentionPage();
	}

	@Given("the eKYC process request is mocked to time out")
	public void theEkycProcessRequestIsMockedToTimeOut() {
		consentPage.mockPrepareSignupRedirectAsResponseTimeout();
	}

	@Then("verify the request timed out error is displayed")
	public void verifyRequestTimedOutErrorIsDisplayed() {
		Assert.assertTrue(consentPage.isResponseTimeoutErrorDisplayedOnRelyingParty(),
				"Relying party did not display the \"request took too long to process\" error after the eKYC process request was mocked to time out");
	}

	@Then("clicks on proceed button in next page")
	public void clickOnProceedButtonInNextPage() {
		consentPage.clickOnProceedButton();
	}

	@Then("select the e-kyc verification provider")
	public void selectEKycVerificationProvider() {
		consentPage.clickOnMockIdentifyVerifier();
	}

	@Then("clicks on proceed button in e-kyc verification provider page")
	public void clickOnProceedButton() {
		consentPage.clickOnProceedButtonInServiceProviderPage();
	}

	@Then("user select the check box in terms and condition page")
	public void userSelectTheCheckBoxInTermsAndConditionPage() {
		consentPage.checkTermsAndCondition();
	}

	@Then("user clicks on proceed button in terms and condition page")
	public void userClicksOnProceedButtonInTermsAndConditionPage() {
		consentPage.clickOnProceedButtonInTermsAndConditionPage();
	}

	@Then("user clicks on proceed button in camera preview page")
	public void userClicksOnProceedButtonInCameraPreviewPage() {
		consentPage.clickOnProceedButtonInCameraPreviewPage();
	}

	@Then("user is navigated to consent screen once liveness check completes")
	public void waitUntilLivenessCheckCompletesInCameraPage() {
		consentPage.waitUntilLivenessCheckCompletes();
	}

	@Then("verify user is navigated to consent screen")
	public void verifyUserIsOnConsentScreen() {
		Assert.assertTrue(consentPage.isConsentScreenVisible(), "User didn't navigated to consent screen");
	}

	@Then("user clicks on language dropdown button")
	public void userClickOnLanguageDropdown() {
		consentPage.clickOnLanguageDropdown();
	}

	@Then("user selects arabic language")
	public void userSelectsArabicLanguage() {
		consentPage.clickOnArabicLanguage();
	}

	@Then("verify screen is displayed in RTL format")
	public void verifyPageDisplayedInRtlFormat() {
		String dirValue = consentPage.getPageDirection();
		assertEquals("rtl", dirValue);
	}

	@Then("verify the tooltip message for Voluntary Claims info icon")
	public void verifyTooltipMessageForVoluntaryClaimsIcon() {
		String actualTooltip = consentPage.getVoluntaryClaimsTooltipText();
		assertFalse(actualTooltip.trim().isEmpty());
	}

	@Then("verify essential claims are listed separately")
	public void verifyEssentialClaimsAreListedSeparately() {
		Assert.assertTrue(consentPage.areEssentialClaimsPresent(), "Essential claims list were not present");
	}

	@Then("verify voluntary claims are listed separately")
	public void verifyVoluntaryClaimsAreListedSeparately() {
		Assert.assertTrue(consentPage.areVoluntaryClaimsPresent(), "Voluntary claims list are not present");
	}

	@Then("verify master toggle should be visible for Voluntary Claims if multiple claims are present")
	public void verifyVoluntaryClaimsMasterToggleVisible() {
		Assert.assertTrue(consentPage.isVoluntaryClaimsMasterToggleVisible(),
				"Master toggle button for voluntary claims is not visisble");
	}

	@Then("verify all toggle buttons for Voluntary Claims are disabled by default")
	public void verifyVoluntaryClaimsMasterToggleDisabled() {
		assertFalse(consentPage.getVoluntaryClaimsMasterToggle().isSelected());
		for (WebElement subToggle : consentPage.getVoluntaryClaimsSubToggles()) {
			assertFalse(subToggle.isSelected());
		}
	}

	@Then("verify if user enables Master toggle,all sub-toggles should be enabled")
	public void enableMasterToggleForVoluntaryClaims() {
		consentPage.enableVoluntaryClaimsMasterToggle();
		for (WebElement subToggle : consentPage.getVoluntaryClaimsSubToggles()) {
			Assert.assertTrue(subToggle.isSelected(), "Sub toggle button did not selected");
		}
	}

	@When("if user deselect one of the Voluntary Claims")
	public void userDeselectOneVoluntaryClaim() throws Exception {
		List<String> voluntaryClaims = consentPage.getClaims("voluntary");
		assertFalse("Voluntary claims were not loaded for this scenario", voluntaryClaims.isEmpty());
		if (!voluntaryClaims.isEmpty()) {
			String firstClaim = voluntaryClaims.get(0);
			consentPage.toggleVoluntaryClaim(firstClaim, false);
		}
	}

	@Then("verify remaining Voluntary Claims stays selected along with master toggle")
	public void verifyRemainingVoluntaryClaim() {
		Assert.assertTrue(consentPage.isVoluntaryClaimsMasterToggleSelected(),
				"Voluntary claims master toggle is not selected");

		int notSelected = 0;

		for (WebElement toggle : consentPage.getVoluntaryClaimsSubToggles()) {
			if (!toggle.isSelected()) {
				notSelected++;
			}
		}
		assertEquals(1, notSelected);
	}

	@Then("if user disables Master toggle,all sub-toggles should be disabled")
	public void disableMasterToggleForAuthorizeScope() {
		consentPage.disableVoluntaryClaimsMasterToggle();
		for (WebElement subToggle : consentPage.getVoluntaryClaimsSubToggles()) {
			assertFalse(subToggle.isSelected());
		}
	}

	@Then("if user manually deselects all sub-toggles,verify master toggle also gets disabled")
	public void verifyDeselectingVoluntaryClaimManually() throws Exception {
		List<String> voluntaryClaims = consentPage.getClaims("voluntary");
		assertFalse("Voluntary claims were not loaded for this scenario", voluntaryClaims.isEmpty());
		for (String claim : voluntaryClaims) {
			consentPage.toggleVoluntaryClaim(claim, false);
		}
		assertFalse(consentPage.isVoluntaryClaimsMasterToggleSelected());
	}

	@When("user enables only one of the Voluntary Claims toggle")
	public void userEnablesOneVoluntaryClaim() throws Exception {
		List<String> voluntaryClaims = consentPage.getClaims("voluntary");
		assertFalse("Voluntary claims were not loaded for this scenario", voluntaryClaims.isEmpty());
		if (!voluntaryClaims.isEmpty()) {
			String firstClaim = voluntaryClaims.get(0);
			consentPage.toggleVoluntaryClaim(firstClaim, true);
		}
	}

	@Then("verify that the master toggle remains in unselected state")
	public void verifyMasterToggleIsDisabled() {
		assertFalse(consentPage.isVoluntaryClaimsMasterToggleSelected());
	}

	List<String> selectedVoluntaryClaims = new ArrayList<>();

	@When("user enables all the voluntary claims sub-toggle manually")
	public void userEnablesAllSubToggles() throws Exception {
		List<String> voluntaryClaims = consentPage.getClaims("voluntary");
		assertFalse("Voluntary claims were not loaded for this scenario", voluntaryClaims.isEmpty());
		selectedVoluntaryClaims.clear();

		for (String claim : voluntaryClaims) {
			consentPage.toggleVoluntaryClaim(claim, true);
			selectedVoluntaryClaims.add(claim);
		}
	}

	@Then("verify that the master toggle is enabled automatically")
	public void verifyMasterToggleIsEnabled() {
		Assert.assertTrue(consentPage.isVoluntaryClaimsMasterToggleSelected(),
				"Voluntary claims master toggle is not enabled");
	}

	@Then("verify the timer starts from 55sec in the consent page via Otp login")
	public void verifyConsentPageTimer() {
		int seconds = consentPage.getConsentTimerSeconds();
		Assert.assertTrue(seconds >= 54 && seconds <= 56, "Timer should start around 55 seconds, but was: " + seconds);
	}

	@Then("verify user is logged out and redirected to the relying party once the consent timer times out")
	public void verifyUserIsLoggedOutAndRedirectedOnceConsentTimerTimesOut() {
		Assert.assertTrue(consentPage.waitForTransactionTimeoutRedirect(),
				"User was not redirected to the relying party with a transaction_timeout error once the consent screen timer expired");
	}

	@Then("refresh the browser tab and verify timer continue with leftover seconds")
	public void verifyTimerPersistsAfterRefresh() {
		int beforeRefresh = consentPage.getConsentTimerSeconds();
		logger.info("Timer before waiting: " + beforeRefresh + " seconds");
		driver.navigate().refresh();
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> consentPage.isConsentScreenVisible());

		int afterRefresh = consentPage.getConsentTimerSeconds();
		logger.info("Timer after refresh: " + afterRefresh + " seconds");

		Assert.assertTrue(afterRefresh <= beforeRefresh && (beforeRefresh - afterRefresh) <= 2,
				"Timer should persist after refresh within 2 seconds tolerance");
	}

	@Then("user verify the header of essential claims")
	public void verifyTheEssentialClaimsHeader() {
		Assert.assertTrue(consentPage.isEssentialClaimsHeaderDisplayed(),
				"The header of the essential is not displayed");
	}

	@Then("user verify the list of essential claims are present")
	public void verifyTheEssentialClaimsList() {
		Assert.assertTrue(consentPage.isEssentialClaimsListDisplayed(),
				"No essential claims were rendered on the consent screen");
	}

	@Then("user verify the action message in consent screen")
	public void verifyTheActionMessage() {
		Assert.assertTrue(consentPage.isActionMessageDisplayed(),
				"The action message in the consent screen is not displayed");
	}

	@Then("user verify the timer is displayed in consent screen")
	public void verifyTheTimerInConsentScreen() {
		Assert.assertTrue(consentPage.isTimerDisplayed(), "The timer is not displayed in the consent screen");
	}

	@Then("verify the otp verification button is disabled on the verification screen")
	public void verifyOtpVerificationButtonIsDisabled() {
		Assert.assertFalse(consentPage.isVerifyOtpButtonEnabled(), "Otp verification button is enabled");
	}

	@Then("verify the otp verification button is enabled on the verification screen")
	public void verifyOtpVerificationButtonIsEnabled() {
		Assert.assertTrue(consentPage.isVerifyOtpButtonEnabled(), "Otp verification button is not enabled");
	}

	@When("user creates the client with purpose type login")
	public void userCreateClientIdPurposeLogin() {
		// Purpose is already handled via scenario tags in BaseTest
	}

	@Then("all auth factors should start with login")
	public void verifyLoginPurposeReflectedInUI() {
		String expectedText = ResourceBundleLoader.getPrefixText("otp.login_with_id");
		Assert.assertTrue(consentPage.isLoginWithOtpDisplayed(expectedText),
				"Expected text not displayed: " + expectedText);
	}
	
	@When("user creates the client without purpose field")
	public void userCreateClientIdWithoutPurpose() {
		// Purpose is already handled via scenario tags in BaseTest
	}

	@When("user creates the client with purpose type link")
	public void userCreateClientIdPurposeLink() {
		// Purpose is already handled via scenario tags in BaseTest
	}

	@Then("all auth factors should start with link")
	public void verifyLinkPurposeReflectedInUI() {
		String expectedText = ResourceBundleLoader.getPrefixText("otp.link_using_id");
		Assert.assertTrue(consentPage.isLoginWithOtpDisplayed(expectedText),
				"Expected text not displayed: " + expectedText);
	}

	@When("user creates the client with purpose type verify")
	public void userCreateClientIdPurposeVerify() {
		// Purpose is already handled via scenario tags in BaseTest
	}

	@Then("all auth factors should start with verify")
	public void validateVerifyPurposeReflectedInUI() {
		String expectedText = ResourceBundleLoader.getPrefixText("otp.verify_with_id");
		Assert.assertTrue(consentPage.isLoginWithOtpDisplayed(expectedText),
				"Expected text not displayed: " + expectedText);
	}

	@When("user creates the client with purpose type none")
	public void userCreateClientIdPurposeNone() {
		// Purpose is already handled via scenario tags in BaseTest
	}

	@Then("verify no title or subtitle should be displayed")
	public void verifyTitleNotDisplayed() {
		Assert.assertFalse(consentPage.isLoginTitleDisplayed(), "Title is displayed");
		Assert.assertFalse(consentPage.isLoginSubTitleDisplayed(), "Subtitle is displayed");
	}

	@Then("verify default title and subtitle should be displayed when both title and subtitle are not configured")
	public void verifyDefaultLoginTitleAndSubtitleWhenBothMissing() {
		Assert.assertEquals(consentPage.getLoginTitleText(), "Login using eSignet",
				"Default title is not displayed when title and subtitle are not configured for the client");
		Assert.assertTrue(consentPage.getLoginSubTitleText().contains("is requesting authentication for login"),
				"Default subtitle is not displayed when title and subtitle are not configured for the client");
	}

	@Then("verify title and subtitle should be displayed as per text given during client creation")
	public void verifyDefaultLoginTitleAndSubtitle() {
		Assert.assertTrue(consentPage.getLoginTitleText().equals("Verify using eSignet"));
		Assert.assertTrue(consentPage.getLoginSubTitleText().contains("is requesting authentication for verification"));
	}

	@When("user creates the client with null title and subtitle values")
	public void userCreateClientIdWithNullTitle() {
		// Title is already handled via scenario tags in BaseTest
	}

	@When("user creates the client with empty title and subtitle values")
	public void userCreateClientIdWithEmptyTitle() {
		// Title is already handled via scenario tags in BaseTest
	}

	@When("user creates the client with updated title and subtitle values")
	public void userCreateClientIdWithUpdatedTitle() {
		// Title is already handled via scenario tags in BaseTest
	}

	@Then("verify title and subtitle should be displayed as per updated client details")
	public void verifyUpdatedLoginTitleAndSubtitle() {
		Assert.assertEquals(consentPage.getLoginTitleText(), "Continue to Health Services Portal",
				"Updated title is not displayed as per the text given while updating client details");
		Assert.assertTrue(
				consentPage.getLoginSubTitleText().contains("requires your consent to share profile details"),
				"Updated subtitle is not displayed as per the text given while updating client details");
	}

	@When("user creates the client with a title but no subtitle for purpose type {string}")
	public void userCreateClientIdWithTitleOnly(String purposeType) {
		// Purpose type/title/(missing) subtitle are already handled via scenario tags in BaseTest
	}

	@Then("verify default subtitle {string} should be displayed when subtitle is not configured")
	public void verifyDefaultSubtitleDisplayedWhenNotConfigured(String expectedDefaultSubtitlePart) {
		Assert.assertTrue(consentPage.isLoginTitleDisplayed(), "Title is not displayed even though title was configured");
		Assert.assertFalse(consentPage.getLoginTitleText().isEmpty(), "Title text is empty");
		Assert.assertTrue(consentPage.getLoginSubTitleText().contains(expectedDefaultSubtitlePart),
				"Default subtitle for the purpose type is not displayed as expected: " + expectedDefaultSubtitlePart);
	}

	@When("user creates the client with a subtitle but no title for purpose type {string}")
	public void userCreateClientIdWithSubtitleOnly(String purposeType) {
		// Purpose type/subtitle/(missing) title are already handled via scenario tags in BaseTest
	}

	@Then("verify default title {string} should be displayed when title is not configured")
	public void verifyDefaultTitleDisplayedWhenNotConfigured(String expectedDefaultTitle) {
		Assert.assertTrue(consentPage.isLoginSubTitleDisplayed(),
				"Subtitle is not displayed even though subtitle was configured");
		Assert.assertFalse(consentPage.getLoginSubTitleText().isEmpty(), "Subtitle text is empty");
		Assert.assertEquals(consentPage.getLoginTitleText(), expectedDefaultTitle,
				"Default title for the purpose type is not displayed as expected: " + expectedDefaultTitle);
	}

	@When("user creates the client with an empty purpose type")
	public void userCreateClientIdWithEmptyPurposeType() {
		// Purpose type is already handled via scenario tags in BaseTest
	}

	@When("user creates the client with client name configured in multiple languages")
	public void userCreateClientIdWithMultiLangClientName() {
		// clientNameLangMap is already handled via scenario tags in BaseTest
	}

	@When("user switches the language to {string} on the consent screen")
	public void userSwitchesLanguageOnConsentScreen(String langCode) {
		consentPage.clickOnLanguageDropdown();
		consentPage.selectLanguage(langCode);
	}

	@Then("verify the relying party name on the consent screen is displayed as {string}")
	public void verifyRelyingPartyNameOnConsentScreen(String expectedClientName) {
		Assert.assertTrue(consentPage.getActionMessageText().contains(expectedClientName),
				"Relying party name is not displayed as expected on the consent screen: expected to contain '"
						+ expectedClientName + "', but was '" + consentPage.getActionMessageText() + "'");
	}

	@Then("verify the relying party name on the login page is displayed as {string}")
	public void verifyRelyingPartyNameOnLoginPage(String expectedClientName) {
		Assert.assertTrue(consentPage.getLoginSubTitleText().contains(expectedClientName),
				"Relying party name is not displayed as expected on the login page: expected to contain '"
						+ expectedClientName + "', but was '" + consentPage.getLoginSubTitleText() + "'");
	}

	@Then("verify the relying party logo alt text on the login page is displayed as {string}")
	public void verifyRelyingPartyLogoAltTextOnLoginPage(String expectedClientName) {
		String actualAlt = consentPage.getBrandLogoAltText();
		Assert.assertNotNull(actualAlt, "Relying party logo does not have an alt attribute on the login page");
		Assert.assertTrue(actualAlt.contains(expectedClientName),
				"Relying party logo alt text is not displayed as expected on the login page: expected to contain '"
						+ expectedClientName + "', but was '" + actualAlt + "'");
	}

	@Then("verify the eSignet logo alt text on the login page is displayed as {string}")
	public void verifyEsignetLogoAltTextOnLoginPage(String expectedAltText) {
		String actualAlt = consentPage.getEsignetLogoAltText();
		Assert.assertNotNull(actualAlt, "eSignet logo does not have an alt attribute on the login page");
		Assert.assertEquals(actualAlt, expectedAltText,
				"eSignet logo alt text is not displayed as expected on the login page");
	}

	@Then("verify the relying party logo alt text on the consent screen is displayed as {string}")
	public void verifyRelyingPartyLogoAltText(String expectedClientName) {
		String actualAlt = consentPage.getBrandLogoAltText();
		Assert.assertNotNull(actualAlt, "Relying party logo does not have an alt attribute");
		Assert.assertTrue(actualAlt.contains(expectedClientName),
				"Relying party logo alt text is not displayed as expected on the consent screen: expected to contain '"
						+ expectedClientName + "', but was '" + actualAlt + "'");
	}

	@Then("verify the eSignet logo alt text on the consent screen is displayed as {string}")
	public void verifyEsignetLogoAltText(String expectedAltText) {
		String actualAlt = consentPage.getEsignetLogoAltText();
		Assert.assertNotNull(actualAlt, "eSignet logo does not have an alt attribute");
		Assert.assertEquals(actualAlt, expectedAltText,
				"eSignet logo alt text is not displayed as expected on the consent screen");
	}

	@When("user's internet connection is disconnected")
	public void userInternetConnectionIsDisconnected() {
		consentPage.setNetworkOffline(true);
	}

	@When("user's internet connection is restored")
	public void userInternetConnectionIsRestored() {
		consentPage.setNetworkOffline(false);
	}

	@Then("verify the network error screen is displayed")
	public void verifyNetworkErrorScreenIsDisplayed() {
		Assert.assertTrue(consentPage.isNetworkErrorScreenDisplayed(),
				"Network error screen (\"Network Error!\" / \"Please check your internet connection and try again.\") is not displayed");
	}

	@Then("verify language dropdown is not displayed on the network error screen")
	public void verifyLanguageDropdownNotDisplayedOnNetworkErrorScreen() {
		Assert.assertFalse(consentPage.isLanguageSelectionElementPresent(),
				"Language dropdown is displayed on the network error screen");
	}

	@Then("verify select preferred mode text is displayed")
	public void verifySelectPreferredModeText() {
		String expectedText = ResourceBundleLoader.get("signInOption.preferred_mode_to_continue");
		Assert.assertEquals(consentPage.getSelectPreferredModeHeaderText(), expectedText, "Expected text mismatch");
	}

	@Then("verify select preferred ID text based on purpose type when more than one auth factor is present")
	public void verifySelectPreferredIdHeaderText() {
		List<String> authFactors = ClaimsUtil.getAuthFactors();
		Assert.assertFalse(authFactors.isEmpty(), "No auth factors were parsed from the authorize URL");

		Assert.assertTrue(authFactors.size() > 1, "Expected multiple auth factors, got " + authFactors.size());
		String expectedText = ResourceBundleLoader.get("otp.login_with_id_multiple");
		Assert.assertEquals(consentPage.getSelectPreferredIdHeaderText(), expectedText, "Expected text mismatch");
	}

	@When("user creates the client with single auth factor")
	public void userCreateClientIdWithSingleAuthFactor() {
		// It is already handled via scenario tags in BaseTest
	}

	@Then("verify select ID type text based on purpose type when one auth factor is displayed")
	public void verifySelectIdTypeHeaderText() {
		List<String> authFactors = ClaimsUtil.getAuthFactors();
		Assert.assertFalse(authFactors.isEmpty(), "No auth factors were parsed from the authorize URL");

		Assert.assertTrue(authFactors.size() == 1, "Expected multiple auth factors, got " + authFactors.size());
		String expectedText = ResourceBundleLoader.get("otp.login_with_id_multiple");
		Assert.assertEquals(consentPage.getSelectPreferredIdHeaderText(), expectedText, "Expected text mismatch");
	}

	@Then("verify select preferred ID text based on purpose type is displayed")
	public void verifySelectPreferredIdHeaderTextDisplayed() {
		String expectedText = ResourceBundleLoader.get("otp.login_with_id_multiple");
		Assert.assertEquals(consentPage.getSelectPreferredIdHeaderText(), expectedText, "Expected text mismatch");
	}
	
	@Then("verify the header Attention in the consent to profile update screen")
	public void verifyHeaderInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isHeaderInConsentUpdateProfileScreenVisible(),
				"Header in consent to profile update screen is not displayed");
	}

	@Then("verify the sub header in the consent to profile update screen")
	public void verifySubHeaderInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isSubHeaderInConsentUpdateProfileScreenVisible(),
				"Sub header in consent to profile update screen is not displayed");
	}

	@Then("verify the essential claim header in consent to update profile screen")
	public void verifyEssentialClaimHeaderInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isEssentialClaimsHeaderInConsentUpdateProfileScreenVisible(),
				"Essential cliams header in consent to profile update screen is not displayed");
	}

	@Then("verify the voluntary claim header in the consent to profile update screen")
	public void verifyVoluntaryClaimHeaderInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isVoluntaryClaimsHeaderInConsentUpdateProfileScreenVisible(),
				"Voluntary cliams header in consent to profile update screen is not displayed");
	}

	@Then("verify info icon is available in consent to update profile screen")
	public void verifyInfoIconInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isInfoIconInConsentUpdateProfileScreenVisible(),
				"Info icon in consent to profile update screen is not displayed");
	}

	@Then("verify proceed button is visible in consent to update profile screen")
	public void verifyProceedBtnInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isProceedButtonInConsentUpdateProfileScreenVisible(),
				"Proceed in consent to profile update screen is not displayed");
	}

	@Then("verify cancel button is visible in consent to update profile screen")
	public void verifyCancelBtnInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isCancelButtonInConsentUpdateProfileScreenVisible(),
				"Cancel in consent to profile update screen is not displayed");
	}

	@Then("user verify the essential claims list")
	public void verifyEssentialClaimListInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isEssentialClaimListInConsentUpdateProfileScreenVisible(),
				"Essential claims list in consent to profile update screen is not displayed");
	}

	@Then("user verify the voluntary claims list")
	public void verifyVoluntaryClaimListInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isVoluntaryClaimListInConsentUpdateProfileScreenVisible(),
				"Voluntary claims list in consent to profile update screen is not displayed");
	}

	@Then("user click on essential claim info icon")
	public void userClickOnEssentialInfoIcon() {
		consentPage.clickOnEssentialInfoIcon();
	}

	@Then("verify the essential claim information displayed on clicking the info icon")
	public void verifyEssentialClaimInfoInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isEssentialClaimInformationDisplayed(),
				"Essential claims information in consent to profile update screen is not displayed");
	}

	@Then("user tab outside the info icon")
	public void userClickOutsideInfoIcon() {
		consentPage.clickOnAttentionHeader();
	}

	@Then("user click on voluntary claim info icon")
	public void userClickOnVoluntaryInfoIcon() {
		consentPage.clickOnVoluntaryInfoIcon();
	}
	
	@Then("verify the voluntary claim information displayed on clicking the info icon")
	public void verifyVoluntaryClaimInfoInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isVoluntaryClaimInformationDisplayed(),
				"Voluntary claims information in consent to profile update screen is not displayed");
	}

	@Then("verify the message click on proceed to begin with the verification process is displayed below")
	public void verifyMessageInConsentProfileUpdateScreenDisplayed() {
		Assert.assertTrue(consentPage.isMessageAboveProceedButtonDisplayed(),
				"Message above proceed in consent to profile update screen is not displayed");
	}

	@When("user click on cancel button in consent update to profile screen")
	public void userClickOnCancelButton() {
		consentPage.clickOnCancelButtonInUpdateProfilePage();
	}

	@Then("verify warning popup with header attention is displayed")
	public void verifyAttentionWarningPopupDisplayed() {
		Assert.assertTrue(consentPage.isAttentionWarningPopupDisplayed(), "Header in warning popup is not displayed");
	}

	@Then("verify the sub header in warning popup is displayed")
	public void verifySubHeaderWarningPopupDisplayed() {
		Assert.assertTrue(consentPage.isSubHeaderInWarningPopupDisplayed(),
				"Sub-header in warning popup is not displayed");
	}

	@Then("verify stay button is available in the warning popup")
	public void verifyStayButtonInWarningPopupAvailable() {
		Assert.assertTrue(consentPage.isStayButtonInWarningPopupScreenDisplayed(),
				"Stay button in warning popup is not displayed");
	}

	@Then("verify discontinue button is available in the warning popup screen")
	public void verifyDiscontinueButtonInWarningPopupAvailable() {
		Assert.assertTrue(consentPage.isDiscontinueButtonInWarningPopupScreenDisplayed(),
				"Discontinue button warning popup is not displayed");
	}

	@When("user click on stay button in warning popup")
	public void userClickStayButtonInWarningPopup() {
		consentPage.clickOnStayButton();
	}

	@When("user click on discontinue button in warning popup screen")
	public void userClickDiscontinueButtonInWarningPopup() {
		consentPage.clickOnDiscontinueButton();
	}

	@Then("verify available claim status is displayed in consent to update profile screen")
	public void verifyAvailableClaimStatusDisplayed() {
		Assert.assertTrue(consentPage.isAvailableClaimStausDisplayed(),
				"Available claim status is not displayed in consent to update profile screen");
	}

	@Then("verify not available claim status is displayed in consent to update profile screen")
	public void verifyNotAvailableClaimStatusDisplayed() {
		Assert.assertTrue(consentPage.isNotAvailableClaimStausDisplayed(),
				"Not available claim status is not displayed in consent to update profile screen");
	}

	@When("user clicks on Allow button in consent screen")
	public void userClicksOnAllowButtonInConsentScreen() {
		consentPage.clickOnAllowBtnInConsentScreen();
	}

	@Then("verify user is no longer on consent screen after clicking allow")
	public void verifyUserIsNoLongerOnConsentScreenAfterAllow() {
		Assert.assertFalse(consentPage.isConsentScreenVisible(),
				"User is still on the consent screen after clicking Allow");
	}

	@Then("verify user is no longer on the attention screen after clicking discontinue")
	public void verifyUserIsNoLongerOnAttentionScreenAfterDiscontinue() {
		Assert.assertFalse(consentPage.isOnAttentionScreen(),
				"User is still on the attention screen after clicking Discontinue");
	}

	@Then("verify user bypasses the attention and consent screens and is redirected to the relying party landing page")
	public void verifyUserBypassesAttentionAndConsentScreens() {
		Assert.assertFalse(consentPage.isOnAttentionScreen(),
				"User was navigated to the attention screen even though essential claims are already verified and consented for this relying party");
		Assert.assertFalse(consentPage.isConsentScreenVisible(),
				"User was navigated to the consent screen even though essential claims are already verified and consented for this relying party");
		Assert.assertTrue(consentPage.isRedirectedToRelyingPartyLandingPage(),
				"User was not redirected to the relying party landing page after logging in a second time with already-consented essential claims");
	}

	@Given("user relaunches esignet url with {string} claim updated to {string}")
	public void userRelaunchesEsignetUrlWithClaimUpdatedTo(String claimName, String essentialOrVoluntary) throws Exception {
		boolean essential = "essential".equalsIgnoreCase(essentialOrVoluntary);
		String url = EsignetUtil.generateAuthorizeUrlWithUpdatedClaim(claimName, essential);
		driver.get(url);
	}

	@Given("user navigates to esignet url with {string} claim updated to {string}")
	public void userNavigatesToEsignetUrlWithClaimUpdatedTo(String claimName, String essentialOrVoluntary) throws Exception {
		userRelaunchesEsignetUrlWithClaimUpdatedTo(claimName, essentialOrVoluntary);
	}

	@Given("user relaunches esignet url requesting verification of an additional {string} claim")
	public void userRelaunchesEsignetUrlRequestingAdditionalVerifiedClaim(String newClaimName) throws Exception {
		String url = EsignetUtil.generateAuthorizeUrlWithAdditionalVerifiedClaim(newClaimName);
		driver.get(url);
	}

	@Then("verify the {string} claim shows verified status in consent to update profile screen")
	public void verifyClaimShowsVerifiedStatus(String claimLabel) {
		Assert.assertTrue(consentPage.isClaimShownAsVerified(claimLabel),
				"\"Verified\" status is not displayed against the '" + claimLabel
						+ "' claim in the consent to update profile screen");
	}

	@Then("verify the {string} claim shows not verified status in consent to update profile screen")
	public void verifyClaimShowsNotVerifiedStatus(String claimLabel) {
		Assert.assertTrue(consentPage.isClaimShownAsNotVerified(claimLabel),
				"\"Not Verified\" status is not displayed against the '" + claimLabel
						+ "' claim in the consent to update profile screen");
	}

	@Given("user captures the authorize url requesting an additional voluntary {string} claim")
	public void userCapturesAuthorizeUrlRequestingAdditionalVoluntaryClaim(String newClaimName) throws Exception {
		String url = EsignetUtil.generateAuthorizeUrlWithAdditionalVoluntaryClaim(newClaimName);
		driver.get(url);
	}

	@Then("verify user is redirected to the eKYC provider list screen")
	public void verifyUserIsRedirectedToEkycProviderListScreen() {
		Assert.assertTrue(consentPage.isOnEkycProviderListScreen(),
				"User was not redirected to the eKYC provider list screen");
	}

	@Then("verify user bypasses the attention screen and is redirected to the consent screen")
	public void verifyUserBypassesAttentionScreenAndReachesConsentScreen() {
		Assert.assertFalse(consentPage.isOnAttentionScreen(),
				"User was navigated to the attention screen even though the verified claim is still verified and consented for this relying party");
		Assert.assertTrue(consentPage.isConsentScreenVisible(),
				"User was not navigated to the consent screen after the claim's essential/voluntary status was updated for the relying party");
	}
}