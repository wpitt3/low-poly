package lowpoly.builder;

class SpotBuilder {
	Random random
	Integer width
	Integer height
	Integer gap

  SpotBuilder(Integer width, Integer height, Integer gap) {
		this.width = width
		this.height = height
		this.gap = gap
		random = new Random()
	}

	Map createSpots() {
		return createSpotsInMiddle(createSpotsOnEdge())
	}

	List createSpotsOnCorners() {
		return [0, width-1].collect{ x -> [0, height-1].collect { y -> [x:x, y:y] } }.flatten()
	}

	Map createSpotsOnEdge(Integer perLine = width / gap * 30) {
		List spots = createSpotsOnCorners()
		perLine.times {
			Map left = createSpot(spots, 0, null)
			if (left) { spots << left }
			
			Map right = createSpot(spots, width-1, null)
			if (right) { spots << right }
			
			Map top = createSpot(spots, null, 0)
			if (top) { spots << top }
			
			Map bottom = createSpot(spots, null, height-1)
			if (bottom) { spots << bottom }
		}
		return (0..<spots.size()).collectEntries{ [(it): spots[it]]}
	}
	
	Map createSpotsInMiddle (Map spots, Integer chances = width*height) {
		Integer spotCount = spots.keySet().size()
		chances.times{
			Map spot = createSpot(spots)
			if(spot) {
				spots[spotCount++] = spot
			}
		}
		return spots
	}

	Map createSpot(List spots, Integer x = null, Integer y = null) {
		x = x != null ? x : random.nextInt(width)
		y = y != null ? y : random.nextInt(height)

		return spots.every{ Math.pow(it.x - x, 2) + Math.pow(it.y - y, 2) > Math.pow(gap, 2)} ? [x:x, y:y] : [:]
	}
	
	Map createSpot(Map spots, Integer x = null, Integer y = null) {
		x = x != null ? x : random.nextInt(width)
		y = y != null ? y : random.nextInt(height)
		return spots.every{ k, v -> Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2) > Math.pow(gap, 2)} ? [x:x, y:y] : [:]
	}
}