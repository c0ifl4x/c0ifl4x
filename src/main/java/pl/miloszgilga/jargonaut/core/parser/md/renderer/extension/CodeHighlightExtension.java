package pl.miloszgilga.jargonaut.core.parser.md.renderer.extension;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;

import org.jetbrains.annotations.NotNull;

import pl.miloszgilga.jargonaut.core.parser.md.renderer.CodeHighlightNodeRenderer;
import pl.miloszgilga.jargonaut.core.polyglot.SyntaxHighlighter;

public class CodeHighlightExtension implements HtmlRenderer.HtmlRendererExtension {
  private final SyntaxHighlighter syntaxHighlighter;

  private CodeHighlightExtension(SyntaxHighlighter syntaxHighlighter) {
    this.syntaxHighlighter = syntaxHighlighter;
  }

  public static CodeHighlightExtension create(SyntaxHighlighter syntaxHighlighter) {
    return new CodeHighlightExtension(syntaxHighlighter);
  }

  @Override
  public void rendererOptions(@NotNull MutableDataHolder options) {
  }

  @Override
  public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
    htmlRendererBuilder.nodeRendererFactory(options -> new CodeHighlightNodeRenderer(syntaxHighlighter));
  }
}
