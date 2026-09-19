package com.pan.algorithm.hashtable;

import com.pan.algorithm.hashtable.PriceTickCounting.PriceTick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceTickCountingTest {

    private final PriceTickCounting priceTickCounting = new PriceTickCounting();

    @BeforeEach
    void setup(){
        priceTickCounting.clearPriceCountList();
    }

    @Test
    public void testPriceTickCounting(){
        priceTickCounting.onPriceEvent(new PriceTick("0700.HK", 100));
        priceTickCounting.onPriceEvent(new PriceTick("0700.HK", 100));
        priceTickCounting.onPriceEvent(new PriceTick("0005.HK", 300));
        priceTickCounting.onPriceEvent(new PriceTick("0005.HK", 400));
        priceTickCounting.onPriceEvent(new PriceTick("0700.HK", 200));
        List<PriceTickCounting.PriceCount> priceCountList = priceTickCounting.getPriceCountList();
        assertThat(priceCountList.size()).isEqualTo(2);
        assertThat(priceCountList.get(0).getTicker()).isEqualTo("0005.HK");
        assertThat(priceCountList.get(0).getCount()).isEqualTo(1);
        assertThat(priceCountList.get(0).getStartTimeStamp()).isEqualTo(300);
        assertThat(priceCountList.get(0).getEndTimeStamp()).isEqualTo(400);

        assertThat(priceCountList.get(1).getTicker()).isEqualTo("0700.HK");
        assertThat(priceCountList.get(1).getCount()).isEqualTo(2);
        assertThat(priceCountList.get(1).getStartTimeStamp()).isEqualTo(100);
        assertThat(priceCountList.get(1).getEndTimeStamp()).isEqualTo(200);
    }
}
