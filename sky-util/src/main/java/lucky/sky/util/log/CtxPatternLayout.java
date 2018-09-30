package lucky.sky.util.log;

import lucky.sky.util.context.Context;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;


public class CtxPatternLayout extends PatternLayout {

  @Override
  protected PatternParser createPatternParser(String pattern) {
    PatternParser parser = new CtxPatternParser(pattern);
    return parser;
  }

  private class CtxPatternParser extends PatternParser {

    CtxPatternParser(String pattern) {
      super(pattern);
    }

    @Override
    protected void finalizeConverter(char c) {
      if (c == 'A') {
        PatternConverter pc = new CtxPatternConverter();
        currentLiteral.setLength(0);
        addConverter(pc);
        return;
      }

      super.finalizeConverter(c);
    }
  }

  private class CtxPatternConverter extends PatternConverter {

    @Override
    protected String convert(LoggingEvent event) {
      return Context.getContextID();
    }
  }
}
