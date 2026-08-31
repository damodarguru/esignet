package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

public class EkycPage extends BasePage {

	public EkycPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "tnc-header")
	WebElement ekycProcessStepsScreenLabel;

	/**
	 * Not confirmed against the real DOM - a full reload wipes this app's
	 * in-memory transaction state, which is expected to surface as this
	 * generic authorization-failure popup before the eventual redirect to the
	 * relying party (as opposed to browser back, where bfcache may preserve
	 * enough state to skip straight to that redirect - see
	 * waitForConsentRejectedRedirect). Text matches oidc-ui's locale file
	 * exactly (lowercase "authorize", not "Authorize").
	 */
	@FindBy(xpath = "//*[contains(text(),'Unable to authorize. Please try again.')]")
	WebElement authorizationFailedMessage;

	@FindBy(xpath = "//button[normalize-space(text())='Okay' or normalize-space(text())='OK']")
	WebElement authorizationFailedOkayButton;

	@FindBy(id = "step-label-0")
	WebElement eKycStep1Title;

	@FindBy(id = "step-description-0")
	WebElement eKycStep1Subtitle;

	@FindBy(id = "step-label-1")
	WebElement eKycStep2Title;

	@FindBy(id = "step-description-1")
	WebElement eKycStep2Subtitle;

	@FindBy(id = "step-label-2")
	WebElement eKycStep3Title;

	@FindBy(id = "step-description-2")
	WebElement eKycStep3Subtitle;

	@FindBy(id = "step-label-3")
	WebElement eKycStep4Title;

	@FindBy(id = "step-description-3")
	WebElement eKycStep4Subtitle;

	@FindBy(id = "step-label-4")
	WebElement eKycStep5Title;

	@FindBy(id = "step-description-4")
	WebElement eKycStep5Subtitle;

	@FindBy(xpath = "(//button[contains(@class,'inline-flex items-center justify-center')])[1]")
	WebElement cancelButton;

	@FindBy(xpath = "//h2[@class='font-semibold flex flex-col items-center justify-center gap-y-4 text-[1.5em]']")
	WebElement ekycWarningPopupHeader;

	@FindBy(xpath = "//p[@class='text-center text-muted-dark-gray text-md']")
	WebElement ekycWarningPopupMessage;

	@FindBy(id = "stay-button")
	WebElement ekycWarningPopupStayButton;

	@FindBy(id = "dismiss-button")
	WebElement ekycWarningPopupDiscontinueButton;

	@FindBy(id = "sign-in-with-esignet")
	WebElement relyingPartyLoginPage;

	@FindBy(id = "sign-in-with-esignet")
	WebElement signInWithEsignetButton;

	@FindBy(xpath = "(//button[contains(@class,'inline-flex items-center justify-center')])[2]")
	WebElement proceedButton;

	@FindBy(id = "kyc-provider-header")
	WebElement ekycServiceProvidersHeader;

	@FindBy(xpath = "(//h3[contains(@class,'kyc-box-header mt-2.5')])[1]")
	WebElement ekycProvidersFoundationalId1;

	@FindBy(xpath = "(//h3[contains(@class,'kyc-box-header mt-2.5')])[2]")
	WebElement ekycProvidersFoundationalId2;

	@FindBy(id = "proceed-preview-button")
	WebElement proceedButtonEnabled;

	@FindBy(id = "cancel-preview-button")
	WebElement ekycProviderCancelButton;

	@FindBy(xpath = "//h2[@class='font-semibold flex flex-col items-center justify-center gap-y-4 text-[1.5em]']")
	WebElement ekycProviderWarningPopup;

	@FindBy(xpath = "//p[@class='text-center text-muted-dark-gray text-md']")
	WebElement ekycProviderWarningMessagePopup;

	@FindBy(id = "proceed-preview-button")
	WebElement ekycProviderProceedButton;

	@FindBy(id = "tnc-header")
	WebElement ekycTermsAndConditionsHeader;

	@FindBy(id = "tnc-sub-header")
	WebElement ekycTermsAndConditionsSubHeader;

	@FindBy(id = "tnc-content")
	WebElement ekycTermsAndConditionsContent;

	@FindBy(xpath = "//div[@class='scrollable-div tnc-content flex text-justify sm:py-0 sm:ps-0']")
	WebElement ekycTermsAndConditionsContentScrollBar;

	@FindBy(id = "consent-button")
	WebElement ekycTermsAndConditionsCheckbox;

	@FindBy(xpath = "//p[@class='tnc-consent-text ml-2']")
	WebElement eKycTermsCheckboxText;

	@FindBy(id = "cancel-tnc-button")
	WebElement eKycTermsAndConditionsCancelButton;

	@FindBy(id = "stay-button")
	WebElement eKycTermsAndConditionsStayButton;

	@FindBy(id = "proceed-tnc-button")
	WebElement eKycTermsAndConditionsProceedButton;

	public boolean isEkycProcessStepsScreenLabelDisplayed() {
		return isElementVisible(ekycProcessStepsScreenLabel, "Verified eKyc screen is displayed");
	}

	// Curly right-single-quote (’), matching the literal character used in
	// oidc-ui's en.json ("consent_details_rejected"/"consent_request_rejected"),
	// not a straight apostrophe - an exact-text match would otherwise silently
	// fail against the real rendered copy.
	private static final String CONSENT_REJECTED_MESSAGE = "We’re sorry! Your login was unsuccessful as consent was not shared.";
	private static final Duration RELYING_PARTY_REDIRECT_WAIT = Duration.ofSeconds(30);

	/**
	 * Confirms the redirect back to the relying party after abandoning the
	 * eKYC process steps screen (e.g. via browser back + confirming the leave
	 * site prompt) carries the "consent not shared" message. Doesn't pin an
	 * exact error=<code> URL param, since oidc-ui's locale file maps two
	 * different error codes ("consent_details_rejected" and
	 * "consent_request_rejected") to this identical message and it isn't
	 * confirmed which one this specific abandonment path actually triggers -
	 * checking the rendered message text directly is the more robust check.
	 */
	public boolean waitForConsentRejectedRedirect() {
		WebDriverWait wait = new WebDriverWait(driver, RELYING_PARTY_REDIRECT_WAIT);
		try {
			wait.until(d -> d.getCurrentUrl().contains("error="));
		} catch (TimeoutException e) {
			return false;
		}

		String pageText = driver.findElement(By.tagName("body")).getText();
		return pageText.contains(CONSENT_REJECTED_MESSAGE);
	}

	public boolean isAuthorizationFailedPopupDisplayed() {
		return isElementVisible(authorizationFailedMessage,
				"Verified 'Unable to authorize. Please try again.' popup is displayed");
	}

	public void clickOkayOnAuthorizationFailedPopup() {
		clickOnElement(authorizationFailedOkayButton, "Clicked Okay on the authorization failed popup");
	}

	public boolean isEkycStep1TitleChooseEkycProviderDisplayed() {
		return isElementVisible(eKycStep1Title, "Verified the title of step 1 in eKyc screen");
	}

	public boolean isEkycStep1SubtitleDisplayed() {
		return isElementVisible(eKycStep1Subtitle, "Verified the sub-title of step 1 in eKyc screen");
	}

	public boolean isEkycStep2TitleTermsAndConditionsDisplayed() {
		return isElementVisible(eKycStep2Title, "Verified the title of step 2 in eKyc screen");
	}

	public boolean isEkycStep2SubtitleDisplayed() {
		return isElementVisible(eKycStep2Subtitle, "Verified the sub-title of step 2 in ekyc screen");
	}

	public boolean isEkycStep3TitlePreVerificationGuideDisplayed() {
		return isElementVisible(eKycStep3Title, "Verified the title of step 3 in eKyc screen");
	}

	public boolean isEkycStep3SubtitleDisplayed() {
		return isElementVisible(eKycStep3Subtitle, "Verified the sub-title of step 3 in ekyc screen");
	}

	public boolean isEkycStep4TitleIdentityVerificationDisplayed() {
		return isElementVisible(eKycStep4Title, "Verified the title of step 4 in eKyc screen");
	}

	public boolean isEkycStep4SubtitleDisplayed() {
		return isElementVisible(eKycStep4Subtitle, "Verified the sub-title of step 4 in ekyc screen");
	}

	public boolean isEkycStep5TitleReviewConsentDisplayed() {
		return isElementVisible(eKycStep5Title, "Verified the title of step 5 in eKyc screen");
	}

	public boolean isEkycStep5SubtitleDisplayed() {
		return isElementVisible(eKycStep5Subtitle, "Verified the sub-title of step 5 in ekyc screen");
	}

	public boolean isCancelWarningPopupDisplayed() {
		return isElementVisible(ekycWarningPopupStayButton, "Verified cancel warning popup is displayed");
	}

	public boolean isWarningPopupHeaderDisplayed() {
		return isElementVisible(ekycWarningPopupHeader, "Verified warning popup header is displayed");
	}

	public boolean isWarningPopupMessageDisplayed() {
		return isElementVisible(ekycWarningPopupMessage, "Verified warning popup message is displayed");
	}

	public boolean isStayButtonVisible() {
		return isElementVisible(ekycWarningPopupStayButton, "Verified stay button is visible in warning popup");
	}

	public boolean isDiscontinueButtonVisible() {
		return isElementVisible(ekycWarningPopupDiscontinueButton,
				"Verified discontinue button is visible in warning popup");
	}

	public void clickOnStayButton() {
		clickOnElement(ekycWarningPopupStayButton, "Clicked on stay button in warning popup");
	}

	public boolean isEkyScreenVisible() {
		return isElementVisible(ekycProcessStepsScreenLabel, "Verified eKyc process steps screen is visible");
	}

	public void clickOnDiscontinueButton() {
		clickOnElement(ekycWarningPopupDiscontinueButton, "Clicked on discontinue button in warning popup");
	}

	public boolean isLoginPageDisplayed() {
		return isElementVisible(relyingPartyLoginPage, "Verified Login page is displayed");
	}

	public void clickOnSignInWithEsignetButton() {
		clickOnElement(signInWithEsignetButton, "Clicked on sign in with eSignet button");
	}

	public boolean isProceedButtonVisible() {
		return isElementVisible(proceedButton, "Verified proceed button is visible");
	}

	public void clickOnProceedButton() {
		clickOnElement(proceedButton, "Clicked on proceed button");
	}

	public boolean isEkycServiceProviderScreenVisible() {
		return isElementVisible(ekycServiceProvidersHeader, "Verified eKyc service provider screen is visible");
	}

	public boolean isEkycProviderHeaderTitleDisplayed() {
		return isElementVisible(ekycServiceProvidersHeader, "Verified header title in eKYC service provider screen");
	}

	public boolean isEkycSpecificProviderNameDisplayed() {
		return isElementVisible(ekycProvidersFoundationalId1,
				"Verified specific provider name in eKYC service provider screen");
	}

	public boolean isEkycProviderFoundationalIdsDisplayed() {
		return isElementVisible(ekycProvidersFoundationalId1,
				"Verified provider foundational ID 1 in eKyc service provider screen")
				&& isElementVisible(ekycProvidersFoundationalId2,
						"Verified provider foundational ID 2 in eKyc service provider screen");
	}

	public boolean isProceedButtonEnabled() {
		return isButtonEnabled(proceedButtonEnabled,
				"Verified Proceed button is enabled  in eKYC service provider screen");

	}

	public boolean isProceedButtonNotClickable() {
		return !isButtonEnabled(proceedButtonEnabled,
				"Verified proceed button is not clickable in eKYC service provider screen");
	}

	public boolean isCancelButtonInEkycProviderScreenVisible() {
		return isElementVisible(ekycProviderCancelButton,
				"Verified cancel button is displayed in list of eKyc provider screen");
	}

	public void clickOnCancelButtonInEkycProviderScreen() {
		clickOnElement(ekycProviderCancelButton, "Clicked on cancel button in the list of eKyc providers screen");
	}

	public boolean isEkycProviderCancelWarningPopupDisplayed() {
		return isElementVisible(ekycProviderWarningPopup, "Verified cancel warning popup is displayed");
	}

	public boolean isEkycProviderWarningPopupHeaderDisplayed() {
		return isElementVisible(ekycProviderWarningPopup, "Verified warning popup header is displayed");
	}

	public boolean isEkycProviderWarningPopupMessageDisplayed() {
		return isElementVisible(ekycProviderWarningMessagePopup, "Verified warning popup message is displayed");
	}

	public boolean isCancelButtonVisible() {
		return isElementVisible(cancelButton, "Verified cancel button is visible on eKyc screen");
	}

	public void clickOnCancelButton() {
		clickOnElement(cancelButton, "Clicked on cancel button on eKyc screen");
	}

	public void clickOnSpecificProviderName() {
		clickOnElement(ekycProvidersFoundationalId1, "Clicked on specific provider name button");
	}

	public void clickOnProceedButtonInEkycProviderScreen() {
		clickOnElement(ekycProviderProceedButton, "Clicked on proceed button");
	}

	public boolean isEkycTermsAndConditionsScreenVisible() {
		return isElementVisible(ekycTermsAndConditionsHeader, "Verified eKyc terms and conditions screen is visible");
	}

	public boolean isTermsAndConditionHeaderDisplayed() {
		return isElementVisible(ekycTermsAndConditionsHeader, "Verified header in terms and condition screen");
	}

	public boolean isTermsAndConditionSubHeaderDisplayed() {
		return isElementVisible(ekycTermsAndConditionsSubHeader,
				"Verified sub header message in terms and condition screen");
	}

	public boolean isTermsAndConditionContentDisplayed() {
		return isElementVisible(ekycTermsAndConditionsContent,
				"Verified content message in terms and condition screen");
	}

	public boolean isTermsAndConditionContentScrollBarVisible() {
		return isElementVisible(ekycTermsAndConditionsContentScrollBar,
				"Verified content scroll bar is visible in terms and condition screen");
	}

	public boolean isTermsAndConditionCheckboxNotSelected() {
		return !ekycTermsAndConditionsCheckbox.isSelected() && isElementVisible(ekycTermsAndConditionsCheckbox,
				"Verified terms and conditions checkbox is not selected by default");
	}

	public void clickOnTermsAndConditionCheckBox() {
		clickOnElement(ekycTermsAndConditionsCheckbox, "Clicked on terms and conditions checkbox");
	}

	public boolean isTermsCheckboxTextDisplayed() {
		return isElementVisible(eKycTermsCheckboxText,
				"Verified text beside terms and conditions checkbox is displayed");
	}

	public boolean isCancelButtonInTermsAndConditionScreenVisible() {
		return isElementVisible(eKycTermsAndConditionsCancelButton,
				"Verified cancel button is displayed in terms and conditions screen");
	}

	public void clickOnCancelButtonInTermsAndConditionScreen() {
		clickOnElement(eKycTermsAndConditionsCancelButton, "Clicked on cancel button on terms and condition screen");
	}

	public boolean isEkycTermsAndConditionWarningPopupDisplayed() {
		return isElementVisible(eKycTermsAndConditionsStayButton, "Verified cancel warning popup is displayed");
	}

	public boolean isEkycTermsAndConditionProceedButtonDisplayed() {
		return isElementVisible(eKycTermsAndConditionsProceedButton, "Verified proceed button is displayed");
	}

	public boolean isTermsProceedButtonEnabled() {
		return isButtonEnabled(eKycTermsAndConditionsProceedButton,
				"Verified Proceed button is enabled in terms and conditions screen");
	}

	public boolean isEkycTermsAndConditionScreenVisible() {
		return isElementVisible(ekycTermsAndConditionsHeader, "Verified eKyc terms and condition screen is visible");
	}

}