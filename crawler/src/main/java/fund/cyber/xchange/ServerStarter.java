package fund.cyber.xchange;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Properties;

public class ServerStarter implements InitializingBean {

    @Value("${rest.port}")
    private String port;

    @Value("${rest.path}")
    private String path;

    @Override
    public void afterPropertiesSet() throws Exception {

        Properties properties = new Properties();
        // читаем файл конфигурации в переменную типа Properties
        InputStream stream = Main.class.getResourceAsStream("/WEB-INF/application.properties");
        properties.load(stream);
        stream.close();
        // то самое непосредственное указание логгеру log4j на получение параметров из конфигурации
        PropertyConfigurator.configure(properties);

        // инициализируем веб-контекст на базе нашей Java-based конфигурации WebContext
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebContext.class);
        // заполняем окружение контекста параметрами из файла конфигурации проекта
        webContext.getEnvironment().getPropertySources().addLast(new PropertiesPropertySource("applicationEnvironment", properties));

        // определяем стандартный сервлет Spring MVC
        ServletHolder servletHolder = new ServletHolder("test-dispatcher", new DispatcherServlet(webContext));
        servletHolder.setAsyncSupported(true);
        servletHolder.setInitOrder(1);

        // определяем стандартный фильтр Spring Security
        FilterHolder filterHolder = new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain"));
        filterHolder.setAsyncSupported(true);

        // определяем веб-контекст Jetty
        WebAppContext webAppContext = new WebAppContext();
        // указываем класс контекста приложения
        webAppContext.setInitParameter("contextConfigLocation", ApplicationContext.class.getName());
        // базовая папка проекта, где находится WEB-INF
        webAppContext.setResourceBase("resource");
        // назначаем стандартного слушателя, Context Path, созданные сервлет и фильтр
        webAppContext.addEventListener(new ContextLoaderListener(webContext));
        webAppContext.setContextPath(properties.getProperty("base.url"));
        webAppContext.addServlet(servletHolder, "/");
        webAppContext.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));

        // запускаем сервер
        Server server = new Server(Integer.parseInt(port));
        server.setHandler(webAppContext);
        server.start();
        server.join();
    }

}