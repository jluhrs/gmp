package edu.gemini.aspen.gds.fits

import com.google.common.hash.Hashing
import java.net.URL
import java.io.{FileOutputStream, File}
import com.google.common.io.{Closeables, ByteStreams, Files}

trait FitsSamplesDownloader {
  // Check if file is available and md5 hashes matches
  def downloadFile(fileName: String, fileHash: String) {
    val sampleFile = new File(fileName)
    val hashingFunction = Hashing.md5()
    val available = sampleFile.exists() && hashingFunction.hashBytes(Files.toByteArray(sampleFile)).toString == fileHash

    if (!available) {
      println("Downloading sample to " + sampleFile)
      val fitsInputStream = new URL("http://sbfgpidev1/gpi/samples/" + sampleFile).openStream()
      val output = new FileOutputStream(sampleFile)
      try {
        ByteStreams.copy(fitsInputStream, output)
      } finally {
        Closeables.closeQuietly(output)
      }
    }
  }

}