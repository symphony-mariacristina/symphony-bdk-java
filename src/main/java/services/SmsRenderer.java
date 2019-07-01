package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class SmsRenderer {
    private static final Logger logger = LoggerFactory.getLogger(SmsRenderer.class);

    public static String renderInBot(String messageContext, SmsTypes smsType) {
        String wrappedContext = "{\"message\":" + messageContext + "}";

        return render(wrappedContext, smsType);
    }

    public static String renderInBot(JSONObject messageContext, SmsTypes smsType) {
        JSONObject wrappedContext = new JSONObject();
        wrappedContext.put("message", messageContext);

        return render(wrappedContext.toString(), smsType);
    }

    private static String render(String wrappedContext, SmsTypes smsType) {
        try {
            HandlebarsTemplateLoader templateLoader = new HandlebarsTemplateLoader();

            JsonNode jsonNode = new ObjectMapper().readValue(wrappedContext, JsonNode.class);

            Template template = templateLoader.getTemplate( smsType.getName() );
            Context templateContext = templateLoader.getContext(jsonNode);

            return template.apply(templateContext);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public enum SmsTypes {
        SIMPLE("simple"),
        ALERT("alert"),
        NOTIFICATION("notification"),
        INFORMATION("information"),
        TABLE("table"),
        LIST("list");

        private String name;

        SmsTypes(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
