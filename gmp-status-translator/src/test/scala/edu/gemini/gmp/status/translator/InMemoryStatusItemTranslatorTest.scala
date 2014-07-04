package edu.gemini.gmp.status.translator

import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.giapi.status.StatusItem
import edu.gemini.aspen.giapi.status.impl.BasicStatus
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.gmp.statusdb.StatusDatabase
import edu.gemini.gmp.top.Top
import edu.gemini.gmp.top.TopImpl
import org.junit.Test
import org.xml.sax.SAXException
import javax.jms.JMSException
import javax.xml.bind.JAXBException
import java.io.IOException
import java.util.List
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.mockito.Matchers.any
import org.mockito.Mockito._

/**
 * Class InMemoryStatusItemTranslatorTest
 */
class InMemoryStatusItemTranslatorTest {
  @Test def testSimpleConfiguration {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: StatusDatabaseService = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    verifyZeroInteractions(aggregate)
  }

  @Test def testTranslations {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: StatusDatabaseService = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    val good: StatusItem[_] = new HealthStatus(top.buildStatusItemName("new"), Health.GOOD)
    val translated: List[StatusItem[_]] = translator.translate(new BasicStatus[AnyRef](top.buildStatusItemName("old"), 0))
    assertFalse(translated.isEmpty)
    assertEquals(good.getName, translated.get(0).getName)
    assertEquals(good.getValue, translated.get(0).getValue)
  }

  @Test def testMultipleTranslationsSameOrigin {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: StatusDatabaseService = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    val translated: List[StatusItem[_]] = translator.translate(new BasicStatus[AnyRef](top.buildStatusItemName("twice"), 0))
    assertEquals(2, translated.size)
    val first: StatusItem[_] = new HealthStatus(top.buildStatusItemName("first"), Health.GOOD)
    assertEquals(first.getName, translated.get(1).getName)
    assertEquals(first.getValue, translated.get(1).getValue)
    val second: StatusItem[_] = new HealthStatus(top.buildStatusItemName("second"), Health.GOOD)
    assertEquals(second.getName, translated.get(0).getName)
    assertEquals(second.getValue, translated.get(0).getValue)
  }

  @Test def testMultipleOriginsSameTranslation {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: StatusDatabaseService = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    val translated: List[StatusItem[_]] = translator.translate(new BasicStatus[AnyRef](top.buildStatusItemName("original1"), 0))
    translated.addAll(translator.translate(new BasicStatus[AnyRef](top.buildStatusItemName("original2"), 0)))
    assertEquals(2, translated.size)
    val first: StatusItem[_] = new HealthStatus(top.buildStatusItemName("target"), Health.GOOD)
    assertEquals(first.getName, translated.get(0).getName)
    assertEquals(first.getValue, translated.get(0).getValue)
    val second: StatusItem[_] = new HealthStatus(top.buildStatusItemName("target"), Health.BAD)
    assertEquals(second.getName, translated.get(1).getName)
    assertEquals(second.getValue, translated.get(1).getValue)
  }

  @Test def testTranslationUpdate {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: StatusDatabaseService = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    val good: StatusItem[_] = new HealthStatus(top.buildStatusItemName("new"), Health.GOOD)
    translator.update(new BasicStatus[AnyRef](top.buildStatusItemName("old"), 0))
    verify(aggregate).update(any(classOf[StatusItem[_]]))
  }

  @Test def testTranslationTwiceUpdate {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: StatusDatabaseService = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    translator.update(new BasicStatus[AnyRef](top.buildStatusItemName("twice"), 0))
    verify(aggregate, times(2)).update(any(classOf[StatusItem[_]]))
  }

  @Test def testTranslateOnStart {
    val top: Top = new TopImpl("gpi", "gpi")
    val file: String = getClass.getResource("status-translator.xml").getFile
    val db: Nothing = new Nothing
    val aggregate: Nothing = mock(classOf[Nothing])
    val original: StatusItem[_] = new BasicStatus[AnyRef](top.buildStatusItemName("old"), 3)
    val original2: StatusItem[_] = new BasicStatus[AnyRef](top.buildStatusItemName("oldone"), 3)
    db.update(original)
    db.update(original2)
    val translator: InMemoryStatusItemTranslator = new InMemoryStatusItemTranslator(top, aggregate, db, file)
    translator.start
    verify(aggregate, times(1)).update(any(classOf[StatusItem[_]]))
  }
}