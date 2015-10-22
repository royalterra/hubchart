package it.hubzilla.hubchart.persistence;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.ImageCache;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class ImageCacheDao {
	
	public ImageCache findByStatAndType(Session ses, Integer statId, String chartType) throws OrmException {
		ImageCache result = null;
		try {
			Query q = ses.createQuery("from ImageCache ic where "+
					"ic.idStat = :id1 and "+
					"ic.chartType = :s1 "+
					"order by ic.updateTime desc");
			q.setParameter("id1", statId, IntegerType.INSTANCE);
			q.setParameter("s1", chartType, StringType.INSTANCE);
			q.setFirstResult(0);
			q.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<ImageCache> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					result = list.get(0);
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public String findTitleByStatAndType(Session ses, Integer statId, String chartType) throws OrmException {
		String result = null;
		try {
			Query q = ses.createQuery("select ic.title from ImageCache ic where "+
					"ic.idStat = :id1 and "+
					"ic.chartType = :s1 "+
					"order by ic.updateTime desc");
			q.setParameter("id1", statId, IntegerType.INSTANCE);
			q.setParameter("s1", chartType, StringType.INSTANCE);
			q.setFirstResult(0);
			q.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<String> list = q.list();
			if (list != null) {
				if (list.size() > 0) {
					result = (String) list.get(0);
				}
			}
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
		return result;
	}
	
	public ImageCache createTransientImageCache(Session ses, Integer statId) throws OrmException {
		ImageCache ic = new ImageCache();
		ic.setImage(" ".getBytes());
		ic.setMimeType(AppConstants.IMAGE_MIME_TYPE);
		ic.setUpdateTime(new Date());
		ic.setIdStat(statId);
		return ic;
	}
	
	//public ImageCache findByStatistics(Session ses, Integer statId) throws OrmException {
	//	ImageCache result = null;
	//	try {
	//		Query q = ses.createQuery("from ImageCache ic where "+
	//				"ic.idStat = :id1 "+
	//				"order by ic.updateTime desc");
	//		q.setParameter("id1", statId, IntegerType.INSTANCE);
	//		q.setFirstResult(0);
	//		q.setMaxResults(1);
	//		@SuppressWarnings("unchecked")
	//		List<ImageCache> list = q.list();
	//		if (list != null) {
	//			if (list.size() > 0) {
	//				result = list.get(0);
	//			}
	//		}
	//	} catch (HibernateException e) {
	//		throw new OrmException(e.getMessage(), e);
	//	}
	//	return result;
	//}
	
	public void clearCache(Session ses) throws OrmException {
		try {
			Query q = ses.createQuery("delete from ImageCache");
			q.executeUpdate();
		} catch (HibernateException e) {
			throw new OrmException(e.getMessage(), e);
		}
	}
}
