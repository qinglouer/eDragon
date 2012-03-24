package jkademlia.gui.actions;

import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.List;

import jkademlia.gui.EDragon;
import jkademlia.gui.KnowContactsModel;
import jkademlia.gui.SearchListModel;
import jkademlia.kademlia.JKademliaSystem;

public class RefreshAction extends AbstractKeyboardAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1943084489251843029L;

	private EDragon eDragon;

	public RefreshAction(EDragon eDragon) {
		this.eDragon = eDragon;
	}

	public void actionPerformedImp(ActionEvent e) {
		JKademliaSystem system = eDragon.jKademlia.getSystem();
		if (system != null) {
			Hashtable<BigInteger,String> knowContacts = system.tableKnowContacts();
			List<String> result = system.listResult();
			eDragon.setSearchResult(result);
			eDragon.getSearchList().setModel(new SearchListModel(result));
			eDragon.getNetTable().setModel(new KnowContactsModel(knowContacts));
			eDragon.getTabPane().repaint();
		}
	}

}
