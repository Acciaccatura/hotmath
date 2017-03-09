package com.acciaccatura.rational;

import java.util.Arrays;

import com.acciaccatura.rational.exceptions.InvalidValueException;
import com.acciaccatura.rational.shortarrays.ShortArrays;

//Java library for expressing rational base-10 numbers, and performing arithmetic on them.

//I just introduced the idea of negatives and subtraction. It is when all the numbers in the array are negative.
//If this is the case, non_neg may no longer be needed. I could just check the LSD for the numerator and denominator
//and XOR.

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
		reduce();
	}
	
	//Define some arithmetic stuff
	
	//TODO: What if one of the numbers are negative?
	public Rational add(Rational numb){
		short[] denom = ShortArrays.multiplyArrays(this.denominator, numb.getDenominator());
		short[] num = ShortArrays.addArrays(ShortArrays.multiplyArrays(this.getNumerator(), numb.getDenominator()), ShortArrays.multiplyArrays(this.getDenominator(), numb.getNumerator()));
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
			return new Rational(ShortArrays.multiplyArrays(this.getNumerator(), numb.getNumerator()), ShortArrays.multiplyArrays(this.getDenominator(), numb.getDenominator()), non_neg, finite);
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
			return new Rational(ShortArrays.multiplyArrays(this.getNumerator(), numb.getDenominator()), ShortArrays.multiplyArrays(this.getDenominator(), numb.getNumerator()), non_neg, finite);
		}
	}
	
	//This function should be called upon creating a new Rational.
	//Since this object is theoretically immutable, it should be fine there.
	private void reduce() {
		//Euclid's thing
		boolean isNegative = ShortArrays.isNegative(numerator) ^ ShortArrays.isNegative(denominator);
		numerator = ShortArrays.isNegative(numerator) ? ShortArrays.flipSign(numerator) : Arrays.copyOf(numerator, numerator.length);
		denominator = ShortArrays.isNegative(denominator) ? ShortArrays.flipSign(denominator) : Arrays.copyOf(denominator, denominator.length);
		int comparison = ShortArrays.compareTo(numerator, 0, numerator.length, denominator);
		if (comparison == 0) {
			short[] one = {1};
		}
		
		short[] max = (comparison > 0) ? numerator : denominator;
		short[] min = (comparison < 0) ? numerator : denominator;
		//System.out.println(Arrays.toString(max) + "\n" + Arrays.toString(min));
		short[] zero = new short[1];
		while (ShortArrays.compareTo(min, 0, min.length, zero) != 0) {
			//System.out.println(Arrays.toString(max) + "\n" + Arrays.toString(min));
			short[] temp = ShortArrays.divideArrays(max, min);
			temp = ShortArrays.addArrays(max, ShortArrays.flipSign(ShortArrays.multiplyArrays(min, temp)));
			max = min;
			min = temp;
			//System.out.println(Arrays.toString(max) + "\n" + Arrays.toString(min) + "\n" + Arrays.toString(temp) + "\n--------------------------------------------------------------------");
		}
		numerator = ShortArrays.divideArrays(numerator, max);
		denominator = ShortArrays.divideArrays(denominator, max);
		if (isNegative)
			numerator = ShortArrays.flipSign(numerator);
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
		Rational a = new Rational("74", "5465734675534");
		System.out.println(a);
		a = new Rational("5", "534");
		System.out.println(a);
	}
	
}
