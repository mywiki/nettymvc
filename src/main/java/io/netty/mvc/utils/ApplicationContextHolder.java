package io.netty.mvc.utils;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * SpringMVC
 * @author 老佛爷 mywiki95@gmail.com
 *
 */
public class ApplicationContextHolder {

    private static Logger logger = Logger.getLogger(ApplicationContextHolder.class);
    private static ApplicationContext rootContext = null;
    private static XmlWebApplicationContext mvcContext = null;
    private static DispatcherServlet dispatcherServlet = null;

    public static final void init() throws ServletException {
        if (mvcContext == null) {
            logger.info("Spring container to initialize");
            mvcContext = new XmlWebApplicationContext();
            mvcContext.setConfigLocation("classpath:app-mvc.xml");
            mvcContext.setParent(getRootContext());
            MockServletConfig servletConfig = new MockServletConfig(
                    ApplicationContextHolder.getMvcContext().getServletContext(), "dispatcherServlet");
            dispatcherServlet = new DispatcherServlet(
                    ApplicationContextHolder.getMvcContext());
            dispatcherServlet.init(servletConfig);
            logger.info("Spring container for initial completion");
        }
    }
    
    public static final void init2() throws ServletException {
    	MockServletContext servletContext = new MockServletContext();
    	MockServletConfig servletConfig = new MockServletConfig(servletContext);
    	AnnotationConfigWebApplicationContext wac = new AnnotationConfigWebApplicationContext();
		wac.setServletContext(servletContext);
		wac.setServletConfig(servletConfig);
    	wac.register(NettyWebConfig.class);
    	wac.refresh();
    	dispatcherServlet = new DispatcherServlet(wac);
    	dispatcherServlet.init(servletConfig);
        logger.info("Spring container for initial completion");
    }
    
    /**
     * Spring容器
     * @return
     */
    private static ApplicationContext getRootContext() {
        if(rootContext==null){
            rootContext = new ClassPathXmlApplicationContext("classpath:app-context.xml");
        }
        return rootContext;
    }

    public static WebApplicationContext getMvcContext() {
        return mvcContext;
    }

    public static DispatcherServlet getDispatcherServlet(){
        return dispatcherServlet;
    }
}
