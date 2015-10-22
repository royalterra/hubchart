package it.hubzilla.hubchart.servlet;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.business.ImageBusiness;
import it.hubzilla.hubchart.model.ImageCache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ImageCacheServlet
 */
@WebServlet("/imagecache")
public class ImageCacheServlet extends HttpServlet  {
	private static final long serialVersionUID = 8566769749717985688L;

	private final Logger LOG = LoggerFactory.getLogger(ImageCacheServlet.class);

	public static final String CHART_TYPE_PARAM = "type";
	public static final String STAT_ID_PARAM = "statId";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageCacheServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Parameters
		//STAT_ID
		String statIdString = request.getParameter(STAT_ID_PARAM);
		Integer statId = null;
		try {
			statId = Integer.parseInt(statIdString);
		} catch (NumberFormatException e1) {
			throw new ServletException("Not a valid hubId");
		}
		//CHART TYPE
		String chartTypeString = request.getParameter(CHART_TYPE_PARAM);
		String type = null;
		if (AppConstants.CHART_TYPE_DESCRIPTIONS.get(chartTypeString) != null) {
			type=chartTypeString;
		}
		
		if (statId == null || type == null) throw new ServletException("Not enough parameters provided");
		
		ImageCache ic = null;
		try {
			if (statId != null) {
				ic = ImageBusiness.findImageCacheByStatAndTypeByStatistics(statId, type);
				if (ic == null) {
					ic = ImageBusiness.persistChart(statId, type);
				}
			}
		} catch (OrmException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		}
		if (ic != null) {
			ByteArrayInputStream bais = new ByteArrayInputStream(ic.getImage());
			
			//Response
			response.setContentType(ic.getMimeType());
			response.setContentLength(ic.getImage().length);
			OutputStream out = response.getOutputStream();
	
			// Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = bais.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			out.close();
			bais.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
