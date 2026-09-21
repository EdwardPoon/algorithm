package com.pan.algorithm.topx;

import java.util.*;

public class FindTopKFrequent {

    public static void main(String[] args) {
        FindTopKFrequent findTopKFrequent = new FindTopKFrequent();
        //int[] items = {1,1,1,2,2,3};
        int[] items = {5,2,5,3,5,3,1,1,3};
        //int[] items = {3,2,3,1,2,4,5,5,6,7,7,8,2,3,1,1,1,10,11,5,6,2,4,7,8,5,6};
        int[] res = findTopKFrequent.findTopKFrequentElements(items, 2);
        for (int i : res) {
            System.out.println(i);
        }
    }

    public int[] findTopKFrequentElements(int[] nums, int k) {
        Map<Integer, ItemCount> itemCountMap = new HashMap<>();
        PriorityQueue<ItemCount> topItemQueue = new PriorityQueue<>((t1, t2) -> Integer.compare(t1.getCount(), t2.getCount())); // count to item map
        Set<Integer> topItems = new HashSet<>();

        for (int i=0; i < nums.length; i++) {
            int current = nums[i];

            ItemCount currentItemCount = itemCountMap.get(current);
            if (currentItemCount == null) {
                currentItemCount = new ItemCount(current);

            } else{
                currentItemCount.incCount();
            }
            itemCountMap.put(current, currentItemCount);
            if (!topItems.contains(current)) {
                if (topItems.size() < k) {
                    topItems.add(current);
                    topItemQueue.add(currentItemCount);
                } else {
                    ItemCount topItemCount = topItemQueue.peek();
                    System.out.println("Current: " + current +", topItemCount: " + topItemCount.getItem() + ",count:" + topItemCount.getCount());
                    if (currentItemCount.getCount() > topItemCount.getCount()){
                        topItems.remove(topItemCount.getItem());
                        topItemQueue.poll();

                        topItems.add(current);
                        topItemQueue.add(currentItemCount);
                    }
                }
            }
            System.out.println("after process: " + current +", topItems: " + topItems);
        }
        return topItemQueue.stream().mapToInt(ItemCount::getItem).toArray();
    }

    class ItemCount {
        private int item;
        private int count;
        public ItemCount(int item) {
            this.item = item;
            this.count = 1;
        }
        public void incCount() {
            count += 1;
        }
        public int getItem() {
            return this.item;
        }
        public int getCount() {
            return this.count;
        }

    }

}
