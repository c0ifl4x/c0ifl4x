package pl.miloszgilga.jargonaut.core.parser.md.renderer;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;

import org.jetbrains.annotations.Nullable;

import pl.miloszgilga.jargonaut.core.polyglot.SyntaxHighlighter;

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
    final String language = node.getInfo().toString().trim();
    final String code = node.getContentChars().normalizeEOL();
    final String highlightedCode = syntaxHighlighter.renderHighlight(code, language);
    html.line()
      .withAttr().tag("pre").openPre()
      .attr("class", "hljs language-" + language).withAttr().tag("code")
      .raw(highlightedCode)
      .closeTag("code")
      .closeTag("pre").closePre()
      .line();
  }
}
