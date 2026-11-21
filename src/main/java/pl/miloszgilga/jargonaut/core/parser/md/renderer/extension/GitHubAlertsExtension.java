package pl.miloszgilga.jargonaut.core.parser.md.renderer.extension;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataHolder;

import org.jetbrains.annotations.NotNull;

import pl.miloszgilga.jargonaut.core.parser.md.renderer.GitHubAlertsNodeRenderer;

public class GitHubAlertsExtension implements HtmlRenderer.HtmlRendererExtension {
  public static GitHubAlertsExtension create() {
    return new GitHubAlertsExtension();
  }

  @Override
  public void rendererOptions(@NotNull MutableDataHolder options) {
  }

  @Override
  public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
    htmlRendererBuilder.nodeRendererFactory(options -> new GitHubAlertsNodeRenderer());
  }
}
