#DirectKafkaWordCount evaluation

(Fix the layout of this)

## DirectKafkaWordCount exemplet
- Steg 1: starta Intellij, projekt kafkadirect
- Steg 2: starta 4 term-fönster (för Zookeeper och Kafka). cd kafka_2.10-0.8.2.0 för alla
- Starta Kafka (se nedan)

### From 1.3 Quick Start

https://kafka.apache.org/documentation.html#quickstart
This tutorial assumes you are starting fresh and have no existing Kafka or ZooKeeper data.

- Step 1: Download the code
    - Download the 0.8.2.0 release and un-tar it.
       
        tar -xzf kafka_2.10-0.8.2.0.tgz
       
        cd kafka_2.10-0.8.2.0

- Step 2: Start the server

Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one. You can use the convenience script packaged with kafka to get a quick-and-dirty single-node ZooKeeper instance.

    bin/zookeeper-server-start.sh config/zookeeper.properties

Now start the Kafka server:

    bin/kafka-server-start.sh config/server.properties

- Step 3: Create a new topic

Let's create a topic named "test" with a single partition and only one replica:

    bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test9

We can now see that topic if we run the list topic command:

    bin/kafka-topics.sh --list --zookeeper localhost:2181 test

Alternatively, instead of manually creating topics you can also configure your brokers to auto-create topics when a non-existent topic is published to.

- Step 4: Send some messages

Kafka comes with a command line client that will take input from a file or from standard input and send it out as messages to the Kafka cluster. By default each line will be sent as a separate message.

 - Alt. 1 From stdin
    
Run the producer and then type a few messages into the console to send to the server.

    bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test9

    This is a message
    This is another message

 - Alt 2 To take a whole file

Note, test is your latest topic

    cat /filename | bin/kafka-console-producer.sh –broker-list localhost:9092 –topic test

    cat ~/spark-1.3.1/data/eeg/chb02_16_data.csv | bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

- Step 5: Start a consumer (or start before producer. in the file example)

Kafka also has a command line consumer that will dump out messages to standard output.

    bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test9 --from-beginning

    This is a message
    This is another message

If you have each of the above commands running in a different terminal then you should now be able to type messages into the producer terminal and see them appear in the consumer terminal.
All of the command line tools have additional options; running the command with no arguments will display usage information documenting them in more detail.

- Step 6: Starta Spark streaming (before producer, if from file)
https://github.com/apache/spark/blob/master/examples/scala-2.10/src/main/scala/org/apache/spark/examples/streaming/DirectKafkaWordCount.scala

 - Alt. 1 Using spark example
    
        bin/run-example streaming.DirectKafkaWordCount localhost:9092 test

Skriv i producer-fönstret (se 'Starta Kafka' ovan)
Spark programmet spottar ur sig wordcounts av det som kommer in (jämför med nc-exemplet för spark streaming)

 - Alt 2. Som ovan, men med egen scala-fil

        /Users/hugin/spark-1.3.1/bin/spark-submit --class DirectKafkaWordCount target/scala-2.10/KafkaDirect-assembly-1.0.jar localhost:9092 test9

Efter ev. uppdateringar

    sudo sbt assembly

build.sbt

    import AssemblyKeys._

    name := "KafkaDirect"

    version := "1.0"

    scalaVersion := "2.10.4"

    // additional libraries
    libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.3.1" % "provided",
    "org.apache.spark" %% "spark-sql" % "1.3.1",
    "org.apache.spark" %% "spark-hive" % "1.3.1",
      "org.apache.spark" %% "spark-streaming" % "1.3.1",
      "org.apache.spark" %% "spark-streaming-kafka" % "1.3.1",
      "org.apache.spark" %% "spark-streaming-flume" % "1.3.1",
      "org.apache.spark" %% "spark-mllib" % "1.3.1",
      "org.apache.commons" % "commons-lang3" % "3.0",
      "org.eclipse.jetty"  % "jetty-client" % "8.1.14.v20131031",
      "com.typesafe.play" % "play-json_2.10" % "2.2.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.3.3",
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.3.3",
      "org.elasticsearch" % "elasticsearch-hadoop-mr" % "2.0.0.RC1",
      "net.sf.opencsv" % "opencsv" % "2.0",
      "com.twitter.elephantbird" % "elephant-bird" % "4.5",
      "com.twitter.elephantbird" % "elephant-bird-core" % "4.5",
      "com.hadoop.gplcompression" % "hadoop-lzo" % "0.4.17",
      "mysql" % "mysql-connector-java" % "5.1.31",
      "com.datastax.spark" %% "spark-cassandra-connector" % "1.0.0-rc5",
      "com.datastax.spark" %% "spark-cassandra-connector-java" % "1.0.0-rc5",
      "com.github.scopt" %% "scopt" % "3.2.0",
      "org.scalatest" %% "scalatest" % "2.2.1" % "test",
      "com.holdenkarau" %% "spark-testing-base" % "0.0.1" % "test"
    )
    
    resolvers ++= Seq(
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
      "Spray Repository" at "http://repo.spray.cc/",
      "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
      "Akka Repository" at "http://repo.akka.io/releases/",
      "Twitter4J Repository" at "http://twitter4j.org/maven2/",
      "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
      "Twitter Maven Repo" at "http://maven.twttr.com/",
      "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
      "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
      Resolver.sonatypeRepo("public")
    )
    
    
    assemblySettings
    
    mergeStrategy in assembly := {
      case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
      case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
      case "log4j.properties"                                  => MergeStrategy.discard
      case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
      case "reference.conf"                                    => MergeStrategy.concat
      case _                                                   => MergeStrategy.first
    }

project/plugins.sbt
    
    addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")
