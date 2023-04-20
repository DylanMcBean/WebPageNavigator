package com.webpagenavigator

suspend fun main() {
	Logger.init()
	Logger.application("Initializing components")

	val crawler = Crawler()
	val linkExtractor = LinkExtractor()
	val linkTracker = LinkTracker(AppConfig.crawler.baseUrl)
	linkTracker.initialize()

	Logger.application("Start processing URLs")
	while (true) {
		val nextUrl = linkTracker.getNextLink() ?: break
		val percentage = String.format("%5.2f", linkTracker.percentage()) // Format the percentage with padding
		Logger.debug("$percentage% - Processing URL: $nextUrl")
		val clicks = linkTracker.getInternalVisitedSites()[nextUrl] ?: continue
		processUrl(nextUrl, clicks + 1, crawler, linkExtractor, linkTracker)
	}

	Logger.info("${linkTracker.getAllLinks().size} Total Links Found")
	Logger.info("${linkTracker.getInternalVisitedSites().size} Internal")
	Logger.info("${linkTracker.getExternalVisitedSites().size} External")

	Logger.application("Saving Files")
	linkTracker.saveLinksToJson("${AppConfig.outputFolder}links.json")
	//linkTracker.savePageLinks("${AppConfig.outputFolder}Obsidian/")

	Logger.close()
}

suspend fun processUrl(url: String, clicks: Int, crawler: Crawler, linkExtractor: LinkExtractor, linkTracker: LinkTracker) {
	val doc = crawler.fetchDocument(url)

	val linkTypes = arrayOf(0,0)

	doc?.let { document ->
		val links = linkExtractor.extractLinks(document, url)
		links.distinct().forEach { link ->
			linkTypes[linkTracker.addLink(link.removeSuffix("/"), url, clicks)]++
		}
	}

	if (linkTypes[0] == 0)
	{
		Logger.info("${linkTypes[1]} External links found")
		return
	} else if (linkTypes[1] == 0)
	{
		Logger.info("${linkTypes[0]} Internal links found")
		return
	}
	Logger.info("${linkTypes[0]} Internal, ${linkTypes[1]} External links found")
}