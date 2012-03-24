package shyhao.windows.explorer.Memory;

/**�ô�����Windows����ϵͳ�ĵ������ڴ�ʹ�����
 * ���ڴ棺
 * long totalmemory=PhysicalMemory.getTotalMemorySize();
 * ʣ���ڴ棺
 * long freememory=PhysicalMemory.getFreeMemorySize();
 * ��ʹ���ڴ棺
 * long usedmemory=PhysicalMemory.getUsedMemorySize();
 */
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class PhysicalMemory {

	private static final int KB = 1024;

	public PhysicalMemory() {

	}

	public static long getTotalMemorySize() {

		long TotalMemorySize = 0;
		try {

			OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean();
			TotalMemorySize = osmb.getTotalPhysicalMemorySize() / KB;

		} catch (Exception e) {

			TotalMemorySize = 0;
		}
		return TotalMemorySize;

	}

	public static long getFreeMemorySize() {

		long FreeMemorySize = 0;

		try {

			OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean();
			FreeMemorySize = osmb.getFreePhysicalMemorySize() / KB;

		} catch (Exception e) {

			FreeMemorySize = 0;
		}

		return FreeMemorySize;
	}

	public static long getUsedMemorySize() {

		long UsedMemorySize = 0;

		try {
			OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean();
			UsedMemorySize = (osmb.getTotalPhysicalMemorySize() - osmb
					.getFreePhysicalMemorySize())
					/ KB;
		} catch (Exception e) {

			UsedMemorySize = 0;
		}

		return UsedMemorySize;
	}
}
