package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v134.network.Network;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testng.SkipException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import constants.ESignetConstants;
import constants.UiConstants;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.JWKKeyUtil;
import io.mosip.testrig.apirig.utils.RestClient;
import io.mosip.testrig.apirig.utils.SecurityXSSException;
import io.restassured.response.Response;
import runners.Runner;

public class EsignetUtil extends AdminTestUtil {

    private static final Logger logger = Logger.getLogger(EsignetUtil.class);
    public static String pluginName = null;
    public static JSONArray signupActiveProfiles = null;

    private static final String TOKEN_URL = EsignetConfigManager.getproperty("keycloak-external-url")
            + EsignetConfigManager.getproperty("keycloakAuthTokenEndPoint");
    private static final String GRANT_TYPE = "client_credentials";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String ACCESS_TOKEN = "access_token";

    private static String partnerCookie = null;
    private static String mobileAuthCookie = null;
    protected static boolean triggerESignetKeyGenForPAR = true;
    protected static boolean triggerESignetKeyGenForPARPurposeLogin = true;
    protected static boolean triggerESignetKeyGenForPARPurposeLink = true;
    protected static boolean triggerESignetKeyGenForPARPurposeNone = true;
    protected static boolean triggerESignetKeyGenForPARNoPurpose = true;
    protected static boolean triggerESignetKeyGenForPARNoTitle = true;
    protected static boolean triggerESignetKeyGenForPAREmptyTitle = true;
    protected static boolean triggerESignetKeyGenForPARSingleAcrValue = true;
    protected static final String OIDC_JWK_FOR_PAR = "oidcJWKForPAR";
    protected static final String OIDC_JWK_FOR_PAR_PURPOSE_LOGIN = "oidcJWKForPARPurposeLogin";
    protected static final String OIDC_JWK_FOR_PAR_PURPOSE_LINK = "oidcJWKForPARPurposeLink";
    protected static final String OIDC_JWK_FOR_PAR_PURPOSE_NONE = "oidcJWKForPARPurposeNone";
    protected static final String OIDC_JWK_FOR_PAR_NO_PURPOSE = "oidcJWKForPARNoPurposeType";
    protected static final String OIDC_JWK_FOR_PAR_NO_TITLE = "oidcJWKForPARNoTitle";
    protected static final String OIDC_JWK_FOR_PAR_EMPTY_TITLE = "oidcJWKForPAREmptyTitle";
    protected static final String OIDC_JWK_FOR_PAR_SINGLE_ACR_VALUE = "oidcJWKForPARSingleAcrValue";
    protected static final String OIDC_JWK_FOR_PAR_UPDATED_TITLE = "oidcJWKForPARUpdatedTitle";
    protected static final String OIDC_JWK_FOR_PAR_TITLE_ONLY_LOGIN = "oidcJWKForPARTitleOnlyLogin";
    protected static final String OIDC_JWK_FOR_PAR_TITLE_ONLY_VERIFY = "oidcJWKForPARTitleOnlyVerify";
    protected static final String OIDC_JWK_FOR_PAR_TITLE_ONLY_LINK = "oidcJWKForPARTitleOnlyLink";
    protected static final String OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_LOGIN = "oidcJWKForPARSubtitleOnlyLogin";
    protected static final String OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_VERIFY = "oidcJWKForPARSubtitleOnlyVerify";
    protected static final String OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_LINK = "oidcJWKForPARSubtitleOnlyLink";
    protected static final String OIDC_JWK_FOR_PAR_EMPTY_PURPOSE_TYPE = "oidcJWKForPAREmptyPurposeType";
    protected static final String OIDC_JWK_FOR_PAR_MULTILANG_NAME = "oidcJWKForPARMultiLangName";
    protected static RSAKey oidc_JWK_Key_For_PAR = null;
    protected static final String CLAIMS_REQUEST = "config/claims.json";

    private static final String display = "popup";
    private static final String responseType = "code";
    private static final String client_assertion_type = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private static final String claim_locales = "en";
    private static final String scope = "openid profile";
    private static final String state = "eree2311";
    private static final String prompt = "consent";
    private static final String aud_key = "pushed_authorization_request_endpoint";

    private static Response sendPostRequest(String url, Map<String, String> params) {
        try {
            return RestClient.postRequestWithFormDataBody(url, params);
        } catch (Exception e) {
            logger.error("Error sending POST request to URL: " + url, e);
            return null;
        }
    }

    private static org.openqa.selenium.devtools.v134.network.model.Response lastResponse;
    private static DevTools devTools;
    private static WebDriver driver;

    public static void setDriver(WebDriver webDriver) {
        driver = webDriver;
    }

    // Initialize ChromeDriver with Network capture
    public static WebDriver startDriverWithNetwork() {
        driver = new ChromeDriver();
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.responseReceived(), response -> {
            lastResponse = response.getResponse();
        });

