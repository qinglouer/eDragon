package shyhao.windows.explorer.CPU;
/**
 * 标题：使用jni求cpu的使用率
 * 用法：int cpuRatio=
 *       shyao.windows.explorer.CPU.CPUResources.getCpuRatio();
 * 注意：使用前请把"cpulib.dll"放在java/jdk/bin中
 */
public class CPUResources {
	
	
		public CPUResources() {

	}
    
    //--------------------------------------------
    //jni：使用下面的getCpu()的方法时，必须加载名为"cpulib"的库文件
	private static final String SILIB = "cpulib";

	static {
		
		try {

			System.loadLibrary(SILIB);

		} catch (Exception e) {
			
			System.out.println("本地库文件" 
				    + SILIB
					+ "在"
					+ System.getProperty("java.library.path")
					+"中不存在");

		}
	}
	//-------------------------------------------------
	//jni:声明一个本地方法，注意使用关键词"native"
	public static native int getCpuRatio();

}
