package com.pan.algorithm.backtracking;

// https://leetcode.com/problems/regular-expression-matching/description/
public class Regex {

    private boolean matched = false;
    private char[] pattern; //
    private int plen; // length of the regex

    public Regex(char[] pattern, int plen) {
        this.pattern = pattern;
        this.plen = plen;
    }

    public boolean match(char[] text, int tlen) {
        matched = false;
        rmatch(0, 0, text, tlen);
        return matched;
    }

    private void rmatch(int ti, int pj, char[] text, int tlen) {
        if (matched) return;
        if (pj == plen) {
            if (ti == tlen) matched = true;
            return;
        }
        if (pattern[pj] == '*') { // * match any length of char
            for (int k = 0; k <= tlen-ti; ++k) {
                rmatch(ti+k, pj+1, text, tlen);
            }
        } else if (pattern[pj] == '?') { // ? match 0 or 1 of char
            rmatch(ti, pj+1, text, tlen);
            rmatch(ti+1, pj+1, text, tlen);
        } else if (ti < tlen && pattern[pj] == text[ti]) { // char match
            rmatch(ti+1, pj+1, text, tlen);
        }
    }
}
