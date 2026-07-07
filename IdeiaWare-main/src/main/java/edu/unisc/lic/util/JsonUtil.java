package edu.unisc.lic.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * REVISAO 2026-07-07 (varredura de servlets): Gson padrao (new Gson()) serializa TODOS os
 * campos de uma entidade via reflection, inclusive Usuario.senha (hash bcrypt) sempre que um
 * objeto com uma referencia a Usuario e devolvido como JSON (ex.: ColaboracaoIdeia.usuario).
 * Antes do M.6, isso ja acontecia em EnviarColaboracaoServlet; o M.6 (RetornaMensagensServlet
 * devolvendo um ARRAY de colaboracoes por poll, em vez de so 1) ampliou a exposicao -- cada
 * participante do grupo passou a receber periodicamente o hash de senha de QUALQUER outro
 * colaborador que postou algo.
 *
 * NAO da pra marcar Usuario.senha como `transient` (o Java transient, em campo mapeado por
 * JPA com acesso por campo, faz o Hibernate parar de PERSISTIR o campo tambem -- quebraria
 * login/senha). Em vez disso, este Gson compartilhado exclui Usuario.senha so na
 * SERIALIZACAO, sem tocar na entidade/persistencia.
 */
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
