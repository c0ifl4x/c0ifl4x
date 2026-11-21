package pl.miloszgilga.jargonaut.core.parser.md.renderer.extension;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;

import org.jetbrains.annotations.NotNull;

import pl.miloszgilga.jargonaut.core.parser.md.renderer.MathNodeRenderer;
import pl.miloszgilga.jargonaut.core.polyglot.MathRenderer;

public class MathExtension implements HtmlRenderer.HtmlRendererExtension {
  private final MathRenderer mathRenderer;

  private MathExtension(MathRenderer mathRenderer) {
    this.mathRenderer = mathRenderer;
  }

  public static MathExtension create(MathRenderer mathRenderer) {
    return new MathExtension(mathRenderer);
  }

  @Override
  public void rendererOptions(@NotNull MutableDataHolder options) {
  }

  @Override
  public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
    htmlRendererBuilder.nodeRendererFactory(options -> new MathNodeRenderer(mathRenderer));
  }
}
