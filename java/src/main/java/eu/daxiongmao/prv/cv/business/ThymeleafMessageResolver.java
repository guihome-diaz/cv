package eu.daxiongmao.prv.cv.business;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ThymeleafMessageResolver implements IMessageResolver {

    private static final String DEFAULT_BASENAME = "i18n/messages";
    private final String basename;

    public ThymeleafMessageResolver(String basename) {
        this.basename = basename;
    }

    @Override
    public String getName() {
        return "thymeleafMessageResolver";
    }

    @Override
    public Integer getOrder() {
        return 1;
    }

    @Override
    public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        Locale locale = context.getLocale();
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(basename, locale);
            String message = bundle.getString(key);
            if (messageParameters != null && messageParameters.length > 0) {
                return MessageFormat.format(message, messageParameters);
            }
            return message;
        } catch (MissingResourceException e) {
            // Will trigger absent message representation
            System.err.println("Cannot resolve message. key=" + key);
            return null;
        }
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return "???" + key + "_" + context.getLocale() + "???";
    }
}
