package com.pan.algorithm.greedy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//There are multiple resources(could be a person or a task), each one is with a startTime and stopTime
public class ResourceScheduler {

    public static void main(String[] args){
        String inputStr = "3_12;101_103;10_23;21_34;49_102;21_23";


    }

    // Compatible Tasks can't have overlap, return the max number of Compatible Task
    // e.g. "1_3;3_4;2_6;4_5",  MostCompatibleTask is 3, (1_3;3_4;4_5)
    public int findMostCompatibleTask(String input){
        List<StartEndTime> startEndTimeList = convertToStartEndTimeList(input);
        // sort by endTime
        Collections.sort(startEndTimeList, new EndTimeAndDurationComparator());
        int taskCount = 0;
        for (int i = 0; i < startEndTimeList.size(); i++) {
            if (i == 0 || startEndTimeList.get(i).end != startEndTimeList.get(i-1).end){
                taskCount++;
            }
        }
        return taskCount;
    }

    // every person is not free from start to end, find the earliest startTime of a day which everyone is available for a duration(e.g 10 minutes)
    // input: startTime_endTime;... e.g: "3_12;101_103;10_23;21_34;49_102;21_23";
    // max: the maxTime of a day (in minutes of day),
    public int findTheEarliestCommonFreeTime(String input, int duration, int max){

        // sort by start time
        // commonFreeStartTime = Math.max(commonFreeStartTime, startEndTime.end)
        List<StartEndTime> startEndTimeList = convertToStartEndTimeList(input);
        Collections.sort(startEndTimeList, new StartTimeComparator());
        int commonFreeStartTime = -1;
        for (StartEndTime startEndTime : startEndTimeList){
            if (commonFreeStartTime == -1){
                if (startEndTime.start > duration){
                    commonFreeStartTime = 0;
                    break;
                }
                else{
                    commonFreeStartTime = startEndTime.end;
                }
            }
            if (startEndTime.start - commonFreeStartTime > duration){
                break;
            }
            commonFreeStartTime = Math.max(commonFreeStartTime, startEndTime.end);
        }
        if (max - commonFreeStartTime < duration){
            commonFreeStartTime = -1;
        }
        return commonFreeStartTime;
    }

    private List<StartEndTime> convertToStartEndTimeList(String input){

        List<StartEndTime> startEndTimeList = new ArrayList<>();
        String[] tokens = input.split(";");
        for (String token : tokens){
            String[] startEndArray = token.split("_");
            startEndTimeList.add(new StartEndTime(Integer.valueOf(startEndArray[0]), Integer.valueOf(startEndArray[1])));
        }

        return startEndTimeList;
    }

    class StartEndTime implements Comparable<StartEndTime>{
        public int start;
        public int end;
        public StartEndTime(int start, int end){
            this.start = start;
            this.end = end;
        }

        @Override
        public int compareTo(@NotNull StartEndTime o) {
            return this.end - o.end;
        }
    }

    class StartTimeComparator implements Comparator<StartEndTime>{

        @Override
        public int compare(StartEndTime o1, StartEndTime o2) {
            return o1.start - o2.start;
        }
    }

    class EndTimeAndDurationComparator implements Comparator<StartEndTime>{

        @Override
        public int compare(StartEndTime o1, StartEndTime o2) {
            int res = o1.start - o2.start;
            if (res != 0){
                return res;
            }
            else{
                int duration1 = o1.end - o1.start;
                int duration2 = o2.end - o2.start;

                return duration1 - duration2;
            }
        }
    }
}
