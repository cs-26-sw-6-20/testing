package example

import org.bytedeco.javacpp.opencv_core._;
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_imgproc._
import org.bytedeco.javacpp.opencv_highgui._

import org.bytedeco.javacpp.opencv_core.{Mat, Scalar, Point}

object Hello extends Greeting with App {
  println(greeting)

  val image = new Mat(400, 400, CV_8UC3, new Scalar(0, 0, 0, 0))

  rectangle(
    image,
    new Point(100, 100),
    new Point(300, 300),
    new Scalar(255, 255, 255, 0),
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
