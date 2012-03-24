package jkademlia.gui.actions;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

public abstract class AbstractKeyboardAction  extends AbstractAction{

	private static final long serialVersionUID = 4841127086586463052L;

	public void actionPerformed(ActionEvent e) {
		//���ص�ǰ���������ߣ�Ȼ�����ǲ����ı����������Ӧֻ�Գ��ı����֮��������Ӧ
		Component focused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (!(focused instanceof JTextComponent))
			try {
				actionPerformedImp(e);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
	}
	public abstract void actionPerformedImp(ActionEvent e) throws UnknownHostException;
}
