package lowpoly.io

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.util.List;


class ImageReadWriter {

	static void storeAsFile(List image, String fileName) {
		writeBufferedImage(convertArrayToImage(image), fileName)
	}

	static void storeEdgeMapAsFile(List edgeMap, String fileName) {
		List image = convertEdgeMapToImage(edgeMap)
		writeBufferedImage(convertArrayToImage(image), fileName)
	}

	static List readFile(fileName) {
		return convertImageToArray(readBufferedImage(fileName))
	}

	private static List convertImageToArray(BufferedImage bufferedImage) {
		Integer width = bufferedImage.getWidth();
		Integer height = bufferedImage.getHeight();
		List image = new int [width][height][3];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
			int colour = bufferedImage.getRGB(x, y);
			image[x][y][0] = (colour & 0x00ff0000) >> 16;
			image[x][y][1] = (colour & 0x0000ff00) >> 8;
			image[x][y][2] = colour & 0x000000ff;
			}
		}
		return image;
	}

	private static BufferedImage convertArrayToImage(List image) {
		Integer width = image.size()
		Integer height = image[0].size()
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				bufferedImage.setRGB(x, y, new Color(
						(int)Math.max(0, Math.min(255, image[x][y][0].toDouble())),
						(int)Math.max(0, Math.min(255, image[x][y][1].toDouble())),
						(int)Math.max(0, Math.min(255, image[x][y][2].toDouble()))).getRGB())
			}
		}
		return bufferedImage;
	}

	private static void writeBufferedImage(BufferedImage image, String fileName) {
		File outputfile = new File(fileName);
	    try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		}
	}

	private static BufferedImage readBufferedImage(String fileName) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(fileName));
		} catch (IOException e) {
		}
		return image;
	}

	private static List convertEdgeMapToImage(List edgeMap) {
		def max = (edgeMap.max{it.max()}).max()
		def min = (edgeMap.min{it.min()}).min()
		Double edgeToColourRatio = 255.0 / (max-min)
		return edgeMap.collect{ column ->
			column.collect {
				int intensity = Math.round((it - min) * edgeToColourRatio)
				return [intensity, intensity, intensity]
			}
		}
	}
}
