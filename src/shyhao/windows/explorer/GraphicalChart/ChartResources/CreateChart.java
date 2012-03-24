package shyhao.windows.explorer.GraphicalChart.ChartResources;
/** 
 *  @用处：创建带标题的JFreeChart
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

		// 横坐标设定
		ValueAxis valueaxis = xyplot.getDomainAxis();
		// 自动设置数据轴数据范围
		valueaxis.setAutoRange(true);
		// 横轴上日期显示格式定义（可根据需要确定使用下面两条语句）
		// DateAxis dateaxis = (DateAxis)xyplot.getDomainAxis();
		// dateaxis.setDateFormatOverride(new SimpleDateFormat("MM hh:mm:ss "));
		// --------------------------------------
		valueaxis.setAutoRangeMinimumSize(0.001D);
		// 数据轴固定数据范围 60s
		valueaxis.setFixedAutoRange(60000D);
		// ---------------------------------------
		// 纵坐标设定
		valueaxis = xyplot.getRangeAxis();
		valueaxis.setRange(0.0D, 100D);
		return jfreechart;

	}

}
