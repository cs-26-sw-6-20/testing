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
        //val x1 = indexer.get(i, 0, 0)
        //val y1 = indexer.get(i, 0, 1)
        //val x2 = indexer.get(i, 0, 2)
        //val y2 = indexer.get(i, 0, 3)

        val p1 = new Point(indexer.get(i, 0, 0), indexer.get(i, 0, 1))
        val p2 = new Point(indexer.get(i, 0, 2), indexer.get(i, 0, 3))

        println(s"(${p1.x()},${p1.y()}) -> (${p2.x()},${p2.y()})")
        val segments = customSplitLine(Line(p1, p2), 15, 10)

        segments.foreach { s =>
          line(
            image,
            s.p1,
            s.p2,
            new Scalar(100,255,0,0)
          )

          circle(image, s.p1, 2, new Scalar(0,0,255,0))
          circle(image, s.p2, 2, new Scalar(0,0,255,0))
        }
      }
    }

    imshow("Scala OpenCV Hello", image)

    waitKey(0)

  destroyAllWindows()
}

trait Greeting {
  lazy val greeting: String = "hello world"
}

case class Line(p1: Point, p2: Point)

def customSplitLine(line: Line, maxLen: Double, gap: Double): Seq[Line] = {

  val x1 = line.p1.x().toDouble
  val y1 = line.p1.y().toDouble
  val x2 = line.p2.x().toDouble
  val y2 = line.p2.y().toDouble

  val dx = x2 - x1
  val dy = y2 - y1
  val length = math.sqrt(dx*dx + dy*dy)

  if (length <= maxLen) return Seq(line)

  val ux = dx / length
  val uy = dy / length

  val result = collection.mutable.ArrayBuffer[Line]()

  var t = 0.0

  while (t < length) {
    val segStart = t
    val segEnd = math.min(t + maxLen, length)

    val sx = x1 + ux * segStart
    val sy = y1 + uy * segStart
    val ex = x1 + ux * segEnd
    val ey = y1 + uy * segEnd

    result += Line(
      new Point(sx.toInt, sy.toInt),
      new Point(ex.toInt, ey.toInt)
    )

    t = segEnd + gap
  }

  result.toSeq
}
