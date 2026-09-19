package com.pan.algorithm.hashtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// run test case in PriceTickCountingTest
public class PriceTickCounting {

    private final int interval = 100;
    private final List<PriceCount> priceCountList = new ArrayList();
    private final Map<String, StartTimeAndCount> startTimeAndCountMap = new HashMap<>();

    public List<PriceCount> getPriceCountList() {
        return priceCountList;
    }

    public void clearPriceCountList() {
        priceCountList.clear();
    }

    private void addPriceCount(PriceCount priceCount) {
        priceCountList.add(priceCount);
    }

    // 700.HK  100
    //
    // 700.HK  100
    // 700.HK  200
    // PriceCounts: count 2, startTimeStamp 100, endTimeStamp: 200

    public void onPriceEvent(PriceTick priceTick) {

        StartTimeAndCount startTimeAndCount = startTimeAndCountMap.computeIfAbsent(priceTick.getTicker(),
                k -> new StartTimeAndCount(priceTick.timeStamp));
        if (priceTick.timeStamp - startTimeAndCount.getStartTimeStamp() < interval) {
            startTimeAndCount.incCount();
        }
        else if (startTimeAndCount.getCount() > 0) {
                addPriceCount(new PriceCount(priceTick.getTicker(), startTimeAndCount.getCount(), startTimeAndCount.getStartTimeStamp(),
                        startTimeAndCount.getStartTimeStamp() + 100 ));

                startTimeAndCount.reset();
        }
    }

    public static class PriceTick {

        private String ticker;
        private long timeStamp;

        public PriceTick(String ticker, long timeStamp) {
            this.ticker = ticker;
            this.timeStamp = timeStamp;
        }
        public String getTicker() {
            return this.ticker;
        }
        public long getTimeStamp() {
            return this.timeStamp;
        }
    }

    private class StartTimeAndCount {
        private long startTimeStamp;
        private int count;
        public StartTimeAndCount(long startTimeStamp) {
            this.startTimeStamp = startTimeStamp;
            this.count = 0;
        }
        public void incCount() {
            count++;
        }
        public void reset() {
            startTimeStamp = -1;
            count = 0;
        }

        public long getStartTimeStamp() {
            return startTimeStamp;
        }

        public int getCount() {
            return count;
        }
    }

    public class PriceCount {
        private String ticker;
        private int count;
        private long startTimeStamp;
        private long endTimeStamp;

        public PriceCount(String ticker, int count, long startTimeStamp, long endTimeStamp) {
            this.ticker = ticker;
            this.count = count;
            this.startTimeStamp = startTimeStamp;
            this.endTimeStamp = endTimeStamp;
        }

        public String getTicker() {
            return ticker;
        }

        public int getCount() {
            return count;
        }

        public long getStartTimeStamp() {
            return startTimeStamp;
        }

        public long getEndTimeStamp() {
            return endTimeStamp;
        }
    }
}
