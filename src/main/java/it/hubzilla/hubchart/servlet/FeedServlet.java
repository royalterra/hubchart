package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.FeedEntries;
import it.hubzilla.hubchart.persistence.FeedEntriesDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
	private static final long serialVersionUID = 7127936402319986884L;

	private final Logger LOG = LoggerFactory.getLogger(FeedServlet.class);

	public static final String BASE_URL_PARAM = "baseUrl"; 

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FeedServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/rss+xml");
		PrintWriter out = response.getWriter();
		String feedContent = createFeedContent(AppConstants.FEED_DEFAULT_TYPE);
		out.print(feedContent);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public String createFeedContent(String feedType) throws ServletException {
		StringBuffer result = null;
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(feedType);

		feed.setTitle(AppConstants.FEED_TITLE);
		feed.setLink(AppConstants.FEED_LINK);
		feed.setDescription(AppConstants.FEED_DESCRIPTION);

		Session ses = HibernateSessionFactory.getSession();
		try {
			List<SyndEntry> entries = new ArrayList<SyndEntry>();
			List<FeedEntries> feList = new FeedEntriesDao().findLatest(ses);
			
			for (FeedEntries fe:feList) {
				SyndEntryImpl entry =new SyndEntryImpl();
				SyndContent description = new SyndContentImpl();
				entry.setTitle(fe.getTitle());
				entry.setLink(fe.getLink()+"#"+fe.getId());
				entry.setPublishedDate(fe.getPublishedDate());
				description.setType(fe.getDescriptionType());//"text/plain" "text/html"
				description.setValue(fe.getDescriptionValue());
				entry.setDescription(description);
				entries.add(entry);
			}
			feed.setEntries(entries);
	
			StringWriter writer = new StringWriter();
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, writer);
			writer.close();
			result = writer.getBuffer();
		} catch (OrmException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		} catch (FeedException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result.toString();
	}
}
