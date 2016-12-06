package com.aparapi.examples.add;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeAccess {
	public static final Unsafe unsafe;
	static {
		try {
			// This is a bit of voodoo to force the unsafe object into
			// visibility and acquire it.
			// This is not playing nice, but as an established back door it is
			// not likely to be
			// taken away.
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
