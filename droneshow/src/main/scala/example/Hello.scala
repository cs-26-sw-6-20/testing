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

  val camImg = new Mat(800, 800, CV_8UC3, new Scalar(0, 0, 0, 0))
  val cam = new VideoCapture(0)

  while true do
    cam.read(camImg)

    val gray = new Mat()
    cvtColor(camImg, gray, COLOR_BGR2GRAY)

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

    var dronecount = 0
    val image = new Mat(800, 800, CV_8UC3, new Scalar(0, 0, 0, 0))

    if (!lines.empty()) {
        
      val indexer: IntIndexer = lines.createIndexer().asInstanceOf[IntIndexer]

      val detected = collection.mutable.ArrayBuffer[Line]()

      for (i <- 0 until lines.rows()) {
        val p1 = new Point(indexer.get(i, 0, 0), indexer.get(i, 0, 1))
        val p2 = new Point(indexer.get(i, 0, 2), indexer.get(i, 0, 3))

        detected += Line(p1, p2)
      }

      val merged = mergeParallelLines(detected.toSeq)

      merged.foreach { l =>

        val segments = customSplitLine(l, 15, 10)

        segments.foreach { s =>
          dronecount += 1
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

    putText(image, s"Drone count: $dronecount", new Point(50, 50), FONT_HERSHEY_COMPLEX, 1, new Scalar(0, 0, 255, 0))
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

def lineAngle(l: Line): Double =
  math.atan2(
    l.p2.y() - l.p1.y(),
    l.p2.x() - l.p1.x()
  )

def midpoint(l: Line): (Double, Double) =
  (
    (l.p1.x() + l.p2.x()) / 2.0,
    (l.p1.y() + l.p2.y()) / 2.0
  )

def dist(a: (Double, Double), b: (Double, Double)): Double =
  math.hypot(a._1 - b._1, a._2 - b._2)

def mergeLineGroup(lines: Seq[Line]): Line = {

  val pts = lines.flatMap(l => Seq(l.p1, l.p2))

  val minX = pts.map(_.x()).min
  val minY = pts.map(_.y()).min
  val maxX = pts.map(_.x()).max
  val maxY = pts.map(_.y()).max

  Line(new Point(minX, minY), new Point(maxX, maxY))
}

def mergeParallelLines(
  lines: Seq[Line],
  angleThresh: Double = Math.toRadians(5),
  distThresh: Double = 20
): Seq[Line] = {

  val used = Array.fill(lines.length)(false)
  val result = collection.mutable.ArrayBuffer[Line]()

  for i <- lines.indices do
    if !used(i) then

      val base = lines(i)
      val baseAngle = lineAngle(base)
      val baseMid = midpoint(base)

      val group = collection.mutable.ArrayBuffer[Line](base)
      used(i) = true

      for j <- (i + 1) until lines.length do
        if !used(j) then

          val other = lines(j)
          val a = lineAngle(other)
          val mid = midpoint(other)

          val angleDiff = math.abs(baseAngle - a)

          if angleDiff < angleThresh &&
             dist(baseMid, mid) < distThresh
          then
            group += other
            used(j) = true

      result += mergeLineGroup(group.toSeq)

  result.toSeq
}