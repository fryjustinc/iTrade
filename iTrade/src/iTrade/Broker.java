package iTrade;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/broker")
public class Broker {
		
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/all")
	public String listUserBrokers(@Context HttpServletRequest req){
		String result;
		String header="Available";
		Connection con = null;
		if(req.getSession().getAttribute("user")==null){result = "<div class=\"innerbubble\">"; header="Brokers";}
		else{
		result="<div class=\"innerbubble\"><span class=\"fundsheader\">Using</span>"
				+ "<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\">Name</div><div class=\"blimitt\" >Limit</div><div class=\"btimet\" >Trade Time</div>"
				+ "<div class=\"btimet\" >Commission</div><div class=\"btimet\">Initial Fee</div><div class=\"btimet\" >Minimum Deposit</div><div class=\"bgap\"></div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call User_Brokers_view(?)}");
			st.setString(1, (String) req.getSession().getAttribute("user"));
			ResultSet rs = st.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				result+="<div class=\"blist\"><div class=\"bno\">"+(cnt++)+"</div>"
						+ "<div class=\"bname\">"+rs.getString("Name")+
						"</div><div class=\"blimit\">"+rs.getInt("Limit")+
						"</div><div class=\"btime\">"+rs.getInt("Trade_time")+"</div>"
						+"<div class=\"btime\">"+rs.getInt("commission")+"</div><div class=\"btime\">"+
						rs.getInt("initial_fee")+"</div>"+"<div class=\"btime\">"+rs.getInt("initial_deposit")+
						"</div><div class=\"binput\"><input type=\"submit\" value=\"Select\" onClick=\"exclusivePolicies("+rs.getInt("Broker_id")+",'"+rs.getString("Name")+"','"+rs.getString("Investment")+"');\"></div></div><br/>";
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		}
		result+="<span class=\"fundsheader\">"+header+"</span>"
				+ "<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\">Name</div><div class=\"blimitt\">Limit</div><div class=\"btimet\">Trade Time</div>"
				+ "<div class=\"btimet\">Commission</div><div class=\"btimet\">Initial Fee</div><div class=\"btimet\">Minimum Deposit</div><div class=\"bgap\"></div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call display_brokers(?)}");
			st.setString(1,(String) req.getSession().getAttribute("user"));
			ResultSet rs = st.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				result+="<div class=\"blist\"><div class=\"bno\">"+(cnt++)+"</div><div class=\"bname\">"+
			rs.getString("Name")+"</div><div class=\"blimit\">"+
						rs.getInt("Limit")+"</div>"
								+ "<div class=\"btime\">"+rs.getInt("Trade_time")+"</div>"
								+ "<div class=\"btime\">"+rs.getInt("commission")+"</div>"
								+ "<div class=\"btime\">"+rs.getInt("initial_fee")+"</div>"
								+ "<div class=\"btime\">"+rs.getInt("initial_deposit")+"</div>"
						+ "<div class=\"binput\"><input type=\"submit\" value=\"Select\" onClick=\"policies("+rs.getInt("Broker_id")+",'"+rs.getString("Name")+"');\"></div></div><br/>";
			}
			result+="</div>";
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/dropdown")
	public static String brokerDropdown(@Context HttpServletRequest req){
		return brokerDropdownById(req, "");
	}
	public static String brokerDropdownById(HttpServletRequest req, String broker){
		Connection con = null;
		String result="";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con.prepareCall("{call User_Brokers_view(?)}");
			proc.setString(1,(String) req.getSession().getAttribute("user"));
			proc.executeQuery();
			ResultSet rs = proc.getResultSet();
			while (rs.next()) {
				if(broker==""||!broker.equals(rs.getString("Broker_id"))||broker.equals("All"))
				result+="<option value=\""+rs.getInt("Broker_id")+"\">"+rs.getString("Name")+": "+rs.getString("Investment")+"</option>";
				else{
					result+="<option selected value=\""+rs.getInt("Broker_id")+"\">"+rs.getString("Name")+": "+rs.getString("Investment")+"</option>";
				}
			}
			rs.close();
			proc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("add/{id}/{amt}")
	public String addBrokerToUser(@Context HttpServletRequest req, @PathParam("id") int id, @PathParam("amt") float amount){
		Connection con = null;
		String result="<div class=\"blistt\"><div class=\"bno\">No.</div><div class=\"bname\" >Name</div><div class=\"blimit\" >Limit</div><div class=\"btime\" >Trade Time</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call add_broker_user(?,?,?)}");
			st.setString(1, (String) req.getSession().getAttribute("user"));
			st.setInt(2, id);
			st.setFloat(3, amount);
			st.executeUpdate();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("close/{id}")
	public String closeAcct(@Context HttpServletRequest req, @PathParam("id") int id){
		Connection con = null;
		String result="<div class=\"blistt\"><div class=\"bno\">No.</div><div class=\"bname\" >Name</div><div class=\"blimit\" >Limit</div><div class=\"btime\" >Trade Time</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call close_acct(?,?)}");
			st.setString(1, (String) req.getSession().getAttribute("user"));
			st.setInt(2, id);
			st.executeUpdate();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{name}/{limit}/{tradeTime}")
	public int addBroker(@PathParam("name") String name,@PathParam("limit") String limit,
			@PathParam("tradeTime") String tradeTime){
		return db(name,Integer.parseInt(limit),Integer.parseInt(tradeTime));	
	}
	
	public static int db(String name, int limit, int tradeTime) {
		Connection con = null;
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con.prepareCall("{? = call create_broker (?,?,?)}");
			proc.registerOutParameter(1, java.sql.Types.INTEGER);
			proc.setString(2, name);
			proc.setInt(3, limit);
			proc.setInt(4, tradeTime);
			proc.executeUpdate();
			int i = proc.getInt(1);
			proc.close();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		return 0;
	}
}
