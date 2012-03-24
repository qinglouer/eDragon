package jkademlia.data;


import java.sql.*;
public  class DatabaseManager {
	
	private static Connection conn = null;
	private static Statement sta = null;
	private static ResultSet rs = null;
	private static String sql;
	
	public static synchronized void connectDatabase(){
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:file:data\\credit_db", "sa", "");
			sta = conn.createStatement(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}

	public static synchronized void createTable(){
		try{
			if(conn == null){
				connectDatabase();
			}else{
				//�������
				sql = "CREATE TABLE credit_table(speed int,connect_success int,transfer_success int,credit double)";
				if(sta != null){
						sta.executeUpdate(sql);
						//�����ʼĬ��ֵ
						sql = "INSERT INTO credit_table VALUES(0,0,0,0)";
						sta.executeUpdate(sql);	
//						sta.executeUpdate("shutdown");
				} 
				conn.commit();
				System.out.println("�������credit_table����ʼ���ɹ�");
			}

		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	
	public static synchronized double getCredit(){
		double credit = -2;
		try{
			if(conn == null)
				connectDatabase();
			sql = "SELECT * FROM credit_table";
			if(sta != null){
				rs = sta.executeQuery(sql);
				while(rs.next()){
					credit = rs.getDouble("credit");
				}
			}
			conn.commit();
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return credit;
	}
	public static synchronized void showTable(){
		try{
			if(conn == null){
				connectDatabase();
				
			}else{
				sql = "SELECT * FROM credit_table";
				if(sta != null){
					rs = sta.executeQuery(sql);
					while(rs.next()){
						System.out.println(rs.getInt("speed")+"  |  "+rs.getInt("connect_success")+"  |  "+rs.getInt("transfer_success")+" | "+rs.getDouble("credit"));
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}	
	}
	public static synchronized void updateSpeed(int newSpeed){
		try{
			if(conn == null){
				System.out.println("updateSpeed()ʱ�������ݿ���������");
				connectDatabase();
			}else{
				sql = "SELECT * FROM credit_table";
				if(sta != null){
					rs = sta.executeQuery(sql);
					while(rs.next()){
						int oldSpeed = rs.getInt("speed");
						System.out.println("ǰһ�ι����ٶ�ֵΪ"+oldSpeed);
						System.out.println("���׺󴫽����ٶ�ֵΪ"+newSpeed);
						int speed = (newSpeed+oldSpeed)/2;
						System.out.println("��������ݿ��д���ֵΪ"+speed);
						sta.executeUpdate("UPDATE credit_table SET speed="+speed+"where speed="+oldSpeed);
//						sta.executeUpdate("shutdown");
						conn.commit();
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static synchronized void addConnectSuccess(){
		try{
			if(conn == null){
				System.out.println("addConnectSuccess()ʱ�������ݿ���������");
				connectDatabase();
			}else{
				if(sta != null){
					rs = sta.executeQuery("SELECT * FROM credit_table");
					while(rs.next())
						{
						int oldConnectSuccess = rs.getInt("connect_success");
						sta.executeUpdate("UPDATE credit_table SET connect_success="+(oldConnectSuccess+1));
//						sta.executeUpdate("shutdown");
						}

				}
				conn.commit();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static synchronized void addTransferSuccess(){
		try{
			if(conn == null){
				System.out.println("addTransferSuccess()ʱ�������ݿ���������");
				connectDatabase();
			}else{
				if(sta != null){
					rs = sta.executeQuery("SELECT * FROM credit_table");
					if(rs.next()){
						int oldTransferSuccess = rs.getInt("transfer_success");
						sta.executeUpdate("UPDATE credit_table SET transfer_success="+(oldTransferSuccess+1));
//						sta.executeUpdate("shutdown");
					}
				}
				conn.commit();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static synchronized void updateCredit(int credit){
		try{
			if(conn == null){
				connectDatabase();
			}else{
				if(sta != null){
					rs = sta.executeQuery("SELECT * FROM credit_table");
					if(rs.next()){
						double oldCredit = rs.getDouble("credit");
						System.out.println("�ɵ�����ֵΪ:"+oldCredit+"  "+"�µ�����ֵΪ"+(oldCredit*0.5+credit*0.5));
						sta.executeUpdate("UPDATE credit_table SET credit="+(oldCredit*0.5+credit*0.5));
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static synchronized void disconnectDatabase(){
		if(conn != null){
			try {
				sta.execute("shutdown");
				sta.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
