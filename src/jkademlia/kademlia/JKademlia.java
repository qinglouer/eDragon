package jkademlia.kademlia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jkademlia.controller.threads.Pausable;
import jkademlia.controller.threads.Stoppable;
import jkademlia.exceptions.PropertiesNotFoundException;
import jkademlia.transfer.server.Manager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class JKademlia extends Manager implements Runnable, Pausable, Stoppable{
	private static Logger logger; // 记录日志文件
	public static String DEFAULT_PROPERTY_FILE = "jkademlia.properties"; // 默认的配置文件

	private File propFile;
	private boolean started;
	private JKademliaSystem system;
	private boolean dead;
	private boolean paused;

	public static final String[] usedProperties = new String[] { "jkademlia.socket.startPort", 
		"jkademlia.datagrambuffer.output.size", "jkademlia.datagrambuffer.input.size",
		"jkademlia.rpcbuffer.output.size", "jkademlia.rpcbuffer.input.size",
		"jkademlia.contacts.size", "jkademlia.contacts.refreshPeriod",
		"jkademlia.contacts.expire", "jkademlia.contacts.findamount", 
		"jkademlia.findvalue.maxqueries", "jkademlia.findvalue.maxwait", 
		"jkademlia.findnode.maxqueries", "jkademlia.findnode.maxwait", 
		"jkademlia.findnode.maxnodes", "jkademlia.login.time","edragon.sharelist" };

	public JKademlia() {
		this(DEFAULT_PROPERTY_FILE);	
	}
	
	

	public JKademlia(String propertiesFileName) {
		system = new JKademliaSystem();
		propFile = new File(propertiesFileName);
		started = false;
		paused = false;
		dead = false;
	}

	public JKademliaSystem getSystem() {
		return system;
	}

	public void run() {
		try {
			try {
				this.initialize();
				this.startSystems();
				while (!isStopped()) {
					synchronized (this) {
						this.wait();
					}
					if (isPaused())
						this.pauseSystem();
					else
						this.playSystem();
				}
			} catch (InterruptedException e) {
				logger.info("Thread Interrupted");
			}
			this.stopSystem();
			this.joinSystem();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void pauseSystem() {
		if (started) {
			logger.info("Pause command received, Kademlia is pausing");
			if (!system.isPaused()) {
				logger.debug("Pausing " + system.getName());
				system.pauseThread();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		} else
			logger.warn("Cannot pause Kademlia now: system not fully started");
		
	}

	public void playSystem() {
		if (started) {
			logger.info("Play command received, Kademlia is playing");
			if (system.isPaused()) {
				logger.debug("Playing " + system.getName());
				system.playThread();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		} else
			logger.warn("Cannot play Kademlia now: system not fully started");
		
	}

	private void joinSystem() throws InterruptedException {
			system.join();
			logger.debug(system.getName() + "joined");
		logger.info("系统成功关闭");
	}

	protected void stopSystem() {
		if (started) {
			logger.info("收到终止命令，系统即将关闭");
			logger.debug("Stopping " + system.getName());
			system.stopThread();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		} else {
			logger.warn("系统没有全部开启，现在不能关闭");
		}
		
	}

	private JKademliaSystem startSystems() {
		logger.debug("Launching " + getBaseName());
		system.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		started = true;
		logger.info("Kademlia sucessfully started");
		return system;
	}

	private String getBaseName() {
		return System.getProperty("jkademlia.systems.name", "Node");
	}

	private void validateProperties(Properties properties) throws PropertiesNotFoundException {
		List<String> failedProperties = new ArrayList<String>();
		for (int i = 0; i < usedProperties.length; i++) {
			if (properties.getProperty(usedProperties[i]) == null)
				failedProperties.add(usedProperties[i]);
		}
		if (failedProperties.size() > 0)
			throw new PropertiesNotFoundException(failedProperties);
	}

	/**
	 *function:初始化日志文件记录，并从配置文件里面读入参数
	 * 
	 * @throws FileNotFoundException
	 *@throws IOException
	 *@throws PropertiesNotFoundException
	 *             return type:void
	 */
	private void initialize() throws FileNotFoundException, IOException, PropertiesNotFoundException {
		this.initializeLogger();
		this.setProperties();
	}

	private void setProperties() throws FileNotFoundException, IOException, PropertiesNotFoundException {
		logger.debug("Reading properties from file " + propFile.getAbsolutePath());
		Properties properties = new Properties();
		properties.load(new FileInputStream(propFile));
		validateProperties(properties);
		logger.debug("All necessary properties found");
		System.getProperties().putAll(properties);
	}

	private void initializeLogger() {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(this.getClass());
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}

	public boolean isStarted() {
		return this.started;
	}

	@Override
	public void pauseThread() {
		this.paused = true;
		synchronized (this) {
			this.notifyAll();
		}
	}

	@Override
	public void playThread() {
		this.paused = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

	@Override
	public boolean isStopped() {
		return this.dead;
	}

	@Override
	public void stopThread() {
		this.dead = true;
		synchronized (this) {
			this.notifyAll();
		}

	}
}
