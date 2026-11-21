package pl.miloszgilga.jargonaut.dictionary;

import java.util.Locale;
import java.util.Map;

public class Dictionaries {
  private final Map<Locale, Map<String, Object>> dictionaries;

  public Dictionaries(Map<Locale, Map<String, Object>> dictionaries) {
    this.dictionaries = dictionaries;
  }

  public Map<String, Object> ofLocale(Locale locale) {
    return dictionaries.get(locale);
  }

  public String resolve(Locale locale, String keyPath) {
    Object current = ofLocale(locale);
    for (final String segment : keyPath.split("\\.")) {
      if (!(current instanceof Map<?, ?> map)) {
        return null;
      }
      current = map.get(segment);
    }
    return current instanceof String str ? str : null;
  }
}
