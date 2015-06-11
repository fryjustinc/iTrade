package iTrade;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;

import org.joda.time.LocalDate;
public class Runtime extends Thread{
	long duration;
	long temp;
	private final int DAYLENGTH=10000;//120000
	long day=DAYLENGTH,week=7*DAYLENGTH,month=4*7*DAYLENGTH;
	long past=System.currentTimeMillis();
	public static ArrayList<Policy> daily = new ArrayList<Policy>();
	public static ArrayList<Policy> weekly = new ArrayList<Policy>();
	public static ArrayList<Policy> monthly = new ArrayList<Policy>();
	public static LocalDate date = new LocalDate(1900, 1, 1);
	@Override
	public void run() {	
		//Parser.Parse();
		//Action.Initialize();
		Connection con=null;
		CallableStatement st;
		while(true){
			try {
				Thread.sleep(3000);
				temp=System.currentTimeMillis();
				duration+=temp-past;
				past=temp;				
				if(duration>=day){
					//date = date.plusDays(1);
					try {
						con = Database.initialize().getConnection();
						st = con.prepareCall("{call increment_date(?)}");
						st.setInt(1, 1);
						st.executeUpdate();
						st.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally{
						if (con != null)
							try {
								con.close();
							} catch (Exception ignore) {
							}
					}
					//System.out.println("New Day"+date.toString());
					runDailyPolicy();
					day+=DAYLENGTH;
				}
				if(duration>=week){
					//System.out.println("New Week");
					runWeeklyPolicy();
					week+=7*DAYLENGTH;
				}
				if(duration>=month){
					//System.out.println("New Month");
					runMonthlyPolicy();
					month+=4*7*DAYLENGTH;
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void initialize(){
		
	}
	
	public void runTradePolicy(String user){
		
	}
	
	public void runDailyPolicy(){

	}

	public void runWeeklyPolicy(){

	}

	public void runMonthlyPolicy(){

	}
}
