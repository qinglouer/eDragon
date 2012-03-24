package jkademlia.shyhao.publicsources;

import java.awt.Component;

import javax.swing.*;

public class LookAndFeel {

	public LookAndFeel() {

	}

	public static void changeLookAndFeel(String type, Component jcomponent) {

		try {

			if (type.equals("Native")) { // 判断来自于哪个菜单项
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName()); // 设置界面样式
			} else if (type.equals("Motif")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} else if (type.equals("Metal")) {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			}
			javax.swing.SwingUtilities.updateComponentTreeUI(jcomponent); // 更新界面

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
