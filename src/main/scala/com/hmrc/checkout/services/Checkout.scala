package com.hmrc.checkout.services

class Checkout(priceLookup: String => Option[BigDecimal]) {
  def price(items: Seq[String]): (BigDecimal, Set[String]) =
    items.foldLeft((BigDecimal(0), Set.empty[String]))((acc, item) => {
      val (runningTotal, notFound) = acc

      priceLookup(item) match {
        case Some(price) => (runningTotal + price, notFound)
        case None => (runningTotal, notFound + item)
      }
    })
}
