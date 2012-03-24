package jkademlia.tools;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DataTools {
	protected DataTools() {

	}

	public byte[] convertArray(Byte[] array) {
		byte[] result = new byte[array.length];
		for (int i = 0; i < array.length; i++)
			result[i] = array[i];
		return result;
	}

	public Byte[] convertArray(byte[] array) {
		Byte[] result = new Byte[array.length];
		for (int i = 0; i < array.length; i++)
			result[i] = array[i];
		return result;
	}

	public ByteBuffer subByteBuffer(ByteBuffer data, int offset, int length) {
		if (offset < 0 || length < 1 || offset > data.capacity() || offset + length > data.capacity()) {
			return null;
		}
		return ByteBuffer.wrap(data.array(), offset, length);
	}

	public ByteBuffer copyByteBuffer(ByteBuffer data, int offset, int length) {
		if (offset < 0 || length < 1 || offset > data.capacity() || offset + length > data.capacity())
			return null;
		byte[] result = new byte[length];
		byte[] array = data.array();
		System.arraycopy(array, offset, result, 0, length);
		return ByteBuffer.wrap(result);
	}

	public byte[] copyByteArray(byte[] array, int offset, int length) {
		if (offset < 0 || length < 1 || offset > array.length || offset + length > array.length)
			return null;
		byte[] result = new byte[length];
		System.arraycopy(array, offset, result, 0, length);
		return result;
	}

	public Byte[] copyByteArray(Byte[] array, int offset, int length) {
		if (offset < 0 || length < 1 || offset > array.length || offset + length > array.length)
			return null;
		Byte[] result = new Byte[length];
		System.arraycopy(array, offset, result, 0, length);
		return result;
	}

	public byte[] formatByteArray(byte[] array, int bytes) {
		byte[] result = new byte[bytes];
		if (array.length >= bytes)
			System.arraycopy(array, array.length - bytes, result, 0, bytes);
		else
			System.arraycopy(array, 0, result, bytes - array.length, array.length);
		return result;
	}

	public String toBinaryString(byte[] data, int length) {
		return this.toBinaryString(new BigInteger(1, data), length);
	}

	public String toBinaryString(BigInteger data, int length) {
		String result = "";
		for (int i = 0; i < length; i++) {
			char bit;
			if (data.testBit(i))
				bit = '1';
			else
				bit = '0';
			result = bit + result;
		}
		return result;
	}

}
