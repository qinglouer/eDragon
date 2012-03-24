package jkademlia.shyhao.publicsources;

import java.awt.Component;

import javax.swing.*;

public class LookAndFeel {

	public LookAndFeel() {

	}

	public static void changeLookAndFeel(String type, Component jcomponent) {

		try {

			if (type.equals("Native")) { // �ж��������ĸ��˵���
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName()); // ���ý�����ʽ
			} else if (type.equals("Motif")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} else if (type.equals("Metal")) {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			}
			javax.swing.SwingUtilities.updateComponentTreeUI(jcomponent); // ���½���

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
