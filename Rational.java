package com.acciaccatura.rational;

import java.util.Arrays;

import com.acciaccatura.rational.exceptions.InvalidValueException;
import com.acciaccatura.rational.shortarrays.ShortArrays;

//Java library for expressing rational base-10 numbers, and performing arithmetic on them.

//I just introduced the idea of negatives and subtraction. It is when all the numbers in the array are negative.
//If this is the case, non_neg may no longer be needed. I could just check the LSD for the numerator and denominator
//and XOR.

public class Rational {

	private short[] numerator, denominator;
	
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
	
	private Rational(short[] a, short[] b) {
		numerator = a;
		denominator = b;
	}
	
	public void setNumber(String a, String b) {
		boolean neg = (a.charAt(0) == '-' ^ b.charAt(0) == '-');
		if (a.charAt(0) == '-') {
			a = a.substring(1);
		}
		if (b.charAt(0) == '-') {
			b = b.substring(1);
		}
		a = a.replaceFirst("^0+", "");
		b = b.replaceFirst("^0+", "");
		if (!a.replaceFirst("[\\D]", "").equals(a) || !b.replaceFirst("[\\D]", "").equals(b)) {
			throw new InvalidValueException("Non-numeric value detected.");
		}
		numerator = new short[a.length()];
		denominator = new short[b.length()];
		for (int x = 0; x < numerator.length; x++)
			numerator[x] = (short) ((a.charAt(numerator.length-1-x)-0x30)*(neg ? -1 : 1));
		for (int x = 0; x < denominator.length; x++)
			denominator[x] = (short) ((b.charAt(denominator.length-1-x)-0x30));
		numerator = ShortArrays.getUnpaddedArray(numerator);
		denominator = ShortArrays.getUnpaddedArray(denominator);
		if (denominator.length == 1 && denominator[0] == 0) {
			throw new InvalidValueException("Denominator is zero.");
		}
		reduce();
	}
	
	//Define some arithmetic stuff
	
	//TODO: What if one of the numbers are negative?
	public Rational add(Rational numb){
		short[] denom = ShortArrays.multiplyArrays(this.denominator, numb.getDenominator());
		short[] num = ShortArrays.addArrays(ShortArrays.multiplyArrays(this.getNumerator(), numb.getDenominator()), ShortArrays.multiplyArrays(this.getDenominator(), numb.getNumerator()));
		return new Rational(num, denom);
	}
	
	//TODO: I think this function's finished
	public Rational multiply(Rational numb) {
		return new Rational(ShortArrays.multiplyArrays(this.getNumerator(), numb.getNumerator()), ShortArrays.multiplyArrays(this.getDenominator(), numb.getDenominator()));
	}
	
	public Rational divide(Rational numb) {
		return new Rational(ShortArrays.multiplyArrays(this.getNumerator(), numb.getDenominator()), ShortArrays.multiplyArrays(this.getDenominator(), numb.getNumerator()));
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
		boolean numIsNeg = ShortArrays.isNegative(numerator);
		boolean denIsNeg = ShortArrays.isNegative(denominator);
		if (numerator.length == 1 && numerator[0] == 0)
			return "0";
		for (int a = numerator.length-1; a >= 0; a--) {
			num.append(numerator[a]*(numIsNeg ? -1 : 1));
		}
		for (int a = denominator.length-1; a >= 0; a--) {
			den.append(denominator[a]*(numIsNeg ? -1 : 1));
		}
		return ((numIsNeg ^ denIsNeg) ? "-" : "") + num.toString() + (denominator.length == 1 && denominator[0] == 1 ? "" : "/" + den.toString());
	}
	
	public String toDecimalString(int acc) {
		boolean numbIsNeg = ShortArrays.isNegative(numerator) ^ ShortArrays.isNegative(denominator);
		short[] num2 = ShortArrays.digitShift(numerator, acc + numerator.length);
		StringBuilder numb = new StringBuilder();
		short[] intdiv = ShortArrays.divideArrays(numerator, denominator);
		short[] mydiv = ShortArrays.divideArrays(num2, denominator);
		int diff = mydiv.length - intdiv.length;
		int at = mydiv.length-1;
		if (numbIsNeg)
			numb.append("-");
		//TODO: I'm still not 100% that this is correct.
		if (ShortArrays.compareTo(numerator, 0, numerator.length, denominator) == -1) {
			numb.append("0.");
			diff = denominator.length - numerator.length;
			for (int a = 0; a < diff; a++) {
				numb.append("0");
			}
			for (; at >= 0; at--) {
				numb.append(Math.abs(mydiv[at]));
			}
			return numb.toString();
		}
		for (; at >= diff; at--) {
			numb.append(Math.abs(mydiv[at]));
		}
		//System.out.println(diff);
		if (diff != 0)
			numb.append(".");
		for (; at >= 0; at--) {
			numb.append(Math.abs(mydiv[at]));
		}
		return numb.toString();
	}
	
	public static void main(String[] args) {
		Rational a = new Rational("1","2348910");
		System.out.println(a.multiply(new Rational("09825", "0000")).toDecimalString(1000));
	}
	
}
