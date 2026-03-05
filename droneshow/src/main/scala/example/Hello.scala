package example

// til amalie: C:\Users\Amalie\AppData\Local\Coursier\data\bin\sbt.bat
import org.bytedeco.javacpp.opencv_core._;
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgproc._
import org.bytedeco.javacpp.opencv_highgui._

import org.bytedeco.javacpp.opencv_core.{Mat, Scalar, Point}
import org.bytedeco.javacpp.opencv_videoio.VideoCapture
import org.bytedeco.javacpp.indexer.DoubleIndexer
import org.bytedeco.javacpp.indexer.IntIndexer

object Hello extends Greeting with App {
  println(greeting)

  val image = new Mat(800, 800, CV_8UC3, new Scalar(0, 0, 0, 0))
  val cam = new VideoCapture(0)

  while true do
    cam.read(image)

    val gray = new Mat()
    cvtColor(image, gray, COLOR_BGR2GRAY)

    val edges = new Mat()
    Canny(gray, edges, 100, 200)
    val lines = new Mat()
    HoughLinesP(
      edges,
      lines,
      1,
      3.141592653589f / 180,
      5,
      2,
      1
    )

    if (!lines.empty()) {
        
      val indexer: IntIndexer = lines.createIndexer().asInstanceOf[IntIndexer]

      for (i <- 0 until lines.rows()) {
        val x1 = indexer.get(i, 0, 0)
        val y1 = indexer.get(i, 0, 1)
        val x2 = indexer.get(i, 0, 2)
        val y2 = indexer.get(i, 0, 3)

        println(s"($x1,$y1) -> ($x2,$y2)")
        line(image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0, 0))
      }
    }

/*    rectangle(
      image,
      new Point(100, 100),
      new Point(300, 300),
      new Scalar(0, 255, 255, 0),
      3,
      LINE_8,
      0
    ) */

    imshow("Scala OpenCV Hello", image)

    waitKey(0)

  destroyAllWindows()
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
