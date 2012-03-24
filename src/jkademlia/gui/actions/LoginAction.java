package jkademlia.gui.actions;

import java.awt.event.ActionEvent;
import java.net.UnknownHostException;
import java.util.List;

import jkademlia.facades.user.NetLocation;
import jkademlia.gui.EDragon;
import jkademlia.kademlia.JKademliaSystem;

/**
 * @author scaler Email:zhuzhengtao520@gmail.com
 *         登陆响应，当已知一个网络中的节点后，只要按下login之后就会运行此响应
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
		// 获得本机上面的所有节点，这个有没有必要，到最后改成获取单一节点，这样就不要迭代遍历
		JKademliaSystem system = eDragon.jKademlia.getSystem();
		try {
			system.login(new NetLocation(eDragon.getIpField().getText(), Integer.parseInt(eDragon.getPortField().getText())));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
