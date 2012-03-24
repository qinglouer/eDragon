/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shyhao.windows.explorer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import shyhao.windows.explorer.CPU.CPUResources;
import shyhao.windows.explorer.GraphicalChart.ChartResources.CreateChart;
import shyhao.windows.explorer.GraphicalChart.ChartResources.LoadClass;
import shyhao.windows.explorer.Memory.PhysicalMemory;

public class CpuAndMemoryDynamicChart extends JPanel implements Runnable,
		ActionListener, ItemListener {
        public static double cpuvalue;
        public static double memvalue;
        
        public static int i = 0;                 //ͳ��cpu������ֵ��ʱ�������������������ֵ��ʱ��ͻ��Զ���kill�ļ�
                                         //������̣߳����û���ļ�������̣߳����ᵯ����ʾ�������û�ע��ϵͳ���ȶ��ԡ�
	// ------------------------------------
	private ButtonGroup buttonGroup = new ButtonGroup();

	private JRadioButton radio_highaccuracy, radio_commonaccuracy,
			radio_lowaccuracy;

	private JToggleButton toggle_control;

	private SpringLayout springLayout;

	final JPanel panel_precision, panel_view, panel_label;

	private JLabel label_cpu, label_memory;

	// ��־λ
	private boolean highsign = true;

	private boolean commonsign = false;

	private boolean lowsign = false;

	private boolean opensign = true;

	// ʱ��ͼ���ݼ�
	private TimeSeries timeseries_cpu, timeseries_memory;

	//Value�������ʼֵ
	private double cpu_value, memory_value;

	static Class class$org$jfree$data$time$Millisecond;

	static Thread process_thread;

	private static final long serialVersionUID = -5227280391834054917L;

	/**
	 * Create the frame
	 */
	public CpuAndMemoryDynamicChart() {
		super();
		process_thread = new Thread(this);
		cpu_value = 100D;

		springLayout = new SpringLayout();
        this.setBackground(new Color(196,226,245));

		// -------------------------------------------------------------------
		// �������ھ��ȵ����
		panel_precision = new JPanel();
		panel_precision.setLayout(new FlowLayout());
		panel_precision.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED), "���ȵ���",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
	//add(panel_precision);
		springLayout.putConstraint(SpringLayout.EAST, panel_precision, -8,
				SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.WEST, panel_precision, 10,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_precision, 80,
				SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, panel_precision, 10,
				SpringLayout.NORTH, this);

		// --------------
		// ������ϵ����

		toggle_control = new JToggleButton();
		toggle_control.addActionListener(this);
		toggle_control.setText("��/�ر�");
		panel_precision.add(toggle_control);

		radio_highaccuracy = new JRadioButton();
		buttonGroup.add(radio_highaccuracy);
		radio_highaccuracy.addItemListener(this);
		radio_highaccuracy.setText("�߾���");
		panel_precision.add(radio_highaccuracy);

		radio_commonaccuracy = new JRadioButton();
		buttonGroup.add(radio_commonaccuracy);
		radio_commonaccuracy.addItemListener(this);
		radio_commonaccuracy.setText("һ�㾫��");
		panel_precision.add(radio_commonaccuracy);

		radio_lowaccuracy = new JRadioButton();
		buttonGroup.add(radio_lowaccuracy);
		radio_lowaccuracy.addItemListener(this);
		radio_lowaccuracy.setText("�;���");
		panel_precision.add(radio_lowaccuracy);
		// Ĭ��ѡ�С�һ�㾫�ȡ�
		radio_commonaccuracy.setSelected(true);

		// --------------------------------------------------------------------
		// ������ͼ����ʾcpu���ڴ�仯�����
		panel_view = new JPanel();
		panel_view.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panel_view.setLayout(new GridLayout(2, 1));
		this.add(panel_view);
		springLayout.putConstraint(SpringLayout.EAST, panel_view, -8,
				SpringLayout.EAST,this);
		springLayout.putConstraint(SpringLayout.WEST, panel_view, 90,
				SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_view, -5,
				SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, panel_view, 5,
				SpringLayout.SOUTH, panel_precision);

		// ----------------------------------------------
		// ����ʱ��ͼ����
		// CPU��ʱ��ͼ
		// -----------------------------------------------
		timeseries_cpu = new TimeSeries(
				"CPU",
				class$org$jfree$data$time$Millisecond != null ? class$org$jfree$data$time$Millisecond
						: (class$org$jfree$data$time$Millisecond = LoadClass
								.getClass("org.jfree.data.time.Millisecond")));

		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(
				timeseries_cpu);


		ChartPanel chartpanel = new ChartPanel(CreateChart.createChart(
				timeseriescollection, "", "ʱ��", "�ٷֱ�(%)"));
		chartpanel.setPreferredSize(new Dimension(700, 170));
		// ----------------------------------------------
		// �ڴ��ʱ��ͼ
		// ----------------------------------------------

		timeseries_memory = new TimeSeries(
				"�ڴ�",
				class$org$jfree$data$time$Millisecond != null ? class$org$jfree$data$time$Millisecond
						: (class$org$jfree$data$time$Millisecond = LoadClass
								.getClass("org.jfree.data.time.Millisecond")));
		TimeSeriesCollection timeseriescollection2 = new TimeSeriesCollection(
				timeseries_memory);



		ChartPanel chartpanel2 = new ChartPanel(CreateChart.createChart(
				timeseriescollection2, "", "ʱ��", "�ٷֱ�(%)"));
		chartpanel2.setPreferredSize(new Dimension(700, 170));

		// -------------------------------------
		// ����ʱ��ͼ���뵽panel_view�����

		panel_view.add(chartpanel);
		panel_view.add(chartpanel2);

		// ------------------------------------------------------------------------------
		// ������������ʽ��ʾcpu���ڴ�仯�����
		panel_label = new JPanel();
		panel_label.setLayout(new GridLayout(2, 1));
		this.add(panel_label);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_label, -5,
				SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.NORTH, panel_label, 5,
				SpringLayout.SOUTH, panel_precision);
		springLayout.putConstraint(SpringLayout.EAST, panel_label, -5,
				SpringLayout.WEST, panel_view);
		springLayout.putConstraint(SpringLayout.WEST, panel_label, 0,
				SpringLayout.WEST, panel_precision);

		// ������ϵ����
		label_cpu = new JLabel();
		label_cpu.setVerticalAlignment(JLabel.CENTER);
		label_cpu.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		label_memory = new JLabel();
		label_memory.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		panel_label.add(label_cpu);
		panel_label.add(label_memory);
		// --------------------------------------------------------------------------
		startProcess();

		//LookAndFeel.changeLookAndFeel("Native", this);

	}

	// ��ӦҪ�󣬽����¼�����
	public void run() {

		while (true) {

			if (opensign == true) {

				try { // ����ʵ����Ҫ�ڴ˴�������Ҫִ�еĴ���

					if (commonsign == true) {
						int a = CPUResources.getCpuRatio();
						int b = CPUResources.getCpuRatio();
						cpu_value = (a + b) / 2;
						if(cpu_value > 95.0){
						    i++;
							System.out.println("�������߳�̫�࣬��Ҫ��С������");
							if(i > 10){
								jkademlia.transfer.client.PartTaskManager.taskLimit--;
							}
						}else{
							i = 0;
						}
					}

					if (highsign == true) {
						cpu_value = CPUResources.getCpuRatio();
						
					}

					if (lowsign == true) {
						int a = CPUResources.getCpuRatio();
						int b = CPUResources.getCpuRatio();
						int c = CPUResources.getCpuRatio();
						cpu_value = (a + b + c) / 2;

					}
					cpu_value = CPUResources.getCpuRatio();
					long use = PhysicalMemory.getUsedMemorySize() / 1024;
					long total = PhysicalMemory.getTotalMemorySize() / 1024;
					memory_value = use * 1.0 * 100 / total;
					Millisecond millisecond = new Millisecond();
					timeseries_memory.add(millisecond, memory_value);
					timeseries_cpu.add(millisecond, cpu_value);
					label_cpu.setText("cpu:" + (int) (cpu_value) + "%");
					label_memory.setText("�ڴ�:" + (int) (memory_value) + "%");
                                        cpuvalue=cpu_value;
                                        memvalue=memory_value;

				} catch (Exception e) {
				}

			}

		}

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == toggle_control) {

			if (opensign == true) {

				opensign = false;

			} else {

				opensign = true;
			}

		}
		System.out.println(opensign);
	}

	public void itemStateChanged(ItemEvent e) {

		if (radio_lowaccuracy.isSelected()) {

			highsign = false;
			commonsign = false;
			lowsign = true;
		}
		if (radio_highaccuracy.isSelected()) {

			highsign = true;
			commonsign = false;
			lowsign = false;
		}
		if (radio_commonaccuracy.isSelected()) {
			highsign = false;
			commonsign = true;
			lowsign = false;
		}

	}

	public void startProcess() {

		process_thread.start();

	}
}
