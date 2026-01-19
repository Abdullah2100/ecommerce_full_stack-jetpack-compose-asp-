package com.example.eccomerce_app.data.repository


import com.example.e_commercompose.model.ProductVarientSelection
import com.example.eccomerce_app.dto.ProductDto
import com.example.eccomerce_app.util.GeneralValue
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.data.NetworkCallHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class ProductRepository(val client: HttpClient)  {

    suspend fun getProduct(pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/Product/all/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
                }

                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun getProductByCategoryId(
        categoryId: UUID,
        pageNumber: Int
    ): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/Product/category/${categoryId}/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
                }

                HttpStatusCode.Companion.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun getProduct(storeId: UUID, pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/Product/${storeId}/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
                }

                HttpStatusCode.Companion.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun getProduct(
        storeId: UUID,
        subCategory: UUID,
        pageNumber: Int
    ): NetworkCallHandler {
        return try {
            val fullUrl =
                Secrets.getUrl() + "/Product/${storeId}/${subCategory}/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<List<ProductDto>>())
                }

                HttpStatusCode.Companion.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }


    suspend fun createProduct(
        name: String,
        description: String,
        thumbnail: File,
        subcategoryId: UUID,
        storeId: UUID,
        price: Int,
        symbol: String,
        productVariants: List<ProductVarientSelection>,
        images: List<File>
    ): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/Product"
            val result = client.post(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", name)
                            append("description", description)
                            append(
                                key = "Thumbnail", // Must match backend expectation
                                value = thumbnail.readBytes(),
                                headers = Headers.Companion.build {
                                    append(
                                        HttpHeaders.ContentType,
                                        "image/${thumbnail.extension}"
                                    )
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${thumbnail.name}"
                                    )
                                }
                            )

                            append("SubcategoryId", subcategoryId.toString())
                            append("StoreId", storeId.toString())
                            append("Price", price)
                            append("symbol", symbol)
                            if (productVariants.isNotEmpty())
                                productVariants.forEachIndexed { it, value ->
                                    append("productVarients[${it}].name", value.name)
                                    append("productVarients[${it}].percentage", value.percentage!!)
                                    append(
                                        "productVarients[${it}].valientId",
                                        value.variantId.toString()
                                    )
                                }
                            images.forEachIndexed { it, value ->
                                append(
                                    key = "images", // Must match backend expectation
                                    value = value.readBytes(),
                                    headers = Headers.Companion.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${value.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${value.name}"
                                        )
                                    }
                                )
                            }


                        }
                    )
                )
            }

            when (result.status) {
                HttpStatusCode.Companion.Created -> {
                    NetworkCallHandler.Successful(result.body<ProductDto>())
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun updateProduct(
        id: UUID,
        name: String?,
        description: String?,
        thumbnail: File?,
        subcategoryId: UUID?,
        storeId: UUID,
        price: Int?,
        symbol: String?,
        productVariants: List<ProductVarientSelection>?,
        images: List<File>?,
        deletedProductVariants: List<ProductVarientSelection>?,
        deleteImages: List<String>?
    ): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/Product"
            val result = client.put(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("id", id.toString())
                            if (name != null)
                                append("Name", name)
                            if (description != null)
                                append("description", description)
                            if (thumbnail != null)
                                append(
                                    key = "Thumbnail", // Must match backend expectation
                                    value = thumbnail.readBytes(),
                                    headers = Headers.Companion.build {
                                        append(
                                            HttpHeaders.ContentType,
                                            "image/${thumbnail.extension}"
                                        )
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${thumbnail.name}"
                                        )
                                    }
                                )
                            if (subcategoryId != null)
                                append("SubcategoryId", subcategoryId.toString())
                            append("StoreId", storeId.toString())

                            if (price != null)
                                append("price", price)

                            if(symbol !=null)
                                append("symbol", symbol)

                            if (!productVariants.isNullOrEmpty())
                                productVariants.forEachIndexed { it, value ->
                                    append("productVariants[${it}].name", value.name)
                                    append("productVariants[${it}].percentage", value.percentage!!)
                                    append(
                                        "productVariants[${it}].variantId",
                                        value.variantId.toString()
                                    )
                                }
                            if (!deletedProductVariants.isNullOrEmpty())
                                deletedProductVariants.forEachIndexed { it, value ->
                                    append("deletedProductVariants[${it}].name", value.name)
                                    append(
                                        "deletedProductVariants[${it}].percentage",
                                        value.percentage!!
                                    )
                                    append(
                                        "deletedProductVariants[${it}].valientId",
                                        value.variantId.toString()
                                    )

                                }

                            if (!deleteImages.isNullOrEmpty())
                                deleteImages.forEachIndexed { it, value ->
                                    val startIndex = "staticFiles"
                                    val indexAt = value.indexOf("staticFiles")
                                    append(
                                        "deletedimages[${it}]", value.substring(
                                            indexAt + startIndex.length,
                                            value.length
                                        )
                                    )
                                }


                            if (!images.isNullOrEmpty())
                                images.forEachIndexed { it, value ->
                                    append(
                                        key = "Images", // Must match backend expectation
                                        value = value.readBytes(),
                                        headers = Headers.Companion.build {
                                            append(
                                                HttpHeaders.ContentType,
                                                "image/${value.extension}"
                                            )
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=${value.name}"
                                            )
                                        }
                                    )
                                }


                        }
                    )
                )
            }

            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<ProductDto>())
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }

    suspend fun deleteProduct(storeId: UUID, productId: UUID): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/Product/${storeId}/${productId}"
            val result = client.delete(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.Companion.NoContent -> {
                    NetworkCallHandler.Successful(true)
                }

                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }


}