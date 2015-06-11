package iTrade;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class Startup extends HttpServlet{
@Override
public void init() throws ServletException {
	System.out.println("Starting...");
	new Runtime().start();
	super.init();
}
}
