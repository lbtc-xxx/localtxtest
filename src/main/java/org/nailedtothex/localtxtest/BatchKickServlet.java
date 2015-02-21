package org.nailedtothex.localtxtest;

import javax.batch.runtime.BatchRuntime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet(urlPatterns = "/")
public class BatchKickServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final long localtxtest = BatchRuntime.getJobOperator().start("localtxtest", new Properties());
        resp.getWriter().write(Long.toString(localtxtest));
    }
}
