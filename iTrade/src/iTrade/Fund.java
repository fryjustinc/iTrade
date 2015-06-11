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

@Path("/fund")
public class Fund {

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/all")
	public String all() {
		Connection con = null;
		String result = "<div class=\"innerbubble\"><div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\">Name</div>"
				+ "<div class=\"blimitt\">Share Price</div><div class=\"blimitt\">52wk. High</div>"
				+ "<div class=\"blimitt\">52wk. Low</div><div class=\"blimitt\">7 day %</div><div class=\"btimet\">Industry</div><div class=\"bgap\"></div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con.prepareCall("{call display_funds}");
			ResultSet rs = proc
					.executeQuery();
			int cnt = 1;
			while (rs.next()) {
				result += "<div class=\"blist\"><div class=\"bno\">"
						+ (cnt++)
						+ "</div><div class=\"bname\">"
						+ rs.getString("Ticker")
						+ "</div><div class=\"blimit\">"
						+ rs.getString("Share_Price")
						+ "</div><div class=\"blimit\">"
						+ rs.getString("52Hi")
						+ "</div><div class=\"blimit\">"
						+ rs.getString("52Low")
						+ "</div><div class=\"blimit\">"
						+ ((Float.parseFloat(rs.getString("Share_Price"))-Float.parseFloat(rs.getString("7open")))/Float.parseFloat(rs.getString("7open")))*100
						+ "</div><div class=\"btime\">"
						+ rs.getString("Type")
						+ "</div>"
						+ "<div class=\"binput\"><input class=\"create\" type=\"submit\" value=\"Select\" onClick=\"selectFund('"
						+ rs.getString("Ticker") + "');\"></div></div><br/>";
			}
			rs.close();
			proc.close();
			result += "</div>";
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
	@Path("/reqoffer/{name}")
	public String transact(@Context HttpServletRequest req, @PathParam("name") String name) {
		Connection con = null;
		if(req.getSession().getAttribute("user")==null)return User.signUp();
		String result = "<div class=\"innerbubble\">";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con.prepareCall("{call display_fund(?)}");
			proc.setString(1, name);
			proc.executeQuery();
			ResultSet rs = proc.getResultSet();
			while (rs.next()) {
				result += "<div style=\"font-size:20pt; text-align: center; margin: auto;\">"
						+ name
						+ "</div><div>Share Price: "
						+ rs.getString("Share_Price")
						+ "</div><div class=\"hidden\" id=\"sprice\">"
						+ rs.getString("Share_Price")
						+ "</div><div><label for=\"bselect\">Select Broker:</label><div class=\"binput\" id=\"bselecter\"><select class=\"biginput\" id=\"bselect\"></select></div></div>"
						+ "<div><label for=\"btype\">Select Type:</label><div class=\"binput\" id=\"btyper\"><select class=\"biginput\" id=\"btype\">"
						+ "<option>buy</option><option>sell</option></select></div></div><label for=\"shares\">Select Shares:</label><input id=\"shares\" onkeyup=\"adjustEstimate();\" type=\"text\"></br>"
						+ "<div id=\"eprice\">Estimated price: 0</div></br><input id=\"subshares\" type=\"submit\" onClick=\"submitOffer();\">";
			}
			rs.close();
			proc.close();
			result += "</div>";
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
	@Path("/equity")
	public String userEquity(@Context HttpServletRequest req) {
		Connection con = null;
		if(req.getSession().getAttribute("user")==null)return User.signUp();
		String result = "<div class=\"innerbubble\">";
		result+="<div style=\"font-size:20pt;\">Free Capital: "+User.getBalance(req)+"</div></br><span class=\"fundsheader\">Shares</span>"
				+ "<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\" >Ticker</div><div class=\"blimitt\" >Shares Owned</div></div><br/>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con
					.prepareCall("{call User_Shares_view(?)}");
			proc.setString(1, (String) req.getSession().getAttribute("user"));
			proc.executeQuery();
			ResultSet rs = proc.getResultSet();
			int cnt = 1;
			while (rs.next()) {
				result += "<div class=\"blist\"><div class=\"bno\">" + (cnt++)
						+ "</div><div class=\"bname\">"
						+ rs.getString("Ticker")
						+ "</div><div class=\"blimit\">"
						+ rs.getString("Shares") + "</div></div><br/>";
			}
			rs.close();
			proc.close();
			proc = con.prepareCall("{call User_Brokers_view(?)}");
			proc.setString(1, (String) req.getSession().getAttribute("user"));
			proc.executeQuery();
			rs = proc.getResultSet();
			cnt = 1;
			result += "<span class=\"fundsheader\">Brokers</span>"
					+ "<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\" >Name</div><div class=\"blimitt\" >Investment</div></div><br/>";
			while (rs.next()) {
				result += "<div class=\"blist\"><div class=\"bno\">" + (cnt++)
						+ "</div><div class=\"bname\">" + rs.getString("Name")
						+ "</div><div class=\"blimit\">"
						+ rs.getString("Investment") + "</div></div><br/>";
			}
			rs.close();
			proc.close();
			result += "</div>";
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
	@Path("/transactions")
	public String transactionsAll(@Context HttpServletRequest req) {
		Connection con = null;
		if (req.getSession().getAttribute("user") == null)
			return User.signUp();
		String result = "<div class=\"innerbubble\"></br><div style=\"text-align:center; font-size:14pt;\">Broker: "
				+ "<select id=\"transb\" onchange=\"transactions();\" class=\"biginput\"><option value=\"All\">All</option>"+Broker.brokerDropdown(req)+"</select></div></br>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc;
			ResultSet rs;
			proc = con.prepareCall("{call transaction_history(?)}");
			proc.setString(1, (String) req.getSession().getAttribute("user"));
			proc.executeQuery();
			rs = proc.getResultSet();
			int cnt = 1;
			result += "<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\" >Fund</div><div class=\"bnamet\" >Date</div><div class=\"blimitt\" >Total</div>"
					+ "<div class=\"blimitt\" >Share Price</div><div class=\"blimitt\" >Shares</div></div><br/>";
			while (rs.next()) {
				result += "<div class=\"blist\"><div class=\"bno\">" + (cnt++)
						+ "</div><div class=\"bname\" >"
						+ rs.getString("Fund_id")
						+ "</div><div class=\"bname\" >" + rs.getString("Time")
						+ "</div><div class=\"blimit\" >"
						+ Float.parseFloat(rs.getString("Sale_price"))
						* Float.parseFloat(rs.getString("No_of_shares"))
						+ "</div>" + "<div class=\"blimit\" >"
						+ rs.getString("Sale_price")
						+ "</div><div class=\"blimit\" >"
						+ rs.getString("No_of_shares") + "</div></div><br/>";
			}
			result += "<div>";
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
	@Path("/transactions/{broker}")
	public String transactionsByBroker(@Context HttpServletRequest req, @PathParam("broker") String broker) {
		Connection con = null;
		if (req.getSession().getAttribute("user") == null)
			return User.signUp();
		String result = "<div class=\"innerbubble\"></br><div style=\"text-align:center; font-size:14pt;\">Broker: "
				+ "<select id=\"transb\" onchange=\"transactions();\" class=\"biginput\"><option value=\"All\">All</option>"+Broker.brokerDropdownById(req,broker)+"</select></div></br>";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc;
			ResultSet rs;
			proc = con.prepareCall("{call transaction_history(?,?)}");
			proc.setString(1, (String) req.getSession().getAttribute("user"));
			proc.setString(2, broker);
			proc.executeQuery();
			rs = proc.getResultSet();
			int cnt = 1;
			result += "<div class=\"blistt\"><div class=\"bnot\">No.</div><div class=\"bnamet\" >Fund</div><div class=\"bnamet\" >Date</div><div class=\"blimitt\" >Total</div>"
					+ "<div class=\"blimitt\" >Share Price</div><div class=\"blimitt\" >Shares</div></div><br/>";
			while (rs.next()) {
				result += "<div class=\"blist\"><div class=\"bno\">" + (cnt++)
						+ "</div><div class=\"bname\" >"
						+ rs.getString("Fund_id")
						+ "</div><div class=\"bname\" >" + rs.getString("Time")
						+ "</div><div class=\"blimit\" >"
						+ Float.parseFloat(rs.getString("Sale_price"))
						* Float.parseFloat(rs.getString("No_of_shares"))
						+ "</div>" + "<div class=\"blimit\" >"
						+ rs.getString("Sale_price")
						+ "</div><div class=\"blimit\" >"
						+ rs.getString("No_of_shares") + "</div></div><br/>";
			}
			result += "<div>";
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
}