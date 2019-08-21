import Config from "common/config";
import Checker from "utils/checker";
import RequestSender from "kernel/network/request-sender";

const basePath = Config.API_BASE_PATH + "/customers";

export default class CustomerService {
  /**
   * Sign up a customer
   *
   * @param {string} email required
   * @param {string} password required
   * @param {string} baseCurrencyCode required
   * @param {boolean} rememberMe required Request a Remember-Me-Token if true
   */
  async signup(email, password, baseCurrencyCode, rememberMe, reCAPTCHAToken) {
    Checker.checkNotNil(email, "email");
    Checker.checkNotNil(password, "password");
    Checker.checkNotNil(rememberMe, "rememberMe");
    Checker.checkNotNil(baseCurrencyCode, "baseCurrencyCode");

    let params = {
      email,
      password,
      baseCurrencyCode,
      rememberMe,
      reCAPTCHAToken
    };
    return await RequestSender.sendPost(`${basePath}/signup`, params);
  }

  /**
   * Sign in a customer by using Remember-Me-Token or email/password
   *
   * @param {string} email optional
   * @param {string} password optional
   * @param {boolean} rememberMe optional, Request a Remember-Me-Token if true
   */
  async signin(email, password, rememberMe) {
    let params = { email, password, rememberMe };
    return await RequestSender.sendPost(`${basePath}/signin`, params);
  }

  /**
   * Sign out a customer and clear the session token and remember me token
   */
  async signout() {
    return await RequestSender.sendPost(`${basePath}/signout`);
  }
  /**
   * change customer's locale
   */
  async changeLocale(locale) {
    Checker.checkNotNil(locale, "locale");
    let params = { locale };
    return await RequestSender.sendPost(`${basePath}/change-locale`, params);
  }
  /**
   * Verify password reset code sent by /misc/verification-codes
   *
   * @param {string} email required Customer's email
   * @param {string} verificationCode required The verification code sent to customer via email
   */
  async verifyPasswordResetCode(email, verificationCode) {
    Checker.checkNotNil(email, "email");
    Checker.checkNotNil(verificationCode, "verificationCode");

    let params = { email, verificationCode };
    return await RequestSender.sendPost(
      `${basePath}/verify-password-reset-code`,
      params
    );
  }

  /**
   * Reset customer's password without signin
   *
   * @param {string} password required. New password
   * @param {string} ticket required. The ticket from the response of /verify-password-reset-code
   */
  async resetPassword(password, ticket) {
    Checker.checkNotNil(password, "password");
    Checker.checkNotNil(ticket, "ticket");

    let params = { password, ticket };
    return await RequestSender.sendPost(`${basePath}/reset-password`, params);
  }

  /**
   * Change password after customer signs in
   *
   * @param {string} oldPassword required existing password
   * @param {string} newPassword required new password
   */
  async changePassword(oldPassword, newPassword) {
    Checker.checkNotNil(oldPassword, "oldPassword");
    Checker.checkNotNil(newPassword, "newPassword");

    let params = { oldPassword, newPassword };
    return await RequestSender.sendPost(`${basePath}/change-password`, params);
  }

  /**
   * Get the account list
   */
  async getAccounts() {
    return await RequestSender.sendGet(`${basePath}/accounts`);
  }

  /**
   * Create a REALY MONEY account
   *
   * @param {string} questionnaires required. JSON format, including questions and corresponding answers
   */
  async createAccount(questionnaires) {
    Checker.checkNotNil(questionnaires, "questionnaires");

    let params = { questionnaires };
    return await RequestSender.sendPost(`${basePath}/accounts`, params);
  }

  /**
   * Switch between REAL_MONEY account and DEMO account
   */
  async switchAccount() {
    return await RequestSender.sendPost(`${basePath}/accounts/switch`);
  }

