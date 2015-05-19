DirectKafkaWordCount exemplet - med stdin som producer i Kafka
	•	Starta Kafka
	•	From 1.3 Quick Start
	•	https://kafka.apache.org/documentation.html#quickstart
	•	This tutorial assumes you are starting fresh and have no existing Kafka or ZooKeeper data.
	•	Step 1: Download the code
	•	Download the 0.8.2.0 release and un-tar it.
	•	> tar -xzf kafka_2.10-0.8.2.0.tgz
	•	> cd kafka_2.10-0.8.2.0
	•	Step 2: Start the server
	•	Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one. You can use the convenience script packaged with kafka to get a quick-and-dirty single-node ZooKeeper instance.
	•	> bin/zookeeper-server-start.sh config/zookeeper.properties
	•	[2013-04-22 15:01:37,495] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig)
	•	...
	•	Now start the Kafka server:
	•	> bin/kafka-server-start.sh config/server.properties
	•	[2013-04-22 15:01:47,028] INFO Verifying properties (kafka.utils.VerifiableProperties)
	•	[2013-04-22 15:01:47,051] INFO Property socket.send.buffer.bytes is overridden to 1048576 (kafka.utils.VerifiableProperties)
	•	...
	•	Step 3: Create a topic
	•	Let's create a topic named "test" with a single partition and only one replica:
	•	> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
	•	We can now see that topic if we run the list topic command:
	•	> bin/kafka-topics.sh --list --zookeeper localhost:2181 test
	•	Alternatively, instead of manually creating topics you can also configure your brokers to auto-create topics when a non-existent topic is published to.
	•	Step 4: Send some messages
	•	Kafka comes with a command line client that will take input from a file or from standard input and send it out as messages to the Kafka cluster. By default each line will be sent as a separate message.
	•	Run the producer and then type a few messages into the console to send to the server.
	•	> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
	•	This is a message
	•	This is another message
	•	Step 5: Start a consumer
	•	Kafka also has a command line consumer that will dump out messages to standard output.
	•	> bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
	•	This is a message
	•	This is another message
	•	If you have each of the above commands running in a different terminal then you should now be able to type messages into the producer terminal and see them appear in the consumer terminal.
	•	All of the command line tools have additional options; running the command with no arguments will display usage information documenting them in more detail.
	•	Starta Spark streaming
	•	https://github.com/apache/spark/blob/master/examples/scala-2.10/src/main/scala/org/apache/spark/examples/streaming/DirectKafkaWordCount.scala
	•	bin/run-example streaming.DirectKafkaWordCount localhost:9092 test
	•	Skriv i producer-fönstret (se 'Starta Kafka' ovan)
	•	Spark programmet spottar ur sig wordcounts av det som kommer in (jämför med nc-exemplet för spark streaming)
