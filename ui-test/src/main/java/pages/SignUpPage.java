package pages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.BaseTestUtil;
import utils.BaseTestUtil.CapturedRequest;
import utils.EsignetConfigManager;

public class SignUpPage extends BasePage {

	public SignUpPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "signup-url-button")
	WebElement signUp;

	@FindBy(id = "register-button")
	WebElement registerButton;

	@FindBy(id = "phone")
	WebElement enterMobileNumberField;

	@FindBy(id = "form-submit-button")
	WebElement continueButton;

	@FindBy(xpath = "//div[@class='pincode-input-container']/input")
	List<WebElement> otpInputFields;

	@FindBy(id = "verify-otp-button")
	WebElement verifyOtpButton;

	@FindBy(id = "mobile-number-verified-continue-button")
	WebElement continueButtonInSuccessPage;

	@FindBy(xpath = "//div[@class='alternate-icon-div']")
	WebElement uploadPhoto;

	@FindBy(xpath = "//button[contains(@id,'capture-button')]")
	WebElement captureButton;

	@FindBy(id = "photoCapture-video")
	WebElement photoCaptureVideo;

	@FindBy(id = "photoCapture-flip-camera-button")
	WebElement flipCameraButton;

	@FindBy(id = "form-submit-button")
	WebElement setupContinueButton;

	@FindBy(xpath = "//div[@class='text-center text-lg font-semibold']")
	WebElement accountCreatedSuccessfullyMessage;

	@FindBy(xpath = "//*[contains(text(),'network connection dropped')]")
	WebElement networkErrorBanner;

	public boolean isNetworkErrorBannerDisplayed() {
		return isElementVisible(networkErrorBanner, "Verified network error banner is displayed on signup");
	}

	/**
	 * The banner is removed from the DOM (not just hidden) once connectivity
	 * recovers, so this polls findElements/isDisplayed defensively rather than
	 * relying on a single isElementVisible check that could hit a stale
	 * element reference mid-removal.
	 */
	public boolean waitUntilNetworkErrorBannerHidden() {
		long deadline = System.currentTimeMillis() + 15000;
		while (System.currentTimeMillis() < deadline) {
			try {
				List<WebElement> banners = driver
						.findElements(By.xpath("//*[contains(text(),'network connection dropped')]"));
				if (banners.stream().noneMatch(WebElement::isDisplayed)) {
					return true;
				}
			} catch (StaleElementReferenceException e) {
				// Banner was mid-removal; re-check next iteration.
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	public void clickOnSignUp() {
		clickOnElement(signUp,"Clicked on signup button");
	}

	public void navigateToSignupPortal() {
		driver.get(EsignetConfigManager.getSignupUrl());
	}

	private static final String SIGNUP_LANGUAGE_STORAGE_KEY = "esignet-signup-language";

	/**
	 * Clears any stored language preference and reloads, so the app has no
	 * signal to pick a language from and must fall back to its own default.
	 */
	public void clearStoredLanguagePreferenceAndReload() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.localStorage.removeItem(arguments[0]);", SIGNUP_LANGUAGE_STORAGE_KEY);
		driver.navigate().refresh();
	}

	public String getSignupLanguageFromLocalStorage() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (String) js.executeScript("return window.localStorage.getItem(arguments[0]);",
				SIGNUP_LANGUAGE_STORAGE_KEY);
	}

	public void clickOnRegisterButton() {
		clickOnElement(registerButton,"Clicked on register button");
	}

	public void enterMobileNumber(String number) {
		enterText(enterMobileNumberField, number,"Entered the mobile number");
	}

	public void clickOnContinueButton() {
		clickOnElement(continueButton,"Clicked on continue button");
	}

	public void enterOtp(String otp) {
		if (otp.length() > otpInputFields.size()) {
			throw new IllegalArgumentException("OTP length exceeds available input fields");
		}
		for (int i = 0; i < otp.length(); i++) {
			WebElement field = otpInputFields.get(i);
			field.click();
			field.sendKeys(String.valueOf(otp.charAt(i)));
		}
	}

	public void clickOnVerifyOtpButton() {
		clickOnElement(verifyOtpButton,"Clicked on verify otp button");
	}

	public void clickOnContinueButtonInSucessScreen() {
		clickOnElement(continueButtonInSuccessPage,"Clicked on continue button in success screen");
	}

	public void clickOnUploadPhoto() {
		clickOnElement(uploadPhoto,"Clicked on upload photo section");
	}

	public void clickOnCaptureButton() {
		new Actions(driver).pause(Duration.ofSeconds(1)).perform();
		clickOnElement(captureButton,"Clicked on Capture button");
	}

	public void clickOnSetupAccountContinueButton() {
		clickOnElement(setupContinueButton,"Clicked on continue button in account setup screen");
	}

	public boolean isAccountCreatedSuccessfullyMessageDisplayed() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		wait.until(ExpectedConditions.visibilityOf(accountCreatedSuccessfullyMessage));
		return isElementVisible(accountCreatedSuccessfullyMessage,"Verified account created successfully message displayed");
	}

	public boolean isSetupAccountPageDisplayed() {
		return isElementVisible(setupContinueButton, "Verified setup account page is still displayed");
	}

	/**
	 * Reads the deviceId of the active camera feeding the photo-capture video
	 * element. Used to prove a camera flip actually switched the underlying
	 * device, rather than just checking the flip button is clickable.
	 */
	public String getActivePhotoCaptureDeviceId() {
		waitForElementVisible(photoCaptureVideo);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		Object deviceId = js.executeScript(
				"var v = arguments[0];" + "if (!v.srcObject) return null;" + "var tracks = v.srcObject.getVideoTracks();"
						+ "return tracks.length ? tracks[0].getSettings().deviceId : null;",
				photoCaptureVideo);
		return deviceId == null ? null : deviceId.toString();
	}

	public void clickOnFlipCameraButton() {
		clickOnElement(flipCameraButton, "Clicked on flip camera button");
	}

	private static final String REGISTRATION_SUBMIT_ENDPOINT = "/v1/signup/registration";

	private List<BaseTestUtil.CapturedSubmission> registrationSubmissions;

	/**
	 * Reads the guidance tooltip shown when hovering the photo capture icon on
	 * the setup account form. Reuses the same react-tooltip container the
	 * Voluntary Claims info icon renders into (see ConsentPage).
	 */
	public String getPhotoIconTooltipText() {
		return getTooltipText(By.xpath("//div[@class='alternate-icon-div']"),
				By.xpath("//div[contains(@class,'react-tooltip')]"));
	}

	@FindBy(xpath = "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'camera')]")
	List<WebElement> cameraRelatedMessages;

	@FindBy(xpath = "//input[@type='file']")
	WebElement fallbackUploadInput;

	/**
	 * When camera access is denied, the app is expected to surface an
	 * explanatory message rather than silently failing. There's no dedicated
	 * element for this on the signup photo capture screen, so this scans for
	 * any visible text mentioning "camera" as a proxy for that message.
	 */
	public boolean isCameraAccessDeniedMessageDisplayed() {
		return cameraRelatedMessages.stream().anyMatch(this::isElementDisplayed);
	}

	/**
	 * Fallback path when capture isn't available: a plain file input should
	 * still let the user attach a photo.
	 */
	public boolean isFallbackUploadOptionDisplayed() {
		return isElementVisible(fallbackUploadInput, "Verified fallback upload option is visible");
	}

	/**
	 * Deliberate upload path for the face photo (as opposed to camera capture):
	 * attaches config/Photo.jpg through the fallback file input.
	 */
	public void uploadPhotoFromFallbackInput() throws IOException {
		File tempFile = File.createTempFile("upload-", "-Photo.jpg");
		tempFile.deleteOnExit();

		try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("config/Photo.jpg")) {
			if (resourceStream == null) {
				throw new IOException("Upload resource not found: config/Photo.jpg");
			}
			Files.copy(resourceStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		if (driver instanceof RemoteWebDriver) {
			((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
		}

		fallbackUploadInput.sendKeys(tempFile.getAbsolutePath());
	}

	/**
	 * Must be called before the action that submits the setup account form
	 * (e.g. before clicking Continue on the setup account page), so the
	 * multipart POST carrying the photo can be captured as it happens.
	 */
	public void startCapturingRegistrationSubmission() {
		registrationSubmissions = BaseTestUtil.captureFormSubmissions(driver, REGISTRATION_SUBMIT_ENDPOINT);
	}

	/**
	 * Confirms the registration submit request was sent as multipart/form-data
	 * (required for the photo to be accepted by the API) and that the server
	 * responded with a success status.
	 */
	public boolean isRegistrationSubmissionMultipartAndSuccessful() {
		if (registrationSubmissions == null || registrationSubmissions.isEmpty()) {
			return false;
		}
		return registrationSubmissions.stream()
				.anyMatch(submission -> submission.contentType != null
						&& submission.contentType.toLowerCase().contains("multipart/form-data")
						&& submission.statusCode >= 200 && submission.statusCode < 300);
	}

	/**
	 * Confirms the signup ui-spec (UI schema) fetch was captured, sent no
	 * Authorization/API-key header, and completed successfully. Capture must
	 * have been started before navigating to the signup portal via the
	 * @signupUiSchemaFetch tag hook in BaseTest, since the request fires as
	 * soon as the Setup Account form renders. The request itself is async, so
	 * this polls briefly rather than assuming it's already landed.
	 */
	public boolean isUiSpecFetchUnauthenticatedAndSuccessful() {
		List<CapturedRequest> requests = BaseTestUtil.getCapturedUiSpecFetchRequests();
		long deadline = System.currentTimeMillis() + 10000;
		while ((requests == null || requests.isEmpty()) && System.currentTimeMillis() < deadline) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			requests = BaseTestUtil.getCapturedUiSpecFetchRequests();
		}
		if (requests == null || requests.isEmpty()) {
			return false;
		}
		return requests.stream().allMatch(request -> {
			boolean hasAuthOrKeyHeader = request.headers.keySet().stream()
					.anyMatch(key -> key.equalsIgnoreCase("Authorization") || key.toLowerCase().contains("api-key")
							|| key.toLowerCase().contains("apikey"));
			boolean successful = request.statusCode >= 200 && request.statusCode < 300;
			return !hasAuthOrKeyHeader && successful;
		});
	}

}