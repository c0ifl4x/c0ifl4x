package pl.miloszgilga.jargonaut.core.parser.md.renderer;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ext.gitlab.GitLabInlineMath;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;

import org.jetbrains.annotations.Nullable;

import pl.miloszgilga.jargonaut.core.polyglot.MathRenderer;

import java.util.Set;

public class MathNodeRenderer implements NodeRenderer {
  private final MathRenderer mathRenderer;

  public MathNodeRenderer(MathRenderer mathRenderer) {
    this.mathRenderer = mathRenderer;
  }

  @Override
  public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
    return Set.of(
      new NodeRenderingHandler<>(FencedCodeBlock.class, this::renderMathBlock),
      new NodeRenderingHandler<>(GitLabInlineMath.class, this::renderMathInline)
    );
  }

  private void renderMathBlock(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
    final String language = node.getInfo().toString().trim();
    if (!"math".equalsIgnoreCase(language)) {
      context.delegateRender();
      return;
    }
    final String code = node.getContentChars().normalizeEOL();
    final String mathHtml = mathRenderer.renderMath(code, true);
    html.line().append(mathHtml).line();
  }

  private void renderMathInline(GitLabInlineMath node, NodeRendererContext context, HtmlWriter html) {
    final String tex = node.getText().toString();
    final String mathHtml = mathRenderer.renderMath(tex, false);
    html.raw(mathHtml);
  }
}
