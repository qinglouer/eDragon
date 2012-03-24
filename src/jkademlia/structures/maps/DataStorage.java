package jkademlia.structures.maps;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import jkademlia.controller.threads.ThreadGroupLocal;
import jkademlia.facades.storage.DataManagerFacade;
import jkademlia.protocol.KademliaProtocol;

public class DataStorage extends DataManagerFacade {

	public static final int KEY_LENGTH = KademliaProtocol.KEY_LENGTH;
	public static ThreadGroupLocal<DataStorage> dataStorage;

	private HashMap<BigInteger, Object> map;

	public DataStorage() {
		this.map = new HashMap<BigInteger, Object>();
	}

	public static DataStorage getDataStorage() {
		if (dataStorage == null) {
			dataStorage = new ThreadGroupLocal<DataStorage>() {
				public DataStorage initialValue() {
					return new DataStorage();
				}
			};
		}
		return dataStorage.get();
	}

	public List<Entry<BigInteger, Object>> getClosestValues(BigInteger key,
			BigInteger proximity) {
		List<Entry<BigInteger, Object>> result = new ArrayList<Entry<BigInteger, Object>>();
		for (Entry<BigInteger, Object> entry : map.entrySet()) {
			BigInteger delta = entry.getKey().subtract(key).abs();
			if (delta.compareTo(proximity) <= 0) {
				result.add(entry);
			}
		}
		return result;
	}

	public Iterator getIterator() {
		return map.entrySet().iterator();
	}

	public Object get(BigInteger key) {
		return map.get(key);
	}

	public int getSize() {
		return map.size();
	}

	public Object put(BigInteger key, Object value) {
		return map.put(key, value);
	}

	public Object remove(BigInteger key) {
		return map.remove(key);
	}

}
