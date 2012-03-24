package jkademlia.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.*;

import shyhao.windows.explorer.CpuAndMemoryDynamicChart;

import jkademlia.credit.CreditValue;
import jkademlia.data.DatabaseManager;
import jkademlia.file.FileInfo;
import jkademlia.file.FileNode;
import jkademlia.gui.actions.LoginAction;
import jkademlia.gui.actions.RefreshAction;
import jkademlia.kademlia.JKademlia;
import jkademlia.kademlia.JKademliaShutdownHook;
import jkademlia.kademlia.JKademliaSystem;
import jkademlia.transfer.Manager;
import jkademlia.transfer.client.Client;

public class EDragon extends JFrame implements ActionListener{
//	private JFrame frame = null;
	private JMenuBar menuBar = null;
	private JMenu netMenu = null;
	private JMenu optionMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem conItem = null;
	private JMenuItem disconItem = null;
	private JMenuItem exitItem = null;
	private JMenuItem setItem = null;
	private JMenuItem helpItem = null;
	private JMenuItem aboutItem = null;
	private JTabbedPane tabPane = null;
	private JPanel netPanel = null;
	private JLabel ipLabel = null;
	private JLabel portLabel = null;
	private JTextField ipField = null;
	private JTextField portField = null;
	private JButton loginButton = null;
	private JButton storeButton = null;
	private JScrollPane nodeListScrollPane = null;
	private JScrollPane shareTableScrollPane = null;
	private JPanel shareDirectoryPanel = null;
	private JTable netTable = null;
	private JPanel searchPanel = null;
	private JPanel searchTopPanel = null;
	private JTextField searchField = null;
	private JButton searchButton = null;
	private JButton downloadButton = null;
	private JScrollPane searchListScrollPane = null;
	private JList searchList = null;
	private JPopupMenu popupMenu = null;
	private JPanel downloadPanel = null;
	private JTable downloadPanelTable = null;
	public DefaultTableModel defaultModel = null;
	private JScrollPane shareScrollPane = null;
	private JSplitPane sharePanel = null;
	private JLabel shareLabel = null;
	private JLabel pathLabel = null;
	private JTable shareTable = null;
	private JTree tree = null;
	private JPanel statisticsPanel = null;
	private String[] fileNames;
	private ShareFileModel shareFileModel = null;

	private Manager manager = null;
	
	private String creditValue;
	Toolkit tk = Toolkit.getDefaultToolkit();
	Dimension screenSize = tk.getScreenSize();
	private int screenWidth = screenSize.width;
	private int screenHeight = screenSize.height;

	protected HashMap<String, Integer> rowMap;// Map文件名和对应表格中行的行序号
	private FileInfo fileInfo;
	private List<String> result = null;//盛放文件的搜索结果

	public JKademlia jKademlia;

	public EDragon() {
		jKademlia = new JKademlia();
	}

	public EDragon(String propertiesFileName) {
		jKademlia = new JKademlia(propertiesFileName);
	}
	


