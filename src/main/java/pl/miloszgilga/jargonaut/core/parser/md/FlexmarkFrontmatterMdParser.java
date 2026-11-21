package pl.miloszgilga.jargonaut.core.parser.md;

import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.data.NullableDataKey;

import pl.miloszgilga.jargonaut.core.event.BuildEventBus;
import pl.miloszgilga.jargonaut.core.parser.md.renderer.extension.CodeHighlightExtension;
import pl.miloszgilga.jargonaut.core.parser.md.renderer.extension.GitHubAlertsExtension;
import pl.miloszgilga.jargonaut.core.parser.md.renderer.extension.MathExtension;
import pl.miloszgilga.jargonaut.core.parser.yaml.YamlParser;
import pl.miloszgilga.jargonaut.core.polyglot.MathRenderer;
import pl.miloszgilga.jargonaut.core.polyglot.SyntaxHighlighter;
import pl.miloszgilga.jargonaut.dictionary.Dictionaries;

import java.util.List;
import java.util.Locale;

public class FlexmarkFrontmatterMdParser extends GenericFrontmatterMdParser {
  public static final NullableDataKey<Locale> LOCALE = new NullableDataKey<>("LOCALE");
  public static final NullableDataKey<Dictionaries> DICTIONARIES = new NullableDataKey<>("DICTIONARIES");

  private final Parser mdParser;
  private final HtmlRenderer htmlRenderer;

  public FlexmarkFrontmatterMdParser(SyntaxHighlighter syntaxHighlighter,
                                     MathRenderer mathRenderer,
                                     Dictionaries dictionaries,
                                     YamlParser yamlParser,
                                     BuildEventBus eventBus) {
    super(eventBus, dictionaries, yamlParser);
    final MutableDataSet options = new MutableDataSet();
    options.set(Parser.EXTENSIONS, List.of(
      TablesExtension.create(),
      GitLabExtension.create()
    ));
    mdParser = Parser.builder(options).build();
    htmlRenderer = HtmlRenderer.builder(options)
      .extensions(List.of(
        CodeHighlightExtension.create(syntaxHighlighter),
        MathExtension.create(mathRenderer),
        GitHubAlertsExtension.create()
      ))
      .build();
  }

  @Override
  protected String renderHtml(String markdownContent, Locale locale, Dictionaries dictionaries) {
    final Document document = mdParser.parse(markdownContent);
    document.set(LOCALE, locale);
    document.set(DICTIONARIES, dictionaries);
    return htmlRenderer.render(document);
  }
}
