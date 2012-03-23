package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.fits.Header
import com.google.common.io.Files
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class FitsUpdaterInSubDirsTest extends FunSuite with BeforeAndAfter {
  val baseFile = new File(classOf[FitsUpdaterInSubDirsTest].getResource("sample1.fits").toURI)
  val dataLabel = new DataLabel("dir1/sample1.fits")
  val originalFile = new File(new File(baseFile.getParentFile, "dir1/"), baseFile.getName)
  val destinationDir = Files.createTempDir()
  val destinationFile = new File(destinationDir, "dir1/sample1.fits")

  Files.createParentDirs(originalFile)
  Files.copy(baseFile, originalFile)

  test("test for bug GIAPI-929") {
    val headers = Header(0, Nil)

    val fitsUpdater = new FitsUpdater(baseFile.getParentFile, destinationDir, dataLabel, headers :: Nil)
    fitsUpdater.updateFitsHeaders()

    assertTrue(destinationFile.exists)
  }

  after {
    destinationFile.delete()
    destinationDir.delete()
  }

}