	public void init() {

		System.out.println("启动了一个MainFrame");
//		DatabaseManager.createTable();
		DatabaseManager.showTable();

		this.setSize((int)(screenWidth / 1.7), (int)(screenHeight / 1.7));
		this.setLocation(screenWidth / 4, screenHeight / 4);
		this.setTitle("eDragon");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getMenuBar1());
		this.add(getTabPane());
		rowMap = new HashMap<String, Integer>();
		// 启动管理器
		manager = new Manager(this);
		new Thread(manager).start();
	}


	// //////////////////////初始化菜单栏//////////////////////////////////////////////

	public JMenuBar getMenuBar1() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBar.add(getNetMenu());
			menuBar.add(getOptionMenu());
			menuBar.add(getHelpMenu());
		}
		return menuBar;
	}

	public JMenu getNetMenu() {
		if (netMenu == null) {
			netMenu = new JMenu("网络");
			netMenu.add(getConItem());
			netMenu.add(getDisconItem());
			netMenu.addSeparator();
			netMenu.add(getExitItem());
		}
		return netMenu;
	}

	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem("退出");
		}
		return exitItem;
	}

	private JMenuItem getDisconItem() {
		if (disconItem == null) {
			disconItem = new JMenuItem("断开");
		}
		return disconItem;
	}

	private JMenuItem getConItem() {
		if (conItem == null) {
			conItem = new JMenuItem("连接");
		}
		return conItem;
	}

	public JMenu getOptionMenu() {
		if (optionMenu == null) {
			optionMenu = new JMenu("选项");
			optionMenu.add(getSetItem());
		}
		return optionMenu;
	}

	private JMenuItem getSetItem() {
		if (setItem == null) {
			setItem = new JMenuItem();
		}
		return setItem;
	}

	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu("帮助");
			helpMenu.add(getHelpItem());
			helpMenu.add(getAboutItem());
		}
		return helpMenu;
	}

	private JMenuItem getAboutItem() {
		if (aboutItem == null) {
			aboutItem = new JMenuItem("关于");
		}
		return aboutItem;
	}

	private JMenuItem getHelpItem() {
		if (helpItem == null) {
			helpItem = new JMenuItem("帮助");
		}
		return helpItem;
	}

	// ////////////////////////////面板的设计///////////////////////////

	public JTabbedPane getTabPane() {
		if (tabPane == null) {
			tabPane = new JTabbedPane(JTabbedPane.TOP);
			tabPane.add("网络", getNetPane());
			tabPane.add("搜索", getSearchPanel());
			tabPane.add("下载", getDownloadPanel());
			tabPane.add("共享", getSharePanel());
			tabPane.add("监控", getStatisticsPanel());
			Image[] images = { tk.getImage(EDragon.class.getClassLoader().getResource("images/net.png")), tk.getImage(EDragon.class.getClassLoader().getResource("images/search.png")), tk.getImage(EDragon.class.getClassLoader().getResource("images/download.png")), tk.getImage(EDragon.class.getClassLoader().getResource("images/share.png")), tk.getImage(EDragon.class.getClassLoader().getResource("images/monitor.png")) };

			for (int i = 0; i <= 4; i++) {
				tabPane.setIconAt(i, new ImageIcon(images[i]));
				tabPane.setBackgroundAt(i, Color.ORANGE);
			}
			InputMap inputMap = tabPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(KeyStroke.getKeyStroke("R"), "refresh");
			ActionMap actionMap = tabPane.getActionMap();
			actionMap.put("refresh", new RefreshAction(this));
		}

		return tabPane;
	}
	//////////////////////////监控面板设计////////////////////////////////////////////
	private JPanel getStatisticsPanel() {
		if (statisticsPanel == null) {
			statisticsPanel = new JPanel();
			statisticsPanel.add(new CpuAndMemoryDynamicChart());
		}
		return statisticsPanel;
	}
	//////////////////////////////共享面板设计///////////////////////////////////////
	private JSplitPane getSharePanel() {
		if (sharePanel == null) {
			sharePanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, getShareDirectoryPane(), getShareTableScrollPane());		
		}
		return sharePanel;
	}
	
	private JScrollPane getShareTableScrollPane() {
		if(shareTableScrollPane == null){
			shareTableScrollPane = new JScrollPane();
			shareTableScrollPane.add(getPathLabel());
			shareTableScrollPane.setViewportView(getShareTable());
		}
		
		return shareTableScrollPane;
		
	}
	
	private JLabel getPathLabel(){
		if(pathLabel == null){
			pathLabel = new JLabel();
//			pathLabel.setText();
		}
		return pathLabel;
	}
	
	private JTable getShareTable() {
		if(shareTable == null){
			shareTable = new JTable(getShareFileModel());
		}
		return shareTable;
	}
	
	
	
	private ShareFileModel getShareFileModel() {
		if(shareFileModel == null){
			String[] columnNames = {"名称","大小"};
			fileInfo = new FileInfo();
			fileNames = fileInfo.getFileName(); 
			for(String str:fileNames)
				System.out.println(str);
		//	File files = new File("D:/");
		//	fileNames = files.list();
			shareFileModel = new ShareFileModel(fileNames,columnNames);
		}
		return shareFileModel;
	}
	
	private JPanel getShareDirectoryPane() {
		if(shareDirectoryPanel == null){
			shareDirectoryPanel = new JPanel();
			shareDirectoryPanel.setLayout(new BorderLayout());
			shareDirectoryPanel.setPreferredSize(new Dimension(230,100));
			shareDirectoryPanel.add(getShareLabel(),BorderLayout.PAGE_START);
			shareDirectoryPanel.add(getShareScrollPane());
		}
		return shareDirectoryPanel;
	}
	
	private JScrollPane getShareScrollPane() {
		if (shareScrollPane == null) {
			shareScrollPane = new JScrollPane();
			shareScrollPane.setPreferredSize(new Dimension(220,100));
			JPanel p = new JPanel();
			p.add(getTree());
			shareScrollPane.setViewportView(p);
		}
		return shareScrollPane;
	}
	
	private JTree getTree() {
		if(tree == null){
			tree = new JTree(createTreeModel());
			tree.addTreeExpansionListener(new TreeExpansionListener() {
				public void treeCollapsed(TreeExpansionEvent e) {
				}
				public void treeExpanded(TreeExpansionEvent e) {
					TreePath path = e.getPath();
					FileNode node = (FileNode) path.getLastPathComponent();
					if (!node.isExplored()) {
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						node.explore();
						model.nodeStructureChanged(node);
					}
				}
			});
			tree.addTreeSelectionListener(new TreeSelectionListener() {
			      public void valueChanged(TreeSelectionEvent event) {
			        File file = (File) tree.getLastSelectedPathComponent();
			      }
			    });
		}
		return tree;
	}
	
	private DefaultTreeModel createTreeModel() {
		File rootFile = new File("D:/");           
		FileNode rootNode = new FileNode(rootFile);            
		rootNode.explore();
		return new DefaultTreeModel(rootNode);
	}  


	private JLabel getShareLabel() {
		if (shareLabel == null) {
			shareLabel = new JLabel();
			shareLabel.setText("共享目录：");
		}
		return shareLabel;
	}

	// /////////////////////下载面板//////////////////////////////////
	private JPanel getDownloadPanel() {
		if (downloadPanel == null) {
			downloadPanel = new JPanel();
			String[] columnNames = { "文件名", "大小", "速度", "进度" };
			Object[][] data = null;
			defaultModel = new DefaultTableModel(data, columnNames);
			downloadPanelTable = new JTable(defaultModel);

			JScrollPane jsp = new JScrollPane(downloadPanelTable);
			downloadPanel.setLayout(new BorderLayout());
			downloadPanel.add(jsp, BorderLayout.CENTER);
		}
		return downloadPanel;
	}

	