  /**
   * Add customer details
   *
   * @param {string} firstName required.
   * @param {string} middleName optional.
   * @param {string} familyName required.
   * @param {string} placeOfBirth required.
   * @param {string} nationalityCode required.
   * @param {string} taxCountryCode required.
   * @param {string} dateOfBirth required.
   * @param {string} countryCallingCode required.
   * @param {string} mobileNumber required.
   * @param {string} streetName required.
   * @param {string} buildingName optional.
   * @param {string} floor optional.
   * @param {string} district optional.
   * @param {string} city required.
   * @param {string} state optional.
   * @param {string} postcode required.
   * @param {boolean} isUsTaxCitizen required.
   * @param {string} usTaxNumber optional.
   * @param {boolean} acceptAgreement required.
   * @param {string} baseCurrencyCode required.
   * @param {string} idNumber required.
   */
  async addDetails(
    firstName,
    middleName,
    familyName,
    nationalityCode,
    dateOfBirth,
    mobileNumber,
    countryCallingCode,
    streetName,
    buildingName,
    floor,
    district,
    city,
    state,
    postcode,
    isUsTaxCitizen,
    usTaxNumber,
    acceptAgreement,
    baseCurrencyCode,
    idNumber,
    idType,
    secondNationalityCode,
    passportNumber
  ) {
    Checker.checkNotNil(firstName, "firstName");
    Checker.checkNotNil(familyName, "familyName");
    Checker.checkNotNil(nationalityCode, "nationalityCode");
    Checker.checkNotNil(dateOfBirth, "dateOfBirth");
    Checker.checkNotNil(countryCallingCode, "countryCallingCode");
    Checker.checkNotNil(mobileNumber, "mobileNumber");
    Checker.checkNotNil(streetName, "streetName");
    Checker.checkNotNil(city, "city");
    Checker.checkNotNil(postcode, "postcode");
    Checker.checkNotNil(isUsTaxCitizen, "isUsTaxCitizen");
    Checker.checkNotNil(acceptAgreement, "acceptAgreement");
    Checker.checkNotNil(baseCurrencyCode, "baseCurrencyCode");
    Checker.checkNotNil(idType, "idType");
    Checker.checkNotNil(idType, "idT secondNationalityCode");
    Checker.checkNotNil(idType, "passportNumberype");

    // secondNationalityCode
    // passportNumber

    let params = {
      firstName,
      middleName,
      familyName,
      nationalityCode,
      dateOfBirth,
      countryCallingCode,
      mobileNumber,
      streetName,
      buildingName,
      floor,
      district,
      city,
      state,
      postcode,
      isUsTaxCitizen,
      acceptAgreement,
      baseCurrencyCode,
      idNumber,
      idType,
      secondNationalityCode,
      passportNumber
    };
    if (isUsTaxCitizen) {
      params.usTaxNumber = usTaxNumber;
    }
    return await RequestSender.sendPost(`${basePath}/details`, params);
  }

  /**
   * Get user details
   */
  async getDetails() {
    return await RequestSender.sendGet(`${basePath}/details`);
  }

  /**
   * Get the questionnaire list
   */
  async getQuestionnaires() {
    return await RequestSender.sendGet(`${basePath}/questionnaires`);
  }

  /**
   * Get the verification status of customer
   */
  async getVerificationStatus() {
    return await RequestSender.sendGet(`${basePath}/verification-status`);
  }

  /**
   * Verify customer's phone or email
   *
   * @param {string} type required (one of MOBILE_PHONE, EMAIL)
   * @param {string} code required The verification code sent to phone or email
   * @param {string} countryCallingCode country calling code
   * @param {string} phoneNumber input phone number if need to change
   * @param {string} email input email if need to change
   * @param {string} password required Need password to verify
   */
  async autoVerify(
    type,
    code,
    countryCallingCode,
    phoneNumber,
    email,
    password
  ) {
    Checker.checkNotNil(type, "type");
    Checker.checkNotNil(code, "code");
    Checker.checkNotNil(password, "password");

    let params = {
      type,
      code,
      countryCallingCode,
      phoneNumber,
      email,
      password
    };

    return await RequestSender.sendPost(
      `${basePath}/verification/auto`,
      params
    );
  }

