package com.pan.algorithm;

import java.util.Arrays;

public class MinimumBribesSolution {

	
	/**
	 * @param q
	 */

	
    static int minimumBribes(int[] q) {
    	int switchTimes = 0;
       
        int index = 1;
        int lastSwitch = 0;
        for (int i=0;i<q.length-1;i++) {
        	if (q[i]-index >2) {
        		switchTimes = -1;
        		break;
        	}else if (q[i]-index ==2) {
        		switchTimes = switchTimes +2;
        		lastSwitch = 2;
        	}else if (q[i]-index ==1) {
        		switchTimes = switchTimes +1;
        		lastSwitch = 1;
        	}else if (q[i]-index ==0) {
        		if (lastSwitch==2) {
	        		switchTimes = switchTimes +1;
	        		lastSwitch = 1;
        		}
        	}else {
        		lastSwitch = 0;
        	}
        	System.out.println("index:"+index +",switchTimes:"+switchTimes);
        	index++;
        }
        
        return switchTimes;
    }


    public static void main(String[] args) {
        String str = "2 1 5 6 3 4 9 8 11 7 10 14 13 12 17 16 15 19 18 22 20 24 23 21 27 28 25 26 30 29 33 32 31 35 36 34 39 38 37 42 40 44 41 43 47 46 48 45 50 52 49 51 54 56 55 53 59 58 57 61 63 60 65 64 67 68 62 69 66 72 70 74 73 71 77 75 79 78 81 82 80 76 85 84 83 86 89 90 88 87 92 91 95 94 93 98 97 100 96 102 99 104 101 105 103 108 106 109 107 112 111 110 113 116 114 118 119 117 115 122 121 120 124 123 127 125 126 130 129 128 131 133 135 136 132 134 139 140 138 137 143 141 144 146 145 142 148 150 147 149 153 152 155 151 157 154 158 159 156 161 160 164 165 163 167 166 162 170 171 172 168 169 175 173 174 177 176 180 181 178 179 183 182 184 187 188 185 190 189 186 191 194 192 196 197 195 199 193 198 202 200 204 205 203 207 206 201 210 209 211 208 214 215 216 212 218 217 220 213 222 219 224 221 223 227 226 225 230 231 229 228 234 235 233 237 232 239 236 241 238 240 243 242 246 245 248 249 250 247 244 253 252 251 256 255 258 254 257 259 261 262 263 265 264 260 268 266 267 271 270 273 269 274 272 275 27";
        String[] array = str.split(" ");
        int[] q = new int[array.length];
        for (int i=0;i<array.length;i++){
        	q[i] = Integer.valueOf( array[i]);
        }
    	
        
        int switchTimes = minimumBribes(q);
        if (switchTimes==-1){
            System.out.println("Too chaotic");
        }else{
        	System.out.println(switchTimes);
        }
        
        
    }
}
