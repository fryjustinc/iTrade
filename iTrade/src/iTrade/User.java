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

@Path("/user")
public class User {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{name}/{password}/{difficulty}")
	public String register(@PathParam("name") String name,
			@PathParam("password") String password,
			@PathParam("difficulty") String difficulty) {
		return ""+db(name, password, Integer.parseInt(difficulty));
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("{name}/{password}")
	public void login(@Context HttpServletRequest req,
			@PathParam("name") String user,
			@PathParam("password") String password) {
		Connection con = null;
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con.prepareCall("{? = call verify_user (?,?)}");
			proc.registerOutParameter(1, java.sql.Types.INTEGER);
			proc.setString(2, user);
			proc.setString(3, password);
			proc.executeUpdate();
			int i = proc.getInt(1);
			if (user.equals("admin"))
				req.getSession().setAttribute("user", user);
			if (i == 0) {
				req.getSession().setAttribute("user", user);
			}
			proc.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception ignore) {
				}
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("logout")
	public void logout(@Context HttpServletRequest req) {
		req.getSession().removeAttribute("user");
	}

	public static int db(String user, String password, int difficulty) {
		Connection con = null;
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con
					.prepareCall("{? = call register_user (?,?,?)}");
			proc.registerOutParameter(1, java.sql.Types.INTEGER);
			proc.setString(2, user);
			proc.setString(3, password);
			proc.setInt(4, difficulty);
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

	public static String getBalance(HttpServletRequest req) {
		Connection con = null;
		String result="";
		try {
			con = Database.initialize().getConnection();
			CallableStatement proc = con
					.prepareCall("{call get_capital(?)}");
			proc.setString(1, (String) req.getSession().getAttribute("user"));
			proc.executeQuery();
			ResultSet rs = proc.getResultSet();
			while (rs.next()) {
				result = rs.getString("Capital");
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
		
	@SuppressWarnings("resource")
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/transfer/{src}/{dst}/{amt}")
	public void transferFunds(@Context HttpServletRequest req, @PathParam("src")int src, 
			@PathParam("dst") int dst, @PathParam("amt") float amt){
		if(amt<=0)return;
		if(amt>Float.parseFloat(User.getBalance(req))&&src==-1)return;
		Connection con = null;
		try {
		if(src!=-1){
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call User_Brokers_view(?)}");
			st.setString(1, (String) req.getSession().getAttribute("user"));
			ResultSet rs = st.executeQuery();
			while(rs.next()){
				if(rs.getInt("Broker_id")==src&&rs.getFloat("Investment")<amt)return;
			}
		}
			con = Database.initialize().getConnection();
			CallableStatement st = con.prepareCall("{call move_funds(?,?,?,?)}");
			st.setString(1, (String) req.getSession().getAttribute("user"));
			st.setInt(2, src);
			st.setInt(3, dst);
			st.setFloat(4, amt);
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
	}

	public static String signUp() {
		return "<div class=\"innerbubble\"><div id=\"outer\">"
				+ "<h1>Register</h1>"
				+ "<div id=\"status\" style=\"text-align: center; width: 100%\"></div>"
				+ "<label for=\"name\">Username</label><br/>"
				+ "<input class=\"biginput\" type=\"text\" id=\"name\" name=\"name\"/><br/>"
				+ "<label for=\"password\">Password</label><br/>"
				+ "<input class=\"biginput\" type=\"password\" id=\"password\" name=\"password\"/><br/>"
				+ "<label for=\"confirmpassword\">Confirm Password</label><br/>"
				+ "<input class=\"biginput\" type=\"password\" id=\"passwordcheck\" name=\"confirmpassword\"/><br/>"
				+ "<label for=\"difficulty\">Starting Difficulty</label><br/>"
				+ "<select class=\"biginput\" name=\"difficulty\" id=\"difficulty\">"
				+ "<option value=\"10000\">Easy $10000</option>"
				+ "<option value=\"5000\">Medium $5000</option>"
				+ "<option value=\"2500\">Hard $2500</option>"
				+ "</select> <br/>"
				+ "<br/>"
				+ "<input style=\"display:block; margin-left: auto; margin-right: auto\" type=\"submit\" value=\"Register\" onclick=\"register();\"/>"
				+ "</div><div>";
	}
}
