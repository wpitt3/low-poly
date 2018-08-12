package lowpoly.io

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class JsonToFile {

  String parentFolder = (new File(getClass().getResource('/marker.txt').toURI()).parent) + "/"

  def readFromFile(filename) {
    println "$parentFolder${filename}.txt"
    return new JsonSlurper().parseText(new File("$parentFolder${filename}.txt").readLines())
  }

  def printToFile(toPrint, filename) {
    new File("$parentFolder${filename}.txt").withWriter {
      it.println JsonOutput.toJson(toPrint)
    }
  }
}


