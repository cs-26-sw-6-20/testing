package example

// til amalie: C:\Users\Amalie\AppData\Local\Coursier\data\bin\sbt.bat
import org.bytedeco.javacpp.opencv_core._;
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgproc._
import org.bytedeco.javacpp.opencv_highgui._

import org.bytedeco.javacpp.opencv_core.{Mat, Scalar, Point}
import org.bytedeco.javacpp.opencv_videoio.VideoCapture

object Hello extends Greeting with App {
  println(greeting)

  var image = new Mat(800, 800, CV_8UC3, new Scalar(0, 0, 0, 0))
  val cam = new VideoCapture(0)

  while true do
    cam.read(image)

    val edges = new Mat()

    val gray = new Mat()
    
    cvtColor(image, gray, COLOR_BGR2GRAY)
    Canny(gray, edges, 100, 200)


/*    rectangle(
      image,
      new Point(100, 100),
      new Point(300, 300),
      new Scalar(0, 255, 255, 0),
      3,
      LINE_8,
      0
    ) */

    imshow("Scala OpenCV Hello", edges)

    waitKey(0)

  destroyAllWindows()
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
