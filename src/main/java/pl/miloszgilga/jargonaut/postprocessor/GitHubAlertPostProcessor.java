package pl.miloszgilga.jargonaut.postprocessor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.miloszgilga.jargonaut.core.event.BuildEventBus;
import pl.miloszgilga.jargonaut.core.processor.SiteBuildContext;

import java.util.Locale;

public class GitHubAlertPostProcessor extends JsoupHtmlPostProcessor {
  public GitHubAlertPostProcessor(BuildEventBus eventBus) {
    super(eventBus);
  }

  @Override
  protected String jsoupProcess(Document doc, Locale locale, SiteBuildContext context) {
    final Elements alertTitles = doc.select(".markdown-alert__title");
    for (final Element title : alertTitles) {
      final String alertType = title.text();
      if (alertType.isEmpty()) {
        continue;
      }
      final String translated = context.getDictionaries().resolve(locale, "alert." + alertType);
      if (translated != null && !translated.isBlank()) {
        title.text(translated);
      }
    }
    return doc.outerHtml();
  }
}
