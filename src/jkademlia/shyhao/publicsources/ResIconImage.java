package jkademlia.shyhao.publicsources;

import java.awt.*;
import javax.swing.*;

public class ResIconImage {

	private static Image img = null;

	private static ImageIcon imgicon = null;

	public ResIconImage() {

	}

	private static Image getImg(String path) {

		img = Toolkit.getDefaultToolkit().getImage(path);

		return img;
	}

	private static ImageIcon getImgicon(String path) {

		imgicon = new ImageIcon(path);

		return imgicon;
	}

	public static Image getImage(String path) {

		return getImg(path);
	}

	public static ImageIcon getIcon(String path) {

		return getImgicon(path);
	}

}
