package jkademlia.gui.actions;

import java.awt.event.ActionEvent;
import java.net.UnknownHostException;
import java.util.List;

import jkademlia.facades.user.NetLocation;
import jkademlia.gui.EDragon;
import jkademlia.kademlia.JKademliaSystem;

/**
 * @author scaler Email:zhuzhengtao520@gmail.com
 *         ��½��Ӧ������֪һ�������еĽڵ��ֻҪ����login֮��ͻ����д���Ӧ
 */
public class LoginAction extends AbstractKeyboardAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1536816607196310405L;

	private EDragon eDragon;

	public LoginAction(EDragon eDragon) {
		this.eDragon = eDragon;
	}

	@Override
	public void actionPerformedImp(ActionEvent e) throws UnknownHostException {
		// ��ñ�����������нڵ㣬�����û�б�Ҫ�������ĳɻ�ȡ��һ�ڵ㣬�����Ͳ�Ҫ��������
		JKademliaSystem system = eDragon.jKademlia.getSystem();
		try {
			system.login(new NetLocation(eDragon.getIpField().getText(), Integer.parseInt(eDragon.getPortField().getText())));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
