package com.webpagenavigator

import org.jsoup.nodes.Document
import java.net.URI

class LinkExtractor {
	fun extractLinks(doc: Document, baseUrl: String): List<String> {
		return doc.getElementsByTag("a").mapNotNull { link ->
			val absHref: String = link.attr("abs:href")
			val modifiedHref = modifyUrl(absHref, baseUrl)
			modifiedHref.ifEmpty {
				null
			}
		}
	}

	private fun modifyUrl(url: String?, currentUrl: String?): String {
		val uri = URI(url ?: return "")
		val host = uri.host ?: ""
		return if (uri.fragment != null && host == URI(currentUrl ?: return "").host) {
			val scheme = uri.scheme ?: ""
			val path = uri.path ?: ""
			val query = if (uri.query.isNullOrEmpty()) "" else "?${uri.query}"
			"$scheme://$host$path$query"
		} else {
			url
		}
	}
}