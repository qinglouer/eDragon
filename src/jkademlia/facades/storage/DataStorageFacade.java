package jkademlia.facades.storage;

import java.math.BigInteger;

public interface DataStorageFacade<Value> {
	public Value get(BigInteger key);
	public Value put(BigInteger key,Value value);
	public Value remove(BigInteger key);
	public int getSize();
}
