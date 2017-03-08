package com.hmrc.checkout.services

import com.hmrc.checkout.services.Checkout.{Item, Price, Quantity}

class Checkout(priceLookup: Item => Option[Price], discounter: String => (Quantity, Price) => Price) {
  def price(items: Seq[Item]): (Price, Set[Item]) = {
    val (found, notFound) = lookupPrices(items)
    val quantityAndPriceByItem = totalQuantities(found)
    val priceAndDiscountByItem = getTotalsAndDiscounts(quantityAndPriceByItem)

    val total = priceAndDiscountByItem.foldLeft(BigDecimal(0)) {
      case (runningTotal, (_, (price, discount))) => runningTotal + price - discount
    }

    (total, notFound)
  }

  private def lookupPrices(items: Seq[Item]) = items
    .map(item => (item, priceLookup(item)))
    .partition {
      case (_, price) => price.isDefined
    } match {
      case (found, notFound) =>
        val foundWithPrice = found.map {
          case (item, Some(price)) => (item, price)
          case (item, _) => (item, BigDecimal(0))
        }

        val itemsNotFound = notFound.map {
          case (item, _) => item
        }

        (foundWithPrice, itemsNotFound.toSet)
    }

  private def totalQuantities(pricedItems: Seq[(Item, Price)]) = pricedItems.groupBy {
    case ((item, _)) => item
  }.mapValues {
    case items @ (_, price) :: _ => (items.length, price)
  }

  private def getTotalsAndDiscounts(quantityAndPriceByItem: Map[Item, (Quantity, Price)]) = quantityAndPriceByItem.map {
    case (item, (quantity, price)) =>
      val total: Price = quantity * price
      val discount: Price = discounter(item.toLowerCase.trim)(quantity, price)

      item -> (total, discount)
  }
}

object Checkout {
  type Quantity = Int
  type Price = BigDecimal
  type Item = String
}
