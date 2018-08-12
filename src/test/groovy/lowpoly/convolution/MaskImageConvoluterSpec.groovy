package lowpoly.convolution

import spock.lang.Specification

class MaskImageConvoluterSpec extends Specification {

  MaskImageConvoluter maskImageConvoluter

  void setup() {
    maskImageConvoluter = new MaskImageConvoluter()
  }

  void runMaskOnEmpty() {
    given:
      List image = createFilledMatrix(10, 10, 0)
    when:
      List result = maskImageConvoluter.runMaskAcrossGreyImage(image, MaskImageConvoluter.Mask.HORIZONTAL_EDGE_MASK)

    then:
      result.every{ column ->
        column.every{it == 0}
      }
  }

  void runMaskOnPoint() {
    given:
      List image = createFilledMatrix(10, 10, 0)
      image[5][5] = 100

    when:
      List result = maskImageConvoluter.runMaskAcrossGreyImage(image, MaskImageConvoluter.Mask.HORIZONTAL_EDGE_MASK)

    then:
      result[4][4] == -70.71
      result[4][5] == -100
      result[4][6] == -70.71
      result[6][4] == 70.71
      result[6][5] == 100
      result[6][6] == 70.71
  }

  void run5MaskOnPoint() {
    given:
      List image = createFilledMatrix(10, 10, 0)
      image[5][5] = 100

    when:
      List result = maskImageConvoluter.runMaskAcrossGreyImage(image, MaskImageConvoluter.Mask.GAUSSIAN_BLUR_5)

    then:
      result[5][5] == MaskImageConvoluter.Mask.GAUSSIAN_BLUR_5[2][2] * 100
  }

  void runEdgeMapOnPoint() {
    given:
      List image = createFilledMatrix(10, 10, 0)
      image[5][5] = 100

    when:
      List result = maskImageConvoluter.createEdgeMapFromGrayImage(image)

    then:
      result[4][5] == 100.0
      result[5][5] == 0.0
      result[6][5] == 100.0
  }

  void runEdgeMapOnColourPoint() {
    given:
      List image = createFilledMatrix(10, 10, [0, 0, 0])
      image[5][5] = [100, 50, 0]

    when:
      List result = maskImageConvoluter.createEdgeMapFromColourImage(image)

    then:
      result[4][5] == 150.0
      result[5][5] == 0.0
      result[6][5] == 150.0
  }

  List createFilledMatrix(Integer width, Integer height, def fill) {
    return  (1..width).collect { x -> (1..height).collect { fill } }
  }
}
