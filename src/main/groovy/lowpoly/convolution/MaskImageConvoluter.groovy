package lowpoly.convolution

class MaskImageConvoluter {

  private static final Double ONE_OVER_ROOT_TWO = 0.7071

  List createEdgeMapFromColourImage(List image) {
    List result = (0..<image.size()).collect { x -> (0..<image[0].size()).collect { 0 } }
    List horizontalEdgeMap = runMaskAcrossColourImage(image, Mask.HORIZONTAL_EDGE_MASK)
    List verticalEdgeMap = runMaskAcrossColourImage(image, Mask.VERTICAL_EDGE_MASK)
    (0..(image.size()-1)).each { x ->
      (0..(image[0].size()-1)).each { y ->
        result[x][y] = (0..2).sum{ Math.abs(horizontalEdgeMap[x][y][it]) + Math.abs(verticalEdgeMap[x][y][it]) }
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

  List runMaskAcrossImage(List image, Mask mask) {
    int maskSize = mask.size()
    int maskBorderSize = maskSize / 2
    int imageWidth = image.size()
    int imageHeight = image[0].size()
    List maskRange = 0..<maskSize

    List result = (0..<imageWidth).collect { x -> (0..<imageHeight).collect { 0 } }
    (maskBorderSize..<(imageWidth - maskBorderSize)).each { x ->
      (maskBorderSize..<(imageHeight - maskBorderSize)).each { y ->

        result[x][y] = maskRange.sum { xIndex -> maskRange.sum { yIndex ->
          mask[xIndex][yIndex] * image[x + xIndex - maskBorderSize][y + yIndex - maskBorderSize]
        }}
      }
    }
    return result
  }

  List runHorizontallyWrappedMask(List image, Mask mask) {
    int maskSize = mask.size()
    int maskBorderSize = maskSize / 2
    int imageWidth = image.size()
    int imageHeight = image[0].size()
    List maskRange = 0..<maskSize

    List result = (0..<imageWidth).collect { x -> (0..<imageHeight).collect { 0 } }
    (0..<imageWidth).each { x ->
      (maskBorderSize..<(imageHeight - maskBorderSize)).each { y ->
        result[x][y] = maskRange.sum { xIndex -> maskRange.sum { yIndex ->
          mask[xIndex][yIndex] * image[(x + xIndex - maskBorderSize + imageWidth)%imageWidth][y + yIndex - maskBorderSize]
        }}
      }
    }
    return result
  }

  List runMaskAcrossColourImage(List image, Mask mask) {
    int maskSize = mask.size()
    int maskBorderSize = maskSize / 2
    int imageWidth = image.size()
    int imageHeight = image[0].size()
    List maskRange = 0..<maskSize

    List result = (0..<imageWidth).collect { x -> (0..<imageHeight).collect { y -> (0..2).collect{ z-> image[x][y][z] }}}
    (maskBorderSize..<(imageWidth - maskBorderSize)).each { x ->
      (maskBorderSize..<(imageHeight - maskBorderSize)).each { y ->
        (0..2).each { colour ->
          result[x][y][colour] = maskRange.sum { xIndex -> maskRange.sum { yIndex ->
            mask[xIndex][yIndex] * image[x + xIndex - maskBorderSize][y + yIndex - maskBorderSize][colour]
          }}
        }
      }
    }
    return result
  }

  static enum Mask {
    HORIZONTAL_EDGE_MASK([[ONE_OVER_ROOT_TWO, 1, ONE_OVER_ROOT_TWO], [0, 0, 0], [-ONE_OVER_ROOT_TWO, -1, -ONE_OVER_ROOT_TWO]]),
    VERTICAL_EDGE_MASK([[ONE_OVER_ROOT_TWO, 0, -ONE_OVER_ROOT_TWO], [1, 0, -1], [ONE_OVER_ROOT_TWO, 0, -ONE_OVER_ROOT_TWO]]),
    GAUSSIAN_BLUR_3([[1/16, 2/16, 1/16],
                     [2/16, 4/16, 2/16],
                     [1/16, 2/16, 1/16]]),
    GAUSSIAN_BLUR_5([[1/256, 4/256, 6/256, 4/256, 1/256],
                     [4/256, 16/256, 24/256, 16/256, 4/256],
                     [6/256, 24/256, 36/256, 24/256, 6/256],
                     [4/256, 16/256, 24/256, 16/256, 4/256],
                     [1/256, 4/256, 6/256, 4/256, 1/256]]),
    BUTTERLY_MASK([[-1, -4, -1],
                   [2, 8, 2],
                   [-1, -4, -1]])

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
