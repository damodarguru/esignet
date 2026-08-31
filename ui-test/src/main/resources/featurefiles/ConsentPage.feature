#@smokeAndRegression
Feature: Esignet Consent Page
  This feature file is for verifying the Consent page  

 @smoke @registrationProcess
  Scenario: Verify user completes registration process
    Given user captures the authorize url
    And user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    Then mark otp request timestamp
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    And user fills the signup form using UI specification
    And user clicks on Continue button in Setup Account Page
    And verify that success screen is displayed

  @smoke @registrationProcess @signupUiSchemaFetch
  Scenario: Verify no authentication is required to fetch the signup UI schema
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    Then verify the signup ui schema fetch request required no authentication

  @smoke @registrationProcess
  Scenario: Verify the signup UI schema is simple and contains no sensitive information
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    Then verify the signup ui schema contains no sensitive information

  @smoke @registrationProcess
  Scenario: Verify the signup UI schema specifies no default language and the form falls back to English
    Then verify the signup ui schema does not specify a default language
    Given user directly navigates to sign-up portal URL
    And user clears the signup language preference and reloads
    Then verify the signup form defaults to English when no language preference is set

  @smoke @registrationProcess @NetworkError
  Scenario: Verify network disconnect shows an appropriate message and the schema is freshly re-fetched on reconnect
    Given user directly navigates to sign-up portal URL
    When user's internet connection is disconnected during signup
    Then verify the network error message is displayed on signup
    When user's internet connection is restored during signup
    Then verify the network error message is no longer displayed on signup
    And verify the signup ui schema is freshly re-fetched after reconnecting

  @smoke @registrationProcess @leaveSitePrompt
  Scenario: Verify the "Leave site?" prompt on browser back from the signup setup account form
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    And user fills the signup form using UI specification

    And user navigates back in the browser from the signup form and a leave site prompt should appear
    And user cancels the leave site prompt on the signup form
    Then verify user is retained on the setup account page

    And user navigates back in the browser from the signup form and a leave site prompt should appear
    And user confirms the leave site prompt on the signup form
    Then verify user is no longer on the setup account page

  @smoke @registrationProcess
  Scenario: Verify that camera flip works if multiple cameras are available
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    When user opens the photo capture camera
    And user notes the active camera device
    And user clicks on the flip camera button
    Then verify the camera view has flipped to a different camera

  @smoke @registrationProcess @ES-2429
  Scenario: TC01,TC04 - Verify photo icon tooltip guidance and multipart form-data registration submission
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    Then verify the tooltip message for photo icon is displayed with guidance text
    And user fills the signup form using UI specification
    And user starts monitoring the registration submission request
    And user clicks on Continue button in Setup Account Page
    Then verify the registration request was submitted as multipart form-data and processed successfully
    And verify that success screen is displayed

  @smoke @registrationProcess @cameraPrompt @ES-2429
  Scenario: TC02 - Verify camera access prompts on first use
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    Then verify camera permission state on signup is "prompt"
    When user opens the photo capture camera
    Then verify camera permission state on signup is "prompt"

  @smoke @registrationProcess @cameraDenied @ES-2429
  Scenario: TC03 - Verify camera capture fails gracefully when camera access is denied
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    When user opens the photo capture camera
    Then verify camera access denied message is displayed on signup photo capture
    And verify fallback upload option is displayed on signup photo capture

  @smoke @registrationProcess @cameraDenied @ES-2429
  Scenario: TC-06 - Verify signup form submits successfully when the face photo is uploaded via the fallback file input
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    When user opens the photo capture camera
    Then verify camera access denied message is displayed on signup photo capture
    And verify fallback upload option is displayed on signup photo capture
    And user uploads a photo file for the face photo instead of using the camera
    And user fills the remaining signup form fields using UI specification
    And user starts monitoring the registration submission request
    And user clicks on Continue button in Setup Account Page
    Then verify the registration request was submitted as multipart form-data and processed successfully
    And verify that success screen is displayed

  @mobile @registrationProcess @ES-2429
  Scenario Outline: TC-07 - Verify signup form submission succeeds with face photo capture on <platform> mobile viewport emulation
    Given user directly navigates to sign-up portal URL
    And user clicks on Register button
    Then user enters mobile_number in the mobile number field
    And user clicks on the Continue button
    When user enters the OTP
    And user clicks on the Verify OTP button
    Then user click on Continue button in Success Screen
    And user fills the signup form using UI specification
    And user starts monitoring the registration submission request
    And user clicks on Continue button in Setup Account Page
    Then verify the registration request was submitted as multipart form-data and processed successfully
    And verify that success screen is displayed

    @device=Pixel 7
    Examples:
      | platform |
      | Android  |

    @device=iPhone 14
    Examples:
      | platform |
      | iOS      |

  @smoke @ToggleButtonInConsentPage
  Scenario: Verifying Toggle button in consent screen
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button
   
   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And verify the timer starts from 55sec in the consent page via Otp login
   And refresh the browser tab and verify timer continue with leftover seconds
  
   And user clicks on language dropdown button
   And user selects arabic language
   Then verify screen is displayed in RTL format
   When click on Language selection option
   And select the mandatory language
   And verify the tooltip message for Voluntary Claims info icon
   Then verify essential claims are listed separately
   And verify voluntary claims are listed separately
   
   Then verify master toggle should be visible for Voluntary Claims if multiple claims are present
   And verify all toggle buttons for Voluntary Claims are disabled by default
   
   Then verify if user enables Master toggle,all sub-toggles should be enabled
   And if user deselect one of the Voluntary Claims 
   Then verify remaining Voluntary Claims stays selected along with master toggle
   And if user disables Master toggle,all sub-toggles should be disabled
   
   Then verify if user enables Master toggle,all sub-toggles should be enabled
   And if user manually deselects all sub-toggles,verify master toggle also gets disabled
   
   When user enables only one of the Voluntary Claims toggle
   Then verify that the master toggle remains in unselected state
   
   When user enables all the voluntary claims sub-toggle manually
   Then verify that the master toggle is enabled automatically

  @smoke @ConsentScreen @ES-1013
  Scenario: TC_Consent_Screen_07,16,17,18,19,21,24,25 - Verify Attention screen content, cancel/stay flow, and Allow redirects the user
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   Then verify the header Attention in the consent to profile update screen
   Then verify the sub header in the consent to profile update screen
   Then verify the essential claim header in consent to update profile screen
   Then user verify the essential claims list
   Then verify available claim status is displayed in consent to update profile screen
   Then verify not available claim status is displayed in consent to update profile screen
   Then verify the voluntary claim header in the consent to profile update screen
   Then user verify the voluntary claims list
   Then verify info icon is available in consent to update profile screen
   Then user click on essential claim info icon
   Then verify the essential claim information displayed on clicking the info icon
   And user tab outside the info icon
   Then user click on voluntary claim info icon
   Then verify the voluntary claim information displayed on clicking the info icon
   And user tab outside the info icon
   Then verify the message click on proceed to begin with the verification process is displayed below
   Then verify proceed button is visible in consent to update profile screen
   Then verify cancel button is visible in consent to update profile screen

   When user click on cancel button in consent update to profile screen
   Then verify warning popup with header attention is displayed
   Then verify the sub header in warning popup is displayed
   Then verify stay button is available in the warning popup
   Then verify discontinue button is available in the warning popup screen
   And user click on stay button in warning popup
   Then verify consent should ask user to proceed in attention page

   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

  @smoke @ConsentScreen @ES-1013
  Scenario: TC_Consent_Screen_20 - Verify clicking Discontinue redirects the user away from the attention screen
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   When user click on cancel button in consent update to profile screen
   Then verify warning popup with header attention is displayed
   And user click on discontinue button in warning popup screen
   Then verify user is no longer on the attention screen after clicking discontinue

  @smoke @ConsentScreen @eKycProcessTimeout
  Scenario: Verify transaction expiry in the eKYC process steps screen shows the request-timed-out error
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   Given the eKYC process request is mocked to time out
   And clicks on proceed button in attention page
   Then verify the request timed out error is displayed

  @smoke @ConsentScreen @ConsentTimerExpiry
  Scenario: Verify user is logged out and redirected to the relying party once the consent screen timer times out
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen

   Then verify user is logged out and redirected to the relying party once the consent timer times out

  @smoke @ConsentScreen @UnavailableVoluntaryClaim
  Scenario: Verify clicking Proceed redirects to the eKYC provider list screen when a voluntary claim is unavailable
   Given user captures the authorize url requesting an additional voluntary "middle_name" claim
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then verify user is redirected to the eKYC provider list screen

  @smoke @ConsentScreen @AttentionConsentBypass @ES-675
  Scenario: TC_Consent_Screen_3b(i) - Verify Attention and Consent screens are bypassed on the second login attempt once essential claims are already verified and consented for the relying party
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

   Given user relaunches esignet url
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button
   Then verify user bypasses the attention and consent screens and is redirected to the relying party landing page

  @smoke @ConsentScreen @AttentionConsentBypass
  Scenario: TC_Consent_Screen_3b(ii) - Verify Attention screen is bypassed but Consent screen is shown again after the essential/voluntary status of a claim is updated for the relying party, keeping the verified claim unchanged
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

   Given user relaunches esignet url with "phone_number" claim updated to "voluntary"
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button
   Then verify user bypasses the attention screen and is redirected to the consent screen

  @smoke @ConsentScreen @VoluntaryClaimVerification
  Scenario: Verify Attention screen is bypassed when logging in with an already verified and available voluntary claim
   Given user navigates to esignet url with "email" claim updated to "voluntary"
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

   Given user relaunches esignet url with "email" claim updated to "voluntary"
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button
   Then verify user bypasses the attention screen and is redirected to the consent screen

  @smoke @ConsentScreen @VerifiedClaimStatus
  Scenario: Verify Verified and Not Verified status are each displayed against the correct claim when the Attention screen reappears for a new claim
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

   Given user relaunches esignet url requesting verification of an additional "gender" claim
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button
   Then verify consent should ask user to proceed in attention page
   And verify the "Email Address" claim shows verified status in consent to update profile screen
   And verify the "Gender" claim shows not verified status in consent to update profile screen

  @smoke @ConsentScreen @VerifiedClaimStatus
  Scenario: Verify user is able to add and verify an additional claim alongside an already verified claim
   Given user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

   Given user relaunches esignet url requesting verification of an additional "gender" claim
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button
   Then verify consent should ask user to proceed in attention page
   And verify the "Email Address" claim shows verified status in consent to update profile screen
   And verify the "Gender" claim shows not verified status in consent to update profile screen

   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen
   And user clicks on Allow button in consent screen
   Then verify user is no longer on consent screen after clicking allow

  @smoke @ConsentScreen @MultiLangClientName @ES-35
  Scenario: TC_01 - Verify relying party's name on the consent screen changes as per the selected language
   Given user creates the client with client name configured in multiple languages
   And user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen

   Then verify the relying party name on the consent screen is displayed as "Health Services Portal"

   And user clicks on language dropdown button
   And user selects arabic language
   Then verify the relying party name on the consent screen is displayed as "بوابة الخدمات الصحية"

   And user switches the language to "khm" on the consent screen
   Then verify the relying party name on the consent screen is displayed as "សេវាកម្មសុខភាព"

  @smoke @ConsentScreen @MultiLangClientName @ES-35
  Scenario: Verify relying party's name and logo on the login page change as per the selected language
   Given user creates the client with client name configured in multiple languages
   And user captures the authorize url

   Then verify the relying party name on the login page is displayed as "Health Services Portal"
   Then verify the relying party logo alt text on the login page is displayed as "Health Services Portal"

   And user clicks on language dropdown button
   And user selects arabic language
   Then verify the relying party name on the login page is displayed as "بوابة الخدمات الصحية"
   Then verify the relying party logo alt text on the login page is displayed as "بوابة الخدمات الصحية"

   And user switches the language to "khm" on the consent screen
   Then verify the relying party name on the login page is displayed as "សេវាកម្មសុខភាព"
   Then verify the relying party logo alt text on the login page is displayed as "សេវាកម្មសុខភាព"

  @smoke @ConsentScreen @MultiLangClientName @ES-35
  Scenario: Verify eSignet's own logo on the login page changes as per the selected language
   Given user creates the client with client name configured in multiple languages
   And user captures the authorize url

   Then verify the eSignet logo alt text on the login page is displayed as "eSignet"

   And user switches the language to "hin" on the consent screen
   Then verify the eSignet logo alt text on the login page is displayed as "ई-हस्ताक्षर"

  @smoke @ConsentScreen @MultiLangClientName @ES-35
  Scenario: TC_02 - Verify default client name is displayed when the selected language is not configured for the client
   Given user creates the client with client name configured in multiple languages
   And user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen

   And user switches the language to "hin" on the consent screen
   Then verify the relying party name on the consent screen is displayed as "Health Services Portal"

  @smoke @ConsentScreen @MultiLangClientName @ES-35
  Scenario: TC_05 - Verify relying party logo's name changes as per the selected language on the consent screen
   Given user creates the client with client name configured in multiple languages
   And user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen

   Then verify the relying party logo alt text on the consent screen is displayed as "Health Services Portal"

   And user clicks on language dropdown button
   And user selects arabic language
   Then verify the relying party logo alt text on the consent screen is displayed as "بوابة الخدمات الصحية"

  @smoke @ConsentScreen @MultiLangClientName @ES-35
  Scenario: TC_06 - Verify eSignet's own logo name changes as per the selected language on the consent screen
   Given user creates the client with client name configured in multiple languages
   And user captures the authorize url
   When click on Language selection option
   And select the mandatory language
   And user click on Login with Otp
   Then user enters Registered mobile number into the mobile number field
   And user click on get otp button
   When user enters the correct otp
   And click on verify Otp button

   Then verify consent should ask user to proceed in attention page
   And clicks on proceed button in attention page
   And clicks on proceed button in next page
   Then select the e-kyc verification provider
   And clicks on proceed button in e-kyc verification provider page
   And user select the check box in terms and condition page
   And user clicks on proceed button in terms and condition page
   And user clicks on proceed button in camera preview page
   And user is navigated to consent screen once liveness check completes
   And verify user is navigated to consent screen

   Then verify the eSignet logo alt text on the consent screen is displayed as "eSignet"

   And user switches the language to "hin" on the consent screen
   Then verify the eSignet logo alt text on the consent screen is displayed as "ई-हस्ताक्षर"

