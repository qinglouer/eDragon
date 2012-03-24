package jkademlia.structure.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import jkademlia.gui.EDragon;
import jkademlia.gui.KnowContactsModel;

import org.apache.log4j.Logger;


public class KnowContacts implements Iterable<KademliaNode>{

	private static final Logger logger = Logger.getLogger(KnowContacts.class);
	private static final BigInteger MAX = new BigInteger("2").pow(160);               //���ֵ����2��160�η�
	
	private List<KademliaNode> contactList;
	private int maxSize;
	private BigInteger myID;
	private String myIDString;
	private Hashtable<BigInteger, String> contactsTable;
	
	public enum AddResult {	
		ADDED, ALREADY_ADDED, CONTACTS_FULL, REPLACED_FARTEST
	}
	
	public KnowContacts(BigInteger myID){
		this(Integer.parseInt(System.getProperty("jkademlia.contacts.size")),myID);
	}
	
	public KnowContacts(int maxSize, BigInteger myID) {
		this.maxSize = maxSize;
		this.contactList = new ArrayList<KademliaNode>();
		this.myID = myID;
		this.myIDString = myID.toString(16);  //�ڵ��ID������ʮ��������ʾ
		contactsTable = new Hashtable<BigInteger,String>();
	}
	
	/**
	 *function:��ӽڵ���Ϣ
	 *@param node
	 *@return
	 */
	public synchronized AddResult addContact(KademliaNode node) {
		logger.debug("Trying to add contact " + node);
		if (node.getNodeID().equals(myID)) {
			logger.warn("Cannot add myself to know contacts!");
			return AddResult.ALREADY_ADDED;
		}
		if (contactList.size() < maxSize) {
			KademliaNode alreadyAdded = findContact(node.getNodeID());
			if (alreadyAdded == null) {
				this.addContactOnList(node);
				logger.debug("�ڵ�" + node + " ��ӳɹ�");
				return AddResult.ADDED;
			} else {
				logger.debug("Contact " + node + " already added!");
				return AddResult.ALREADY_ADDED;
			}
		} else {
			logger.debug("���Ѿ�����, looking for fartest contact");
			KademliaNode fartest = getFartestContact();
			logger.debug("Fartest contact is " + fartest);
			int fartestCompare = fartest.getNodeID().compareTo(myID);
			int nodeCompare = fartest.getNodeID().compareTo(node.getNodeID());
			if (nodeCompare * fartestCompare > 0) {
				logger.debug("Contact added: " + node + " is closer to "+ myIDString + " than " + fartest);
				this.removeContact(fartest);
				this.addContactOnList(node);
				return AddResult.REPLACED_FARTEST;
			} else {
				logger.debug("Contact not added: " + node + " is farter to "+ myIDString + " than " + fartest);
				return AddResult.CONTACTS_FULL;
			}
		}
	}
	
	/**
	 *function:���б��в����Ƿ��иýڵ�
	 *@param nodeID
	 *@return
	 */
	public KademliaNode findContact(BigInteger nodeID) {
		for (Iterator<KademliaNode> it = contactList.iterator(); it.hasNext();) {
			KademliaNode nextNode = it.next();
			BigInteger nextNodeID = nextNode.getNodeID();
			int compare = nextNodeID.compareTo(nodeID);
			if (compare == 0)
				return nextNode;
			else if (compare > 0)
				break;
		}
		return null;
	}
	
	private void addContactOnList(KademliaNode node) {
		BigInteger id = node.getNodeID();
		int position = 0;
		while (position < contactList.size()) {
			if (contactList.get(position).getNodeID().compareTo(id) >= 0)
				break;
			else
				position++;
		}
		contactList.add(position, node);
	}
	
	private KademliaNode getFartestContact() {
		KademliaNode node = null;
		if (contactList.size() > 0) {
			KademliaNode firstNode = contactList.get(0);
			if (contactList.size() == 1) {
				node = firstNode;
			} else {
				KademliaNode lastNode = contactList.get(contactList.size() - 1);
				BigInteger firstNodeDelta = firstNode.getNodeID().xor(myID);
				BigInteger lastNodeDelta = lastNode.getNodeID().xor(myID);
				node = firstNodeDelta.compareTo(lastNodeDelta) >= 0 ? firstNode : lastNode;
			}
		}
		return node;
	}
	
	public synchronized boolean removeContact(KademliaNode node) {
		return contactList.remove(node);
	}
	
	//���ؽڵ��ID
	public BigInteger getNodeID(KademliaNode node){
		return node.getNodeID();
	}

	public synchronized boolean removeContact(BigInteger nodeID) {
		return contactList.remove(new KademliaNode(nodeID));
	}
	
	public List<KademliaNode> findClosestContacts(BigInteger nodeID, int amount) {
		List<KademliaNode> result = new ArrayList<KademliaNode>();
		Integer position = this.getClosestContactPosition(nodeID);
		if (position != null) {
			int left = position;
			int right = position;
			int compare = nodeID.compareTo(contactList.get(position).getNodeID());
			if (compare > 0)
				right++;
			else
				left--;
			int i = 0;
			while (i < amount && left >= 0 && right < contactList.size()) {
				KademliaNode leftNode = contactList.get(left);
				KademliaNode rightNode = contactList.get(right);
				BigInteger leftRange = nodeID.subtract(leftNode.getNodeID());
				BigInteger rightRange = rightNode.getNodeID().subtract(nodeID);
				if (leftRange.compareTo(rightRange) < 0) {
					result.add(leftNode);
					left--;
				} else {
					result.add(rightNode);
					right++;
				}
				i++;
			}
			if (i < amount) {
				while (i < amount && left >= 0) {
					result.add(contactList.get(left));
					left--;
					i++;
				}
				while (i < amount && right < contactList.size()) {
					result.add(contactList.get(right));
					right++;
					i++;
				}
			}
		}
		return result;
	}

	public KademliaNode findClosestContact(BigInteger nodeID) {
		Integer position = getClosestContactPosition(nodeID);
		return position != null ? contactList.get(position) : null;
	}
	/**
	 *function:ͨ�����ϵ�������ĵ㿿��������ҵ�����ĵ�
	 *@param nodeID
	 *@return
	 */
	private Integer getClosestContactPosition(BigInteger nodeID) {
		Integer position = null;
		if (contactList.size() > 0) {
			boolean converging = true;
			BigInteger range = MAX;
			for (int i = 0; i < contactList.size() && converging; i++) {
				KademliaNode node = contactList.get(i);
				BigInteger newRange = nodeID.subtract(node.getNodeID()).abs();
				converging = newRange.compareTo(range) < 0;
				range = newRange;
				position = i;
			}
			if (!converging)
				position--;
		}
		return position;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public int getSize() {
		return this.contactList.size();
	}

	public Iterator<KademliaNode> iterator() {
		return this.contactList.iterator();
	}
	
	public List<KademliaNode> getContactList(){
		return contactList;
	}
	
	public Hashtable<BigInteger,String> getContactsTable(){
		for(KademliaNode node : contactList)
			contactsTable.put(node.getNodeID(), node.getCreditValue());
		return contactsTable;
	}
}

class KademliaNodeIDComparator implements Comparator<KademliaNode> {
	public int compare(KademliaNode node1, KademliaNode node2) {
		return node1.getNodeID().compareTo(node2.getNodeID());
	}

}
