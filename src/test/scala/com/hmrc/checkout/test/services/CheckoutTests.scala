package com.hmrc.checkout.test.services

import com.hmrc.checkout.services.Checkout
import org.scalatest.{FlatSpec, Matchers}

class CheckoutTests extends FlatSpec with Matchers {
  private val prices = Map("apple" -> BigDecimal(60), "orange" -> BigDecimal(25))
  private val priceLookup: String => Option[BigDecimal] = item => prices.get(item.toLowerCase.trim)

  private val discountFunctions = Map[String, (Int, BigDecimal) => BigDecimal](
    "apple" -> ((quantity, price) => if (quantity > 1) (quantity % 2) * price else 0),
    "orange" -> ((quantity, price) => Math.floor(quantity / 3) * price)
  )

  private val discounter: String => (Int, BigDecimal) => BigDecimal =
    discountFunctions.getOrElse(_, (_, _) => BigDecimal(0))

  it should "return nothing for an empty basket" in {
    val items = Seq()
    val checkout = new Checkout(priceLookup, discounter)

    val (total, notFound) = checkout.price(items)

    total should be (0)
    notFound should be (Set.empty)
  }

  it should "calculate the price of items" in {
    val items = Seq("Apple", "Orange")
    val checkout = new Checkout(priceLookup, discounter)

    val (total, notFound) = checkout.price(items)

    total should be (85)
    notFound should be (Set.empty)
  }

  it should "handle repeated items" in {
    val items = Seq("Apple", "Orange", "Apple")
    val checkout = new Checkout(priceLookup, discounter)

    val (total, notFound) = checkout.price(items)

    total should be (145)
    notFound should be (Set.empty)
  }

  it should "return a list of unknown items" in {
    val items = Seq("Apple", "Hedgehog")
    val checkout = new Checkout(priceLookup, discounter)

    val (total, notFound) = checkout.price(items)

    total should be (60)
    notFound should be (Set("Hedgehog"))
  }

  it should "apply discount functions (buy one get one free)" in {
    val items = Seq("Apple", "Apple", "Apple")
    val checkout = new Checkout(priceLookup, discounter)

    val (total, notFound) = checkout.price(items)

    total should be (120)
    notFound should be (Set.empty)
  }

  it should "apply discount functions (3 for 2)" in {
    val items = Seq("Orange", "Orange", "Orange")
    val checkout = new Checkout(priceLookup, discounter)

    val (total, notFound) = checkout.price(items)

    total should be (50)
    notFound should be (Set.empty)
  }
}
