package example

import org.bytedeco.javacpp.opencv_core._;
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgproc._
import org.bytedeco.javacpp.opencv_highgui._

import org.bytedeco.javacpp.opencv_core.{Mat, Scalar, Point}
import org.bytedeco.javacpp.opencv_videoio.VideoCapture

object Hello extends Greeting with App {
  println(greeting)

  var image = new Mat(600, 600, CV_8UC3, new Scalar(0, 0, 0, 0))
  val cam = new VideoCapture(0)

  while true do
    cam.read(image)

    val edges = new Mat()
    Canny(image, edges, 100,200)


    rectangle(
      image,
      new Point(100, 100),
      new Point(300, 300),
      new Scalar(0, 255, 255, 0),
      3,
      LINE_8,
      0
    )

    imshow("Scala OpenCV Hello", image)

    waitKey(0)

  destroyAllWindows()
}

trait Greeting {
  lazy val greeting: String = "hello world"
}
