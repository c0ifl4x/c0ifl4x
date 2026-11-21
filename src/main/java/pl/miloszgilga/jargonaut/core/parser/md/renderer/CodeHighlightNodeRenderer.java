package pl.miloszgilga.jargonaut.core.parser.md.renderer;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.ast.Document;

import org.jetbrains.annotations.Nullable;

import pl.miloszgilga.jargonaut.core.parser.md.FlexmarkFrontmatterMdParser;
import pl.miloszgilga.jargonaut.core.polyglot.SyntaxHighlighter;
import pl.miloszgilga.jargonaut.dictionary.Dictionaries;

import java.util.Locale;
import java.util.Set;

public class CodeHighlightNodeRenderer implements NodeRenderer {
  private final SyntaxHighlighter syntaxHighlighter;

  public CodeHighlightNodeRenderer(SyntaxHighlighter syntaxHighlighter) {
    this.syntaxHighlighter = syntaxHighlighter;
  }

  @Override
  public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
    return Set.of(
      new NodeRenderingHandler<>(FencedCodeBlock.class, this::renderCodeBlock)
    );
  }

  private void renderCodeBlock(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
    final Document document = node.getDocument();
    final Locale locale = FlexmarkFrontmatterMdParser.LOCALE.get(document);
    final Dictionaries dictionaries = FlexmarkFrontmatterMdParser.DICTIONARIES.get(document);

    final String language = node.getInfo().toString().trim();
    final String code = node.getContentChars().normalizeEOL();
    final String highlightedCode = syntaxHighlighter.renderHighlight(code, language);
    final String codeClass = language.isEmpty() ? "hljs" : "hljs language-" + language;

    html
      .line()
      .attr("class", "code-block")
      .withAttr().tag("div").indent().line()
      .attr("type", "button")
      .attr("class", "code-block__copy");
    if (locale != null && dictionaries != null) {
      final String copyLabel = dictionaries.resolve(locale, "copyCode");
      html
        .attr("aria-label", copyLabel)
        .attr("title", copyLabel);
    }
    html
      .withAttr().tag("button")
      .closeTag("button").line()
      .withAttr().tag("pre").openPre()
      .attr("class", codeClass).withAttr().tag("code")
      .raw(highlightedCode)
      .closeTag("code")
      .closeTag("pre").closePre().line()
      .unIndent().closeTag("div")
      .line();
  }
}
