package edu.gemini.gmp.status.translator

import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.statusservice.generated.DataType
import edu.gemini.aspen.giapi.statusservice.generated.StatusType
import org.junit.Test
import org.xml.sax.SAXException
import javax.xml.bind.JAXBException
import java.util.List
import org.junit.Assert.assertEquals

/**
 * Class StatusItemTranslatorConfigurationTest
 *
 * @author Nicolas A. Barriga
 *         Date: 4/9/12
 */
class StatusItemTranslatorConfigurationTest {
  @Test def testSimpleConfiguration {
    val configuration: StatusItemTranslatorConfiguration = new StatusItemTranslatorConfiguration(getClass.getResourceAsStream("status-translator.xml"))
    val statuses: List[Nothing] = configuration.getStatuses
    assertEquals(6, statuses.size)
    assertEquals("old", statuses.get(0).getOriginalName)
    assertEquals("new", statuses.get(0).getTranslatedName)
    assertEquals(DataType.INT, statuses.get(0).getOriginalType)
    assertEquals(DataType.HEALTH, statuses.get(0).getTranslatedType)
    {
      var i: Int = 0
      while (i < 2) {
        {
          assertEquals(Integer.toString(i), statuses.get(0).getMaps.getMap.get(i).getFrom)
          assertEquals(Health.values(i), Health.valueOf(statuses.get(0).getMaps.getMap.get(i).getTo))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    assertEquals("BAD", statuses.get(0).getDefault)
    assertEquals(null, statuses.get(1).getDefault)
  }
}