///////////////////////////搜索面板///////////////////////////////	
	private JTable searchPanelTable;
	private JPanel getSearchPanel() {
		if(searchPanel == null){
			searchPanel = new JPanel();
			String[] columnNames = {"文件名","大小","类型"};
			Object[][] data ={
				{"jdk_setup", new Integer(5),"可执行文件"},
		        {"tianshi", new Integer(3),"mp3"},
		        {"图画", new Integer(2),"JPG"}
			};
			searchPanelTable = new JTable(data,columnNames);
			searchPanelTable.setShowHorizontalLines(false);
			searchPanelTable.setShowVerticalLines(false);
			
			searchPanel.setLayout(new BorderLayout());
//			searchPanel.add(getSearchField(),null);
//			searchPanel.add(getSearchButton(),null);
//			searchPanel.add(getSearchListScrollPane(),null);
			
			popupMenu = new JPopupMenu();
			JMenuItem downloadItem = new JMenuItem("下载");
			
			downloadItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("点击了...触发下载事件");//重要位置，在此触发文件传输功能
					clickDownload();
				}
			});		
			popupMenu.add(downloadItem);
			searchPanelTable.addMouseListener(new MouseAdapter(){
				public void mousePressed (MouseEvent e){
					int row = e.getY()/searchPanelTable.getRowHeight();
					System.out.println("取得表的第"+row+"行...");
					popupMenu.show(e.getComponent(),e.getX(),e.getY());
				}
			});
