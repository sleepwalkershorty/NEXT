package de.ibh_impex.androidhelputils;

public class Hash {
	
	static int rot(int x, int k) { return ((x)<<(k)) | ((x)>>>(32-(k))); }

	/**
	 * @param k	 the key to hash
	 * @param length	 length of the key
	 * @return	the 32 bit hash code
	 */
	@SuppressWarnings("fallthrough")
	public static int GetHash(int[] k, int length) {
		int a, b, c;
		a = b = c = 0xDEADBEEF + (length << 2);

		int i = 0;
		while (length > 3) {
			a += k[i];
			b += k[i+1];
			c += k[i+2];

			// mix(a,b,c)... Java needs "out" parameters!!!
			// Note: recent JVMs (Sun JDK6) turn pairs of shifts (needed to do a rotate)
			// into real x86 rotate instructions.
			{
				a -= c; a ^= rot(c,  4); c += b;
				b -= a; b ^= rot(a,  6); a += c;
				c -= b; c ^= rot(b,  8); b += a;
				a -= c; a ^= rot(c, 16); c += b;
				b -= a; b ^= rot(a, 19); a += c;
				c -= b; c ^= rot(b,  4); b += a;
			}

			length -= 3;
			i += 3;
		}

		switch (length) {
			case 3 : c+=k[i+2];	// fall through
			case 2 : b+=k[i+1];	// fall through
			case 1 : a+=k[i+0];	// fall through
				// final(a,b,c);
			{
				c ^= b; c -= rot(b, 14);
				a ^= c; a -= rot(c, 11);
				b ^= a; b -= rot(a, 25);
				c ^= b; c -= rot(b, 16);
				a ^= c; a -= rot(c,  4);
				b ^= a; b -= rot(a, 14);
				c ^= b; c -= rot(b, 24);
			}
			case 0:
				break;
		}
		return c;
	}
}
