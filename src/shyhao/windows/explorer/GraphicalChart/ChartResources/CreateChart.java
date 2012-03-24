package shyhao.windows.explorer.GraphicalChart.ChartResources;
/** 
 *  @�ô��������������JFreeChart
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public class CreateChart {

	public CreateChart() {

	}

	public static JFreeChart createChart(XYDataset xydataset, String topaLabel,
			String bottomLabel, String leftLabel) {

		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(topaLabel,
				bottomLabel, leftLabel, xydataset, true, true, false);
		jfreechart.setBorderVisible(false);
		XYPlot xyplot = jfreechart.getXYPlot();
		// -------------------------------------------

		// �������趨
		ValueAxis valueaxis = xyplot.getDomainAxis();
		// �Զ��������������ݷ�Χ
		valueaxis.setAutoRange(true);
		// ������������ʾ��ʽ���壨�ɸ�����Ҫȷ��ʹ������������䣩
		// DateAxis dateaxis = (DateAxis)xyplot.getDomainAxis();
		// dateaxis.setDateFormatOverride(new SimpleDateFormat("MM hh:mm:ss "));
		// --------------------------------------
		valueaxis.setAutoRangeMinimumSize(0.001D);
		// ������̶����ݷ�Χ 60s
		valueaxis.setFixedAutoRange(60000D);
		// ---------------------------------------
		// �������趨
		valueaxis = xyplot.getRangeAxis();
		valueaxis.setRange(0.0D, 100D);
		return jfreechart;

	}

}
