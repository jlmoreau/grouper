package edu.internet2.middleware.grouper.internal.dao.hib3;

import java.util.Set;

import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.flat.FlatStem;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.FlatStemDAO;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;

/**
 * @author shilen
 * $Id$
 */
public class Hib3FlatStemDAO extends Hib3DAO implements FlatStemDAO {

  /**
   *
   */
  private static final String KLASS = Hib3FlatStemDAO.class.getName();

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.FlatStemDAO#saveOrUpdate(edu.internet2.middleware.grouper.flat.FlatStem)
   */
  public void saveOrUpdate(FlatStem flatStem) {
    HibernateSession.byObjectStatic().saveOrUpdate(flatStem);
  }

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.FlatStemDAO#delete(edu.internet2.middleware.grouper.flat.FlatStem)
   */
  public void delete(FlatStem flatStem) {
    HibernateSession.byObjectStatic().delete(flatStem);
  }
  
  public void saveBatch(Set<FlatStem> flatStems) {
    HibernateSession.byObjectStatic().saveBatch(flatStems);
  }
  
  /**
   * reset flat stem 
   * @param hibernateSession
   */
  public static void reset(HibernateSession hibernateSession) {
    Stem rootStem = GrouperDAOFactory.getFactory().getStem().findByName(Stem.ROOT_INT, true, null);
    
    hibernateSession.byHql()
      .createQuery("delete from FlatStem where id not like :rootStemId")
      .setString("rootStemId", rootStem.getUuid())
      .executeUpdate();
  }

  /**
   * @see edu.internet2.middleware.grouper.internal.dao.FlatStemDAO#findById(java.lang.String)
   */
  public FlatStem findById(String flatStemId) {
    FlatStem flatStem = HibernateSession
      .byHqlStatic()
      .createQuery("select flatStem from FlatStem as flatStem where flatStem.id = :id")
      .setCacheable(true).setCacheRegion(KLASS + ".FindById")
      .setString("id", flatStemId)
      .uniqueResult(FlatStem.class);
    
    return flatStem;
  }
  
  /**
   * @see edu.internet2.middleware.grouper.internal.dao.FlatStemDAO#removeStemForeignKey(java.lang.String)
   */
  public void removeStemForeignKey(String flatStemId) {
    HibernateSession.byHqlStatic()
      .createQuery("update FlatStem set stemId = null where id = :id")
      .setString("id", flatStemId)
      .executeUpdate();
  }

  public Set<Stem> findMissingFlatStems(int page, int batchSize) {
    Set<Stem> stems = HibernateSession
      .byHqlStatic()
      .createQuery("select s from Stem s where not exists (select 1 from FlatStem flatStem where flatStem.id=s.uuid) " +
          "and not exists (select 1 from ChangeLogEntryTemp temp, ChangeLogType type where temp.string01 = s.uuid " +
          "and type.actionName='addStem' and type.changeLogCategory='stem' and type.id=temp.changeLogTypeId) " +
          "order by s.uuid")
      .setCacheable(false).setCacheRegion(KLASS + ".FindMissingFlatStems")
      .options(new QueryOptions().paging(batchSize, page, false))
      .listSet(Stem.class);
    
    return stems;
  }
  
  public long findMissingFlatStemsCount() {
    long count = HibernateSession
      .byHqlStatic()
      .createQuery("select count(*) from Stem s where not exists (select 1 from FlatStem flatStem where flatStem.id=s.uuid) " +
          "and not exists (select 1 from ChangeLogEntryTemp temp, ChangeLogType type where temp.string01 = s.uuid " +
          "and type.actionName='addStem' and type.changeLogCategory='stem' and type.id=temp.changeLogTypeId)")
      .setCacheable(false).setCacheRegion(KLASS + ".FindMissingFlatStemsCount")
      .uniqueResult(Long.class);
    
    return count;
  }
  
  public Set<FlatStem> findBadFlatStems() {
    Set<FlatStem> stems = HibernateSession
      .byHqlStatic()
      .createQuery("select flatStem from FlatStem flatStem where not exists (select 1 from Stem s where flatStem.id=s.uuid) " +
          "and not exists (select 1 from ChangeLogEntryTemp temp, ChangeLogType type where temp.string01 = flatStem.id " +
          "and type.actionName='deleteStem' and type.changeLogCategory='stem' and type.id=temp.changeLogTypeId)")
      .setCacheable(false).setCacheRegion(KLASS + ".FindBadFlatStems")
      .listSet(FlatStem.class);
    
    return stems;
  }
}

