import Config from "common/config";
import Checker from "utils/checker";
import RequestSender from "kernel/network/request-sender";

const basePath = Config.API_BASE_PATH + "/instruments";

export default class InstrumentService {
  /**
   * Get a page of instrument details
   *
   * @param {string} type one of FOREX, COMMODITIES, INDICES, CRYPTO, OPTIONS, SHARES
   * @param {number} page 0 based page index
   * @param {number} size How many records a page has
   */
  async getPage(type, page, size) {
    let params = { type, page, size };
    return await RequestSender.sendGet(`${basePath}/page`, params);
  }

  /**
   * get three active instrument symbol list(GAINER,LOSER,VOLATILITY)
   */
  async getActiveSymbols() {
    return await RequestSender.sendGet(`${basePath}/active`);
  }
  /**
   * Get instrument list by symbols
   *
   * @param {string} symbols instrument symbols
   */
  async getBySymbol(symbols) {
    let params = { symbols };
    return await RequestSender.sendGet(`${basePath}/all`, params);
  }

  /**
   * Search the instruments
   *
   * @param {string} keyword The keyword for search
   */
  async search(keyword) {
    Checker.checkNotNil(keyword, "keyword");
    let params = { keyword };
    return await RequestSender.sendGet(`${basePath}/search`, params);
  }

  /**
   * Get a page of status of instruments
   *
   * @param {string} type one of FOREX, COMMODITIES, INDICES, CRYPTO, OPTIONS, SHARES
   * @param {number} page 0 based page index
   * @param {number} size How many records a page has
   */
  async getStatusPage(type, page, size) {
    let params = { type, page, size };
    return await RequestSender.sendGet(`${basePath}/status/page`, params);
  }

  /**
   * Get the status of instruments
   *
   * @param {string} symbols The instrument sysmbols seperated by commas
   */
  async getStatusBySymbols(symbols) {
    let params = { symbols };
    return await RequestSender.sendGet(`${basePath}/status/all`, params);
  }

  /**
   * Get a page of instrument quotations
   *
   * @param {string} type one of FOREX, COMMODITIES, INDICES, CRYPTO, OPTIONS, SHARES
   * @param {number} page 0 based page index
   * @param {number} size How many records a page has
   */
  async getQuotationPage(type, page, size) {
    let params = { type, page, size };
    return await RequestSender.sendGet(`${basePath}/quotations/page`, params);
  }

  /**
   * Get the quotation of specified instruments seperated by commas
   *
   * @param {string} symbols  The instrument sysmbols seperated by commas
   */
  async getQuotationBySymbols(symbols) {
    let params = { symbols };
    return await RequestSender.sendGet(`${basePath}/quotations/all`, params);
  }

  /**
   * Get the KLine chart data
   *
   * @param {string} symbol The symbol
   * @param {string} interval one of MI1, MI5, MI10, MI15, MI30, H1, D1, W1, MO1, Q1, Y1
   * @param {string} datetime The base date and time
   * @param {number} size The size of data array
   */
  async getKLine(symbol, interval, datetime, size) {
    Checker.checkNotNil(symbol, "symbol");
    Checker.checkNotNil(interval, "interval");
    Checker.checkNotNil(datetime, "datetime");
    Checker.checkNotNil(size, "size");

    let params = { symbol, interval, datetime, size };
    return await RequestSender.sendGet(`${basePath}/klines`, params);
  }

  /**
   * Get the timeLine chart data
   *
   * @param {string} code The symbol
   * @param {string} period 1 or 2
   */
  async getTimeLine(code, period) {
    Checker.checkNotNil(code, "code");
    Checker.checkNotNil(period, "period");

    let params = { code, period };
    return await RequestSender.sendGet(`${basePath}/timeline`, params);
  }
}
