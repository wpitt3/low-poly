package lowpoly.builder

import spock.lang.Specification

class SpotBuilderTest extends Specification {

	SpotBuilder spotBuilder
	Random random

	void setup() {
		spotBuilder = new SpotBuilder(10, 10, 3)
		random = Mock()
		spotBuilder.random = random
	}
	
	void "can create spot in empty position"() {
		expect:
			spotBuilder.createSpot([:], 3, 3) == [x:3, y:3]
			spotBuilder.createSpot([:], 0, 0) == [x:0, y:0]
	}

	void "create spot in random position if given null"() {
		when:
			random.nextInt(10) >>> [1, 2]

		then:
			spotBuilder.createSpot([:], null, null) == [x:1, y: 2]
	}
	
	void "do not create spot closer than or equal to gap"() {
		when:
			Map existingSpots = [0:[x:3, y:3]]
		then:
			!spotBuilder.createSpot(existingSpots, 3, 3)
			!spotBuilder.createSpot(existingSpots, 3, 6)
			spotBuilder.createSpot(existingSpots, 3, 7)
	}
	
	void create_spots_onBorder() {
		given:
			random.nextInt(10) >> 5
		when:
			Map spots = spotBuilder.createSpotsOnEdge(2)
		then:
			spots.collect{ k, v -> v } == [
					[x:0, y:0],
					[x:0, y:9],
					[x:9, y:0],
					[x:9, y:9],
					[x:0, y:5],
					[x:9, y:5],
					[x:5, y:0],
					[x:5, y:9]
			]
	}

	void "createSpots in middle"() {
		given:
			200 * random.nextInt(10) >> 5
		when:
			Map spots = spotBuilder.createSpotsInMiddle([:])
		then:
			spots == [0:[x:5, y:5]]
	}
}
