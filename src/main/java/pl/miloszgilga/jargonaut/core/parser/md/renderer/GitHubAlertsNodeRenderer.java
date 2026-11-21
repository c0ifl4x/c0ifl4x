package pl.miloszgilga.jargonaut.core.parser.md.renderer;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubAlertsNodeRenderer implements NodeRenderer {
  private static final Pattern ALERT_PATTERN =
    Pattern.compile("^\\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)]\\s*", Pattern.CASE_INSENSITIVE);

  @Override
  public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
    return Set.of(
      new NodeRenderingHandler<>(BlockQuote.class, this::renderBlockQuote)
    );
  }

  private void renderBlockQuote(BlockQuote node, NodeRendererContext context, HtmlWriter html) {
    final Node firstChild = node.getFirstChild();
    if (!(firstChild instanceof Paragraph paragraph)) {
      context.delegateRender();
      return;
    }

    final BasedSequence paragraphChars = paragraph.getChars();
    final Matcher matcher = ALERT_PATTERN.matcher(paragraphChars);
    if (!matcher.find()) {
      context.delegateRender();
      return;
    }

    final String type = matcher.group(1).toLowerCase(Locale.ROOT);
    final String title = type.toLowerCase(Locale.ROOT);

    int charsToRemove = matcher.end();
    Node current = paragraph.getFirstChild();
    while (current != null && charsToRemove > 0) {
      final Node next = current.getNext();
      final BasedSequence nodeChars = current.getChars();
      if (nodeChars.length() <= charsToRemove) {
        charsToRemove -= nodeChars.length();
        current.unlink();
      } else {
        current.setChars(nodeChars.subSequence(charsToRemove));
        charsToRemove = 0;
      }
      current = next;
    }

    if (paragraph.getFirstChild() instanceof SoftLineBreak softBreak) {
      softBreak.unlink();
    }

    if (paragraph.getFirstChild() == null) {
      paragraph.unlink();
    }

    html.line()
      .attr("class", "markdown-alert markdown-alert--" + type)
      .withAttr().tag("div").indent().line()
      .attr("class", "markdown-alert__title")
      .withAttr().tag("p")
      .text(title)
      .closeTag("p").line();

    context.renderChildren(node);

    html.unIndent().closeTag("div").line();
  }
}
