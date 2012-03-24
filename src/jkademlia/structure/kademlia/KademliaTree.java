package jkademlia.structure.kademlia;

import java.util.List;

public interface KademliaTree {
	public KademliaTreeNode getRoot();

    public int countBuckets();

    public int countNodes();

    public List<Bucket> getBuckets();
}
