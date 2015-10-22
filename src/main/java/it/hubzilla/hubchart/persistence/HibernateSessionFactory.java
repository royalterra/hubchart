package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.EnvSingleton;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Configures and provides access to Hibernate sessions
 */
public class HibernateSessionFactory {

	private static final Logger LOG = LoggerFactory.getLogger(HibernateSessionFactory.class);

	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	//private static AnnotationConfiguration configuration = new AnnotationConfiguration();
	private static final org.hibernate.SessionFactory sessionFactory;

	static {
		try {
			LOG.debug("Trying to configure hibernate.");
			Configuration conf = new Configuration().configure(AppConstants.HIBERNATE_CONFIG_FILE);
			conf.setProperty("hibernate.connection.username", EnvSingleton.get().getMysqlUsername());
			conf.setProperty("hibernate.connection.password", EnvSingleton.get().getMysqlPassword());
			String connectionUrl = "jdbc:mysql://"+EnvSingleton.get().getMysqlHost()+":"+
					EnvSingleton.get().getMysqlPort()+"/"+EnvSingleton.get().getMysqlDbName();
			conf.setProperty("hibernate.connection.url", connectionUrl);
			ServiceRegistryBuilder builder = new ServiceRegistryBuilder().applySettings(conf.getProperties());
            sessionFactory = conf.buildSessionFactory(builder.buildServiceRegistry());
            LOG.info("SessionFactory created successfuly. Configuration is successful.");
		} catch (Throwable ex) {
			LOG.error("Initial SessionFactory creation failed.", ex);
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}

	private HibernateSessionFactory() {
	}

	/**
	 * Returns the ThreadLocal Session instance.  Lazy initialize
	 * the <code>SessionFactory</code> if needed.
	 *
	 *  @return Session
	 *  @throws HibernateException
	 */
	public static Session getSession() throws HibernateException {
		Session session = (Session) threadLocal.get();
		if (session == null || !session.isOpen()) {
			//			if (sessionFactory == null) {
			//				rebuildSessionFactory();
			//			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			threadLocal.set(session);
		}

		return session;
	}

	//	/**
	//     *  Rebuild hibernate session factory
	//     *
	//     */
	//	public static void rebuildSessionFactory() throws HibernateException {
	//		configuration.configure(configFile);
	//		sessionFactory = configuration.buildSessionFactory();
	//	}
	//
	//	/**
	//     *  Close the single hibernate session instance.
	//     *
	//     *  @throws HibernateException
	//     */
	//    public static void closeSession() throws HibernateException {
	//        Session session = (Session) threadLocal.get();
	//        threadLocal.set(null);
	//
	//        if (session != null) {
	//            session.close();
	//        }
	//    }
	
		/**
	     *  return session factory
	     *
	     */
		public static org.hibernate.SessionFactory getSessionFactory() {
			return sessionFactory;
		}
	
	//	/**
	//     *  return session factory
	//     *
	//     *	session factory will be rebuilded in the next call
	//     */
	//	public static void setConfigFile(String configFile) {
	//		HibernateSessionFactory.configFile = configFile;
	//		sessionFactory = null;
	//	}
	//
	//	/**
	//     *  return hibernate configuration
	//     *
	//     */
	//	public static Configuration getConfiguration() {
	//		return configuration;
	//	}

}