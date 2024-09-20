package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator

import org.springframework.stereotype.Service
import ru.alxstn.tastycoffeebulkpurchase.entity.Session
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService
import java.lang.System

@Service
class ImprovedTextFileCreatorService(
    val purchaseManagerService: PurchaseManagerService
): OrderCreatorService {

    override fun placeFullOrder(session: Session) {
        val allPurchases = purchaseManagerService.findAllPurchasesInSession(session)
        val customerPurchases = allPurchases.groupBy({ it.customer }, { it })
        val report = StringBuilder()

        customerPurchases.forEach { it ->
            val customer = it.key
            val purchases = it.value.filter { it.count > 0 }
            val productToCount = purchases.groupBy({ it.product }, { it.count }).mapValues { v -> v.value.sum() }
            val customerCategories = productToCount.map { it.key.productCategory }.toSet()
            val categoriesToSubcategories = customerCategories.associateWith { cat ->
                val categoryProducts = purchases.filter { p -> p.product.productCategory == cat }
                val categorySubcategories = categoryProducts.map { it.product.productSubCategory }
                categorySubcategories.toSet()
            }
            val newLine = System.lineSeparator()
            val customerReport = StringBuilder("$customer$newLine")

            categoriesToSubcategories.forEach { entry ->
                val category = entry.key
                val subcategories = entry.value

                customerReport.append("\t$category$newLine")
                subcategories.forEach { subcategory ->
                    customerReport.append("\t\t$subcategory$newLine")
                    productToCount.filter { it.key.productCategory == category && it.key.productSubCategory == subcategory }
                        .takeIf { it.isNotEmpty() }
                        ?.forEach { productToCount ->
                            val product = productToCount.key
                            val productCount = productToCount.value
                            val productTitle = ProductCaptionBuilder(product).createNamePackageFormView();
                            customerReport.append("\t\t\t$productCount шт. - $productTitle $newLine")
                        }
                }
            }

            customerReport.append("$newLine$newLine")
            report.append(customerReport)
        }
        TextReportSaver.saveReport(session, report.toString(), this.javaClass.simpleName, null)
    }

    override fun placeOrderWithProductFilter(productTypes: SessionProductFilters?) {
        TODO("Not yet implemented")
    }

}