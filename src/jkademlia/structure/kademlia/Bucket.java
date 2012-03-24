package jkademlia.structure.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jkademlia.protocol.KademliaProtocol;
import jkademlia.structure.tree.MultiTreeNode;

public class Bucket extends KademliaTreeNode implements MultiTreeNode<Bucket, KademliaTreeNode> {

	private BigInteger prefix;     //前缀，因为k桶的划分是不包含自己的子树
	private int size;
	private int capacity;
	private boolean structural;
	private KademliaNode ownNode;
	private List<KademliaTreeNode> children;

	public Bucket(KademliaNode ownNode) {
		this(ownNode, null, KademliaProtocol.BUCKET_SIZE);
	}

	public Bucket(KademliaNode ownNode, BigInteger prefix) {
		this(ownNode, prefix, KademliaProtocol.BUCKET_SIZE);
	}

	public Bucket(KademliaNode ownNode, BigInteger prefix, int capacity) {
		this.ownNode = ownNode;
		this.prefix = prefix;
		this.size = 0;
		this.capacity = capacity;
		this.children = new ArrayList<KademliaTreeNode>(capacity);
	}

	public int getCapacity() {
		return capacity;
	}

	public int getSize() {
		return size;
	}

	public boolean isFull() {
		return size == capacity;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean isLeaf() {
		return (children == null || children.size() == 0);
	}

	public BigInteger getPrefix() {
		return prefix;
	}

	public void setPrefix(BigInteger prefix) {
		this.prefix = prefix;
	}

	public boolean isStructure() {
		return structural;
	}

	public void setStructure(boolean structure) {
		this.structural = structure;
	}

	@Override
	public List<KademliaTreeNode> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<KademliaTreeNode> nodes) {
		if (nodes != null) {
			if (nodes.size() <= capacity) {
				children = nodes;
				size = nodes.size();
				adjustChildren();     
			}
		} else {
			children = nodes;
		}
	}

	private void adjustChildren() {
		adjustChildren(this, children);
	}
	
	private void adjustChildren(Bucket bucket, List<KademliaTreeNode> nodes) {
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				children.get(i).setParent(bucket);
			}
		}
	}

	@Override
	public KademliaTreeNode getChild(int position) {
		return children.get(position);
	}

	@Override
	public boolean addChild(KademliaTreeNode node) {
		if (!isFull() && node != null) {
			node.setParent(this);
			children.add(node);
			size++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addChild(KademliaTreeNode node, int position) {
		if (!isFull() && node != null) {
			node.setParent(this);
			children.add(position, node);
			size++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addChildren(List<KademliaTreeNode> nodes) {
		if (nodes != null && nodes.size() <= capacity - size) {
			adjustChildren(this, nodes);
			children.addAll(nodes);
			size += nodes.size();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public KademliaTreeNode removeChild(int position) {
		KademliaTreeNode removed = children.remove(position);
		if (removed != null) {
			removed.setParent(null);
			size--;
		}
		return removed;
	}

	@Override
	public boolean removeChild(KademliaTreeNode node) {
		boolean removed = children.remove(node);
		if (removed) {
			node.setParent(null);
			size--;
		}
		return removed;
	}

	@Override
	public List<? extends KademliaTreeNode> removeChildren() {
		List<KademliaTreeNode> removedList = children;
		adjustChildren(null, removedList);
		children = null;
		size = 0;
		return removedList;
	}

	protected KademliaNode getOwnNode() {
		return ownNode;
	}

	protected void setOwnNode(KademliaNode ownNode) {
		this.ownNode = ownNode;
	}

	/**
	 *function:当添加的节点在包含该节点的子树里面时，就会将该子树分裂
	 */
	public void split() {
		if (!isStructure()) {
			Bucket left = new Bucket(getOwnNode());
			Bucket right = new Bucket(getOwnNode());
			this.setStructure(true);
			int prefixPos;

			if (this.getPrefix() == null) {
				prefixPos = 1;
				left.setPrefix(BigInteger.ONE);
				right.setPrefix(BigInteger.ZERO);
			} else {
				prefixPos = this.getPrefix().bitLength();
				left.setPrefix((this.getPrefix().shiftLeft(1)).setBit(0));
				right.setPrefix((this.getPrefix().shiftLeft(1)).clearBit(0));
			}

			for (int i = this.getChildren().size(); i > 0; i--) {
				KademliaNode removed = (KademliaNode) this.removeChild(0);
				BigInteger nodeID = removed.getNodeID();
				if (nodeID.testBit(4 - prefixPos)) {
					left.addChild(removed);
				} else {
					right.addChild(removed);
				}
			}
			this.addChild(left);
			this.addChild(right);
		}
	}

	protected void divideContents(Bucket left, Bucket right, int prefixPosition) {
		this.divideContents(left, right, prefixPosition, this.getChildren());
	}

	protected void divideContents(Bucket left, Bucket right,
			int prefixPosition, List<KademliaTreeNode> list) {
		for (Iterator<KademliaTreeNode> it = list.iterator(); it.hasNext();) {
			KademliaNode node = (KademliaNode) it.next();
			BigInteger nodeId = node.getNodeID();
			if (nodeId.testBit(KademliaProtocol.NODE_ID_LENGTH - prefixPosition)) {
				left.addChild(node);
			} else {
				right.addChild(node);
			}
		}
	}
}
