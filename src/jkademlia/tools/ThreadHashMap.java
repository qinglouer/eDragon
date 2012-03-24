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
	 *function:����ָ����group�ڴ˹�ϣӳ���е�ֵT.���û��ӳ�䣬�򷵻�null.
	 *HashMap�ǻ��ڹ�ϣ���map�ӿڵ�ʵ�֣���ͬ��
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
	 *function:�ڴ�ӳ���й���ָ��ֵ��ָ����
	 *@param obj
	 */
	public void put(T obj) {
		map.put(Thread.currentThread().getThreadGroup(), obj);
	}

	public abstract T initialValue();
}
