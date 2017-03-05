package com.acciaccatura.rational;

import com.acciaccatura.rational.exceptions.InvalidValueException;

//Java library for expressing rational base-10 numbers, and performing arithmetic on them.

public class Rational {
	
	private boolean non_neg;
	private short[] numerator, denominator;
	private boolean finite;
	
	public Rational(long a) {
		setNumber(Long.toString(a), "1");
	}
	
	public Rational(long a, long b) {
		setNumber(Long.toString(a), Long.toString(b));
	}
	
	public Rational(String a, String b) {
		setNumber(a, b);
	}
	
	public Rational(String a) {
		setNumber(a, "1");
	}
	
	private Rational(short[] a, short[] b, boolean neg, boolean fin) {
		numerator = a;
		denominator = b;
		non_neg = neg;
		finite = fin;
	}
	
	public void setNumber(String a, String b) {
		non_neg = !(a.charAt(0) == '-' ^ b.charAt(0) == '-');
		finite = true;
		if (a.charAt(0) == '-') {
			a = a.substring(1);
		}
		if (b.charAt(0) == '-') {
			b.substring(1);
		}
		a = a.replaceFirst("^0+", "");
		b = b.replaceFirst("^0+", "");
		if (!a.replaceFirst("[\\D]", "").equals(a) || !b.replaceFirst("[\\D]", "").equals(b)) {
			throw new InvalidValueException("Non-numeric value detected.");
		}
		numerator = new short[a.length()];
		denominator = new short[b.length()];
		for (int x = 0; x < numerator.length; x++)
			numerator[x] = (short) (a.charAt(numerator.length-1-x)-0x30);
		for (int x = 0; x < denominator.length; x++)
			denominator[x] = (short) (b.charAt(denominator.length-1-x)-0x30);
	}
	
	//Define some arithmetic stuff
	
	//TODO: What if one of the numbers are negative?
	public Rational add(Rational numb){
		short[] denom = multiplyVectors(this.denominator, numb.getDenominator());
		short[] num = addVectors(multiplyVectors(this.getNumerator(), numb.getDenominator()), multiplyVectors(this.getDenominator(), numb.getNumerator()));
		return new Rational(num, denom, false, false);
	}
	
	//TODO: I think this function's finished
	public Rational multiply(Rational numb) {
		boolean finite = this.finite || numb.finite;
		boolean non_neg = !(numb.non_neg ^ this.non_neg);
		if (!finite) {
			short[] a = {0};
			short[] b = {1};
			return new Rational(a, b, non_neg, finite);
		} else {
			return new Rational(multiplyVectors(this.getNumerator(), numb.getNumerator()), multiplyVectors(this.getDenominator(), numb.getDenominator()), non_neg, finite);
		}
	}
	
	public Rational divide(Rational numb) {
		boolean finite = this.finite || numb.finite;
		boolean non_neg = !(numb.non_neg ^ this.non_neg);
		if (!finite) {
			short[] a = {0};
			short[] b = {1};
			return new Rational(a, b, non_neg, finite);
		} else {
			return new Rational(multiplyVectors(this.getNumerator(), numb.getDenominator()), multiplyVectors(this.getDenominator(), numb.getNumerator()), non_neg, finite);
		}
	}
	
	//This function should be called upon creating a new Rational.
	//Since this object is immutable, it should be fine there.
	private void reduce() {
		
	}
	
	private static short[] addVectors(short[] a, short[] b) {
		short[] one = (a.length > b.length) ? a : b;
		short[] two = (one == a) ? b : a;
		short[] three = new short[one.length + 1];
		short carry = 0;
		for (int at = 0; at < two.length; at++) {
			short answer = (short) (one[at]+two[at]+carry);
			three[at] = (short) (answer%10);
			carry = (short) (answer/10);
		}
		for (int at = two.length; at < one.length; at++) {
			short answer = (short) (one[at]+carry);
			three[at] = (short) (answer%10);
			carry = (short) (answer/10);
		}
		three[three.length-1] = carry;
		return three;
	}
	
	//Entering an empty list is similar to multiplying by 1.
	//Should I use Karatsuba's algorithm?
	//TODO: think about it
	private static short[] multiplyVectors(short[] a, short[] b) {
		short[] one = (a.length > b.length) ? a : b;
		short[] two = (a == one) ? b : a;
		int[] three = new int[one.length+two.length];
		int carry = 0;
		for (int at = 0; at < one.length; at++) {
			for (int at2 = 0; at2 < two.length; at2++) {
				three[at+at2] += two[at2]*one[at];
			}
		}
		for (int at = 0; at < three.length; at++) {
			three[at] += carry;
			carry = three[at]/10;
			three[at] -= carry*10;
		}
		three[three.length-1] += carry;
		int newLength = three.length-1;
		while (newLength >= 0 && three[newLength] == 0)
			newLength--;
		short[] four = new short[newLength+1];
		for (int at = 0; at < four.length; at++) {
			four[at] = (short) three[at];
		}
		return four;
	}
	
	//Performs integer division on two vectors.
	//TODO: this
	public static short[] divideVectors(short[] a, short[] b) {
		if (b.length > a.length) {
			short[] answer = {0};
			return answer;
		} else {
			int pointer = 0;
			while (pointer + b.length <= a.length) {
				if (a[pointer] < b[pointer]) {
					
				} else {
					
				}
			}
			return null;
		}
	}
	
	public short[] getNumerator() {
		return numerator;
	}
	public short[] getDenominator() {
		return denominator;
	}
	
	public String toString() {
		StringBuilder num = new StringBuilder();
		StringBuilder den = new StringBuilder();
		boolean numIsZero = true;
		boolean denIsZero = true;
		for (int a = numerator.length-1; a >= 0; a--) {
			num.append(numerator[a]);
		}
		for (int a = denominator.length-1; a >= 0; a--) {
			den.append(denominator[a]);
		}
		return ((non_neg) ? "" : "-") + num.toString() + (den.toString().equals("1") ? "" : "/"+den.toString());
	}
	
	public static void main(String[] args) {
		Rational r1 = new Rational("36", "4");
		Rational r2 = new Rational("12", "3");
		Rational r3 = new Rational("12", "1");
		System.out.println(r1 + "\n----\n" + r2);
		System.out.println(r1.multiply(r2));
		System.out.println(r3);
	}
	
}
