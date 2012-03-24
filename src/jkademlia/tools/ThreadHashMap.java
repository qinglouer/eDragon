package jkademlia.tools;

import java.util.HashMap;

/**
 * @author zhuzhengtao  Email:zhuzhengtao520@gmail.com
 *
 * @param <T>
 */
public abstract class ThreadHashMap<T> {
	private HashMap<ThreadGroup, T> map;

	public ThreadHashMap() {
		map = new HashMap<ThreadGroup, T>();
	}

	/**
	 *function:返回指定键group在此哈希映射中的值T.如果没有映射，则返回null.
	 *HashMap是基于哈希表的map接口的实现，不同步
	 *@return T
	 */
	public T get() {
		T result = null;
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		synchronized (map) {
			result = map.get(group);
			if (result == null) {
				result = initialValue();
				map.put(group, result);
			}
		}
		return result;
	}

	/**
	 *function:在此映射中关联指定值与指定键
	 *@param obj
	 */
	public void put(T obj) {
		map.put(Thread.currentThread().getThreadGroup(), obj);
	}

	public abstract T initialValue();
}
