import lodash from "lodash";
import intl from "react-intl-universal";

import sharedStore from "kernel/shared-store";
import EventEmitter from "kernel/event-emitter";
import SystemConstants from "constants/system-constants";
import SharedStoreKeys from "constants/shared-store-keys";
import { EVENT_ERROR_RESPONSE } from "constants/event-names";
import sharedDataUpdater from "kernel/shared-data-updater";
import { EVENT_SESSION_UPDATED } from "constants/event-names";
import FalseResponseHandler from "kernel/network/false-response-handler";

const processNon2xxResponse = statusCode => {
  let errorMessage = intl.get("httpError.general");

  if ((statusCode >= 300) & (statusCode < 400)) {
    errorMessage = intl.get("httpError.redirection");
  } else if ((statusCode >= 400) & (statusCode < 500)) {
    errorMessage = intl.get("httpError.client");
  } else if (statusCode >= 500) {
    errorMessage = intl.get("httpError.server");
  }

  EventEmitter.emit(EVENT_ERROR_RESPONSE, errorMessage);
};

const processValidUser = response => {
  const url = response.config.url;

  if (url.includes("signin") || url.includes("signup")) {
    sharedDataUpdater.updateMode(response.data.value.mode);
    sharedDataUpdater.updateIsValidUser(true);
  } else if (url.includes("signout")) {
    sharedDataUpdater.updateIsValidUser(false);
  }
};

const saveSessionToken = response => {
  let sessionToken =
    response.headers[SystemConstants.HTTP_HEADER_SESSION_TOKEN] ||
    response.headers[SystemConstants.HTTP_HEADER_SESSION_TOKEN.toLowerCase()];

  let oldSessionToken = sharedStore.getSessionVariable(
    SharedStoreKeys.SESSION_TOKEN
  );

  if (!lodash.isNil(sessionToken) && sessionToken !== oldSessionToken) {
    sharedStore.setSessionVariable(SharedStoreKeys.SESSION_TOKEN, sessionToken);
    EventEmitter.emit(EVENT_SESSION_UPDATED, sessionToken);
  }
};

const saveRememberMeToken = response => {
  let rememberMeToken =
    response.headers[SystemConstants.HTTP_HEADER_REMEMBER_ME_TOKEN] ||
    response.headers[
      SystemConstants.HTTP_HEADER_REMEMBER_ME_TOKEN.toLowerCase()
    ];

  if (!lodash.isNil(rememberMeToken)) {
    sharedStore.setPersistentVariable(
      SharedStoreKeys.REMEMBER_ME_TOKEN,
      rememberMeToken
    );
  }
};

const falseResponseHandler = new FalseResponseHandler();

export default class ResponseInterceptor {
  interceptNon2xxResponse(error) {
    if (error.name === "SyntaxError") {
      processNon2xxResponse(error.response.status);
    } else {
      EventEmitter.emit(EVENT_ERROR_RESPONSE, intl.get("httpError.network"));
    }

    Promise.reject(error);
  }

  intercept2xxResponse(response) {
    saveSessionToken(response);

    if (response.data.success === true) {
      saveRememberMeToken(response);
      processValidUser(response);
    } else {
      falseResponseHandler.handle(response);
    }

    return response;
  }
}
