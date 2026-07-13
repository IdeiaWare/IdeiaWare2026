package edu.unisc.lic.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// GT-01: exclui Usuario.senha so na serializacao (marcar `transient` quebraria a persistencia pelo Hibernate).
public final class JsonUtil {

    /** Gson que nunca inclui Usuario.senha no JSON de saida. Uso: JsonUtil.GSON_SEM_SENHA.toJson(obj). */
    public static final Gson GSON_SEM_SENHA = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaringClass() == edu.unisc.lic.domain.Usuario.class
                            && "senha".equals(f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    private JsonUtil() {
    }
}
