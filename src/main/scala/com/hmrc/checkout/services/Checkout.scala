package com.hmrc.checkout.services

class Checkout(priceLookup: String => Option[BigDecimal]) {
  def price(items: Seq[String]): (BigDecimal, Set[String]) = {
    val (found, notFound) = items
      .map(item => (item, priceLookup(item)))
      .partition {
        case (_, price) => price.isDefined
      }

    val total = found.foldLeft(BigDecimal(0)) {
      case (runningTotal, (_, Some(price))) => runningTotal + price
    }

    val notFoundItems = notFound.map { case (item, _) => item }.toSet

    (total, notFoundItems)
  }
}
