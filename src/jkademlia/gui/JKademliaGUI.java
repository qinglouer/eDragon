//package jkademlia.gui;
//
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.List;
//
//import javax.swing.ActionMap;
//import javax.swing.InputMap;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.KeyStroke;
//
//import jkademlia.file.FileInfo;
//import jkademlia.gui.actions.LoginAction;
//import jkademlia.gui.actions.RefreshAction;
//import jkademlia.kademlia.JKademlia;
//import jkademlia.kademlia.JKademliaShutdownHook;
//import jkademlia.kademlia.JKademliaSystem;
//
//public class JKademliaGUI extends JKademlia {
//	private JFrame frame = null;
//	private JPanel contentPanel = null;
//	private JPanel loginPanel = null;
//	private JPanel storePanel = null;
//	private JPanel searchPanel = null;
//	private JButton storeButton = null;
//	private JButton searchButton = null;
//	private JButton loginButton = null;
//	private JTextField ipField = null;
//	private JTextField portField = null;
//	private JTextField keyField = null;
//	private JTextField searchField = null;
//	private JTextField valueField = null;
//	private JLabel ipLabel = null;
//	private JLabel portLabel = null;
//	private JLabel keyLabel = null;
//	private JLabel valueLabel = null;
//	
//	private JList nodeList = null;
//	private JList searchList = null;
//	
//	private JScrollPane nodeListScrollPane = null;
//	private JScrollPane searchListScrollPane = null;
//	
//	private FileInfo fileInfo = new FileInfo();
//	
//	public JKademliaGUI(){
//		super();
//	}
//	
//	public JKademliaGUI(String propertiesFileName){
//		super(propertiesFileName);
//	}
//	
//	
//	
//	protected void initialize(){
//		this.getFrame().setVisible(true);
//	}
//
//	private JFrame getFrame() {
//		if(frame == null){
//			frame = new JFrame();
//			frame.setSize(new Dimension(1000,700));
//			frame.setTitle("Kademlia");
//			frame.setResizable(false);
//			frame.setLocationRelativeTo(null);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.setContentPane(getContentPanel());		
//		}
//		return frame;
//	}
//
//	public JPanel getContentPanel() {
//		if(contentPanel == null){
//			contentPanel = new JPanel();
//			contentPanel.setLayout(null);
//			contentPanel.add(getLoginPane(),null);
//			contentPanel.add(getStorePanel(),null);
//			contentPanel.add(getSearchPanel(),null);
//			InputMap inputMap  = contentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//			inputMap.put(KeyStroke.getKeyStroke("R"),"refresh");
//			ActionMap actionMap = contentPanel.getActionMap();
//			actionMap.put("refresh", new RefreshAction(this));
//		}
//		return contentPanel;
//	}
//
//////////////////////////////////Login面板//////////////////////////////////////////
//	private JPanel getLoginPane() {
//		if(loginPanel == null){
//			loginPanel = new JPanel();
//			loginPanel.setLayout(null);
//			loginPanel.setBounds(0, 0, 1000, 330);
//			loginPanel.add(getIpLabel());
//			loginPanel.add(getIpField());
//			loginPanel.add(getPortLabel());
//			loginPanel.add(getPortField());
//			loginPanel.add(getLoginButton(),null);
//			loginPanel.add(getNodeListScrollPane(),null);
//		}
//		return loginPanel;
//	}
//	
//	private JLabel getIpLabel(){
//		if(ipLabel == null){
//			ipLabel = new JLabel();
//			ipLabel.setBounds(20, 0, 40, 30);
//			ipLabel.setText("IP");
//		}
//		return ipLabel;
//	}
//	
//	public JTextField getIpField(){
//		if(ipField == null){
//			ipField = new JTextField();
//			ipField.setBounds(70, 0, 150, 30);
//		}
//		return ipField;
//	}
//	
//	private JLabel getPortLabel(){
//		if(portLabel == null){
//			portLabel = new JLabel();
//			portLabel.setBounds(300, 0, 40, 30);
//			portLabel.setText("Port");
//		}
//		return portLabel;
//	}
//	
//	public JTextField getPortField(){
//		if(portField == null){
//			portField = new JTextField();
//			portField.setBounds(350, 0, 150, 30);
//		}
//		return  portField;
//	}
//	
//	private JButton getLoginButton(){
//		if(loginButton == null){
//			loginButton = new JButton();
//			loginButton.setText("login");
//			loginButton.setBounds(800,0,80,30);
//			loginButton.addActionListener(new LoginAction(this));
//		}
//		return loginButton;
//	}
//	
//	private JScrollPane getNodeListScrollPane() {
//		if(nodeListScrollPane == null){
//			nodeListScrollPane = new JScrollPane();
//			nodeListScrollPane.setLocation(0, 30);
//			nodeListScrollPane.setSize(new Dimension(1000,300));
//			nodeListScrollPane.setViewportView(getKnownContactNodeList());
//		}
//		return nodeListScrollPane;
//	}
//
//	public JList getKnownContactNodeList() {
//		if(nodeList == null){
//			nodeList = new JList();
//			nodeList.setFont(new Font("Courier new",Font.PLAIN,12));
//		}
//		return nodeList;
//	}
//
//	/////////////////////////////////Store面板/////////////////////////////////
//	private JPanel getStorePanel(){
//		if(storePanel == null){
//			storePanel = new JPanel();
//			storePanel.setLayout(null);
//			storePanel.setBounds(0,330,1000,30);
//			storePanel.add(getKeyLabel(),null);
//			storePanel.add(getKeyField(),null);
//			storePanel.add(getValueLabel(),null);
//			storePanel.add(getValueField(),null);
//			storePanel.add(getStoreButton(),null);
//		}
//		return storePanel;
//	}
//	
//	private JLabel getKeyLabel(){
//		if(keyLabel == null){
//			keyLabel = new JLabel();
//			keyLabel.setBounds(30, 0, 50, 30);
//			keyLabel.setText("key");
//		}
//		return keyLabel;
//	}
//	
//	private JTextField getKeyField(){
//		if(keyField == null){
//			keyField = new JTextField();
//			keyField.setBounds(90,0,150,30);
//		}
//		return keyField;
//	}
//	
//	private JLabel getValueLabel(){
//		if(valueLabel == null){
//			valueLabel = new JLabel();
//			valueLabel.setBounds(330,0,50,30);
//			valueLabel.setText("value");
//		}
//		return valueLabel;
//	}
//	
//	private JTextField getValueField(){
//		if(valueField == null){
//			valueField = new JTextField();
//			valueField.setBounds(390, 0, 150, 30);
//		}
//		return valueField;
//	}
//	
//	private JButton getStoreButton(){
//		if(storeButton == null){
//			storeButton = new JButton();
//			storeButton.setText("store");
//			storeButton.setBounds(600, 0, 80, 30);
//			storeButton.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){
//					//store文件
////					getKeyField().setEditable(false);
////					getValueField().setEditable(false);
////					getStoreButton().setEnabled(false);
////					List<JKademliaSystem> systems = getSystems();
////					for(JKademliaSystem system : systems)
////						system.store(getKeyField().getText(), getValueField().getText());
////					getKeyField().setEditable(true);
////					getValueField().setEditable(true);
////					getStoreButton().setEnabled(true);
////					
//					//将文件放在文件夹里面，然后读取出所有的文件名和文件的大小。
//					//在D盘中建立一个共享文件夹share.
//					List<JKademliaSystem> systems = getSystems();
//					for(JKademliaSystem system : systems)
//						system.store(fileInfo.getFileName(),system.getIP().toString());			
//				}
//			});
//		}
//		return storeButton;
//	}
//	
//	///////////////////////////////search面板//////////////////////////////////
//	private JPanel getSearchPanel(){
//		if(searchPanel == null){
//			searchPanel = new JPanel();
//			searchPanel.setLayout(null);
//			searchPanel.setBounds(0, 360, 1000, 330);
//			searchPanel.add(getSearchField(),null);
//			searchPanel.add(getSearchButton(),null);
//			searchPanel.add(getSearchListScrollPane(),null);
//		}
//		return searchPanel;
//	}
//	
//	private JTextField getSearchField(){
//		if(searchField == null){
//			searchField = new JTextField();
//			searchField.setBounds(200,0,200,30);
//		}
//		return searchField;
//	}
//	
//	private JButton getSearchButton(){
//		if(searchButton == null){
//			searchButton = new JButton();
//			searchButton.setText("search");
//			searchButton.setBounds(600, 0, 80, 30);
//			searchButton.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){
//					//搜索文件
//					getSearchField().setEditable(false);
//					getSearchButton().setEnabled(false);
//					List<JKademliaSystem> systems = getSystems();
//					for(JKademliaSystem system : systems)
//						system.findValue(getSearchField().getText());
////						System.out.println(system.findValue(getSearchField().getText()));
//					getSearchField().setEditable(true);
//					getSearchButton().setEnabled(true);
//				}
//			});
//		}
//		return searchButton;
//	}
//	
//	private JScrollPane getSearchListScrollPane(){
//		if(searchListScrollPane == null){
//			searchListScrollPane = new JScrollPane();
//			searchListScrollPane.setBounds(0, 30, 1000, 300);
//			searchListScrollPane.setViewportView(getSearchList());
//		}
//		return searchListScrollPane;
//	}
//	
//	public JList getSearchList(){
//		if(searchList == null){
//			searchList = new JList();
//			searchList.setFont(new Font("Courier new",Font.PLAIN,12));
//		}
//		return searchList;
//	}
//	public static void main(String args[]){
//		JKademliaGUI gui = new JKademliaGUI(args.length>0 ? args[0] : DEFAULT_PROPERTY_FILE);
//		Thread thread = new Thread(gui);
//		thread.start();
//		try {
//			Thread.sleep(2000);
//		}catch(InterruptedException e){
//			e.printStackTrace();
//		}
//		
//		gui.initialize();
//		
//		Runtime.getRuntime().addShutdownHook(new JKademliaShutdownHook(thread, gui)); //关闭程序
//	}
//	
//
//}
