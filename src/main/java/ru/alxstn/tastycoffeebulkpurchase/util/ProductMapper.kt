package ru.alxstn.tastycoffeebulkpurchase.util

import ru.alxstn.tastycoffeebulkpurchase.entity.Product
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage
import ru.alxstn.tastycoffeebulkpurchase.model.api.categories.CategoriesResponse
import ru.alxstn.tastycoffeebulkpurchase.model.api.categories.CategoryData
import ru.alxstn.tastycoffeebulkpurchase.model.api.product.ProductsResponse
import java.math.BigDecimal

class ProductMapper {

    companion object {
        @JvmStatic
        fun map(categoriesResp: CategoriesResponse, productsResp: ProductsResponse): List<Product> {
            val categories = categoriesResp.data
            val products = productsResp.data

            val productsByCategory = products.groupBy { it.category_id }
            return productsByCategory.flatMap {
                val rawProducts = it.value
                rawProducts.flatMap { rawProduct ->
                    val cats = categories.buildCategories(it.key)
                    rawProduct.offers.map { offer ->
                        Product(
                            rawProduct.name,
                            BigDecimal(offer.price),
                            rawProduct?.label?.name ?: "",
                            if (offer.isIs_coffee_or_tea) findPackage(offer.weight) else ProductPackage(""),
                            cats[0],
                            cats[1],
                            offer.type?.toProductForm(),
                            offer.type?.toGrindableFlag() ?: false
                        ).apply { isActual = !rawProduct.isNot_available }
                    }
                }
            }.filter { it.isActual }
        }

        private fun List<CategoryData>.buildCategories(id: Int): List<String> {
            val finalCategory = getCategoryById(id)
            val categoryList = listOf(finalCategory).toMutableList()

            var parentId: Int? = finalCategory.parentId
            while (parentId != null) {
                val parentCategory = getCategoryById(parentId)
                categoryList.add(parentCategory)
                parentId = parentCategory.parentId
            }
            return categoryList.map { it.name }
        }

        private fun List<CategoryData>.getCategoryById(id: Int): CategoryData {
            return first { it.id == id }
        }

        private fun String?.toProductForm(): String {
            return when (this) {
                "bean_coffee" -> "Зерно"
                "ground_coffee" -> "Молотый"
                else -> ""
            }
        }

        private fun String?.toGrindableFlag(): Boolean {
            return when (this) {
                "bean_coffee" -> true
                else -> false
            }
        }

        private fun findPackage(weight: Int) =
            availablePackages.firstOrNull { it.weight == weight.toDouble() / 1000 }

        private val availablePackages = listOf(
            ProductPackage("Упаковка 100 г"),
            ProductPackage("Упаковка 250 г"),
            ProductPackage("Упаковка 1 кг"),
            ProductPackage("Упаковка 2 кг")
        )
    }
}