        return driver;
    }

    // Get last response status
    public static int getLastStatusCode() {
        return (lastResponse != null) ? lastResponse.getStatus() : -1;
    }

    // Check if a network request was made to a given endpoint
    public static boolean verifyRequestMade(String endpointPath) {
        return (lastResponse != null && lastResponse.getUrl().contains(endpointPath));
    }

    public static void setLogLevel() {
        if (EsignetConfigManager.IsDebugEnabled())
            logger.setLevel(Level.ALL);
        else
            logger.setLevel(Level.ERROR);
    }

    public static String getPluginName() {
        if (pluginName != null)
            return pluginName;
        pluginName = EsignetConfigManager.getproperty("pluginToExecute");
        return pluginName;
    }

    public static JSONArray signupActuatorResponseArray = null;

    public static String getValueFromSignupActuator(String section, String key) {

        String value = null;
        // Normalize the key for environment variables
        String keyForEnvVariableSection = key.toUpperCase().replace("-", "_").replace(".", "_");

        // Try to fetch profiles if not already fetched
        if (signupActiveProfiles == null || signupActiveProfiles.length() == 0) {
            signupActiveProfiles = getActiveProfilesFromActuator(UiConstants.SIGNUP_ACTUATOR_URL,
                    UiConstants.ACTIVE_PROFILES);
        }

        // First try to fetch the value from system environment
        value = getValueFromSignupActuatorWithUrl(UiConstants.SYSTEM_ENV_SECTION, keyForEnvVariableSection,
                UiConstants.SIGNUP_ACTUATOR_URL);

        // Fallback to other sections if value is not found
        if (value == null || value.isBlank()) {
            value = getValueFromSignupActuatorWithUrl(UiConstants.CLASS_PATH_APPLICATION_PROPERTIES, key,
                    UiConstants.SIGNUP_ACTUATOR_URL);
        }

        if (value == null || value.isBlank()) {
            value = getValueFromSignupActuatorWithUrl(UiConstants.CLASS_PATH_APPLICATION_DEFAULT_PROPERTIES, key,
                    UiConstants.SIGNUP_ACTUATOR_URL);
        }

        // Try fetching from active profiles if available
        if (value == null || value.isBlank()) {
            if (signupActiveProfiles != null && signupActiveProfiles.length() > 0) {
                for (int i = 0; i < signupActiveProfiles.length(); i++) {
                    String propertySection = signupActiveProfiles.getString(i).equals(UiConstants.DEFAULT_STRING)
                            ? UiConstants.MOSIP_CONFIG_APPLICATION_HYPHEN_STRING + signupActiveProfiles.getString(i)
                              + UiConstants.DOT_PROPERTIES_STRING
                            : signupActiveProfiles.getString(i) + UiConstants.DOT_PROPERTIES_STRING;

                    value = getValueFromSignupActuatorWithUrl(propertySection, key, UiConstants.SIGNUP_ACTUATOR_URL);

                    if (value != null && !value.isBlank()) {
                        break;
                    }
                }
            } else {
                logger.warn("No active profiles were retrieved.");
            }
        }

        // Fallback to a default section if no value found
        if (value == null || value.isBlank()) {
            value = getValueFromSignupActuatorWithUrl(EsignetConfigManager.getEsignetActuatorPropertySection(), key,
                    UiConstants.SIGNUP_ACTUATOR_URL);
        }

        // Final fallback to the original section if no value was found
        if (value == null || value.isBlank()) {
            value = getValueFromSignupActuatorWithUrl(section, key, UiConstants.SIGNUP_ACTUATOR_URL);
        }

        // Log the final result or an error message if not found
        if (value == null || value.isBlank()) {
            logger.error("Value not found for section: " + section + ", key: " + key);
        }

        return value;
    }

    public static String getValueFromSignupActuatorWithUrl(String section, String key, String url) {
        // Generate cache key based on the url, section, and key
        String actuatorCacheKey = url + section + key;
        String value = actuatorValueCache.get(actuatorCacheKey);

        if (value != null && !value.isEmpty()) {
            return value; // Return cached value if available
        }

        try {
            // Fetch the actuator response array if not already populated
            if (signupActuatorResponseArray == null) {
                Response response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
                JSONObject responseJson = new JSONObject(response.getBody().asString());
                signupActuatorResponseArray = responseJson.getJSONArray("propertySources");
            }

            // Search through the property sources for the section
            for (int i = 0, size = signupActuatorResponseArray.length(); i < size; i++) {
                JSONObject eachJson = signupActuatorResponseArray.getJSONObject(i);
                if (eachJson.get("name").toString().contains(section)) {
                    logger.info("Found properties: " + eachJson.getJSONObject(GlobalConstants.PROPERTIES));
                    value = eachJson.getJSONObject(GlobalConstants.PROPERTIES).getJSONObject(key)
                            .get(GlobalConstants.VALUE).toString();
                    if (EsignetConfigManager.IsDebugEnabled()) {
                        logger.info("Actuator: " + url + " key: " + key + " value: " + value);
                    }
                    break;
                }
            }

            // Cache the retrieved value
            if (value != null && !value.isEmpty()) {
                actuatorValueCache.put(actuatorCacheKey, value);
            }

            return value;
        } catch (JSONException e) {
            logger.error("Error parsing JSON for section: " + section + ", key: " + key + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching value for section: " + section + ", key: " + key + " - " + e.getMessage());
            return null;
        }
    }

    public static JSONArray getActiveProfilesFromActuator(String url, String key) {
        JSONArray activeProfiles = null;

        try {
            Response response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
            JSONObject responseJson = new JSONObject(response.getBody().asString());

            // If the key exists in the response, return the associated JSONArray
            if (responseJson.has(key)) {
                activeProfiles = responseJson.getJSONArray(key);
            } else {
                logger.warn("The key '" + key + "' was not found in the response.");
            }

        } catch (Exception e) {
            // Handle other errors like network issues, etc.
            logger.error("Error fetching active profiles from the actuator: " + e.getMessage());
        }

        return activeProfiles;
    }

    public static String generateMobileNumberFromRegex() {
        String regex = getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.identifier.regex");

        String phoneNumber = "";
        try {
            phoneNumber = AdminTestUtil.genStringAsperRegex(regex);
        } catch (Exception e) {
            logger.info("Phone Number is not generated with regex: " + e);
        }

        String countryCode = regex.substring(regex.indexOf('\\') + 1, regex.indexOf('['));
        phoneNumber = phoneNumber.replace(countryCode, "");

        return phoneNumber;
    }

    public static String getPasswordPattern() {
        return getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.password.pattern");
    }

    public static int getPasswordMinLength() {
        String value = getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.password.min-length");
        return Integer.parseInt(value);
    }

    public static int getPasswordMaxLength() {
        String value = getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.password.max-length");
        return Integer.parseInt(value);
    }

    public static String generateValidPasswordFromActuator() {
        int min = getPasswordMinLength();
        int max = getPasswordMaxLength();
        int length = min + new Random().nextInt(max - min + 1);

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "_!@#$%^&*";
        String all = upper + lower + digits + special;

        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(new Random().nextInt(upper.length())));
        password.append(lower.charAt(new Random().nextInt(lower.length())));
        password.append(digits.charAt(new Random().nextInt(digits.length())));
        password.append(special.charAt(new Random().nextInt(special.length())));

        for (int i = 4; i < length; i++) {
            password.append(all.charAt(new Random().nextInt(all.length())));
        }

        return password.toString();
    }

    private static JSONObject signupUISpecResponse;

    private static JSONObject getSignupUISpecResponse() {
        if (signupUISpecResponse == null) {
            try {
                logger.info("Loading Signup UI Spec from " + UiConstants.SIGNUP_UI_SPEC_URL);
                Response response = RestClient.getRequest(UiConstants.SIGNUP_UI_SPEC_URL, MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_JSON);
                signupUISpecResponse = new JSONObject(response.getBody().asString());
            } catch (Exception e) {
                logger.error("Failed to load Signup UI Spec from URL.", e);
                signupUISpecResponse = new JSONObject();
            }
        }
        return signupUISpecResponse;
    }

    public static String getFieldProperty(String fieldId, String property, String langCode) {
        try {
            JSONArray schema = getSignupUISpecResponse().optJSONObject("response").optJSONArray("schema");

            if (schema == null) {
                logger.warn("Schema missing in UI Spec");
                return null;
            }

            for (int i = 0; i < schema.length(); i++) {
                JSONObject field = schema.getJSONObject(i);
                if (fieldId.equals(field.optString("id"))) {

                    if (field.has(property) && field.opt(property) instanceof JSONObject) {
                        JSONObject obj = field.optJSONObject(property);
                        if (obj != null) {
                            String value = obj.optString(langCode, null);
                            logger.info(property + " for " + fieldId + " in " + langCode + ": " + value);
                            return value;
                        }
                    }

                    if ("validators".equals(property)) {
                        JSONArray validators = field.optJSONArray("validators");
                        if (validators == null)
                            continue;

                        List<String> regexList = new ArrayList<>();

                        for (int j = 0; j < validators.length(); j++) {
                            JSONObject validator = validators.getJSONObject(j);

                            if (validator.has("langCode")) {
                                if (langCode.equalsIgnoreCase(validator.optString("langCode"))) {
                                    String regex = validator.optString("regex", null);
                                    if (regex != null && !regex.isEmpty()) {
                                        logger.info("Regex for " + fieldId + " in " + langCode + ": " + regex);
                                        return regex;
                                    }
                                }
                            } else {
                                String regex = validator.optString("regex", null);
                                if (regex != null && !regex.isEmpty()) {
                                    regexList.add(regex);
                                }
                            }
                        }
                        if (!regexList.isEmpty()) {
                            StringBuilder combined = new StringBuilder();
                            for (String r : regexList) {
                                combined.append("(?=").append(r).append(")");
                            }
                            String combinedRegex = combined.append(".*").toString();
                            logger.info("Combined Regex for " + fieldId + ": " + combinedRegex);
                            return combinedRegex;
                        }
                    }
                }
            }

            logger.warn("No " + property + " for " + fieldId + " in " + langCode);
        } catch (Exception e) {
            logger.error("Error getting " + property + " for " + fieldId + " - " + langCode, e);
        }
        return null;
    }

    public static String getRegexForField(String fieldId, String langCode) {
        return getFieldProperty(fieldId, "validators", langCode);
    }

    public static String getRegexForFullName(String langCode) {
        return getRegexForField("fullName", langCode);
    }

    public static class FullName {
        public String english;
        public String khmer;
    }

    public static FullName generateNamesFromUiSpec() {
        String enRegex = getRegexForFullName("en");
        String kmRegex = getRegexForFullName("km");

        int enMax = extractMaxLength(enRegex);
        int kmMax = extractMaxLength(kmRegex);

        FullName fullName = new FullName();
        fullName.english = generateEnglishName(enMax);
        fullName.khmer = generateKhmerName(kmMax);

        return fullName;
    }

    private static int extractLength(String regex, boolean isMax) {
        if (regex == null)
            return isMax ? 10 : 2;
        int start = regex.indexOf('{');
        int end = regex.indexOf('}');
        if (start != -1 && end != -1) {
            String[] parts = regex.substring(start + 1, end).split(",");
            if (parts.length == 2)
                return Integer.parseInt(parts[isMax ? 1 : 0].trim());
            return Integer.parseInt(parts[0].trim());
        }
        return isMax ? 10 : 2;
    }

    public static int extractMaxLength(String regex) {
        return extractLength(regex, true);
    }

    public static int extractMinLength(String regex) {
        return extractLength(regex, false);
    }

    public static String generateEnglishName(int maxLength) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ";
        Random random = new Random();
        int length = 2 + random.nextInt(Math.max(1, maxLength - 1));
        StringBuilder name = new StringBuilder();
        name.append((char) ('A' + random.nextInt(26)));
        for (int i = 1; i < length; i++) {
            name.append(letters.charAt(random.nextInt(letters.length())));
        }
        return name.toString().trim();
    }

    public static String generateKhmerName(int maxLength) {
        Random random = new Random();
        int length = 2 + random.nextInt(Math.max(1, maxLength - 1));
        StringBuilder name = new StringBuilder();

        int[][] ranges = {{0x1780, 0x17FF}, {0x19E0, 0x19FF},};

        for (int i = 0; i < length; i++) {
            int[] range = ranges[random.nextInt(ranges.length)];
            int codePoint = range[0] + random.nextInt(range[1] - range[0] + 1);

            name.append((char) codePoint);
        }
        return name.toString();
    }

    public class RegisteredDetails {

        private static String registeredMobileNumber;
        private static String registeredFullName;
        private static String registeredPassword;

        public static String getMobileNumber() {
            return registeredMobileNumber;
        }

        public static void setMobileNumber(String mobileNumber) {
            registeredMobileNumber = mobileNumber;
        }

        public static String getPassword() {
            return registeredPassword;
        }

        public static void setPassword(String password) {
            registeredPassword = password;
        }

        public static String getFullName() {
            return registeredFullName;
        }

        public static void setFullName(String reisteredFullName) {
            registeredFullName = reisteredFullName;
        }
    }

    public static String getRegexForField(String fieldId) {
        return getRegexForField(fieldId, "en");
    }

    public static JSONArray getSignupSchemaArray() {
        JSONObject resp = null;
        try {
            resp = getSignupUISpecResponse().optJSONObject("response");
        } catch (Exception e) {
            return new JSONArray();
        }
        if (resp != null && resp.has("schema")) {
            return resp.optJSONArray("schema");
        }
        return new JSONArray();
    }

    /**
     * The ui-spec response carries a "language" object (mandatory/optional
     * language lists + langCodeMap) but is expected to not declare which one
     * is the default - the client is expected to fall back to English on its
     * own when no preference is stored. Confirmed against the live response:
     * {"mandatory":["eng","khm"],"optional":[],"langCodeMap":{"eng":"en","khm":"km"}}.
     */
    public static boolean isDefaultLanguageUnspecifiedInSignupSchema() {
        JSONObject resp = getSignupUISpecResponse().optJSONObject("response");
        if (resp == null) {
            return false;
        }
        JSONObject languageConfig = resp.optJSONObject("language");
        if (languageConfig == null) {
            return false;
        }
        for (String key : languageConfig.keySet()) {
            if (key.toLowerCase().contains("default")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Keys a benign UI-schema field definition is known to use (based on the
     * live signup ui-spec response). Anything outside this set is flagged as
     * "unexpected" rather than failed outright, since it may just be a new
     * legitimate field-metadata key the UI team added - unlike the sensitive
     * key/value checks below, which are hard failures.
     */
    private static final Set<String> SIGNUP_SCHEMA_ALLOWED_KEYS = Set.of("id", "controltype", "subtype", "type",
            "labelname", "placeholder", "validators", "required", "info", "alignmentgroup", "disabled", "prefix",
            "minage", "maxage", "format", "capslockcheck", "encrypt", "regex", "error", "langcode", "fieldcategory",
            "documenttypes", "description", "options", "value", "en", "eng", "khm", "km", "acceptedfiletypes",
            "maxfilesizemb");

    /** Substrings of a JSON key name that indicate it holds a credential/secret rather than UI field metadata. */
    private static final Set<String> SIGNUP_SCHEMA_SENSITIVE_KEY_TERMS = Set.of("password", "secret", "token",
            "apikey", "api_key", "clientsecret", "client_secret", "privatekey", "private_key", "accesskey",
            "access_key", "authorization", "credential", "connectionstring", "connection_string", "dburl", "db_url");

    private static final Pattern JWT_VALUE_PATTERN = Pattern.compile("^eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]*$");
    private static final Pattern AWS_ACCESS_KEY_PATTERN = Pattern.compile("AKIA[0-9A-Z]{16}");
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("-----BEGIN [A-Z ]*PRIVATE KEY-----");

    /**
     * Recursively scans the public signup ui-spec schema for anything that
     * looks like leaked PII, credentials/secrets, or non-UI business logic.
     * Returns a human-readable finding per issue; an empty list means the
     * schema only contains the plain field-metadata it's expected to.
     */
    public static List<String> findSensitiveDataInSignupUiSchema() {
        List<String> findings = new ArrayList<>();
        JSONArray schema = getSignupSchemaArray();
        for (int i = 0; i < schema.length(); i++) {
            scanSignupSchemaNode(schema.get(i), "schema[" + i + "]", findings);
        }
        return findings;
    }

    private static void scanSignupSchemaNode(Object node, String path, List<String> findings) {
        if (node instanceof JSONObject obj) {
            for (String key : obj.keySet()) {
                String lowerKey = key.toLowerCase();
                if (SIGNUP_SCHEMA_SENSITIVE_KEY_TERMS.stream().anyMatch(lowerKey::contains)) {
                    findings.add("Sensitive-looking key '" + key + "' found at " + path);
                } else if (!SIGNUP_SCHEMA_ALLOWED_KEYS.contains(lowerKey)) {
                    findings.add("Unexpected key '" + key + "' found at " + path + " (schema may not be minimal)");
                }
                scanSignupSchemaNode(obj.get(key), path + "." + key, findings);
            }
        } else if (node instanceof JSONArray arr) {
            for (int i = 0; i < arr.length(); i++) {
                scanSignupSchemaNode(arr.get(i), path + "[" + i + "]", findings);
            }
        } else if (node instanceof String str) {
            if (JWT_VALUE_PATTERN.matcher(str).matches() || AWS_ACCESS_KEY_PATTERN.matcher(str).find()
                    || PRIVATE_KEY_PATTERN.matcher(str).find()) {
                findings.add("Value at " + path + " looks like a credential/secret/token");
            }
        }
    }

    public static String generateEmailFromRegex(String regex) {
        if (regex == null || regex.isEmpty()) {
            return "user" + System.currentTimeMillis() + "@example.com";
        }

        String localChars;
        if (regex.contains("A-Z") && regex.contains("a-z")) {
            localChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-";
        } else if (regex.contains("a-z")) {
            localChars = "abcdefghijklmnopqrstuvwxyz0123456789._-";
        } else if (regex.contains("A-Z")) {
            localChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-";
        } else {
            localChars = "abcdefghijklmnopqrstuvwxyz0123456789";
        }

        Random random = new Random();
        int localLength = 6 + random.nextInt(5);
        StringBuilder localPart = new StringBuilder();
        for (int i = 0; i < localLength; i++) {
            localPart.append(localChars.charAt(random.nextInt(localChars.length())));
        }

        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "example.com"};
        String domain = domains[random.nextInt(domains.length)];

        String email = localPart + "@" + domain;

        return email;
    }

    public static String generateValueFromRegex(String regex) {
        return generateValueFromRegex(regex, -1);
    }

    public static String generateValueFromRegex(String regex, int exactLength) {
        if (regex == null || regex.isEmpty()) {
            return "defaultValue";
        }
        Random random = new Random();
        StringBuilder chars = new StringBuilder();

        if (regex.contains("A-Z"))
            chars.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        if (regex.contains("a-z"))
            chars.append("abcdefghijklmnopqrstuvwxyz");

        if (regex.contains("\\d") || regex.contains("0-9"))
            chars.append("0123456789");

        if (chars.length() == 0)
            chars.append("abcdefghijklmnopqrstuvwxyz");

        int min = 8, max = 8;

        if (exactLength > 0) {
            min = max = exactLength;
        } else {
            try {
                Matcher matcher = Pattern.compile("\\{(\\d*)(?:,(\\d*))?\\}").matcher(regex);

                if (matcher.find()) {
                    String minStr = matcher.group(1);
                    String maxStr = matcher.group(2);

                    min = minStr.isEmpty() ? 1 : Integer.parseInt(minStr);
                    max = (maxStr == null || maxStr.isEmpty()) ? min : Integer.parseInt(maxStr);

                    if (max < min) {
                        max = min;
                    }
                }
            } catch (Exception e) {
                min = 8;
                max = 8;
            }
        }

        int length = min + random.nextInt(max - min + 1);

        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append(chars.charAt(random.nextInt(chars.length())));
        }

        if (regex.contains("(?!0)") && value.charAt(0) == '0') {
            value.setCharAt(0, (char) ('1' + random.nextInt(9)));
        }

        return value.toString();
    }

    public static Map<String, Map<String, Object>> getUiSpecFields() {
        Map<String, Map<String, Object>> fieldsMap = new LinkedHashMap<>();

        JSONObject response = getSignupUISpecResponse().optJSONObject("response");
        if (response == null)
            return fieldsMap;

        JSONArray schema = response.optJSONArray("schema");
        if (schema == null)
            return fieldsMap;

        for (int i = 0; i < schema.length(); i++) {
            JSONObject field = schema.optJSONObject(i);
            if (field == null)
                continue;

            String fieldId = field.optString("id", null);
            if (fieldId != null) {
                Map<String, Object> fieldDetails = field.toMap();
                fieldsMap.put(fieldId, fieldDetails);
            }
        }

        return fieldsMap;
    }

    public static String getRandomDOB() {
        LocalDate today = LocalDate.now();
        LocalDate earliest = today.minusYears(120);
        long daysRange = ChronoUnit.DAYS.between(earliest, today);

        long randomDays = ThreadLocalRandom.current().nextLong(daysRange);
        LocalDate dob = earliest.plusDays(randomDays);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return dob.format(formatter);
    }

    private static boolean getTriggerESignetKeyGenForPAR() {
        return triggerESignetKeyGenForPAR;
    }

    private static void setTriggerESignetKeyGenForPAR(boolean value) {
        triggerESignetKeyGenForPAR = value;
    }

    private static boolean getTriggerESignetKeyGenForPARPurposeLogin() {
        return triggerESignetKeyGenForPARPurposeLogin;
    }

    private static void setTriggerESignetKeyGenForPARPurposeLogin(boolean value) {
        triggerESignetKeyGenForPARPurposeLogin = value;
    }

    private static boolean getTriggerESignetKeyGenForPARPurposeLink() {
        return triggerESignetKeyGenForPARPurposeLink;
    }

    private static void setTriggerESignetKeyGenForPARPurposeLink(boolean value) {
        triggerESignetKeyGenForPARPurposeLink = value;
    }

    private static boolean getTriggerESignetKeyGenForPARPurposeNone() {
        return triggerESignetKeyGenForPARPurposeNone;
    }

    private static void setTriggerESignetKeyGenForPARPurposeNone(boolean value) {
        triggerESignetKeyGenForPARPurposeNone = value;
    }

    private static boolean getTriggerESignetKeyGenForPARNoPurpose() {
        return triggerESignetKeyGenForPARNoPurpose;
    }

    private static void setTriggerESignetKeyGenForPARNoPurpose(boolean value) {
        triggerESignetKeyGenForPARNoPurpose = value;
    }

    private static boolean getTriggerESignetKeyGenForPARNoTitle() {
        return triggerESignetKeyGenForPARNoTitle;
    }

    private static void setTriggerESignetKeyGenForPARNoTitle(boolean value) {
        triggerESignetKeyGenForPARNoTitle = value;
    }

    private static boolean getTriggerESignetKeyGenForPARSingleAcrValue() {
        return triggerESignetKeyGenForPARSingleAcrValue;
    }

    private static void setTriggerESignetKeyGenForPARSingleAcrValue(boolean value) {
        triggerESignetKeyGenForPARSingleAcrValue = value;
    }

    private static boolean getTriggerESignetKeyGenForPAREmptyTitle() {
        return triggerESignetKeyGenForPAREmptyTitle;
    }

    private static void setTriggerESignetKeyGenForPAREmptyTitle(boolean value) {
        triggerESignetKeyGenForPAREmptyTitle = value;
    }

    public static void getSupportedLanguage() {

        if (EsignetConfigManager.getproperty("esignetSupportedLanguage") != null) {
            BaseTestCase.languageList
                    .add(EsignetConfigManager.getproperty(ESignetConstants.ESIGNET_SUPPORTED_LANGUAGE));
            logger.info("Supported Language = "
                    + EsignetConfigManager.getproperty(ESignetConstants.ESIGNET_SUPPORTED_LANGUAGE));
        } else {
            logger.error("Language not found");
        }
    }

    public static String inputstringKeyWordHandler(String jsonString, String testCaseName) {
        if (jsonString.contains("$ID:")) {
            jsonString = replaceIdWithAutogeneratedId(jsonString, "$ID:");
        }

        if (jsonString.contains(GlobalConstants.TIMESTAMP)) {
            jsonString = replaceKeywordWithValue(jsonString, GlobalConstants.TIMESTAMP, generateCurrentUTCTimeStamp());
        }

        if (jsonString.contains("$UNIQUENONCEVALUEFORESIGNET$")) {
            jsonString = replaceKeywordWithValue(jsonString, "$UNIQUENONCEVALUEFORESIGNET$",
                    String.valueOf(Calendar.getInstance().getTimeInMillis()));
        }

        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT$", OIDC_JWK_FOR_PAR);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR$", OIDC_JWK_FOR_PAR);

        // PURPOSE_LOGIN
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_PURPOSE_LOGIN$",
                OIDC_JWK_FOR_PAR_PURPOSE_LOGIN);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_PURPOSE_LOGIN$", OIDC_JWK_FOR_PAR_PURPOSE_LOGIN);

        // PURPOSE_LINK
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_PURPOSE_LINK$",
                OIDC_JWK_FOR_PAR_PURPOSE_LINK);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_PURPOSE_LINK$", OIDC_JWK_FOR_PAR_PURPOSE_LINK);

        // PURPOSE_NONE
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_PURPOSE_NONE$",
                OIDC_JWK_FOR_PAR_PURPOSE_NONE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_PURPOSE_NONE$", OIDC_JWK_FOR_PAR_PURPOSE_NONE);

        // NO PURPOSE
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_NO_PURPOSE$",
                OIDC_JWK_FOR_PAR_NO_PURPOSE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_NO_PURPOSE$", OIDC_JWK_FOR_PAR_NO_PURPOSE);

        // NO TITLE
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_NO_TITLE$",
                OIDC_JWK_FOR_PAR_NO_TITLE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_NO_TITLE$", OIDC_JWK_FOR_PAR_NO_TITLE);

        // EMPTY TITLE
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_EMPTY_TITLE$",
                OIDC_JWK_FOR_PAR_EMPTY_TITLE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_EMPTY_TITLE$", OIDC_JWK_FOR_PAR_EMPTY_TITLE);

        // SINGLE AUTH FACTOR
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_SINGLE_ACR_VALUE$",
                OIDC_JWK_FOR_PAR_SINGLE_ACR_VALUE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_SINGLE_ACR_VALUE$",
                OIDC_JWK_FOR_PAR_SINGLE_ACR_VALUE);

        // UPDATED TITLE
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_UPDATED_TITLE$",
                OIDC_JWK_FOR_PAR_UPDATED_TITLE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_UPDATED_TITLE$", OIDC_JWK_FOR_PAR_UPDATED_TITLE);

        // TITLE ONLY - PURPOSE LOGIN (verifies default login subtitle fallback)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_TITLE_ONLY_LOGIN$",
                OIDC_JWK_FOR_PAR_TITLE_ONLY_LOGIN);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_TITLE_ONLY_LOGIN$",
                OIDC_JWK_FOR_PAR_TITLE_ONLY_LOGIN);

        // TITLE ONLY - PURPOSE VERIFY (verifies default verify subtitle fallback)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_TITLE_ONLY_VERIFY$",
                OIDC_JWK_FOR_PAR_TITLE_ONLY_VERIFY);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_TITLE_ONLY_VERIFY$",
                OIDC_JWK_FOR_PAR_TITLE_ONLY_VERIFY);

        // TITLE ONLY - PURPOSE LINK (verifies default link subtitle fallback)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_TITLE_ONLY_LINK$",
                OIDC_JWK_FOR_PAR_TITLE_ONLY_LINK);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_TITLE_ONLY_LINK$",
                OIDC_JWK_FOR_PAR_TITLE_ONLY_LINK);

        // SUBTITLE ONLY - PURPOSE LOGIN (verifies default login title fallback)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_SUBTITLE_ONLY_LOGIN$",
                OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_LOGIN);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_SUBTITLE_ONLY_LOGIN$",
                OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_LOGIN);

        // SUBTITLE ONLY - PURPOSE VERIFY (verifies default verify title fallback)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_SUBTITLE_ONLY_VERIFY$",
                OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_VERIFY);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_SUBTITLE_ONLY_VERIFY$",
                OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_VERIFY);

        // SUBTITLE ONLY - PURPOSE LINK (verifies default link title fallback)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_SUBTITLE_ONLY_LINK$",
                OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_LINK);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_SUBTITLE_ONLY_LINK$",
                OIDC_JWK_FOR_PAR_SUBTITLE_ONLY_LINK);

        // EMPTY PURPOSE TYPE (verifies "select preferred ID to login" text once an auth type is chosen)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_EMPTY_PURPOSE_TYPE$",
                OIDC_JWK_FOR_PAR_EMPTY_PURPOSE_TYPE);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_EMPTY_PURPOSE_TYPE$",
                OIDC_JWK_FOR_PAR_EMPTY_PURPOSE_TYPE);

        // MULTI-LANGUAGE CLIENT NAME (ES-35: relying party name should follow selected UI language)
        jsonString = processClientAssertion(jsonString, "$CLIENT_ASSERTION_PAR_JWT_MULTILANG_NAME$",
                OIDC_JWK_FOR_PAR_MULTILANG_NAME);

        jsonString = processJWKKey(jsonString, "$OIDC_JWK_KEY_PAR_MULTILANG_NAME$", OIDC_JWK_FOR_PAR_MULTILANG_NAME);

        if (jsonString.contains("$ESIGNET_REDIRECT_URI$")) {
            jsonString = replaceKeywordWithValue(jsonString, "$ESIGNET_REDIRECT_URI$",
                    EsignetConfigManager.getproperty("baseurl") + "userprofile");
        }

        return jsonString;

    }

    private static String processClientAssertion(String jsonString, String placeholder, String jwkKeyName) {

        if (jsonString.contains(placeholder)) {

            String keyString = JWKKeyUtil.getJWKKey(jwkKeyName);
            RSAKey rsaKey;

            try {
                rsaKey = RSAKey.parse(keyString);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to parse JWK for placeholder " + placeholder + " (key=" + jwkKeyName + ")", e);
            }

            JSONObject root = new JSONObject(jsonString);
            String clientId = root.optString("client_id", null);
            String audKey = null;

            if (root.has("aud_key")) {
                audKey = root.optString("aud_key", null);
                root.remove("aud_key");
                jsonString = root.toString();
            }

            String url = getValueFromEsignetWellKnownEndPoint(audKey, EsignetConfigManager.getEsignetBaseUrl());

            if (clientId != null) {
                jsonString = replaceKeywordWithValue(jsonString, placeholder, signJWKKey(clientId, rsaKey, url));
            }
        }

        return jsonString;
    }

    private static final Set<String> generatedJwkKeys = ConcurrentHashMap.newKeySet();

    private static String processJWKKey(String jsonString, String placeholder, String jwkKeyName) {
        if (!jsonString.contains(placeholder))
            return jsonString;
        String jwkKey = generatedJwkKeys.add(jwkKeyName) ? JWKKeyUtil.generateAndCacheJWKKey(jwkKeyName)
                : JWKKeyUtil.getJWKKey(jwkKeyName);
        return replaceKeywordWithValue(jsonString, placeholder, jwkKey);
    }

    public static String getValueFromEsignetWellKnownEndPoint(String key, String baseURL) {
        String url = baseURL + EsignetConfigManager.getproperty("esignetWellKnownEndPoint");
        Response response = null;
        try {
            response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
            JSONObject responseJson = new JSONObject(response.getBody().asString());
            return responseJson.getString(key);
        } catch (Exception e) {
            logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
            return null;
        }
    }

    public static String signJWKKey(String clientId, RSAKey jwkKey, String tempUrl) {
        int idTokenExpirySecs = Integer
                .parseInt(getValueFromEsignetActuator(EsignetConfigManager.getEsignetActuatorPropertySection(),
                        GlobalConstants.MOSIP_ESIGNET_ID_TOKEN_EXPIRE_SECONDS));
        JWSSigner signer;

        try {
            signer = new RSASSASigner(jwkKey);

            Date currentTime = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);

            calendar.add(Calendar.SECOND, idTokenExpirySecs);

            Date expirationTime = calendar.getTime();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(clientId).audience(tempUrl).issuer(clientId)
                    .issueTime(currentTime).expirationTime(expirationTime).jwtID(UUID.randomUUID().toString()).build();

            logger.info("JWT current and expiry time " + currentTime + " & " + expirationTime);

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwkKey.getKeyID()).build(), claimsSet);

            signedJWT.sign(signer);
            clientAssertionToken = signedJWT.serialize();
        } catch (Exception e) {
            logger.error("Exception while signing oidcJWKKey for client assertion: " + e.getMessage());
        }
        return clientAssertionToken;
    }

    public static JSONObject getOauthDetailsBody() {
        if (driver == null) {
            logger.error("WebDriver not initialized. Call startDriverWithNetwork() or setDriver() first.");
            return null;
        }
        LogEntries logs = driver.manage().logs().get("performance");

        for (LogEntry log : logs) {
            try {
                JSONObject msg = new JSONObject(log.getMessage());
                JSONObject request = msg.getJSONObject("message").getJSONObject("params").optJSONObject("request");

                if (request == null)
                    continue;

                String url = request.optString("url", "");
                if (!url.contains("oauth-details"))
                    continue;

                String postData = request.optString("postData", "");
                if (!postData.isEmpty()) {
                    return new JSONObject(postData);
                }

            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static String isTestCaseValidForExecution(TestCaseDTO testCaseDTO) {
        String testCaseName = testCaseDTO.getTestCaseName();

        int indexof = testCaseName.indexOf("_");
        String modifiedTestCaseName = testCaseName.substring(indexof + 1);

        addTestCaseDetailsToMap(modifiedTestCaseName, testCaseDTO.getUniqueIdentifier());

        if (isCaptchaEnabled() == true) {
            GlobalMethods.reportCaptchaStatus(GlobalConstants.CAPTCHA_ENABLED, true);
            throw new SkipException(GlobalConstants.CAPTCHA_ENABLED_MESSAGE);
        }

        if (Runner.skipAll == true) {
            throw new SkipException(GlobalConstants.PRE_REQUISITE_FAILED_MESSAGE);
        }

        if (pluginName.equals("mock")) {
            BaseTestCase.setSupportedIdTypes(Arrays.asList("UIN"));

            String endpoint = testCaseDTO.getEndPoint();
            if (endpoint.contains("/esignet/") == false && endpoint.contains("/mock-identity-system/") == false) {
                throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
            }

        } else if (pluginName.equals("mosipid")) {
            getSupportedIdTypesValueFromActuator();

            logger.info("supportedIdType = " + supportedIdType);

            String endpoint = testCaseDTO.getEndPoint();
            if (endpoint.contains("/mock-identity-system/") == true
                    || ((testCaseName.equals("ESignetUI_CreateOIDCClient_all_Valid_Smoke_sid"))
                    && endpoint.contains("/v1/esignet/client-mgmt/client"))) {
                throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
            }
        }
        return testCaseName;
    }

    public static String getAuthTokenFromKeyCloak(String clientId, String clientSecret) {
        Map<String, String> params = new HashMap<>();
        params.put(CLIENT_ID, clientId);
        params.put(CLIENT_SECRET, clientSecret);
        params.put(GRANT_TYPE_KEY, GRANT_TYPE);

        Response response = sendPostRequest(TOKEN_URL, params);

        if (response == null) {
            return "";
        }
        logger.info(response.getBody().asString());

        JSONObject responseJson = new JSONObject(response.getBody().asString());
        return responseJson.optString(ACCESS_TOKEN, "");
    }

    public static String getAuthTokenByRole(String role) {
        if (role == null)
            return "";

        String roleLowerCase = role.toLowerCase();
        switch (roleLowerCase) {
            case "partner":
                if (!AdminTestUtil.isValidToken(partnerCookie)) {
                    partnerCookie = getAuthTokenFromKeyCloak(EsignetConfigManager.getPmsClientId(),
                            EsignetConfigManager.getPmsClientSecret());
                }
                return partnerCookie;
            case "mobileauth":
                if (!AdminTestUtil.isValidToken(mobileAuthCookie)) {
                    mobileAuthCookie = getAuthTokenFromKeyCloak(EsignetConfigManager.getMPartnerMobileClientId(),
                            EsignetConfigManager.getMPartnerMobileClientSecret());
                }
                return mobileAuthCookie;
            default:
                return "";
        }
    }

    public static Response postWithBodyAndBearerToken(String url, String jsonInput, String cookieName, String role,
                                                      String testCaseName, String idKeyName) {
        Response response = null;
        if (testCaseName.contains("Invalid_Token")) {
            token = "xyz";
        } else if (testCaseName.contains("NOAUTH")) {
            token = "";
        } else {
            token = getAuthTokenByRole(role);
        }
        logger.info(GlobalConstants.POST_REQ_URL + url);
        GlobalMethods.reportRequest(null, jsonInput, url);
        try {
            response = RestClient.postRequestWithBearerToken(url, jsonInput, MediaType.APPLICATION_JSON,
                    MediaType.APPLICATION_JSON, cookieName, token);
            GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

            return response;
        } catch (Exception e) {
            logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
            return response;
        }
    }

    protected static Response postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(String url, String jsonInput)
            throws SecurityXSSException {
        Response response = null;
        jsonInput = inputstringKeyWordHandler(jsonInput, "");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = mapper.readValue(jsonInput, Map.class);
            logger.info(GlobalConstants.POST_REQ_URL + url);
            logger.info(jsonInput);
            GlobalMethods.reportRequest(null, jsonInput, url);
            response = RestClient.postRequestWithFormDataBody(url, map);
            GlobalMethods.checkXSSProtectionHeader(response, url);
            GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

            return response;
        } catch (SecurityXSSException se) {
            String responseHeadersString = (response == null) ? "No response"
                    : response.getHeaders().asList().toString();
            String errorMessageString = "XSS check failed for URL: " + url + "\nHeaders: " + responseHeadersString
                    + "\nError: " + se.getMessage();
            logger.error(errorMessageString, se);
            throw se;
        } catch (Exception e) {
            logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
            return response;
        }
    }

    public static String generateParRequestUri(String clientIdKey, String clientAssertionPlaceholder)
            throws SecurityXSSException, JsonProcessingException {

        String baseUrl = EsignetConfigManager.getproperty("eSignetbaseurl");
        String parUrl = baseUrl + "/v1/esignet/oauth/par";

        org.json.simple.JSONObject claimRequest = getRequestJson(CLAIMS_REQUEST);
        JSONObject requestBody = new JSONObject();

        requestBody.put("display", display);
        requestBody.put("response_type", responseType);
        requestBody.put("nonce", "$UNIQUENONCEVALUEFORESIGNET$");
        if (clientIdKey == null || clientIdKey.isEmpty()) {
            clientIdKey = "$ID:CreateOIDCClient_all_Valid_Smoke_sid_clientId$";
        }
        requestBody.put("client_id", AdminTestUtil.replaceIdWithAutogeneratedId(clientIdKey, "$ID:"));
        requestBody.put("requestTime", "$TIMESTAMP$");
        requestBody.put("client_assertion_type", client_assertion_type);
        requestBody.put("claim_locales", claim_locales);
        requestBody.put("claims", claimRequest.toString());
        requestBody.put("scope", scope);
        requestBody.put("acr_values",
                "mosip:idp:acr:generated-code mosip:idp:acr:biometrics mosip:idp:acr:linked-wallet mosip:idp:acr:password");
        requestBody.put("redirect_uri", "$ESIGNET_REDIRECT_URI$");
        requestBody.put("state", state);
        requestBody.put("client_assertion", clientAssertionPlaceholder);
        requestBody.put("prompt", prompt);
        requestBody.put("aud_key", aud_key);

        Response response = postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(parUrl, requestBody.toString());

        if (response == null) {
            throw new RuntimeException("PAR request failed: null response");
        }

        JSONObject responseJson = new JSONObject(response.asString());

        if (!responseJson.has("request_uri")) {
            logger.error("PAR response missing request_uri: " + responseJson.toString());
            throw new RuntimeException("PAR response missing request_uri");
        }

        return responseJson.getString("request_uri");
    }

    public static String generateParRequestUriWithUpdatedClaim(String clientIdKey, String clientAssertionPlaceholder,
            String claimName, boolean essential) throws SecurityXSSException, JsonProcessingException {

        String baseUrl = EsignetConfigManager.getproperty("eSignetbaseurl");
        String parUrl = baseUrl + "/v1/esignet/oauth/par";

        JSONObject claimRequest = getClaimsRequestWithUpdatedClaim(claimName, essential);
        JSONObject requestBody = new JSONObject();

        requestBody.put("display", display);
        requestBody.put("response_type", responseType);
        requestBody.put("nonce", "$UNIQUENONCEVALUEFORESIGNET$");
        if (clientIdKey == null || clientIdKey.isEmpty()) {
            clientIdKey = "$ID:CreateOIDCClient_all_Valid_Smoke_sid_clientId$";
        }
        requestBody.put("client_id", AdminTestUtil.replaceIdWithAutogeneratedId(clientIdKey, "$ID:"));
        requestBody.put("requestTime", "$TIMESTAMP$");
        requestBody.put("client_assertion_type", client_assertion_type);
        requestBody.put("claim_locales", claim_locales);
        requestBody.put("claims", claimRequest.toString());
        requestBody.put("scope", scope);
        requestBody.put("acr_values",
                "mosip:idp:acr:generated-code mosip:idp:acr:biometrics mosip:idp:acr:linked-wallet mosip:idp:acr:password");
        requestBody.put("redirect_uri", "$ESIGNET_REDIRECT_URI$");
        requestBody.put("state", state);
        requestBody.put("client_assertion", clientAssertionPlaceholder);
        requestBody.put("prompt", prompt);
        requestBody.put("aud_key", aud_key);

        Response response = postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(parUrl, requestBody.toString());

        if (response == null) {
            throw new RuntimeException("PAR request failed: null response");
        }

        JSONObject responseJson = new JSONObject(response.asString());

        if (!responseJson.has("request_uri")) {
            logger.error("PAR response missing request_uri: " + responseJson.toString());
            throw new RuntimeException("PAR response missing request_uri");
        }

        return responseJson.getString("request_uri");
    }

    /**
     * Clones config/claims.json and flips the "essential" flag for the given claim,
     * leaving every other claim (including verified_claims) untouched so that a
     * previously verified claim stays verified across the relaunch.
     */
    private static JSONObject getClaimsRequestWithUpdatedClaim(String claimName, boolean essential) {
        org.json.simple.JSONObject original = getRequestJson(CLAIMS_REQUEST);
        JSONObject claims = new JSONObject(original.toString());
        JSONObject userinfo = claims.getJSONObject("userinfo");

        if (userinfo.has(claimName)) {
            userinfo.getJSONObject(claimName).put("essential", essential);
            return claims;
        }

        JSONArray verifiedClaims = userinfo.optJSONArray("verified_claims");
        if (verifiedClaims != null) {
            for (int i = 0; i < verifiedClaims.length(); i++) {
                JSONObject verifiedClaimAttrs = verifiedClaims.getJSONObject(i).optJSONObject("claims");
                if (verifiedClaimAttrs != null && verifiedClaimAttrs.has(claimName)) {
                    verifiedClaimAttrs.getJSONObject(claimName).put("essential", essential);
                    return claims;
                }
            }
        }

        throw new IllegalArgumentException(
                "Claim '" + claimName + "' was not found in " + CLAIMS_REQUEST + " to update its essential status");
    }

    public static String generateAuthorizeUrlWithUpdatedClaim(String claimName, boolean essential)
            throws SecurityXSSException, JsonProcessingException {

        String baseUrl = EsignetConfigManager.getproperty("eSignetbaseurl");
        String template = EsignetConfigManager.getproperty("authorizeUrlTemplate");
        String clientIdKey = "$ID:CreateOIDCClient_all_Valid_Smoke_sid_clientId$";

        String requestUri = generateParRequestUriWithUpdatedClaim(clientIdKey, "$CLIENT_ASSERTION_PAR_JWT$",
                claimName, essential);
        String clientId = AdminTestUtil.replaceIdWithAutogeneratedId(clientIdKey, "$ID:");
        String updatedTemplate = template.replace("$REQUEST_URI$", requestUri).replace("$CLIENT_ID$", clientId);

        return baseUrl + updatedTemplate;
    }

    /**
     * Clones config/claims.json and adds newClaimName as an additional
     * essential verified_claims entry alongside the existing ones (e.g.
     * "email"), which is left untouched. Used to force the Attention/claim-details
     * screen to reappear (a new claim needs verification) while a previously
     * verified claim still shows as verified on that same render, since simply
     * flipping an existing claim's essential/voluntary status doesn't reliably
     * do that (see getClaimsRequestWithUpdatedClaim).
     */
    private static JSONObject getClaimsRequestWithAdditionalVerifiedClaim(String newClaimName) {
        org.json.simple.JSONObject original = getRequestJson(CLAIMS_REQUEST);
        JSONObject claims = new JSONObject(original.toString());
        JSONObject userinfo = claims.getJSONObject("userinfo");

        JSONArray verifiedClaims = userinfo.optJSONArray("verified_claims");
        if (verifiedClaims == null || verifiedClaims.isEmpty()) {
            throw new IllegalStateException("No verified_claims entry found in " + CLAIMS_REQUEST + " to add to");
        }

        JSONObject verifiedClaimAttrs = verifiedClaims.getJSONObject(0).optJSONObject("claims");
        if (verifiedClaimAttrs == null) {
            throw new IllegalStateException("verified_claims[0] has no 'claims' object in " + CLAIMS_REQUEST);
        }

        JSONObject newClaim = new JSONObject();
        newClaim.put("essential", true);
        verifiedClaimAttrs.put(newClaimName, newClaim);

        return claims;
    }

    public static String generateAuthorizeUrlWithAdditionalVerifiedClaim(String newClaimName)
            throws SecurityXSSException, JsonProcessingException {
        return generateAuthorizeUrlForClaimsRequest(getClaimsRequestWithAdditionalVerifiedClaim(newClaimName));
    }

    /**
     * Clones config/claims.json and adds newClaimName as an additional plain
     * voluntary (non-verified) claim - one that isn't populated in the test
     * identity's data, so the relying party's claim is "unavailable". Used to
     * force the Attention/claim-details screen to reappear because a
     * voluntary claim can't be satisfied, without touching verification state.
     */
    private static JSONObject getClaimsRequestWithAdditionalVoluntaryClaim(String newClaimName) {
        org.json.simple.JSONObject original = getRequestJson(CLAIMS_REQUEST);
        JSONObject claims = new JSONObject(original.toString());
        JSONObject userinfo = claims.getJSONObject("userinfo");

        JSONObject newClaim = new JSONObject();
        newClaim.put("essential", false);
        userinfo.put(newClaimName, newClaim);

        return claims;
    }

    public static String generateAuthorizeUrlWithAdditionalVoluntaryClaim(String newClaimName)
            throws SecurityXSSException, JsonProcessingException {
        return generateAuthorizeUrlForClaimsRequest(getClaimsRequestWithAdditionalVoluntaryClaim(newClaimName));
    }

    private static String generateAuthorizeUrlForClaimsRequest(JSONObject claimRequest)
            throws SecurityXSSException, JsonProcessingException {

        String baseUrl = EsignetConfigManager.getproperty("eSignetbaseurl");
        String template = EsignetConfigManager.getproperty("authorizeUrlTemplate");
        String clientIdKey = "$ID:CreateOIDCClient_all_Valid_Smoke_sid_clientId$";

        String parUrl = baseUrl + "/v1/esignet/oauth/par";
        JSONObject requestBody = new JSONObject();

        requestBody.put("display", display);
        requestBody.put("response_type", responseType);
        requestBody.put("nonce", "$UNIQUENONCEVALUEFORESIGNET$");
        requestBody.put("client_id", AdminTestUtil.replaceIdWithAutogeneratedId(clientIdKey, "$ID:"));
        requestBody.put("requestTime", "$TIMESTAMP$");
        requestBody.put("client_assertion_type", client_assertion_type);
        requestBody.put("claim_locales", claim_locales);
        requestBody.put("claims", claimRequest.toString());
        requestBody.put("scope", scope);
        requestBody.put("acr_values",
                "mosip:idp:acr:generated-code mosip:idp:acr:biometrics mosip:idp:acr:linked-wallet mosip:idp:acr:password");
        requestBody.put("redirect_uri", "$ESIGNET_REDIRECT_URI$");
        requestBody.put("state", state);
        requestBody.put("client_assertion", "$CLIENT_ASSERTION_PAR_JWT$");
        requestBody.put("prompt", prompt);
        requestBody.put("aud_key", aud_key);

        Response response = postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(parUrl, requestBody.toString());
        if (response == null) {
            throw new RuntimeException("PAR request failed: null response");
        }

        JSONObject responseJson = new JSONObject(response.asString());
        if (!responseJson.has("request_uri")) {
            logger.error("PAR response missing request_uri: " + responseJson.toString());
            throw new RuntimeException("PAR response missing request_uri");
        }

        String clientId = AdminTestUtil.replaceIdWithAutogeneratedId(clientIdKey, "$ID:");
        String updatedTemplate = template.replace("$REQUEST_URI$", responseJson.getString("request_uri"))
                .replace("$CLIENT_ID$", clientId);

        return baseUrl + updatedTemplate;
    }

    public static String getIdentityPluginNameFromEsignetActuator() {
        if (pluginName != null && !pluginName.isBlank()) {
            return pluginName;
        }
        pluginName = getValueFromEsignetActuator(ESignetConstants.CLASS_PATH_APPLICATION_PROPERTIES,
                "mosip.esignet.integration.authenticator");
        return pluginName;
    }

    public static String generateParRequestWithoutNonceAndState() throws SecurityXSSException, JsonProcessingException {

        String baseUrl = EsignetConfigManager.getproperty("eSignetbaseurl");
        String parUrl = baseUrl + "/v1/esignet/oauth/par";

        org.json.simple.JSONObject claimRequest = getRequestJson(CLAIMS_REQUEST);
        JSONObject requestBody = new JSONObject();

        requestBody.put("display", display);
        requestBody.put("response_type", responseType);
        requestBody.put("client_id", AdminTestUtil
                .replaceIdWithAutogeneratedId("$ID:CreateOIDCClient_all_Valid_Smoke_sid_clientId$", "$ID:"));
        requestBody.put("requestTime", "$TIMESTAMP$");
        requestBody.put("client_assertion_type", client_assertion_type);
        requestBody.put("claim_locales", claim_locales);
        requestBody.put("claims", claimRequest.toString());
        requestBody.put("scope", scope);
        requestBody.put("acr_values",
                "mosip:idp:acr:generated-code mosip:idp:acr:biometrics mosip:idp:acr:linked-wallet mosip:idp:acr:password");
        requestBody.put("redirect_uri", "$ESIGNET_REDIRECT_URI$");
        requestBody.put("client_assertion", "$CLIENT_ASSERTION_PAR_JWT$");
        requestBody.put("prompt", prompt);
        requestBody.put("aud_key", aud_key);

        Response response = postWithBodyAndCookieForAutoGeneratedIdForUrlEncoded(parUrl, requestBody.toString());

        if (response == null) {
            throw new RuntimeException("PAR request failed: null response");
        }

        JSONObject responseJson = new JSONObject(response.asString());

        if (!responseJson.has("request_uri")) {
            logger.error("PAR response missing request_uri: " + responseJson.toString());
            throw new RuntimeException("PAR response missing request_uri");
        }

        return responseJson.getString("request_uri");
    }

    public static String getIdentifierFieldId() {
        return getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.identifier.name");
    }

    public static String normalizeIdentifierForOtp(String number) {
        boolean removeCode = Boolean.parseBoolean(getRemoveCountryCode());
        String prefix = getIdentifierPrefix();

        prefix = removeLeadingPlusSigns(prefix);
        number = removeLeadingPlusSigns(number);

        if (removeCode) {
            if (number.startsWith(prefix)) {
                number = number.substring(prefix.length());
            }
        } else {
            if (!number.startsWith(prefix)) {
                number = prefix + number;
            }
        }

        return number;
    }

    public static String getRemoveCountryCode() {
        return getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.sms-notification.remove-country-code");
    }

    public static String getIdentifierPrefix() {
        return getValueFromSignupActuator("applicationConfig: [classpath:/application-default.properties]",
                "mosip.signup.identifier.prefix");
    }

}