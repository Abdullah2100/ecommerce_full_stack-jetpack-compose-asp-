package com.example.eccomerce_app.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.toColorInt
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.e_commercompose.R
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import com.example.eccomerce_app.data.Room.Model.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.util.Locale
import java.util.Properties


object General {

    val currentLocal = MutableStateFlow<String?>(null)

    val BASED_URL = Secrets.getUrl()


    fun encryptionFactory(databaseName: String): SupportFactory {
        val passPhraseBytes = SQLiteDatabase.getBytes(databaseName.toCharArray())
        return SupportFactory(passPhraseBytes)
    }

    val satoshiFamily = FontFamily(
        Font(R.font.satoshi_bold, FontWeight.Bold),
        Font(R.font.satoshi_medium, FontWeight.Medium),
        Font(R.font.satoshi_regular, FontWeight.Normal)
    )

    fun handlingImageForCoil(imageUrl: String?, context: Context): ImageRequest? {
        if (imageUrl == null) return null
        return when (imageUrl.endsWith(".svg")) {
            true -> {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build()
            }

            else -> {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .build()
            }

        }
    }


    fun convertColorToInt(value: String): Color? {
        return try {
            Color(value.toColorInt())
        } catch (ex: Exception) {
            null
        }
    }

    fun whenLanguageUpdateDo(locale: String, context: Context) {
//        val locale = Locale(locale)
//        Locale.setDefault(locale)
//
//        val config = context.resources.configuration
//        config.setLocale(locale)
//        config.setLayoutDirection(locale)
//
//        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun convertPriceToAnotherCurrency(price: Double,productSymbol:String,selectedCurrency: Currency?,currencies:List<Currency>?): Double{
        if(currencies.isNullOrEmpty())return price
        if(selectedCurrency==null)return price

        val productCurrency = currencies.firstOrNull{it->it.symbol==productSymbol}

        return when  {
         productCurrency!=selectedCurrency ->{
                  when{
                      selectedCurrency.isDefault ->{
                          val changer = (price/productCurrency!!.value)
                          changer
                      }
                      productCurrency!!.isDefault->{
                          val changer = (price*selectedCurrency.value)
                          changer
                      }
                      else ->{
                          val defaultCurrency = currencies.firstOrNull{it->it.isDefault}
                          val changer = (price/(defaultCurrency?.value?:1))*selectedCurrency.value
                          changer
                      }
                  }
          }
        else-> price
    }
    }

    fun getProperty(key:String):String?{
        val property = Properties()
        val propertyFile = Thread.currentThread().contextClassLoader?.getResourceAsStream("local.properties")
        if(propertyFile != null)
        {
            property.load(propertyFile)
            return  property.getProperty(key)
        }
        return  null
    }
    fun Uri.toCustomFil(context: Context): File? {
        var file: File? = null

        try {
            val resolver = context.contentResolver
            resolver.query(this, null, null, null, null)
                .use { cursor ->
                    if (cursor == null) throw Exception("could not accesses Local Storage")

                    cursor.moveToFirst()
                    val column = arrayOf(MediaStore.Images.Media.DATA)
                    val filePath = cursor.getColumnIndex(column[0])
                    file = File(cursor.getString(filePath))

                }
            return file
        } catch (e: Exception) {
            throw e
        }
    }


    fun LazyListState.reachedBottom(): Boolean {
        val visibleItemsInfo = layoutInfo.visibleItemsInfo // Get the visible items
        return if (layoutInfo.totalItemsCount == 0) {
            false // Return false if there are no items
        } else {
            val lastVisibleItem = visibleItemsInfo.last() // Get the last visible item
            val viewportHeight =
                layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset // Calculate the viewport height

            // Check if the last visible item is the last item in the list and fully visible
            // This indicates that the user has scrolled to the bottom
            (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                    lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
        }
    }

    fun LocalDateTime.toCustomString(isTime: Boolean = false): String {
        return when (isTime) {
            true -> {
                if (this.hour == 0) ""
                else "${this.hour}:${this.minute}"
            }

            else -> {
                if(year==1)return ""
                "${day}/${month.number}/${year}"
            }
        }

    }

}
