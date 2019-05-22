package io.github.sds100.keymapper.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import io.github.sds100.keymapper.interfaces.IContext

/**
 * Created by sds100 on 31/12/2018.
 */

/**
 * Get a resource string
 */

// Using varargs doesn't work since prints [LJava.lang.object@32f...etc
fun Context.str(@StringRes resId: Int, formatArg: Any? = null): String = getString(resId, formatArg)

fun IContext.str(@StringRes resId: Int, formatArgs: Any? = null): String = ctx.getString(resId, formatArgs)
fun View.str(@StringRes resId: Int, formatArgs: Any? = null): String = context.str(resId, formatArgs)

fun Context.bool(@BoolRes resId: Int): Boolean = resources.getBoolean(resId)

fun View.bool(attributeSet: AttributeSet, @StyleableRes styleableId: IntArray, @StyleableRes attrId: Int) =
        context.bool(attributeSet, styleableId, attrId)

/**
 * Get a boolean from an attribute
 */
fun Context.bool(
        attributeSet: AttributeSet,
        @StyleableRes styleableId: IntArray,
        @StyleableRes attrId: Int,
        defaultValue: Boolean = false
): Boolean {
    val typedArray = theme.obtainStyledAttributes(attributeSet, styleableId, 0, 0)

    val attrValue: Boolean?

    try {
        attrValue = typedArray.getBoolean(attrId, defaultValue)
    } finally {
        typedArray.recycle()
    }

    return attrValue!!
}

/**
 * Get a string from an attribute
 */
fun Context.str(attributeSet: AttributeSet, @StyleableRes styleableId: IntArray, @StyleableRes attrId: Int): String {
    val typedArray = theme.obtainStyledAttributes(attributeSet, styleableId, 0, 0)

    val attrValue: String?

    try {
        attrValue = typedArray.getString(attrId)
    } finally {
        typedArray.recycle()
    }

    return attrValue!!
}

fun View.str(attributeSet: AttributeSet, @StyleableRes styleableId: IntArray, @StyleableRes attrId: Int) =
        context.str(attributeSet, styleableId, attrId)

/**
 * Get a resource drawable. Can be safely used to get vector drawables on pre-lollipop.
 */
fun Context.drawable(@DrawableRes resId: Int): Drawable {
    return AppCompatResources.getDrawable(this, resId)!!
}

fun View.drawable(@DrawableRes resId: Int): Drawable = context.drawable(resId)
fun IContext.drawable(@DrawableRes resId: Int): Drawable = ctx.drawable(resId)

fun Context.color(@ColorRes resId: Int): Int = ContextCompat.getColor(this, resId)
fun View.color(@ColorRes resId: Int): Int = context.color(resId)

fun Context.int(@IntegerRes resId: Int) = resources.getInteger(resId)

/**
 * @return If the setting can't be found, it returns null
 */
inline fun <reified T> Context.getSystemSetting(name: String): T? {
    return try {
        when (T::class) {

            Int::class -> Settings.System.getInt(contentResolver, name) as T?
            String::class -> Settings.System.getString(contentResolver, name) as T?
            Float::class -> Settings.System.getFloat(contentResolver, name) as T?
            Long::class -> Settings.System.getLong(contentResolver, name) as T?

            else -> {
                throw Exception("Setting type ${T::class} is not supported")
            }
        }
    } catch (e: Settings.SettingNotFoundException) {
        Logger.write(
                this,
                isError = true,
                title = "Exception",
                message = "SettingNotFoundException: $name in ContentUtils")
        null
    }
}

inline fun <reified T> Context.putSystemSetting(name: String, value: T) {

    when (T::class) {

        Int::class -> Settings.System.putInt(contentResolver, name, value as Int)
        String::class -> Settings.System.putString(contentResolver, name, value as String)
        Float::class -> Settings.System.putFloat(contentResolver, name, value as Float)
        Long::class -> Settings.System.putLong(contentResolver, name, value as Long)

        else -> {
            throw Exception("Setting type ${T::class} is not supported")
        }
    }
}