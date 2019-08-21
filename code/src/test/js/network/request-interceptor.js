import lodash from "lodash";

import sharedStore from "kernel/shared-store";
import LocaleResolver from "kernel/locale-resolver";
import SystemConstants from "constants/system-constants";
import SharedStoreKeys from "constants/shared-store-keys";

const setRememberMeToken = headers => {
  let rememberMeToken = sharedStore.getPersistentVariable(
    SharedStoreKeys.REMEMBER_ME_TOKEN
  );

  if (!lodash.isNil(rememberMeToken)) {
    headers[SystemConstants.HTTP_HEADER_REMEMBER_ME_TOKEN] = rememberMeToken;
  }
};

export default class RequestInterceptor {
  interceptRequestConfig(config) {
    let locale = LocaleResolver.getCurrentLocale();
    config.headers.common[SystemConstants.HTTP_HEADER_LOCALE] = locale;

    let sessionToken = sharedStore.getSessionVariable(
      SharedStoreKeys.SESSION_TOKEN
    );

    if (
      lodash.isNil(sessionToken) ||
      config.url.includes("signup") ||
      config.url.includes("signin")
    ) {
      setRememberMeToken(config.headers.common);
    } else {
      config.headers.common[
        SystemConstants.HTTP_HEADER_SESSION_TOKEN
      ] = sessionToken;
    }

    if (config.url.includes("orders")) {
      config.headers.common[SystemConstants.HTTP_HEADER_SYSTEM] =
        SystemConstants.HTTP_HEADER_SYSTEM_VALUE;
    }

    return config;
  }

  interceptRequestError(error) {
    Promise.reject(error);
  }
}