//			searchPanel.add(searchPanelTable,BorderLayout.CENTER);
//			searchPanel.add(getSearchField(),BorderLayout.NORTH);
//			searchPanel.add(getSearchButton(),BorderLayout.NORTH);
//			searchPanel.add(getDownloadButton(),BorderLayout.NORTH);
			searchPanel.add(getSearchTopPanel(),BorderLayout.NORTH);
			searchPanel.add(getSearchListScrollPane(),BorderLayout.CENTER);
			

		}	
		return searchPanel;
	}
	
	private JPanel getSearchTopPanel(){
		if(searchTopPanel == null){
			searchTopPanel = new JPanel();
			searchTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			searchTopPanel.add(getSearchField());
			searchTopPanel.add(getSearchButton());
			searchTopPanel.add(getDownloadButton());
		}
		return searchTopPanel;
	}

	public JScrollPane getSearchListScrollPane() {
		if (searchListScrollPane == null) {
			searchListScrollPane = new JScrollPane();
			searchListScrollPane.setBounds(0, 30, screenWidth / 2, 200);
			searchListScrollPane.setViewportView(getSearchList());
		}
		return searchListScrollPane;
	}

	public JList getSearchList() {
		if (searchList == null) {
			String[] str = {"haha","hehe","heihei"};
			searchList = new JList();
//			searchList.a
			searchList.setFont(new Font("Courier new", Font.PLAIN, 12));
		}
		
		return searchList;
	}

	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("搜索");
			searchButton.setBounds(250, 0, 80, 30);
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 搜索文件
					getSearchField().setEditable(false);
					getSearchButton().setEnabled(false);
					JKademliaSystem system = jKademlia.getSystem();
					system.findValue(getSearchField().getText());
					// System.out.println(system.findValue(getSearchField().getText()));
					result = system.listResult();
					getSearchField().setEditable(true);
					getSearchButton().setEnabled(true);
				}
			});
		}
		return searchButton;
	}
	
	private JButton getDownloadButton(){
		if(downloadButton == null){
			downloadButton = new JButton();
			downloadButton.setText("下载");
			downloadButton.setBounds(350,0,80,30);
			downloadButton.addActionListener(this);
		}
		return downloadButton;
	}

	private JTextField getSearchField() {
		if (searchField == null) {
			searchField = new JTextField();
			searchField.setBounds(0, 0, 200, 30);
			searchField.setPreferredSize(new Dimension(200,30));
		}
		return searchField;
	}

	private void clickDownload() {
/*		ClientTest aTask = new ClientTest(this);
		Thread taskThread = new Thread(aTask);
		Object[] newRow = { new String(aTask.getFileName()), new String(aTask.getFileLength() / 1024 + "K"), new Integer(0), new Integer(0) };
		defaultModel.addRow(newRow);

		rowMap.put(aTask.getFileName(), defaultModel.getRowCount());

		manager.tasks.add(aTask);
		manager.sizeMap.put(aTask.getFileName(), 0);
		taskThread.start();*/

	}

		// ///////////////////////网络面板//////////////////////////////////
		private JPanel getNetPane() {
			if (netPanel == null) {
				netPanel = new JPanel();
				// String[] columnNames = {"ID","类型","距离"};
				// Object[][] data ={
				// {"192.168.1.105", new Integer(5),"000111000111"},
				// {"200.10.1.1", new Integer(3),"111000111000"},
				// {"192.168.1.87", new Integer(2),"111000000111"}
				// };
				// JTable netPanelTable = new JTable(data,columnNames);
				// netPanelTable.setEnabled(false);
				// netPanelTable.setShowHorizontalLines(false);
				// netPanelTable.setShowVerticalLines(false);
				// JScrollPane jsp = new JScrollPane(netPanelTable);
				// netPanel.setLayout(new BorderLayout());
				// netPanel.add(jsp,BorderLayout.CENTER);
				netPanel.setLayout(null);
				netPanel.add(getIpLabel());
				netPanel.add(getIpField());
				netPanel.add(getPortLabel());
				netPanel.add(getPortField());
				netPanel.add(getLoginButton(), null);
				netPanel.add(getNodeListScrollPane(), null);
				netPanel.add(getStoreButton());
				InputMap inputMap = netPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				inputMap.put(KeyStroke.getKeyStroke("R"), "refresh");
				ActionMap actionMap = netPanel.getActionMap();
				actionMap.put("refresh", new RefreshAction(this));
			}
			return netPanel;
		}
	
		private JLabel getIpLabel() {
			if (ipLabel == null) {
				ipLabel = new JLabel();
				ipLabel.setBounds(0, 0, 30, 30);
				ipLabel.setText("IP");
			}
			return ipLabel;
		}
	
		public JTextField getIpField() {
			if (ipField == null) {
				ipField = new JTextField();
				ipField.setBounds(40, 0, 120, 30);
			}
			return ipField;
		}
	
		private JLabel getPortLabel() {
			if (portLabel == null) {
				portLabel = new JLabel();
				portLabel.setBounds(180, 0, 30, 30);
				portLabel.setText("Port");
			}
			return portLabel;
		}
	
		public JTextField getPortField() {
			if (portField == null) {
				portField = new JTextField();
				portField.setBounds(220, 0, 120, 30);
			}
			return portField;
		}
	
		private JButton getLoginButton() {
			if (loginButton == null) {
				loginButton = new JButton();
				loginButton.setText("login");
				loginButton.setBounds(400, 0, 80, 30);
				loginButton.addActionListener(new LoginAction(this));
			}
			return loginButton;
		}
	
		private JScrollPane getNodeListScrollPane() {
			if (nodeListScrollPane == null) {
				nodeListScrollPane = new JScrollPane(getNetTable());
				nodeListScrollPane.setLocation(0, 30);
				nodeListScrollPane.setSize(new Dimension((int)(screenWidth / 1.7), 200));
			}
			return nodeListScrollPane;
		}
	
		public JTable getNetTable() {
			if (netTable == null) {
				JKademliaSystem system = jKademlia.getSystem();
				KnowContactsModel model = new KnowContactsModel(system.tableKnowContacts());
				netTable = new JTable(model);
			}
			return netTable;
		}
	
		public JButton getStoreButton() {
			if (storeButton == null) {
				storeButton = new JButton();
				storeButton.setText("store");
				storeButton.setBounds(0, 250, 80, 30);
				storeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// store文件
						// getKeyField().setEditable(false);
						// getValueField().setEditable(false);
						// getStoreButton().setEnabled(false);
						// List<JKademliaSystem> systems = getSystems();
						// for(JKademliaSystem system : systems)
						// system.store(getKeyField().getText(),
						// getValueField().getText());
						// getKeyField().setEditable(true);
						// getValueField().setEditable(true);
						// getStoreButton().setEnabled(true);
						//					
						// 将文件放在文件夹里面，然后读取出所有的文件名和文件的大小。
						// 在D盘中建立一个共享文件夹share.
						JKademliaSystem system = jKademlia.getSystem();
						CreditValue credit = new CreditValue();
						creditValue = credit.getCreditValue();
						fileInfo = new FileInfo();
						for (String fileName : fileInfo.getFileName()){
							system.store(fileName, system.getIP().toString() + "@" + creditValue + "*" + fileName + "&" + fileInfo.getFileSize(fileName));
							System.out.println("存储的文件名是：" + fileName);
						}
						 getStoreButton().setEnabled(true);
					}
				});
			}
			return storeButton;
		}


	
	public void actionPerformed(ActionEvent e) {
		List<String> resourses = null;
		if(result == null){
			System.out.println("输入搜索文件名...");
			
//			//单机测试------------
//			List<String> testResourses = new ArrayList<String>();
//			testResourses.add("127.0.0.1@hello");
//			ClientTest aTask = new ClientTest(this,testResourses,"123.mp4",80899*1024);
//			
//			Thread taskThread = new Thread(aTask);
//			Object[] newRow = { new String(aTask.getFileName()), new String(aTask.getFileLength() / 1024 + "K"), new Integer(0), new Integer(0) };
//			defaultModel.addRow(newRow);
//			rowMap.put(aTask.getFileName(), defaultModel.getRowCount());
//			manager.tasks.add(aTask);
//			manager.sizeMap.put(aTask.getFileName(), 0);
//			taskThread.start();
//			//----------------------单机测试
		}else{
			resourses = new ArrayList<String>();
			for(int i=0;i<result.size();i++){
				int indexOfFileName = result.get(i).indexOf("*");
				String ipAndCreditValue = result.get(i).substring(1,indexOfFileName);
				resourses.add(ipAndCreditValue);
				System.out.println(ipAndCreditValue);
			}
			System.out.println(result.size());
			//得到搜索到的文件名,文件大小
			int indexOfFileName = result.get(0).indexOf("*");
			int indexOfFileSize = result.get(0).indexOf("&");
			String fileName = result.get(0).substring(indexOfFileName+1,indexOfFileSize);
			String chFileSize = result.get(0).substring(indexOfFileSize+1);
			double doubleFileSize = Double.parseDouble(chFileSize);
			int fileSize = (int)doubleFileSize;
			
//			启动ClientTest线程,开始下载任务

			List<String> testResourses = new ArrayList<String>();
			testResourses.add("127.0.0.1@hello");
			Client aTask = new Client(this,resourses,fileName,fileSize);
			Thread taskThread = new Thread(aTask);
			Object[] newRow = { new String(aTask.getFileName()), new String(aTask.getFileLength() / 1024 + "K"), new Integer(0), new Integer(0) };
			defaultModel.addRow(newRow);
			rowMap.put(aTask.getFileName(), defaultModel.getRowCount());
			manager.tasks.add(aTask);
			manager.sizeMap.put(aTask.getFileName(), 0);
			taskThread.start();

		}
		
	}
	
	public void setSearchResult(List<String> result){
		this.result = result;
	}
	public static void main(String args[]){
		EDragon eDragon = new EDragon(JKademlia.DEFAULT_PROPERTY_FILE);
      eDragon.setVisible(true);
		Thread thread = new Thread(eDragon.jKademlia);
		thread.start();
		
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		eDragon.init();
		Runtime.getRuntime().addShutdownHook(new JKademliaShutdownHook(thread, eDragon.jKademlia)); // 关闭程序
	}
//	public static void main(String args[]) {
//		
////		JFrame.setDefaultLookAndFeelDecorated(true);
////		JDialog.setDefaultLookAndFeelDecorated(true);
//		SwingUtilities.invokeLater(new Runnable() {
//		      public void run() {
//
//		    	
////		    	SubstanceLookAndFeel.setToUseConstantThemesOnDialogs(true);
////		    	SubstanceLookAndFeel.setSkin(new GeminiSkin());
//		        
//		        EDragon eDragon = new EDragon(JKademlia.DEFAULT_PROPERTY_FILE);
//		        eDragon.setVisible(true);
//				Thread thread = new Thread(eDragon.jKademlia);
//				thread.start();
//				
//				
//				
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				eDragon.init();
//				Runtime.getRuntime().addShutdownHook(new JKademliaShutdownHook(thread, eDragon.jKademlia)); // 关闭程序
//		      }
//		    });
//		
//
//	}
	


}
