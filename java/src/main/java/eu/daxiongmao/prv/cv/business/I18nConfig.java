package eu.daxiongmao.prv.cv.business;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.IMessageResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class I18nConfig {

    private final ResourceBundle.Control control;
    /** Dictionary of all messages for a particular language */
    private final Map<Locale, MessageSource> cache;
    private final String baseName;

    public I18nConfig(String baseName) {
        this.baseName = baseName;
        this.control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        this.cache = new ConcurrentHashMap<>();
    }

    public MessageSource forLocale(Locale locale) {
        return cache.computeIfAbsent(locale, this::loadMessages);
    }

    private MessageSource loadMessages(Locale locale) {
        Map<String, String> messages = new HashMap<>();

        // Load from classpath: i18n/messages[_locale].properties
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, getClass().getClassLoader(), control);
            for (String key : bundle.keySet()) {
                messages.put(key, bundle.getString(key));
            }
        } catch (MissingResourceException e) {
            // Fallback: load default bundle
            try {
                ResourceBundle defaultBundle = ResourceBundle.getBundle(baseName, Locale.ROOT, getClass().getClassLoader());
                for (String key : defaultBundle.keySet()) {
                    messages.put(key, defaultBundle.getString(key));
                }
            } catch (MissingResourceException ex) {
                throw new RuntimeException("No messages bundle found: " + baseName);
            }
        }

        return new MessageSource(messages);
    }

    /**
     * Message resolver for Thymeleaf integration.
     */
    public IMessageResolver createThymeleafResolver(Locale locale) {
        MessageSource source = forLocale(locale);
        return new AbstractMessageResolver() {
            @Override
            public String resolveMessage(ITemplateContext context,
                                         Class<?> origin,
                                         String key,
                                         Object[] messageParameters) {
                String template = source.get(key);
                if (template == null) return null;
                if (messageParameters == null || messageParameters.length == 0) return template;
                return String.format(template, messageParameters);
            }

            @Override
            public String createAbsentMessageRepresentation(ITemplateContext context,
                                                            Class<?> origin,
                                                            String key,
                                                            Object[] messageParameters) {
                return "??" + key + "??";
            }
        };
    }

    public static class MessageSource {
        private final Map<String, String> messages;

        public MessageSource(Map<String, String> messages) {
            this.messages = messages;
        }

        public String get(String key) {
            return messages.get(key);
        }

        public String get(String key, Object... args) {
            String template = messages.get(key);
            return template != null ? String.format(template, args) : null;
        }

        public boolean contains(String key) {
            return messages.containsKey(key);
        }
    }

}
