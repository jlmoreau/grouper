package edu.internet2.middleware.grouper.app.sqlProvisioning;

import java.sql.Types;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupSave;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemSave;
import edu.internet2.middleware.grouper.app.loader.GrouperLoaderConfig;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioner;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningAttributeValue;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningOutput;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningService;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningType;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.ddl.DdlUtilsChangeDatabase;
import edu.internet2.middleware.grouper.ddl.DdlVersionBean;
import edu.internet2.middleware.grouper.ddl.GrouperDdlUtils;
import edu.internet2.middleware.grouper.ddl.GrouperTestDdl;
import edu.internet2.middleware.grouper.helper.GrouperTest;
import edu.internet2.middleware.grouper.helper.SubjectTestHelper;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import edu.internet2.middleware.grouperClient.util.GrouperClientConfig;
import junit.textui.TestRunner;

/**
 * 
 * @author mchyzer
 *
 */
public class SqlMembershipProvisionerTest extends GrouperTest {

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    TestRunner.run(new SqlMembershipProvisionerTest("testSimpleGroupMembershipProvisioningFull"));

  }
  
  /**
   * 
   * @param name
   */
  public SqlMembershipProvisionerTest(String name) {
    super(name);
  }

  /**
   * grouper session
   */
  private GrouperSession grouperSession = null;

  /**
   * @see edu.internet2.middleware.grouper.helper.GrouperTest#setUp()
   */
  @Override
  protected void setUp() {
    super.setUp();
    
    try {

      this.grouperSession = GrouperSession.startRootSession();
      
      ensureTableSyncTables();
  
      new GcDbAccess().sql("delete from testgrouper_prov_group");
      new GcDbAccess().sql("delete from testgrouper_prov_mship0");
      new GcDbAccess().sql("delete from testgrouper_prov_mship1");
      
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
  }

  /**
   * @see edu.internet2.middleware.grouper.helper.GrouperTest#tearDown()
   */
  @Override
  protected void tearDown() {
    super.tearDown();
    
    GrouperClientConfig.retrieveConfig().propertiesOverrideMap().clear();
    
    dropTableSyncTables();
    GrouperSession.stopQuietly(this.grouperSession);

  }

  /**
   * 
   */
  public void dropTableSyncTables() {
    
    dropTableSyncTable("testgrouper_prov_group");
    dropTableSyncTable("testgrouper_prov_mship0");
    dropTableSyncTable("testgrouper_prov_mship1");
    
  }
  
  
  /**
   * just do a simple full sync of groups and memberships
   */
  public void testSimpleGroupMembershipProvisioningFull_1() {
    
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.class", SqlMembershipProvisioner.class.getName());
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.dbExternalSystemConfigId", "grouper");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.subjectSourcesToProvision", "jdbc");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipTableName", "testgrouper_prov_mship0");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipUserColumn", "subject_id");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipUserValueFormat", "${targetEntity.id}");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipGroupColumn", "group_name");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipGroupValueFormat", "${targetGroup.id}");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.syncMemberToId3AttributeValueFormat", "${targetEntity.id}");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.syncGroupToId3AttributeValueFormat", "${targetGroup.name}");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipCreationNumberOfAttributes", "2");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipCreationColumnTemplate_attr_0", "group_name");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipCreationColumnTemplate_val_0", "${targetGroup.name}");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipCreationColumnTemplate_attr_1", "subject_id");
    GrouperLoaderConfig.retrieveConfig().propertiesOverrideMap().put("provisioner.sqlProvTest.membershipCreationColumnTemplate_val_1", "${targetEntity.id}");
    
    // # if provisioning in ui should be enabled
    //# {valueType: "boolean", required: true}
    GrouperConfig.retrieveConfig().propertiesOverrideMap().put("provisioningInUi.enable", "true");

        
    Stem stem = new StemSave(this.grouperSession).assignName("test").save();
    Stem stem2 = new StemSave(this.grouperSession).assignName("test2").save();
    
    // mark some folders to provision
    Group testGroup = new GroupSave(this.grouperSession).assignName("test:testGroup").save();
    Group testGroup2 = new GroupSave(this.grouperSession).assignName("test2:testGroup2").save();
    
    testGroup.addMember(SubjectTestHelper.SUBJ0);
    testGroup.addMember(SubjectTestHelper.SUBJ1);
    
    testGroup2.addMember(SubjectTestHelper.SUBJ2);
    testGroup2.addMember(SubjectTestHelper.SUBJ3);
    
    final GrouperProvisioningAttributeValue attributeValue = new GrouperProvisioningAttributeValue();
    attributeValue.setDirectAssignment(true);
    attributeValue.setDoProvision(true);
    attributeValue.setTargetName("sqlProvTest");
    attributeValue.setStemScopeString("sub");

    GrouperProvisioningService.saveOrUpdateProvisioningAttributes(attributeValue, stem);

    //AttributeAssign attributeAssign = stem.getAttributeDelegate().addAttribute(GrouperProvisioningAttributeNames.retrieveAttributeDefNameMarker()).getAttributeAssign();
    //attributeAssign.getAttributeValueDelegate().assignValueString(GrouperProvisioningAttributeNames.retrieveAttributeDefNameDoProvision())
    
    //lets sync these over
    GrouperProvisioner grouperProvisioner = GrouperProvisioner.retrieveProvisioner("sqlProvTest");
    
    GrouperProvisioningOutput grouperProvisioningOutput = grouperProvisioner.provision(GrouperProvisioningType.fullProvisionFull); 
  
  }

  /**
   * @param tableName
   */
  public void dropTableSyncTable(final String tableName) {
    try {
      // if you cant connrc to it, its not there
      HibernateSession.bySqlStatic().select(Integer.class, "select count(1) from " + tableName);
    } catch (Exception e) {
      return;
    }
    try {
      HibernateSession.bySqlStatic().executeSql("drop table " + tableName);
    } catch (Exception e) {
      return;
    }
    try {
      // if you cant connrc to it, its not there
      HibernateSession.bySqlStatic().select(Integer.class, "select count(1) from " + tableName);
    } catch (Exception e) {
      return;
    }
    //we need to delete the test table if it is there, and create a new one
    //drop field id col, first drop foreign keys
    GrouperDdlUtils.changeDatabase(GrouperTestDdl.V1.getObjectName(), new DdlUtilsChangeDatabase() {
  
      public void changeDatabase(DdlVersionBean ddlVersionBean) {
        
        Database database = ddlVersionBean.getDatabase();
  
        {
          Table loaderTable = database.findTable(tableName);
          
          if (loaderTable != null) {
            database.removeTable(loaderTable);
          }
        }
                
      }
      
    });
  }

  /**
   * 
   */
  public void ensureTableSyncTables() {
    //we need to delete the test table if it is there, and create a new one
    //drop field id col, first drop foreign keys
    GrouperDdlUtils.changeDatabase(GrouperTestDdl.V1.getObjectName(), new DdlUtilsChangeDatabase() {
  
      public void changeDatabase(DdlVersionBean ddlVersionBean) {
        
        Database database = ddlVersionBean.getDatabase();
  
        createTableGroup(ddlVersionBean, database);
        
        createTableMship0(ddlVersionBean, database);
        
        createTableMship1(ddlVersionBean, database);
      }
      
    });
  }

  /**
   * @param ddlVersionBean
   * @param database
   */
  public void createTableGroup(DdlVersionBean ddlVersionBean, Database database) {
  
    String tableName = "testgrouper_prov_group";
    
    Table loaderTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database, tableName);
    
    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "uuid", Types.VARCHAR, "40", true, true);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "posix_id", Types.BIGINT, "10", false, true);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "name", Types.VARCHAR, "1024", false, true);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "display_name", Types.VARCHAR, "1024", false, false);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "description", Types.VARCHAR, "1024", false, false);
    
  }
  
  /**
   * @param ddlVersionBean
   * @param database
   */
  public void createTableMship1(DdlVersionBean ddlVersionBean, Database database) {
  
    String tableName = "testgrouper_prov_mship1";
    
    Table loaderTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database, tableName);
    
    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "uuid", Types.VARCHAR, "40", true, true);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "group_uuid", Types.VARCHAR, "40", false, true);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "subject_id", Types.VARCHAR, "1024", false, true);
    
  }

  /**
   * @param ddlVersionBean
   * @param database
   */
  public void createTableMship0(DdlVersionBean ddlVersionBean, Database database) {
  
    String tableName = "testgrouper_prov_mship0";
    
    Table loaderTable = GrouperDdlUtils.ddlutilsFindOrCreateTable(database, tableName);
    
    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "group_name", Types.VARCHAR, "180", true, true);

    GrouperDdlUtils.ddlutilsFindOrCreateColumn(loaderTable, "subject_id", Types.VARCHAR, "70", true, true);
    
  }

}
