package com.pan;

import com.pan.algorithm.greedy.ResourceScheduler;
import org.junit.Test;
import static org.junit.Assert.*;

public class JunitTest {

    @Test
    public void testResourceSchedulerFindTheEarliestCommonFreeTime(){
        String inputStr = "3_12;101_103;10_23;21_34;49_102;21_23";
        ResourceScheduler resourceScheduler = new ResourceScheduler();
        assertEquals(34, resourceScheduler.findTheEarliestCommonFreeTime(inputStr, 10, 300));
    }

    @Test
    public void testResourceSchedulerFindMostCompatibleTask(){
        String inputStr = "1_3;3_4;2_6;4_5";
        ResourceScheduler resourceScheduler = new ResourceScheduler();
        assertEquals(3, resourceScheduler.findMostCompatibleTask(inputStr));
    }
}
