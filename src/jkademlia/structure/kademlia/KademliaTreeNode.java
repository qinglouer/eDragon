package jkademlia.structure.kademlia;

import jkademlia.structure.tree.SimpleTreeNode;

public class KademliaTreeNode implements SimpleTreeNode<Bucket> {

	private Bucket parent;

	public Bucket getParent() {
		return parent;
	}

	public boolean isRoot() {
		return (getParent() == null);
	}

	public void setParent(Bucket node) {
		this.parent = node;
	}
}
