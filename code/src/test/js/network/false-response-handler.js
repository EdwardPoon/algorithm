import lodash from "lodash";

import sharedStore from "kernel/shared-store";
import EventEmitter from "kernel/event-emitter";
import SharedStoreKeys from "constants/shared-store-keys";
import {
  EVENT_ERROR_RESPONSE,
  EVENT_FORCE_SIGNOUT
} from "constants/event-names";
import Checker from "utils/checker";

const popupErrors = [20004];
const toastErrors = [[-1046, -1006], [10000, 19999], [20003, 30000]];
const ignoredErrors = [[10001, 10009], 10011, 10014, 10016];

const isInRange = (array, target) => {
  Checker.checkArray(array);

  if (array.includes(target)) {
    return true;
  }

  for (let i = 0; i < array.length; i++) {
    let item = array[i];
    if (lodash.isArray(item) && target >= item[0] && target <= item[1]) {
      return true;
    }
  }

  return false;
};

const handleInvalidSessionToken = response => {
  sharedStore.removeSessionVariable(SharedStoreKeys.SESSION_TOKEN);
  // Resending is handled in request-sender.js
};

const handleInvalidRememberMeToken = () => {
  EventEmitter.emit(EVENT_FORCE_SIGNOUT, true);
};

const handlePermissionDenied = response => {
  sharedStore.removeSessionVariable(SharedStoreKeys.SESSION_TOKEN);

  let rememberMeToken = sharedStore.getPersistentVariable(
    SharedStoreKeys.REMEMBER_ME_TOKEN
  );

  if (lodash.isNil(rememberMeToken)) {
    // Need to sign out
    EventEmitter.emit(EVENT_FORCE_SIGNOUT, true);
  } else {
    // Resending is handled in request-sender.js
  }
};

const handleOtherResponse = response => {
  let { errCode, message } = response;
  if (isInRange(ignoredErrors, errCode)) {
    console.error(`ErrorCode: ${errCode}, Message: ${message}`);
  } else if (isInRange(popupErrors, errCode)) {
    // This kind of error will be handled by UI
  } else if (isInRange(toastErrors, errCode)) {
    EventEmitter.emit(EVENT_ERROR_RESPONSE, message);
  } else {
    console.error(`ErrorCode: ${errCode}, Message: ${message}`);
  }
};

export default class FalseResponseHandler {
  handle(response) {
    Checker.checkNotNil(response, "response");

    switch (response.data.errCode) {
      case 10006:
        handleInvalidSessionToken(response);
        break;

      case 10009:
        handleInvalidRememberMeToken();
        break;

      case 10010:
        handlePermissionDenied(response);
        break;

      default:
        handleOtherResponse(response.data);
    }
  }
}
