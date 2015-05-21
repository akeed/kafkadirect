/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: DirectKafkaWordCount <brokers> <topics>
 *   <brokers> is a list of one or more Kafka brokers
 *   <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *    $ bin/run-example streaming.DirectKafkaWordCount broker1-host:port,broker2-host:port \
 *    topic1,topic2
 */

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}


object DirectKafkaWordCountV3 {

  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println(s"""
        |Usage: DirectKafkaWordCount <brokers> <topics>
        |  <brokers> is a list of one or more Kafka brokers
        |  <topics> is a list of one or more kafka topics to consume from
        |
        """".stripMargin)
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    val Array(brokers, topics) = args

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCountV3")
    val ssc =  new StreamingContext(sparkConf, Seconds(3))

    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    val lines = messages.map(_._2)
    val words = lines.flatMap(_.split(" "))

    // Convert RDDs of the words DStream to DataFrame and run SQL query
    words.foreachRDD((rdd: RDD[String], time: Time) => {
      // Get the singleton instance of SQLContext
      val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)
      import sqlContext.implicits._

      // Convert RDD[String] to RDD[case class] to DataFrame - file input

      val wordsDataFrame = rdd.map(s => s.split(",")).map(
        s => EEG(s(0).toDouble,
          s(1).toDouble,
          s(2).toDouble,
          s(3).toDouble,
          s(4).toDouble,
          s(5).toDouble,
          s(6).toDouble,
          s(7).toDouble,
          s(8).toDouble,
          s(9).toDouble,
          s(10).toDouble,
          s(11).toDouble,
          s(12).toDouble,
          s(13).toDouble,
          s(14).toDouble,
          s(15).toDouble,
          s(16).toDouble)
      ).toDF()

      // Convert RDD[String] to RDD[case class] to DataFrame - simple input
      //val wordsDataFrame = rdd.map(w => Record(w)).toDF()

      // Register as table
      wordsDataFrame.registerTempTable("words")

      // Do word count on table using SQL and print it
      val wordCountsDataFrame =
        //sqlContext.sql("select word, count(*) as total from words group by word")
        sqlContext.sql("select second, s1, count(*) as total from words group by second, s1")

      println(s"========= $time =========")
      //wordCountsDataFrame.select("second").show()
      wordCountsDataFrame.show()
    })

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}

/** Case class for converting RDD to DataFrame */
//Not needed, already elsewhere
//case class EEG(second: Double, s1: Double,  s2: Double,  s3: Double,  s4: Double,  s5: Double, s6: Double, s7: Double, s8: Double, s9: Double, s10: Double, s11: Double, s12: Double, s13: Double, s14: Double, s15: Double, s16: Double)