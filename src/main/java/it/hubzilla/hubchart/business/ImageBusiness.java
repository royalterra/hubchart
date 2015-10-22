package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.ImageCache;
import it.hubzilla.hubchart.model.Statistics;
import it.hubzilla.hubchart.persistence.GenericDao;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.ImageCacheDao;
import it.hubzilla.hubchart.persistence.StatisticsDao;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ImageBusiness {

		
	public static ImageCache findImageCacheByStatAndTypeByStatistics(Integer statId, String chartType) throws OrmException {
		ImageCache result = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			result = new ImageCacheDao().findByStatAndType(ses, statId, chartType);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static ImageCache persistChart(Integer statId, String chartType) throws OrmException {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		ImageCache ic = null;
		try {
			//Find the correct stat list by chartType
			List<Statistics> statHistory = null;
			Statistics referenceStat = GenericDao.findById(ses, Statistics.class, statId);
			if (referenceStat != null) {
				if (AppConstants.CHART_TYPE_HUB_CHANNELS.equals(chartType)) {				
					statHistory = new StatisticsDao().findByHub(ses, referenceStat.getHub().getId(),
							AppConstants.DATE_FAR_PAST, AppConstants.DATE_FAR_FUTURE);
				}
				if (AppConstants.CHART_TYPE_TOTAL_CHANNELS.equals(chartType) || 
						AppConstants.CHART_TYPE_TOTAL_HUBS.equals(chartType)) {
					statHistory = new StatisticsDao().findGlobalStats(ses,
							AppConstants.DATE_FAR_PAST, AppConstants.DATE_FAR_FUTURE);
				}
				//Create the correct value sequence by chartType
				ChartBuilder cb = new ChartBuilder(
						AppConstants.GLOBAL_CHART_WIDTH, AppConstants.GLOBAL_CHART_HEIGHT);
				Date chartStart = AppConstants.DATE_FAR_FUTURE;
				//Date chartFinish = AppConstants.DATE_FAR_PAST;
				for (Statistics stat:statHistory) {
					if (AppConstants.CHART_TYPE_HUB_CHANNELS.equals(chartType)) {
						cb.addChartValue(stat.getPollTime(), stat.getActiveChannelsLast6Months());
					}
					if (AppConstants.CHART_TYPE_TOTAL_CHANNELS.equals(chartType)) {
						if (stat.getActiveChannelsLast6Months() != null) {
							cb.addChartValue(stat.getPollTime(), stat.getActiveChannelsLast6Months());
						}
					}
					if (AppConstants.CHART_TYPE_TOTAL_HUBS.equals(chartType)) {
						if (stat.getActiveHubs() != null) {
							cb.addChartValue(stat.getPollTime(), stat.getActiveHubs());
						}
					}
					if (stat.getPollTime().before(chartStart)) chartStart=stat.getPollTime();
					//if (stat.getPollTime().after(chartFinish)) chartFinish=stat.getPollTime();
				}
				//Persist graphic and description data in ImageCache
				try {
					byte[] imageBytes = cb.drawPngChart();
					ImageCacheDao icDao = new ImageCacheDao();
					ic = icDao.createTransientImageCache(ses, statId);
					ic.setImage(imageBytes);
					ic.setChartType(chartType);
					ic.setMimeType(AppConstants.IMAGE_MIME_TYPE);
					ic.setUpdateTime(referenceStat.getPollTime());
					ic.setTitle(AppConstants.CHART_TYPE_DESCRIPTIONS.get(chartType)+" since "+
							AppConstants.FORMAT_DAY.format(chartStart));
					GenericDao.saveGeneric(ses, ic);
				} catch (IOException e) {/* TODO what happens when image cannot be created? */}
			}
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return ic;
	}
	
	public static String findTitleByStatAndType(Integer statId, String chartType) throws OrmException {
		String result = null;
		Session ses = HibernateSessionFactory.getSession();
		try {
			result = new ImageCacheDao().findTitleByStatAndType(ses, statId, chartType);
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	
//	public static byte[] createChartImage(Integer hubId) throws OrmException, IOException {
//		byte[] result = null;
//		Session ses = HibernateSessionFactory.getSession();
//		try {
//			result = createChartImage(ses, hubId);
//		} catch (OrmException e) {
//			throw new OrmException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//		return result;
//	}
	
//	private static byte[] createChartImage(Session ses, Integer hubId) throws OrmException, IOException {
//		StatisticsDao hsd = new StatisticsDao();
//		Statistics firstStats = hsd.findOldestStatsByHub(ses, hubId);
//		Statistics lastStats = hsd.findLastStatsByHub(ses, hubId);
//		List<Statistics> hsList = null;
//		if (firstStats != null && lastStats != null) {
//			hsList = hsd.findByHub(ses, hubId,
//					firstStats.getPollTime(), 
//					lastStats.getPollTime());
//			ChartBuilder cb = new ChartBuilder(
//					AppConstants.HUB_CHART_WIDTH, AppConstants.HUB_CHART_HEIGHT);
//			//Add stats to chart
//			for (Statistics hs:hsList) {
//				cb.addChartValue(hs.getPollTime(), hs.getActiveChannelsLast6Months());
//			}
//			byte[] imageBytes = cb.drawPngChart();
//			return imageBytes;
//		}
//		return null;
//	}
}
