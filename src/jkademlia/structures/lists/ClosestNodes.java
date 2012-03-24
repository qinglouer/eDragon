package jkademlia.structures.lists;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import jkademlia.structure.kademlia.KademliaNode;

import org.apache.log4j.Logger;

public class ClosestNodes {
	private static Logger logger = Logger.getLogger(ClosestNodes.class);

	private TreeSet<KademliaNode> backSet;
	private int maxSize;
	private BigInteger closestNode;

	public ClosestNodes(int maxSize, BigInteger closestNode) {
		backSet = new TreeSet<KademliaNode>(new Comparator<KademliaNode>() {
			// ����һ���µĿյ�set,��set����ָ���ıȽ�����������
			@Override
			public int compare(KademliaNode o1, KademliaNode o2) {
				return o1.getNodeID().compareTo(o2.getNodeID());
			}
		});
		this.maxSize = maxSize;
		this.closestNode = closestNode;
	}

	/**
	 *function:��֤�˽ڵ�����ķ���ͷ�������ķ���β������������Ӵ�ȷ�ϣ�����ӽڵ�ʱ�õ�
	 *   least-recently  &   most-recently
	 *   ��ӳɹ�����TRUEʧ�ܷ���FALSE
	 *@param node
	 *@return
	 */
	public boolean add(KademliaNode node) {
		boolean result = true;
		if (backSet.size() < maxSize) {
			backSet.add(node);
			logger.debug("��ӽڵ㣺 " + node);
		} else {
			BigInteger add = node.getNodeID();
			BigInteger addDelta = add.xor(closestNode); 
			KademliaNode first = backSet.first();      //�����������е�set��Сֵ
			BigInteger firstDelta = first.getNodeID().xor(closestNode);
			if (addDelta.compareTo(firstDelta) < 0) {
				backSet.remove(first);
				logger.debug("Removed node " + first);
				backSet.add(node);
				logger.debug("Added node " + node);
			} else {
				KademliaNode last = backSet.last();
				BigInteger lastDelta = last.getNodeID().xor(closestNode);
				if (addDelta.compareTo(lastDelta) < 0) {
					backSet.remove(last);
					logger.debug("Removed node " + last);
					backSet.add(node);
					logger.debug("Added node " + node);
				} else {
					logger.debug("Node " + node + " not added");
					result = false;
				}
			}
		}
		return result;
	}

	public int getSize() {
		return backSet.size();
	}

	public int getMaxSize() {
		return maxSize;
	}

	public BigInteger getClosestNode() {
		return closestNode;
	}

	public List<KademliaNode> toList() {
		List<KademliaNode> list = new ArrayList<KademliaNode>();
		for (KademliaNode node : backSet)
			list.add(node);
		return list;
	}
}
