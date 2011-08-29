package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._
import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import java.io.{FileReader, FileWriter, BufferedWriter, BufferedReader, File}
import org.junit.{Ignore, Before, Test}

/**
 * Specify how the GDSConfiguration parser should behave
 */
class GDSConfigurationServiceTest {
  val ORIGINAL_CONFIG = "gds-keywords.conf"
  val NEW_CONFIG = "gds-keywords.conf.test"
  val TEST_DIR = "/tmp/"

  protected def copyFile(fromResource: String, toAbsolute: String) {
    val in = new File(this.getClass.getResource(fromResource).toURI)
    val out = new File(toAbsolute)
    val reader = new BufferedReader(new FileReader(in))
    val writer = new BufferedWriter(new FileWriter(out))
    var line = reader.readLine
    while (line != null) {
      line = reader.readLine
      if (line != null) {
        writer.write(line + "\n")
      }
    }
    reader.close()
    writer.close()
  }

  protected def checkOriginalContent(config: List[GDSConfiguration]) {
    assertTrue(config.contains(GDSConfiguration("GPI", "OBS_END_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "ws:massAirmass", 0, "Mean airmass for the observation")))
    //todo: check for the rest of the items
  }


  @Test
  def testGet() {
    copyFile(ORIGINAL_CONFIG, TEST_DIR + ORIGINAL_CONFIG)
    val serviceOrig: GDSConfigurationService = new GDSConfigurationServiceImpl(TEST_DIR + ORIGINAL_CONFIG)

    val readConfig = serviceOrig.getConfiguration

    assertEquals(9, readConfig.length)
    checkOriginalContent(readConfig)
  }

  @Test
  def testGetForUpdate() {
    copyFile(ORIGINAL_CONFIG, TEST_DIR + ORIGINAL_CONFIG)
    val serviceOrig: GDSConfigurationService = new GDSConfigurationServiceImpl(TEST_DIR + ORIGINAL_CONFIG)

    val readConfig = serviceOrig.getConfigurationForUpdate

    assertEquals(15, readConfig.size)
    assertEquals(9, GDSConfigurationFile.getConfiguration(readConfig).length)
    checkOriginalContent(GDSConfigurationFile.getConfiguration(readConfig))
  }

  @Test
  def testNew() {
    copyFile(ORIGINAL_CONFIG, TEST_DIR + NEW_CONFIG)
    val serviceNew: GDSConfigurationService = new GDSConfigurationServiceImpl(TEST_DIR + NEW_CONFIG)

    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation")
    serviceNew.newConfiguration(config :: Nil)
    val readConfig = serviceNew.getConfiguration
    assertEquals(1, readConfig.length)
    assertTrue(readConfig.contains(config))
  }

  @Test
  def testAdd() {
    copyFile(ORIGINAL_CONFIG, TEST_DIR + NEW_CONFIG)
    val serviceNew: GDSConfigurationService = new GDSConfigurationServiceImpl(TEST_DIR + NEW_CONFIG)

    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation")
    serviceNew.addConfiguration(config :: Nil)
    val readConfig = serviceNew.getConfiguration

    assertEquals(10, readConfig.length)
    checkOriginalContent(readConfig)
    assertTrue(readConfig.contains(config))
  }

  @Test
  def testUpdate() {
    copyFile(ORIGINAL_CONFIG, TEST_DIR + NEW_CONFIG)
    val serviceNew: GDSConfigurationService = new GDSConfigurationServiceImpl(TEST_DIR + NEW_CONFIG)

    val newConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation")
    val modConfig = GDSConfiguration("MOD", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation")
    val readConfig: List[Option[ConfigItem[_]]] = serviceNew.getConfigurationForUpdate
    val newElement: Option[ConfigItem[_]] = new Some(new ConfigItem(newConfig))
    val removedConfig = GDSConfigurationFile.getConfiguration(readConfig).tail.head //2nd element
    val newConfigList: List[Option[ConfigItem[_]]] = readConfig.updated(6, new Some(new ConfigItem(modConfig))) :+ newElement
    serviceNew.updateConfiguration(newConfigList)
    val readAgainConfig: List[Option[ConfigItem[_]]] = serviceNew.getConfigurationForUpdate

    assertEquals(17, readAgainConfig.size) //5 comments,9 old keywords, 1 new keyword, 1 blank line, end of file
    checkOriginalContent(GDSConfigurationFile.getConfiguration(readAgainConfig))
    assertTrue(GDSConfigurationFile.getConfiguration(readAgainConfig).contains(newConfig))
    assertTrue(GDSConfigurationFile.getConfiguration(readAgainConfig).contains(modConfig))
    assertFalse(GDSConfigurationFile.getConfiguration(readAgainConfig).contains(removedConfig))
  } //todo

}