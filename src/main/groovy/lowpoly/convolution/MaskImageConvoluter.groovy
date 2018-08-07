package lowpoly.convolution

class MaskImageConvoluter {

  private static final Double ONE_OVER_ROOT_TWO = 0.7071

  List createEdgeMapFromColourImage(List image) {
    List result = (0..<image.size()).collect { x -> (0..<image[0].size()).collect { 0 } }
    List horizontalEdgeMap = (0..2).collect{ colour -> runMaskAcrossImage(image, Mask.HORIZONTAL_EDGE_MASK, { it[colour] }) }
    List verticalEdgeMap = (0..2).collect{ colour -> runMaskAcrossImage(image, Mask.VERTICAL_EDGE_MASK, { it[colour] }) }
    (0..(image.size()-1)).each { x ->
      (0..(image[0].size()-1)).each { y ->
        result[x][y] = (0..2).sum{ Math.abs(horizontalEdgeMap[it][x][y]) + Math.abs(verticalEdgeMap[it][x][y]) }
      }
    }
    return result
  }

  List createEdgeMapFromGrayImage(List image) {
    List horizontalEdgeMap = runMaskAcrossImage(image, Mask.HORIZONTAL_EDGE_MASK)
    List verticalEdgeMap = runMaskAcrossImage(image, Mask.VERTICAL_EDGE_MASK)
    (0..<image.size()).each { x ->
      (0..<image[0].size()).each { y ->
        horizontalEdgeMap[x][y] = Math.abs(horizontalEdgeMap[x][y]) + Math.abs(verticalEdgeMap[x][y])
      }
    }
    return horizontalEdgeMap
  }

  private List runMaskAcrossImage(List image, Mask mask, Closure getPixel = Closure.IDENTITY) {
    int maskSize = mask.size()
    int maskBorderSize = maskSize / 2
    int imageWidth = image.size()
    int imageHeight = image[0].size()
    List maskRange = 0..<maskSize

    List result = (0..<imageWidth).collect { x -> (0..<imageHeight).collect { 0 } }
    (maskBorderSize..<(imageWidth - maskBorderSize)).each { x ->
      (maskBorderSize..<(imageWidth - maskBorderSize)).each { y ->

        result[x][y] = maskRange.sum { xIndex -> maskRange.sum { yIndex ->
          mask[xIndex][yIndex] * getPixel(image[x + xIndex - maskBorderSize][y + yIndex - maskBorderSize])
        }}
      }
    }
    return result
  }

  static enum Mask {
    HORIZONTAL_EDGE_MASK([[ONE_OVER_ROOT_TWO, 1, ONE_OVER_ROOT_TWO], [0, 0, 0], [-ONE_OVER_ROOT_TWO, -1, -ONE_OVER_ROOT_TWO]]),
    VERTICAL_EDGE_MASK([[ONE_OVER_ROOT_TWO, 0, -ONE_OVER_ROOT_TWO], [1, 0, -1], [ONE_OVER_ROOT_TWO, 0, -ONE_OVER_ROOT_TWO]]),
    GAUSSIAN_BLUR_3([[1/16, 2/16, 1/16], [2/16, 4/16, 2/16], [1/16, 2/16, 1/16]]),
    GAUSSIAN_BLUR_5([[1/256, 4/256, 6/256, 4/256, 1/256],
                     [4/256, 16/256, 24/256, 16/256, 4/256],
                     [6/256, 24/256, 36/256, 24/256, 6/256],
                     [4/256, 16/256, 24/256, 16/256, 4/256],
                     [1/256, 4/256, 6/256, 4/256, 1/256]])

    private List mask

    Mask(List mask) {
      this.mask = mask
    }

    int size() {
      return this.mask.size()
    }

    List getAt(int index) {
      return this.mask.get(index)
    }
  }
}
