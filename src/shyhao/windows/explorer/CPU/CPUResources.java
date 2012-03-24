package shyhao.windows.explorer.CPU;
/**
 * ���⣺ʹ��jni��cpu��ʹ����
 * �÷���int cpuRatio=
 *       shyao.windows.explorer.CPU.CPUResources.getCpuRatio();
 * ע�⣺ʹ��ǰ���"cpulib.dll"����java/jdk/bin��
 */
public class CPUResources {
	
	
		public CPUResources() {

	}
    
    //--------------------------------------------
    //jni��ʹ�������getCpu()�ķ���ʱ�����������Ϊ"cpulib"�Ŀ��ļ�
	private static final String SILIB = "cpulib";

	static {
		
		try {

			System.loadLibrary(SILIB);

		} catch (Exception e) {
			
			System.out.println("���ؿ��ļ�" 
				    + SILIB
					+ "��"
					+ System.getProperty("java.library.path")
					+"�в�����");

		}
	}
	//-------------------------------------------------
	//jni:����һ�����ط�����ע��ʹ�ùؼ���"native"
	public static native int getCpuRatio();

}
