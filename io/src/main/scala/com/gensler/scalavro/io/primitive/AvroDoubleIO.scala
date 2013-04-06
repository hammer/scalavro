package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.primitive.AvroDouble
import com.gensler.scalavro.error.{AvroSerializationException, AvroDeserializationException}

import scala.util.{Try, Success, Failure}
import java.io.{InputStream, OutputStream}

object AvroDoubleIO extends AvroDoubleIO

trait AvroDoubleIO extends AvroTypeIO[Double] {

  def avroType = AvroDouble

  def asGeneric(value: Double): Double = value

  def fromGeneric(obj: Any): Double = obj.asInstanceOf[Double]

  def write(value: Double, stream: OutputStream) = {
    val bits: Long = java.lang.Double doubleToLongBits value

    stream write Array(
      bits.toByte,        // little endian
      (bits >>> 8).toByte,
      (bits >>> 16).toByte,
      (bits >>> 24).toByte,
      (bits >>> 32).toByte,
      (bits >>> 40).toByte,
      (bits >>> 48).toByte,
      (bits >>> 56).toByte
    )
  }

  def read(stream: InputStream) = Try {
    val bytes = Array.ofDim[Byte](8)
    val bytesRead = stream read bytes
    if (bytesRead != 8) throw new AvroDeserializationException[Long]

    java.lang.Double.longBitsToDouble(
      (bytes(0)  & 0xFF).toLong        |
      ((bytes(1) & 0xFF).toLong << 8)  |
      ((bytes(2) & 0xFF).toLong << 16) |
      ((bytes(3) & 0xFF).toLong << 24) |
      ((bytes(4) & 0xFF).toLong << 32) |
      ((bytes(5) & 0xFF).toLong << 40) |
      ((bytes(6) & 0xFF).toLong << 48) |
      ((bytes(7) & 0xFF).toLong << 56)
    )
  }

}