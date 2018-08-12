package lowpoly

import lowpoly.convolution.MaskImageConvoluter

class HoughTransform {
  List houghSpace
  int size
  List edgeMap

  HoughTransform(List edgeMap) {
    this.edgeMap = edgeMap
    this.size = edgeMap[0] + edgeMap[1]
    houghSpace = (0..<180).collect { x -> (0..<size*2).collect { new BigDecimal('0') } }
  }

  void markInHoughSpace() {
    (0..<edgeMap.size()).each { x ->
      (0..<edgeMap[0].size()).each { y ->
        markPointOnSpace(x, y, edgeMap[x][y])
      }
    }
  }

  void runButterflyOnHoughSpace() {
    houghSpace = new MaskImageConvoluter().runHorizontallyWrappedMask(houghSpace, MaskImageConvoluter.Mask.BUTTERLY_MASK)
    houghSpace = houghSpace.collect{ row -> row.collect{ Math.abs(it) }}
  }

  private void markPointOnSpace(int x, int y, strength) {
    (0..<180 ).each{ theta ->
      Double rho = y * Math.sin(Math.toRadians(theta)) + x * Math.cos(Math.toRadians(theta))
      int roundedRho = (int)Math.round(rho) + size
      houghSpace[theta][roundedRho] = houghSpace[theta][roundedRho].add(strength)
    }
  }

  int yFromLineAndX(int x, Map line) {
    return Math.round((line.rho - 1500 - (x * Math.cos(Math.toRadians(line.theta)))) / Math.sin(Math.toRadians(line.theta)))
  }

  int xFromLineAndY(int y, Map line) {
    return Math.round((line.rho - 1500 - (y * Math.sin(Math.toRadians(line.theta)))) / Math.cos(Math.toRadians(line.theta)))
  }
}
