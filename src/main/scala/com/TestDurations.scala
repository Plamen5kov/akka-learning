package com

import scala.annotation.tailrec
import scala.concurrent.duration.Duration.Zero
import scala.concurrent.duration.{DAYS, DurationInt, FiniteDuration, HOURS, MILLISECONDS, MINUTES, SECONDS, TimeUnit}

object TestDurations extends App {

  val `aggregates.healthchecker.throttle.rate`: Int = 20 //Number of allowed requests per period
  val `aggregates.healthchecker.throttle.period`: FiniteDuration = 1.day //Duration of throttling period
  //    val `aggregates.healthchecker.throttle.period`: FiniteDuration = 1.hour //Duration of throttling period
  //  val `aggregates.healthchecker.throttle.period`: FiniteDuration = 60000.milliseconds //Duration of throttling period
  //    val `aggregates.healthchecker.throttle.period`: FiniteDuration = 2.minutes //Duration of throttling period
  //      val `aggregates.healthchecker.throttle.period`: FiniteDuration = 30.seconds //Duration of throttling period
  //  val `aggregates.healthchecker.throttle.period`: FiniteDuration = 120.seconds //Duration of throttling period
  val `aggregates.healthchecker.buffer.factor`: Int = 10

  def calculateBufferSize(): Unit = {
    val minutesMultiplier: Double = getMinutesMultiplicationFactor(
      `aggregates.healthchecker.throttle.period`.length,
      `aggregates.healthchecker.throttle.period`.unit
    )
    val ratePerMinute = `aggregates.healthchecker.throttle.rate` / minutesMultiplier
    val bufferSizeFromRatio = Math.ceil(ratePerMinute) * `aggregates.healthchecker.buffer.factor`
    println(bufferSizeFromRatio)
    val bufferSize = Math.min(bufferSizeFromRatio, 4096)
    println(bufferSize)
  }

  /**
   * Converts a rate expressed as count-per-period to the equivalent rate
   * expressed as count-per-minute. For example, if the input rate is
   * a count of 50 items per 30-second duration, then the expected return
   * value is 100, indicating a rate of 100 items per minute. When a fractional
   * value occurs in the conversion, this function uses `math.ceil` to
   * find the next higher whole integer value. For example, if the parameters
   * given are 1000 items per 3-minute period, the return value will be 334.
   *
   * @param count  The initial count.
   * @param period The initial time period.
   * @return The adjusted count for a one-minute time period.
   */
  def convertToCountPerMinute(count: Int, period: FiniteDuration): Int = {
    import scala.concurrent.duration.FiniteDuration
    require(count >= 0)
    require(period > Zero)
    val ratio: Double = FiniteDuration(1, MINUTES) / period
    (count * ratio).ceil.toInt
  }

  @tailrec
  private def getMinutesMultiplicationFactor(significantPart: Double, unitOfMeasure: TimeUnit): Double = {
    unitOfMeasure match {
      case DAYS => getMinutesMultiplicationFactor(24 * significantPart, HOURS)
      case HOURS => getMinutesMultiplicationFactor(60 * significantPart, MINUTES)
      case MINUTES => significantPart
      case SECONDS => getMinutesMultiplicationFactor(significantPart / 60.toDouble, MINUTES)
      case MILLISECONDS => getMinutesMultiplicationFactor(significantPart / 1000.toDouble, SECONDS)
    }
  }

  println(convertToCountPerMinute(50, FiniteDuration(1, "minute"))) //should equal (50)
  println(convertToCountPerMinute(50, FiniteDuration(30, "seconds"))) //should equal (100)
  println(convertToCountPerMinute(50, FiniteDuration(2, "minutes"))) //should equal (25)
  println(convertToCountPerMinute(1, FiniteDuration(1, "millisecond"))) //should equal (60000)
  println(convertToCountPerMinute(1000, FiniteDuration(3, "minutes"))) //should equal (334)
  //  println(getMinutesMultiplicationFactor(MILLISECONDS))

  //  calculateBufferSize()
}
