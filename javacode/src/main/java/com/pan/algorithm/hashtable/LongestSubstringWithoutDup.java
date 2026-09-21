package com.pan.algorithm.hashtable;

import java.util.LinkedHashSet;

// find the length of the longest substring without duplicate characters.
public class LongestSubstringWithoutDup {

    public static void main(String[] args) {
        String str = "1R1T7";
        System.out.println(lengthOfLongestSubstring(str));
    }
    public static int lengthOfLongestSubstring(String s) {
        int longest = 0;
        LinkedHashSet<Character> set = new LinkedHashSet<>();
        for (int i = 0; i < s.length(); i++) {
            if (!set.add(s.charAt(i))) {
                longest = Math.max(longest, set.size());
                while (!set.isEmpty()) {
                    if (set.removeFirst() == s.charAt(i)) {
                        break;
                    }
                }
                set.add(s.charAt(i));
            }
        }
        longest = Math.max(longest, set.size());
        return longest;
    }
}
