
package filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebFilter("/*")
public class OnlineUserFilter implements Filter, HttpSessionListener {

    private ServletContext context;

    @Override
    public void init(FilterConfig filterConfig) {
        context = filterConfig.getServletContext();
        context.setAttribute("onlineUsers", 0);
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // Tạo session nếu chưa có
        HttpSession session = req.getSession();

        // Đánh dấu session đã được tính
        if (session.getAttribute("counted") == null) {
            session.setAttribute("counted", true);

            synchronized (this) {
                int count = (int) context.getAttribute("onlineUsers");
                context.setAttribute("onlineUsers", count + 1);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        synchronized (this) {
            int count = (int) context.getAttribute("onlineUsers");
            context.setAttribute("onlineUsers", Math.max(0, count - 1));
        }
    }

    @Override
    public void destroy() {
    }
}