  /**
   * Verify customer's ID or address
   *
   * @param {string} type required (one of ID, ADDRESS)
   * @param {string} uploadIds required. Upload IDs returned from /mis/upload
   */
  async manuallyVerify(type, uploadIds) {
    Checker.checkNotNil(type, "type");
    Checker.checkNotNil(uploadIds, "uploadIds");

    let params = { type, uploadIds };
    return await RequestSender.sendPost(
      `${basePath}/verification/manual`,
      params
    );
  }

  /**
   * Get the watch list of the customer
   */
  async getWatchList() {
    return await RequestSender.sendGet(`${basePath}/watch-lists`);
  }

  /**
   * Add one or several instruments to the watch list, always add, not replace
   *
   * @param {string} instrumentSymbols The instrument sysmbols seperated by commas
   */
  async addWatchList(instrumentSymbols) {
    Checker.checkNotNil(instrumentSymbols, "instrumentSymbols");

    let params = { instrumentSymbols };
    return await RequestSender.sendPost(`${basePath}/watch-lists`, params);
  }

  /**
   * Replace the watch list, and save the sorting with the sequence
   *
   * @param {string} instrumentSymbols required The instrument sysmbols seperated by commas
   */
  async replaceWatchList(instrumentSymbols) {
    Checker.checkNotNil(instrumentSymbols, "instrumentSymbols");

    let params = { instrumentSymbols };
    return await RequestSender.sendPost(
      `${basePath}/watch-lists/replace`,
      params
    );
  }

  /**
   * Remove one or several instruments from watch list
   *
   * @param {string} instrumentSymbols The instrument sysmbol seperated by commas
   */
  async removeWatchList(instrumentSymbols) {
    Checker.checkNotNil(instrumentSymbols, "instrumentSymbols");

    let params = { instrumentSymbols };
    return await RequestSender.sendPost(
      `${basePath}/watch-lists/remove`,
      params
    );
  }

  /**
   * Get the instrument alerts
   */
  async getAlerts() {
    return await RequestSender.sendGet(`${basePath}/alerts`);
  }

  /**
   *
   * Get an alert
   *
   * @param {string} id reuiqred. Alert ID
   */
  async getAnAlert(id) {
    Checker.checkNotNil(id, "id");
    return await RequestSender.sendGet(`${basePath}/alerts/${id}`);
  }

  /**
   * Add an instrument alert
   *
   * @param {string} instrumentSymbol required. The instrument symbol
   * @param {string} type required (one of BUY, SELL)
   * @param {string} trigger required (one of LOWER_OR_EQUAL, HIGHER_OR_EQUAL)
   * @param {number} value required Trigger value
   */
  async addAlert(instrumentSymbol, type, trigger, value) {
    Checker.checkNotNil(instrumentSymbol, "instrumentSymbol");
    Checker.checkNotNil(type, "type");
    Checker.checkNotNil(trigger, "trigger");
    Checker.checkNotNil(value, "value");

    let params = { instrumentSymbol, type, trigger, value };
    return await RequestSender.sendPost(`${basePath}/alerts/add`, params);
  }

  /**
   * Delete an alert
   * @param {string} id required. The alert ID
   */
  async deleteAlert(id) {
    Checker.checkNotNil(id, "id");

    let params = { id };
    return await RequestSender.sendPost(`${basePath}/alerts/delete`, params);
  }

  async getEmploymentStatus() {
    return await RequestSender.sendGet(`${basePath}/employment-status`);
  }

  async setEmploymentStatus(questionnaires) {
    Checker.checkNotNil(questionnaires, "questionnaires");

    let params = { questionnaires };
    return await RequestSender.sendPost(
      `${basePath}/employment-status`,
      params
    );
  }
}
