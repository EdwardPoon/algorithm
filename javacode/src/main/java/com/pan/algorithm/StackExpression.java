package com.pan.algorithm;

import java.util.Stack;

public class StackExpression {
	
	public static void main(String[] args) {
		
		String formula = "( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) )";
		Stack<String> ops = new Stack<String>();
		Stack<Double> vals = new Stack<Double>();
		for(int i=0;i<formula.length();i++) { // Read token, push if operator.
			String s = String.valueOf(formula.charAt(i));
			if (s.equals(" "))
				;
			else if (s.equals("("))
				;
			else if (s.equals("+"))
				ops.push(s);
			else if (s.equals("-"))
				ops.push(s);
			else if (s.equals("*"))
				ops.push(s);
			else if (s.equals("/"))
				ops.push(s);
			else if (s.equals("sqrt"))
				ops.push(s);
			else if (s.equals(")")) { // Pop, evaluate, and push result if token
										// is ")".
				String op = ops.pop();
				double v = vals.pop();
				if (op.equals("+"))
					v = vals.pop() + v;
				else if (op.equals("-"))
					v = vals.pop() - v;
				else if (op.equals("*"))
					v = vals.pop() * v;
				else if (op.equals("/"))
					v = vals.pop() / v;
				else if (op.equals("sqrt"))
					v = Math.sqrt(v);
				vals.push(v);
			} // Token not operator or paren: push double value.
			else
				vals.push(Double.parseDouble(s));
		}
		System.out.println(vals.pop());
	}
}
