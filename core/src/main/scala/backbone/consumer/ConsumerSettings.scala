package backbone.consumer

import java.util.{List => JList, Optional => JOption}

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters

object ConsumerSettings {

  def create(
      topics: JList[String],
      queue: String,
      parallelism: Integer,
      consumeWithin: JOption[Limitation]
  ): ConsumerSettings = {
    apply(topics.asScala.toList, queue, parallelism, OptionConverters.toScala(consumeWithin))

  }
  def create(
      topics: JList[String],
      queue: String,
      parallelism: Integer,
      consumeWithin: JOption[Limitation],
      receiveSettings: ReceiveSettings
  ): ConsumerSettings =
    apply(topics.asScala.toList, queue, parallelism, OptionConverters.toScala(consumeWithin), receiveSettings)

}

/**
 *
 * @param topics          a list of topics to subscribe to
 * @param queue           the name of a queue to consume from
 * @param parallelism     number of concurrent messages in process
 * @param consumeWithin   optional limitation when backbone should stop consuming
 * @param receiveSettings settings for the SQS Source from alpakka
 *
 */
case class ConsumerSettings(
    topics: List[String],
    queue: String,
    parallelism: Int = 1,
    consumeWithin: Option[Limitation] = None,
    receiveSettings: ReceiveSettings = ReceiveSettings.Defaults
)
