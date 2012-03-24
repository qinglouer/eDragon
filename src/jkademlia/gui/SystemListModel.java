//package jkademlia.gui;
//
//import java.util.Iterator;
//import java.util.List;
//
//import javax.swing.AbstractListModel;
//
//import jkademlia.structure.kademlia.KademliaNode;
//
//public class SystemListModel extends AbstractListModel implements Iterable<KademliaNode> {
//
//	/**自定义的ListModel实现的应用程序
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	private List<KademliaNode> node;
//	public SystemListModel(List<KademliaNode> node){
//		this.node = node;
//	}
//	
//	public Iterator<KademliaNode> iterator() {
//		return node.iterator();
//	}
//	@Override
//	public Object getElementAt(int index) {
//		return node.get(index);
//	}
//	@Override
//	public int getSize() {	
//		return node.size();
//	}
//	
//}
