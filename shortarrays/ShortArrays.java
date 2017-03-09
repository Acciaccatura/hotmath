package com.acciaccatura.rational.shortarrays;

import java.util.Arrays;

public class ShortArrays {
	
	//Now with subtraction!!
	public static short[] addArrays(short[] a, short[] b) {
		short[] one = (a.length > b.length) ? a : b;
		short[] two = (one == a) ? b : a;
		short[] three = new short[one.length+1];
		for (int at = 0; at < one.length; at++) {
			three[at] = one[at];
			if (at < two.length) {
				three[at] += two[at];
			}
		}
		int pointer = three.length;
		while (--pointer >= 0 && three[pointer] == 0);
		if (pointer < 0) {
			return new short[1];
		}
		//pointer will always be < three.length-1!!!
		boolean negative = three[pointer] < 0;
		for (int at = 0; at < pointer; at++) {
			if (negative ^ (three[at] < 0)) {
				three[at] += (negative ? -10 : 10);
				three[at+1] += (negative ? 1 : -1);
			}
		}
		return getUnpaddedArray(three);
	}
	
	//Entering an empty list is similar to multiplying by 1.
	//Should I use Karatsuba's algorithm?
	//TODO: think about it
	public static short[] multiplyArrays(short[] a, short[] b) {
		short[] one = (a.length > b.length) ? a : b;
		short[] two = (a == one) ? b : a;
		int[] three = new int[one.length+two.length];
		int carry = 0;
		for (int at = 0; at < one.length; at++) {
			for (int at2 = 0; at2 < two.length; at2++) {
				three[at+at2] += two[at2]*one[at];
			}
		}
		short[] four = new short[three.length];
		for (int at = 0; at < three.length; at++) {
			three[at] += carry;
			carry = three[at]/10;
			four[at] = (short) (three[at] - carry*10);
		}
		four[four.length-1] += carry;
		return getUnpaddedArray(four);
	}
	
	//Performs integer division on two vectors.
	//TODO: this
	public static short[] divideArrays(short[] a, short[] b) {
		if (b.length > a.length || (b.length == 1 && b[0] == 0)) {
			return new short[1];
		} else {
			boolean is_neg = isNegative(a) ^ isNegative(b);
			short[] negb = !isNegative(b) ? flipSign(b) : b;
			//System.out.println(Arrays.toString(negb));
			a = isNegative(a) ? flipSign(a) : a;
			int pointer = a.length-b.length;
			short[] three = new short[a.length-b.length+1];
			while (pointer >= 0) {
				short[] negb_ext = digitShift(negb,pointer);
				short count = -1;
				//System.out.println(Arrays.toString(a) + "\n " + Arrays.toString(negb_ext));
				while (!isNegative(a)) {
					a = addArrays(a, negb_ext);
					count++;
				}
				a = addArrays(a, flipSign(negb_ext));
				if (count >= 0) {
					three[pointer] = count;
				}
				pointer--;
			}
			return getUnpaddedArray(is_neg ? flipSign(three) : three);
		}
	}
	
	public static short[] getUnpaddedArray(short[] a) {
		int pointer = a.length-1;
		while (pointer >= 0 && a[pointer] == 0) {
			pointer--;
		}
		if (pointer == -1) {
			return new short[1];
		}
		short[] four = new short[pointer+1];
		for (int at = 0; at < pointer+1; at++) {
			four[at] = a[at];
		}
		return four;
	}
	
	public static short[] digitShift(short[] a, int shift) {
		if (-shift > a.length) {
			return new short[1];
		} else if (shift == 0) {
			return Arrays.copyOf(a, a.length);
		} else {
			short[] a2 = new short[a.length + shift];
			for (int at = 0; at < a.length; at++) {
				a2[shift+at] = a[at];
			}
			return a2;
		}
	}
	
	//This is an auxiliary function to divideArrays()
	public static int compareTo(short[] a, int rangea1, int rangea2, short[] b) {
		int maxLength = Math.max(rangea2, b.length)-1;
		while (maxLength >= rangea1) {
			int aval = (maxLength < a.length) ? a[maxLength] : -10;
			int bval = (maxLength < b.length) ? b[maxLength] : -10;
			if (aval != bval) {
				return aval > bval ? 1 : -1;
			}
			maxLength--;
		}
		return 0;
	}

	public static boolean isNegative(short[] a) {
		int pointer = -1;
		while (++pointer < a.length && a[pointer] >= 0);
		return pointer != a.length;
	}
	
	public static short[] flipSign(short[] a) {
		short[] a2 = new short[a.length];
		for (int at = 0; at < a.length; at++) {
			a2[at] = (short) -a[at];
		}
		return a2;
	}
}
