package jkademlia.controller.threads;

import java.util.HashMap;

public abstract class ThreadGroupLocal<T> {
	private HashMap<ThreadGroup, T> map;

	public ThreadGroupLocal() {
		map = new HashMap<ThreadGroup, T>();
	}

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

	public void set(T obj) {
		map.put(Thread.currentThread().getThreadGroup(), obj);
	}

	public abstract T initialValue();
}
