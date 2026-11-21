package pl.miloszgilga.jargonaut.postprocessor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.miloszgilga.jargonaut.core.event.BuildEventBus;
import pl.miloszgilga.jargonaut.core.processor.SiteBuildContext;

import java.util.Locale;

public class ExternalLinkHtmlPostProcessor extends JsoupHtmlPostProcessor {
  public ExternalLinkHtmlPostProcessor(BuildEventBus eventBus) {
    super(eventBus);
  }

  @Override
  protected String jsoupProcess(Document doc, Locale locale, SiteBuildContext context) {
    final String domain = context.getConfig().getSeo().getDomain();
    final Elements links = doc.select("a[href^=http]");
    for (final Element link : links) {
      final String href = link.attr("href");
      if (domain != null && !domain.isBlank() && href.startsWith(domain)) {
        continue;
      }
      link.attr("target", "_blank");
      final String existingRel = link.attr("rel");
      if (existingRel.isEmpty()) {
        link.attr("rel", "noopener noreferrer");
      } else if (!existingRel.contains("noopener")) {
        link.attr("rel", existingRel.trim() + " noopener noreferrer");
      }
    }
    return doc.outerHtml();
  }
}
