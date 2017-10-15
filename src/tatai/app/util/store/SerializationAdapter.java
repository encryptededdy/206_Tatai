package tatai.app.util.store;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Stores the classtype of classes that extend StoreItem in order to allow them to be serialised correctly.
 *
 * Source: https://stackoverflow.com/questions/3629596/deserializing-an-abstract-class-in-gson
 * Author: Guruprasad GV
 */
public class SerializationAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_META_KEY = "_ClassType";

    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        String className = jsonObj.get(CLASS_META_KEY).getAsString(); // Get the object type
        try {
            Class<?> clz = Class.forName(className);
            return jsonDeserializationContext.deserialize(jsonElement, clz); // Construct the appropriate object
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
        jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName()); // Store the object type
        return jsonEle;
    }
}

