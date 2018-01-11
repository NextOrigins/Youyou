package com.neworld.youyou.utils

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.Reader

/**
 * @author by user on 2018/1/10.
 */
object XmlHelper {

	@Throws(Exception::class)
	fun parse(ips: Reader) {
		val parser = Xml.newPullParser()
		parser.setInput(ips)
		var type = parser.eventType
		while (type != XmlPullParser.END_DOCUMENT) {
			LogUtils.E("ttttttt text : ${parser.text}")
			LogUtils.E("tag : ${parser}")
			when (type) {
				XmlPullParser.START_DOCUMENT -> {
					LogUtils.E("----start----")
				}
				XmlPullParser.START_TAG -> {
					LogUtils.E("----start tag value----")
				}
				XmlPullParser.END_TAG -> {
					LogUtils.E("----end tag----")
				}
			}
			type = parser.next()
		}
	}
}