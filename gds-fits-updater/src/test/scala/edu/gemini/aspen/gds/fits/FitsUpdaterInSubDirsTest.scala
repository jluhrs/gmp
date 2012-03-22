package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.fits.{HeaderItem, Header}
import edu.gemini.aspen.gds.api.Conversions._
import com.google.common.base.Stopwatch
import com.google.common.io.Files

@RunWith(classOf[JUnitRunner])
class FitsUpdaterInSubDirsTest extends FitsBaseTest {
  val baseFile = new File(classOf[FitsUpdaterInSubDirsTest].getResource("sample1.fits").toURI)
  val dataLabel = new DataLabel("dir1/dir2/sample1.fits")
  val originalFile = new File(new File(baseFile.getParentFile, "dir1/dir2"), baseFile.getName)
  val destinationFile = new File(baseFile.getParentFile, "N-sample1.fits")
  println(baseFile)
  println(originalFile)

  Files.createParentDirs(originalFile)
  Files.copy(baseFile, originalFile)

  println(originalFile)

  ignore("test for bug GIAPI-929") {
    val headers = Header(0, Nil)

    updateFitsFile(headers :: Nil)

    assertTrue(destinationFile.exists)
  }

}