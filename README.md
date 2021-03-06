# Backbone

[![Build Status](https://travis-ci.org/firstbirdtech/backbone.svg?branch=master)](https://travis-ci.org/firstbirdtech/backbone)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/805331624b64414ab4bebae67557d5f7)](https://www.codacy.com/app/daniel-pfeiffer/backbone?utm_source=github.com&utm_medium=referral&utm_content=firstbirdtech/backbone&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/805331624b64414ab4bebae67557d5f7)](https://www.codacy.com/app/daniel-pfeiffer/backbone?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=firstbirdtech/backbone&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.firstbird/backbone-core_2.11/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Ccom.firstbird)  

**ATTENTION: WORK IN PROGRESS, UNPUBLISHED, NOT PRODUCTION READY**

Backbone is a durable publish-subscribe platform built on top of Amazon AWS SQS and Amazon AWS SNS utilizing
Akka to stream events. This library currently is under active development, changes to the public API are still subject
to change.

Backbone provides nice high-level API for consuming events.

```scala
backbone.consume[String](settings){ str =>
    println(str)
    Consumed //or Rejected in case of an failure
}
```

## Quick Start

### Installation
To use backbone add the following dependency to your `build.sbt`.
```scala
libraryDependencies += "com.firstbird" %% "backbone-core"   % "X.Y.Z"
libraryDependencies += "com.firstbird" %% "backbone-circe"  % "X.Y.Z" //or any other JSON library
```

Or add the dependency to your `pom.xml`.
```xml
<dependency>
    <groupId>com.firstbird</groupId>
    <artifactId>backbone-core_2.11</artifactId>
    <version>X.Y.Z</version>
</dependency>
```

### Consuming Events

To consume messages you have to specify the topics from which messages
should be consumed and a queue name that should be subscribed to the topics. The `parallelism` parameter
indicates how many messages are getting processed at a single time. To create an instance of `Backbone`
the only things you need are the according Amazon clients and a running `ActorSystem` which are
passed as implicit parameters.

When consuming of messages starts, Backbone subscribes the SQS queue automatically to the SNS topics
and sets the needed permissions on SQS side to give permissions to the SNS topics to send messages
to the queue.

```scala
implicit val system = ActorSystem()
implicit val sns = new AmazonSNSAsyncClient()
implicit val sqs = new AmazonSQSAsyncClient()

val backbone = Backbone()

val settings = ConsumerSettings(
    topics = "arn:aws:sns:eu-central-1:AWS_ACCOUNT_ID:topic-name" :: Nil,
    queue = "queue-name",
    parallelism = 5
)

//You need to define a MessageReader that tells Backbone how to decode the message body of the AWS SNS Message
implicit val messageReader = MessageReader(s => Success(Some(s)))

//Synchronous API
backbone.consume[String](settings){ str =>
    //process the event
    Consumed
}

//Asynchronous API
backbone.consumeAsync[String](settings){ _ =>
  Future.succesful(Consumed)
}
```

### Publishing Events

Backbone provides a couple of different methods which can be used to send messages to a Amazon AWS SNS Topic.
Basically they behave the same but provide an interface for various technologies as: `Future`s, Akka Streams,
Akka Actors.

```scala
implicit val system = ActorSystem()
implicit val sns = new AmazonSNSAsyncClient()
implicit val sqs = new AmazonSQSAsyncClient()

val backbone = Backbone()

val publishSettings = PublisherSettings("aws-sns-topic-arn")

//You need to define a MessageWriter that tells Backbone how to encode the message body of the AWS SNS Message
implicit val writer = MessageWriter(s => s)

//Actor Publisher
val actor: ActorRef = backbone.actorPublisher[String](publishSettings)
actor ! "send this to sns"

//Akka Streams Sink
implicit val mat = ActorMaterializer()

val sink = backbone.publisherSink[String](publishSettings)
Source.single("send this to sns").to(sink)

//Async Publish
val f: Future[PublishResult] = backbone.publishAsync[String]("send this to sns", publishSettings)
val f1: Future[PublishResult] = backbone.publishAsync[String]("send this to sns" :: "and this" :: Nil, publishSettings)
```

## AWS Policies

To work properly the AWS user used for running Backbone needs special permission which are being set
by following example policies.

To allow publishing of events via Backbone the AWS user needs the right to publish to the SNS topic.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "Stmt1474367067000",
            "Effect": "Allow",
            "Action": [
                "sns:*"
            ],
            "Resource": [
                "{topicArnToPublishTo}"
            ]
        }
    ]
}
```

To allow Backbone consuming events from the configured queue the AWS user needs full permissions for the
queue that should be created for consuming messages as well as permissions to subscribe and unsubscribe
the queue to the configured SNS topics.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "Stmt1474984885000",
            "Effect": "Allow",
            "Action": [
                "sqs:*"
            ],
            "Resource": [
                "arn:aws:sqs:eu-central-1:{awsAccountId}:{queueName}"
            ]
        },
        {
            "Sid": "Stmt1474985033000",
            "Effect": "Allow",
            "Action": [
                "sns:Subscribe",
                "sns:Unsubscribe"
            ],
            "Resource": [
                "{topicArn1}",
                "{topicArn2}"
            ]
        }
    ]
}
```

### JSON Modules

The core module of backbone is completely independent of a JSON library (because there are many around in the JVM ecosystem).
You can configure which library you want to use by adding one of the following to the classpath. 

```scala
libraryDependencies += "com.firstbird" %% "backbone-circe"      % "X.Y.Z"
libraryDependencies += "com.firstbird" %% "backbone-play-json"  % "X.Y.Z"
libraryDependencies += "com.firstbird" %% "backbone-gson"       % "X.Y.Z"
```

## Running the Tests

Run the tests from sbt with:
```
test
```

## Feedback
We very much appreciate feedback, please open an issue and/or create a PR.

## Contributors

* Pedro Dias
* Fabian Grutsch
* Georgi Lichev
* [Daniel Pfeiffer](https://github.com/dpfeiffer)
