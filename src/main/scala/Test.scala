package edu.nyu.libraries.dlts.aspace

import java.io._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source
import Traits._

object Recon extends App with HttpSupport {

  println("ACM Top Container Update Tool, v.0.1b")


 
  //initialize logs
  val logger = new FileWriter(new File("testout.tsv"))
  val errorLogger = new FileWriter(new File("testerrors.tsv"))

  //process the csv file
  process

  def process() { 
    
    Source.fromFile(new File(args(0))).getLines.foreach{ line =>
      
      val cols = line.split(",")
      val ao = cols(0)
      val newTC = JString(cols(2))

      try {

        val json = get(ao).get
        val tc = json.\("instances")(0).\("sub_container").\("top_container").\("ref")
        val title = json.\("title").extract[String]

        if(tc != newTC) {
          println(s"$ao\t${tc.extract[String]}\t${newTC.extract[String]}")
        }
      
      } catch {
        case e: Exception => {
          errorLogger.write(e.toString + "\n")
          errorLogger.write(e.printStackTrace + "\n")
          errorLogger.flush
        }
      }
    }

    //cleanup
    logger.flush
    errorLogger.flush
    logger.close
    errorLogger.close
    client.close
  }

}
