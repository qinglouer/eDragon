package shyhao.windows.explorer.GraphicalChart.ChartResources;

public class LoadClass {

	public LoadClass() {

	}

	public static Class getClass(String s) {
		Class cls = null;
		try {
			cls = Class.forName(s);
		} catch (ClassNotFoundException cnfe) {
			throw new NoClassDefFoundError(cnfe.getMessage());
		}
		return cls;
	}
}
