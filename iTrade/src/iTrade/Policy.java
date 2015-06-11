package iTrade;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/policy")
public class Policy {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{type}/{frequency}/{condition}")
	public String register(@PathParam("type") String type,@PathParam("frequency") String frequency,
			@PathParam("condition") String condition){
		return ""+db(type,frequency,condition);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{type}/{frequency}/{condition}/{broker}")
	public String registerToBroker(@PathParam("type") String type,@PathParam("frequency") String frequency,
			@PathParam("condition") String condition,@PathParam("broker") String broker){
		return addPolicyToBroker(broker,""+db(type,frequency,condition) );
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/all")
	public String all(){
		Connection con = null;
		String result="<div class=\"blist\"><div class=\"bno\">No.</div><div class=\"bname\" >Type</div><div class=\"blimit\" >Frequency</div><div class=\"btime\" >Condition</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call display_policies()}");
			ResultSet rs = st.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				result+="<div class=\"blist\"><div class=\"bno\">"+(cnt++)+"</div><div class=\"bname\">"+rs.getString("Type")+"</div><div class=\"blimit\">"+rs.getString("Frequency")+"</div><div class=\"btime\">"+rs.getString("Condition")+"</div>"
						+ "<div class=\"binput\"><input type=\"submit\" value=\"Add\" onClick=\"addToBroker("+rs.getString("Rule_id")+")\"></div></div><br/>";
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
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/all/{broker}")
	public String allByBroker(@PathParam("broker")String broker){
		Connection con = null;
		String result="<div class=\"blist\"><div class=\"bno\">No.</div><div class=\"bname\" >Type</div><div class=\"blimit\" >Frequency</div><div class=\"btime\" >Condition</div><div class=\"action\">Actions</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call get_broker_policies(?)}");
			st.setInt(1, Integer.parseInt(broker));
			ResultSet rs = st.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				String s="<div class=\"action\">";
				CallableStatement sta = con.prepareCall("{call show_policy_actions(?)}");
				sta.setString(1, (String) rs.getString("Rule_id"));
				ResultSet rss = sta.executeQuery();
				while(rss.next()){
					s+=rss.getString("Type")+" "+rss.getString("Effect")+"<br>";
				}
				s+="</div>";
				rss.close();
				sta.close();
				result+="<div class=\"blist\"><div class=\"bno\">"+(cnt++)+"</div><div class=\"bname\">"+rs.getString("Type")+"</div><div class=\"blimit\">"+rs.getString("Frequency")+"</div><div class=\"btime\">"+rs.getString("Condition")+"</div>"
						+ s+"<div class=\"binput\"><input type=\"submit\" value=\"Remove\" onClick=\"removeFromBroker("+rs.getString("Rule_id")+")\"></div></div><br/>";
			}
			rs.close();
			st.close();
			st = con.prepareCall("{call unassigned_policies(?)}");
			st.setInt(1, Integer.parseInt(broker));
			rs = st.executeQuery();
			cnt = 1;
			while (rs.next()) {
				String s="<div class=\"action\">";
				CallableStatement sta = con.prepareCall("{call unassigned_policy_actions(?)}");
				sta.setString(1, rs.getString("Rule_id"));
				ResultSet rss = sta.executeQuery();
				while(rss.next()){
					s+=rss.getString("Type")+" "+rss.getString("Effect")+"<br>";
				}
				s+="</div>";
				rss.close();
				sta.close();
				result+="<div class=\"blist\"><div class=\"bno\">"+(cnt++)+"</div><div class=\"bname\">"+rs.getString("Type")+"</div><div class=\"blimit\">"+rs.getString("Frequency")+"</div><div class=\"btime\">"+rs.getString("Condition")+"</div>"
						+ s+"<div class=\"binput\"><input type=\"submit\" value=\"Add\" onClick=\"addToBroker("+rs.getString("Rule_id")+")\"></div></div><br/>";
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
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/all/exclusive/{broker}")
	public String allOnlyByBroker(@Context HttpServletRequest req, @PathParam("broker")String broker){
		Connection con = null;
		String result="<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\">Type</div><div class=\"blimitt\">Frequency</div>"
				+ "<div class=\"btimet\">Condition</div><div class=\"bactiont\">Actions</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call get_broker_policies(?)}");
			st.setInt(1, Integer.parseInt(broker));
			ResultSet rs = st.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				String s="<div class=\"action\">";
				CallableStatement sta = con.prepareCall("{call show_policy_actions(?)}");
				sta.setString(1, (String) rs.getString("Rule_id"));
				ResultSet rss = sta.executeQuery();
				while(rss.next()){
					s+=rss.getString("Type")+" "+rss.getString("Effect")+"<br>";
				}
				s+="</div>";
				rss.close();
				sta.close();
				String state="";
				if(req.getSession().getAttribute("user")!=null&&req.getSession().getAttribute("user").equals("admin")){
					state="<div class=\"binput\"><input type=\"submit\" value=\"Remove\" onClick=\"removeFromBroker("+rs.getString("Rule_id")+")\"></div>";
				}
				result+="<div class=\"blist\"><div class=\"bno\">"+(cnt++)+"</div><div class=\"bname\">"+rs.getString("Type")+"</div><div class=\"blimit\">"+rs.getString("Frequency")+"</div><div class=\"btime\">"+rs.getString("Condition")+"</div>"
						+ s+state+"</div><br/>";
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
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{broker}/{policy}")
	public String addPolicyToBroker(@PathParam("broker") String broker, @PathParam("policy") String policy){
		Connection con = null;
		//String result="<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\">Type</div><div class=\"blimitt\" >Frequency</div><div class=\"btimet\" >Condition</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call broker_policy(?,?)}");
			st.setInt(1, Integer.parseInt(broker));
			st.setInt(2, Integer.parseInt(policy));
			st.executeQuery();
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
		return allByBroker(broker);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("remove/{broker}/{policy}")
	public String removePolicy(@PathParam("broker") String broker, @PathParam("policy") String policy){
		Connection con = null;
		try {
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call remove_broker_policy(?,?)}");
			st.setInt(1, Integer.parseInt(broker));
			st.setInt(2, Integer.parseInt(policy));
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
		return allByBroker(broker);
	}
	
	public static int db(String name, String limit, String tradeTime) {


		Connection con = null;
		int id = 0;
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con.prepareCall("{call create_policy (?,?,?,?)}");
			proc.setString(1, name);
			proc.setString(2, limit);
			proc.setString(3, tradeTime);
			proc.registerOutParameter(4, Types.INTEGER);
			proc.executeUpdate();
			id=proc.getInt(4);
			proc.close();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
		return id;
	}
	
}
