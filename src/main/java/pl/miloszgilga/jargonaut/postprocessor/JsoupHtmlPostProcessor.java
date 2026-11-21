package pl.miloszgilga.jargonaut.postprocessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pl.miloszgilga.jargonaut.core.event.BuildEventBus;
import pl.miloszgilga.jargonaut.core.processor.SiteBuildContext;

import java.util.Locale;

public abstract class JsoupHtmlPostProcessor extends GenericHtmlPostProcessor {
  public JsoupHtmlPostProcessor(BuildEventBus eventBus) {
    super(eventBus);
  }

  @Override
  protected final String safetyProcess(String rawHtml, Locale locale, SiteBuildContext context) {
    final Document document = Jsoup.parse(rawHtml);
    document.outputSettings().prettyPrint(false);
    return jsoupProcess(document, locale, context);
  }

  protected abstract String jsoupProcess(Document doc, Locale locale, SiteBuildContext context);
}
