package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v134.fetch.Fetch;
import org.openqa.selenium.devtools.v134.fetch.model.HeaderEntry;
import org.openqa.selenium.devtools.v134.fetch.model.RequestPattern;
import org.openqa.selenium.devtools.v134.fetch.model.RequestStage;
import org.openqa.selenium.devtools.v134.network.Network;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.yaml.snakeyaml.Yaml;

import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTestUtil {
	private static final Logger LOGGER = Logger.getLogger(BaseTestUtil.class.getName());
	private static final ThreadLocal<String> scenarioBrowserThreadLocal = new ThreadLocal<>();
	private static final ThreadLocal<String> threadLocalLanguage = new ThreadLocal<>();
	private static final ThreadLocal<String> cameraPermissionModeThreadLocal = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> ignoreUnhandledPromptsThreadLocal = new ThreadLocal<>();

	public static URI getBrowserStackUrl() {
		String accessKey = StringUtils.isBlank(EsignetConfigManager.getproperty("browserstack_access_key"))
				? getKeyValueFromYaml("/browserstack.yml", "accessKey")
				: EsignetConfigManager.getproperty("browserstack_access_key");
		String userName = StringUtils.isBlank(EsignetConfigManager.getproperty("browserstack_username"))
				? getKeyValueFromYaml("/browserstack.yml", "userName")
				: EsignetConfigManager.getproperty("browserstack_username");
		try {
			return new URI("https://" + userName + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub");
		} catch (URISyntaxException e) {
			throw new RuntimeException("Invalid BrowserStack URI", e);
		}
	}

	public static String getKeyValueFromYaml(String filePath, String key) {
		try (FileReader reader = new FileReader(System.getProperty("user.dir") + filePath)) {
			Yaml yaml = new Yaml();
			Object data = yaml.load(reader);
			if (data instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) data;
				return map.get(key);
			} else {
				throw new RuntimeException("Invalid YAML format, expected a map");
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("YAML file not found: " + filePath, e);
		} catch (IOException e) {
			throw new RuntimeException("Error closing FileReader for: " + filePath, e);
		}
	}

	public static List<DesiredCapabilities> getAllCapabilities() {
		List<DesiredCapabilities> capsList = new ArrayList<>();
		String browsers = EsignetConfigManager.getProperty("browsers", EsignetConfigManager.getproperty("browserName"));

		for (String browser : browsers.split(",")) {
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability("browserName", browser.trim());
			caps.setCapability("browserVersion", EsignetConfigManager.getproperty("browserVersion"));

			HashMap<String, Object> bsOptions = new HashMap<>();
			bsOptions.put("os", EsignetConfigManager.getproperty("browserStackOs"));
			bsOptions.put("osVersion", EsignetConfigManager.getproperty("osVersion"));
			bsOptions.put("projectName", "MOSIP ESignet UI Test");
			bsOptions.put("local", true);
			bsOptions.put("sessionName", "ESignet-" + Thread.currentThread().getId());
			caps.setCapability("bstack:options", bsOptions);

			if (browser.equalsIgnoreCase("chrome")) {
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("--use-fake-ui-for-media-stream"); // auto allow camera
				chromeOptions.addArguments("--use-fake-device-for-media-stream");

				caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			}

			else if (browser.equalsIgnoreCase("firefox")) {
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.addPreference("media.navigator.streams.fake", true);
				firefoxOptions.addPreference("media.navigator.permission.disabled", true);
				caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
			}

			else if (browser.equalsIgnoreCase("edge")) {
				EdgeOptions edgeOptions = new EdgeOptions();
				edgeOptions.addArguments("--use-fake-ui-for-media-stream");
				edgeOptions.addArguments("--use-fake-device-for-media-stream");
				caps.setCapability(EdgeOptions.CAPABILITY, edgeOptions);
			}

			else if (browser.equalsIgnoreCase("safari")) {
				LOGGER.info("Note: Safari does not support auto-allow camera via options.");
			}
			capsList.add(caps);
		}
		return capsList;
	}

	public static WebDriver getWebDriverInstance(String browserName) throws MalformedURLException {
		URL remoteUrl = getBrowserStackUrl().toURL();

		List<DesiredCapabilities> allCaps = getAllCapabilities();
		DesiredCapabilities caps = allCaps.stream()
				.filter(c -> c.getCapability("browserName").toString().equalsIgnoreCase(browserName)).findFirst()
				.orElse(allCaps.get(0)); // fallback

		LOGGER.info("Running on BrowserStack with browser: " + browserName);
		LOGGER.info("Running with capabilities: " + caps.toString());
		return new RemoteWebDriver(remoteUrl, caps);
	}

	/**
	 * @param setting 1 = allow, 2 = block, per Chrome's managed_default_content_settings values.
	 */
	private static void setCameraContentSetting(ChromeOptions chromeOptions, int setting) {
		Map<String, Object> prefs = new HashMap<>();
		Map<String, Object> profile = new HashMap<>();
		Map<String, Object> contentSettings = new HashMap<>();
		contentSettings.put("media_stream_camera", setting);
		profile.put("managed_default_content_settings", contentSettings);
		prefs.put("profile", profile);
		chromeOptions.setExperimentalOption("prefs", prefs);
	}

	public static WebDriver getLocalWebDriverInstance(String browser, boolean isMobile, String deviceName)
			throws IOException {
		browser = browser.toLowerCase();
		boolean isHeadless = Boolean.parseBoolean(EsignetConfigManager.getproperty("headless"));
		WebDriver driver;

		switch (browser) {
		case "chrome":
			if (System.getProperty("os.name").equalsIgnoreCase("Linux")
					&& "yes".equalsIgnoreCase(EsignetConfigManager.getDocker())) {
				String chromedriverPath = EsignetConfigManager.getProperty("chromeDriverPath", "/usr/bin/chromedriver");

				File driverFile = new File(chromedriverPath);

				if (!driverFile.exists() || !driverFile.canExecute()) {
					throw new RuntimeException("Invalid ChromeDriver path configured: " + chromedriverPath
							+ ". Ensure ChromeDriver exists and is executable.");
				}

				System.setProperty("webdriver.chrome.driver", chromedriverPath);

			} else {
				WebDriverManager.chromedriver().setup();
			}

			ChromeOptions chromeOptions = new ChromeOptions();
			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			chromeOptions.setCapability("goog:loggingPrefs", logPrefs);

			chromeOptions.addArguments("--use-fake-device-for-media-stream");
			chromeOptions.addArguments("--enable-media-stream");

			String cameraMode = getCameraPermissionMode();
			LOGGER.info("Camera permission mode for this scenario: " + cameraMode);

			switch (cameraMode) {
			case "denied":
				// Simulates the user clicking "Never allow" - camera_stream_camera=2 blocks
				// outright, no native prompt is shown, matching a persisted denial.
				setCameraContentSetting(chromeOptions, 2);
				break;
			case "prompt":
				// Leaves Chrome's camera permission undecided so the app's getUserMedia()
				// call lands on a real "prompt" state (checked via navigator.permissions.query,
				// since the native permission bubble itself isn't automatable).
				break;
			case "granted":
			default:
				chromeOptions.addArguments("--use-fake-ui-for-media-stream"); // auto-allow camera
				setCameraContentSetting(chromeOptions, 1);
				break;
			}

			if (getIgnoreUnhandledPrompts()) {
				chromeOptions.setCapability("unhandledPromptBehavior", "ignore");
			}

			// Enable mobile emulation if requested
			if (isMobile) {
				Map<String, String> mobileEmulation = new HashMap<>();
				mobileEmulation.put("deviceName", deviceName);
				chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
			}

			// Always set headless flags if needed
			if (isHeadless) {
				LOGGER.info("Running in headless mode");
				chromeOptions.addArguments("--headless=new");
				chromeOptions.addArguments("--disable-gpu");
				chromeOptions.addArguments("--window-size=1920x1080");
			}

			// Always add these for Docker safety
			chromeOptions.addArguments("--no-sandbox");
			chromeOptions.addArguments("--disable-dev-shm-usage");

			// Optional: allow Chrome to open a debugging port (harmless)
			chromeOptions.addArguments("--remote-debugging-port=0");

			LOGGER.info("Chrome args: " + chromeOptions);
			driver = new ChromeDriver(chromeOptions);
			break;

		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addPreference("media.navigator.streams.fake", true);
			firefoxOptions.addPreference("media.navigator.permission.disabled", true);

			if (isHeadless)
				firefoxOptions.addArguments("--headless");
			driver = new FirefoxDriver(firefoxOptions);
			break;

		case "edge":
			WebDriverManager.edgedriver().setup();
			EdgeOptions edgeOptions = new EdgeOptions();

			edgeOptions.addArguments("--use-fake-ui-for-media-stream");
			edgeOptions.addArguments("--use-fake-device-for-media-stream");
			edgeOptions.addArguments("--enable-media-stream");

			if (isHeadless)
				edgeOptions.addArguments("--headless=new");
			driver = new EdgeDriver(edgeOptions);
			break;

		case "safari":
			driver = new SafariDriver();
			LOGGER.info("Safari doesn’t support auto camera permissions via code");
			break;

		default:
			throw new IllegalArgumentException("Unsupported browser: " + browser);
		}

		return driver;
	}

	public static boolean isBrowserTagPresent(Scenario scenario) {
		return scenario.getSourceTagNames().stream().anyMatch(tag -> tag.toLowerCase().startsWith("@browser="));
	}

	public static String getBrowserForScenario(Scenario scenario) {
		return scenario.getSourceTagNames().stream().filter(tag -> tag.toLowerCase().startsWith("@browser="))
				.map(tag -> tag.split("=")[1]).findFirst().orElseGet(() -> {
					String fallback = getThreadLocalBrowser();
					return fallback != null ? fallback : EsignetConfigManager.getProperty("browserName", "chrome");
				});
	}

	/**
	 * Lets an @mobile scenario pick its Chrome device-emulation profile via an
	 * "@device=<name>" tag (e.g. "@device=iPhone 14" for an iOS-shaped viewport,
	 * "@device=Pixel 7" for Android), falling back to the global "mobileDevice"
	 * config property when no such tag is present.
	 */
	public static String getDeviceForScenario(Scenario scenario, String fallbackDevice) {
		return scenario.getSourceTagNames().stream().filter(tag -> tag.toLowerCase().startsWith("@device="))
				.map(tag -> tag.split("=", 2)[1]).findFirst().orElse(fallbackDevice);
	}

	public static List<String> getSupportedLocalBrowsers() {
		String browsers = EsignetConfigManager.getProperty("browsers", "chrome");
		return Arrays.stream(browsers.split(",")).map(String::toLowerCase).toList();
	}

	public static void setThreadLocalBrowser(String browser) {
		scenarioBrowserThreadLocal.set(browser);
	}

	public static String getThreadLocalBrowser() {
		return scenarioBrowserThreadLocal.get();
	}

	public static void setThreadLocalLanguage(String lang) {
		threadLocalLanguage.set(lang);
	}

	public static String getThreadLocalLanguage() {
		return threadLocalLanguage.get();
	}

	public static void setCameraPermissionMode(String mode) {
		cameraPermissionModeThreadLocal.set(mode);
	}

	public static String getCameraPermissionMode() {
		String mode = cameraPermissionModeThreadLocal.get();
		return mode != null ? mode : "granted";
	}

	public static void clearCameraPermissionMode() {
		cameraPermissionModeThreadLocal.remove();
	}

	/**
	 * ChromeDriver's default unhandledPromptBehavior silently auto-accepts
	 * beforeunload ("Leave site?") prompts, so by default the dialog can never be
	 * cancelled or asserted on. Scenarios that need to interact with it explicitly
	 * (via BasePage#acceptAlert/dismissAlert) must opt in via this flag.
	 */
	public static void setIgnoreUnhandledPrompts(boolean ignore) {
		ignoreUnhandledPromptsThreadLocal.set(ignore);
	}

	public static boolean getIgnoreUnhandledPrompts() {
		return Boolean.TRUE.equals(ignoreUnhandledPromptsThreadLocal.get());
	}

	public static void clearIgnoreUnhandledPrompts() {
		ignoreUnhandledPromptsThreadLocal.remove();
	}

	/**
	 * Flips the camera permission for the current origin mid-session via CDP,
	 * without a driver restart. Used for scenarios where the user grants access
	 * from browser settings after an earlier denial (e.g. TC_Pre_Video_Preview_07).
	 */
	public static void setCameraPermissionAtRuntime(WebDriver driver, String setting) {
		if (!(driver instanceof ChromeDriver)) {
			LOGGER.warning("CDP permission override skipped: not a ChromeDriver session");
			return;
		}
		Map<String, Object> permission = new HashMap<>();
		permission.put("name", "camera");

		URI currentUri = URI.create(driver.getCurrentUrl());
		String origin = currentUri.getScheme() + "://" + currentUri.getAuthority();

		Map<String, Object> params = new HashMap<>();
		params.put("permission", permission);
		params.put("setting", setting); // "granted" | "denied" | "prompt"
		params.put("origin", origin);

		((ChromeDriver) driver).executeCdpCommand("Browser.setPermission", params);
	}

	/**
	 * Simulates disconnecting the network via CDP right at the point the test
	 * needs it (e.g. immediately after clicking Proceed), rather than relying on
	 * actually toggling the host machine's Wi-Fi/adapter mid-scenario.
	 */
	public static void setNetworkOffline(WebDriver driver, boolean offline) {
		if (!(driver instanceof ChromeDriver)) {
			LOGGER.warning("CDP network override skipped: not a ChromeDriver session");
			return;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("offline", offline);
		params.put("latency", 0);
		params.put("downloadThroughput", offline ? 0 : -1);
		params.put("uploadThroughput", offline ? 0 : -1);

		((ChromeDriver) driver).executeCdpCommand("Network.emulateNetworkConditions", params);
	}

	/**
	 * Attaches a CDP listener that records a timestamp (epoch millis) every time
	 * a request matching urlSubstring is sent, for as long as the returned list
	 * is being appended to. Used to verify polling contracts (e.g. slot
	 * availability checked every 6s, max 10 times) that aren't observable from
	 * the DOM alone.
	 */
	public static List<Long> captureRequestTimestamps(WebDriver driver, String urlSubstring) {
		List<Long> timestamps = Collections.synchronizedList(new ArrayList<>());
		if (!(driver instanceof HasDevTools)) {
			LOGGER.warning("Network request capture skipped: driver does not support DevTools");
			return timestamps;
		}
		DevTools devTools = ((HasDevTools) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
		devTools.addListener(Network.requestWillBeSent(), request -> {
			if (request.getRequest().getUrl().contains(urlSubstring)) {
				timestamps.add(System.currentTimeMillis());
				LOGGER.info("Captured request to " + urlSubstring + " at " + System.currentTimeMillis());
			}
		});
		return timestamps;
	}

	/** Content-type and response status of a captured POST submission. */
	public static class CapturedSubmission {
		public final String contentType;
		public final int statusCode;

		public CapturedSubmission(String contentType, int statusCode) {
			this.contentType = contentType;
			this.statusCode = statusCode;
		}
	}

	/**
	 * Attaches a CDP listener that records the Content-Type header and response
	 * status of every POST request matching urlSubstring. Used to prove the
	 * registration submit request is actually sent as multipart/form-data (the
	 * browser sets this header itself when a File/Blob is part of a FormData
	 * body, so it can't be asserted from the DOM) and that the server accepted
	 * it. Must be called before the action that triggers the submission (e.g.
	 * before clicking Continue on the setup account page).
	 */
	public static List<CapturedSubmission> captureFormSubmissions(WebDriver driver, String urlSubstring) {
		List<CapturedSubmission> captured = Collections.synchronizedList(new ArrayList<>());
		if (!(driver instanceof HasDevTools)) {
			LOGGER.warning("Network request capture skipped: driver does not support DevTools");
			return captured;
		}
		DevTools devTools = ((HasDevTools) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

		Map<String, String> pendingContentTypes = Collections.synchronizedMap(new HashMap<>());

		devTools.addListener(Network.requestWillBeSent(), request -> {
			String url = request.getRequest().getUrl();
			if (url.contains(urlSubstring) && "POST".equalsIgnoreCase(request.getRequest().getMethod())) {
				String contentType = request.getRequest().getHeaders().entrySet().stream()
						.filter(entry -> entry.getKey().equalsIgnoreCase("Content-Type")).map(Map.Entry::getValue)
						.map(String::valueOf).findFirst().orElse("");
				pendingContentTypes.put(request.getRequestId().toString(), contentType);
				LOGGER.info("Captured registration submit request to " + url + " with content-type " + contentType);
			}
		});

		devTools.addListener(Network.responseReceived(), response -> {
			String contentType = pendingContentTypes.remove(response.getRequestId().toString());
			if (contentType != null) {
				captured.add(new CapturedSubmission(contentType, response.getResponse().getStatus()));
				LOGGER.info("Registration submit response status: " + response.getResponse().getStatus());
			}
		});

		return captured;
	}

	/** Full request headers and response status of a captured request, regardless of HTTP method. */
	public static class CapturedRequest {
		public final Map<String, Object> headers;
		public final int statusCode;

		public CapturedRequest(Map<String, Object> headers, int statusCode) {
			this.headers = headers;
			this.statusCode = statusCode;
		}
	}

	/**
	 * Attaches a CDP listener that records the full request headers and response
	 * status of every request matching urlSubstring, regardless of HTTP method.
	 * Unlike captureFormSubmissions, this doesn't filter by method - used to
	 * prove a request carried no Authorization header at all (e.g. the KBI
	 * schema fetch embedded in the oauth-details call, which must be reachable
	 * without authentication).
	 */
	public static List<CapturedRequest> captureRequests(WebDriver driver, String urlSubstring) {
		List<CapturedRequest> captured = Collections.synchronizedList(new ArrayList<>());
		if (!(driver instanceof HasDevTools)) {
			LOGGER.warning("Network request capture skipped: driver does not support DevTools");
			return captured;
		}
		DevTools devTools = ((HasDevTools) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

		Map<String, Map<String, Object>> pendingHeaders = Collections.synchronizedMap(new HashMap<>());

		devTools.addListener(Network.requestWillBeSent(), request -> {
			String url = request.getRequest().getUrl();
			if (url.contains(urlSubstring)) {
				pendingHeaders.put(request.getRequestId().toString(), request.getRequest().getHeaders());
				LOGGER.info("Captured request to " + url);
			}
		});

		devTools.addListener(Network.responseReceived(), response -> {
			Map<String, Object> headers = pendingHeaders.remove(response.getRequestId().toString());
			if (headers != null) {
				captured.add(new CapturedRequest(headers, response.getResponse().getStatus()));
				LOGGER.info("Response status for captured request: " + response.getResponse().getStatus());
			}
		});

		return captured;
	}

	/**
	 * Intercepts every request matching urlSubstring via the CDP Fetch domain
	 * and fulfills it with a synthetic JSON body/status, instead of letting it
	 * reach the real backend. Used to deterministically reproduce backend error
	 * conditions (e.g. a "response timed out" error code) that can't reliably
	 * be forced by actually waiting for a slow/hanging server. Must be called
	 * before the action that triggers the request.
	 */
	public static void mockApiErrorResponse(WebDriver driver, String urlSubstring, int statusCode, String jsonBody) {
		if (!(driver instanceof HasDevTools)) {
			throw new UnsupportedOperationException("Request mocking is only supported on Chromium-based drivers");
		}
		DevTools devTools = ((HasDevTools) driver).getDevTools();
		devTools.createSession();

		RequestPattern pattern = new RequestPattern(Optional.of("*" + urlSubstring + "*"), Optional.empty(),
				Optional.of(RequestStage.RESPONSE));
		devTools.send(Fetch.enable(Optional.of(List.of(pattern)), Optional.empty()));

		devTools.addListener(Fetch.requestPaused(), request -> {
			String url = request.getRequest().getUrl();
			if (url.contains(urlSubstring)) {
				String encodedBody = Base64.getEncoder().encodeToString(jsonBody.getBytes(StandardCharsets.UTF_8));
				List<HeaderEntry> headers = List.of(new HeaderEntry("Content-Type", "application/json"));
				devTools.send(Fetch.fulfillRequest(request.getRequestId(), statusCode, Optional.of(headers),
						Optional.empty(), Optional.of(encodedBody), Optional.empty()));
				LOGGER.info("Mocked response for " + url + " with status " + statusCode);
			} else {
				devTools.send(Fetch.continueRequest(request.getRequestId(), Optional.empty(), Optional.empty(),
						Optional.empty(), Optional.empty(), Optional.empty()));
			}
		});
	}

	private static final ThreadLocal<List<CapturedRequest>> kbiSchemaFetchCaptureThreadLocal = new ThreadLocal<>();

	/**
	 * The KBI schema is embedded in the oauth-details request/response that
	 * fires as soon as the authorize page loads - i.e. before any Cucumber step
	 * runs. Must be called from BaseTest right before the initial
	 * driver.get(authorizeUrl), gated by scenario tag, so the listener is
	 * attached before that request happens.
	 */
	public static void startCapturingKbiSchemaFetchRequest(WebDriver driver) {
		kbiSchemaFetchCaptureThreadLocal.set(captureRequests(driver, "oauth-details"));
	}

	public static List<CapturedRequest> getCapturedKbiSchemaFetchRequests() {
		return kbiSchemaFetchCaptureThreadLocal.get();
	}

	public static void clearKbiSchemaFetchCapture() {
		kbiSchemaFetchCaptureThreadLocal.remove();
	}

	private static final ThreadLocal<List<CapturedRequest>> uiSpecFetchCaptureThreadLocal = new ThreadLocal<>();

	/**
	 * The signup UI schema (ui-spec) is fetched by the Setup Account form as
	 * soon as it renders, which happens well after the initial page load. The
	 * DevTools listener stays attached across navigations within the same
	 * session, so starting this early (gated by the @signupUiSchemaFetch tag
	 * hook in BaseTest, before the first driver.get()) still catches it.
	 */
	public static void startCapturingUiSpecFetchRequest(WebDriver driver) {
		uiSpecFetchCaptureThreadLocal.set(captureRequests(driver, "ui-spec"));
	}

	public static List<CapturedRequest> getCapturedUiSpecFetchRequests() {
		return uiSpecFetchCaptureThreadLocal.get();
	}

	public static void clearUiSpecFetchCapture() {
		uiSpecFetchCaptureThreadLocal.remove();
	}

}