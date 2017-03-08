package com.hmrc.checkout.test.services

import com.hmrc.checkout.services.Checkout
import org.scalatest.{FlatSpec, Matchers}

class CheckoutTests extends FlatSpec with Matchers {
  private val prices = Map("apple" -> BigDecimal(60), "orange" -> BigDecimal(25))

  it should "return nothing for an empty basket" in {
    val items = Seq()
    val checkout = new Checkout(item => prices.get(item.toLowerCase.trim))

    val (total, notFound) = checkout.price(items)

    total should be (0)
    notFound should be (Set.empty)
  }

  it should "calculate the price of items" in {
    val items = Seq("Apple", "Orange")
    val checkout = new Checkout(item => prices.get(item.toLowerCase.trim))

    val (total, notFound) = checkout.price(items)

    total should be (85)
    notFound should be (Set.empty)
  }

  it should "handle repeated items" in {
    val items = Seq("Apple", "Orange", "Apple")
    val checkout = new Checkout(item => prices.get(item.toLowerCase))

    val (total, notFound) = checkout.price(items)

    total should be (145)
    notFound should be (Set.empty)
  }

  it should "return a list of unknown items" in {
    val items = Seq("Apple", "Hedgehog")
    val checkout = new Checkout(item => prices.get(item.toLowerCase.trim))

    val (total, notFound) = checkout.price(items)

    total should be (60)
    notFound should be (Set("Hedgehog"))
  }
}
