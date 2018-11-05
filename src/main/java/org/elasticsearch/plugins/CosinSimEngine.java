package org.elasticsearch.plugins;

import org.elasticsearch.script.ScriptEngine;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.*;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.SearchScript;

/**
 * @Author luorenshu(626115221 @ qq.com)
 * @date 2018/11/2 下午4:28
 **/

public class CosinSimEngine implements ScriptEngine {
    private final static Logger logger = LogManager.getLogger(CosinSimEngine.class);
    final String FIELD = "field";
    final String VECTOR = "vector";

    @Override
    public String getType() {
        // script name
        return "cosinSimPlugin";
    }

    @Override
    @SuppressWarnings (value="unchecked")
    public <T> T compile(String scriptName, String scriptSource, ScriptContext<T> context, Map<String, String> params) {
        if (!context.equals(SearchScript.CONTEXT)) {
            throw new IllegalArgumentException(getType() + " scripts cannot be used for context [" + context.name + "]");
        }
        // we use the script "source" as the script identifier
        if ("vector".equals(scriptSource)) {
            SearchScript.Factory factory = (p, lookup) -> new SearchScript.LeafFactory() {
                final String field;
                List<Double> vector;

                {
                    if (!p.containsKey(FIELD)) {
                        throw new IllegalArgumentException("Missing parameter [field]");
                    }
                    if (!p.containsKey(VECTOR)) {
                        throw new IllegalArgumentException("Missing parameter [vector]");
                    }

                    field = p.get("field").toString();


                    vector = (List<Double>) p.get(VECTOR);
                }

                @Override
                public SearchScript newInstance(LeafReaderContext context) throws IOException {
                    if (!field.equals("")) {
                        // the field and/or term don't exist in this segment, so always return 0
                        return new SearchScript(p, lookup, context) {
                            @Override
                            public double runAsDouble() {
                                if (!lookup.source().containsKey(field)) {
                                    return 0;
                                }

                                List<Double> titleVec = (List<Double>) lookup.source().get(field);
                                if (vector.size() != titleVec.size()) {
                                    throw new IllegalArgumentException("vector size is not equal " + field + "vector size:" + vector.size());
                                }
                                double sum1 = 0, sum2 = 0;
                                double a = 0;
                                for (int i = 0; i < vector.size(); i++) {
                                    a += vector.get(i) * titleVec.get(i);
                                    sum1 += vector.get(i) * vector.get(i);
                                    sum2 += titleVec.get(i) * titleVec.get(i);
                                }
                                return a / (Math.sqrt(sum1) * Math.sqrt(sum2));
                            }
                        };
                    }
                    return new SearchScript(p, lookup, context) {
                        int currentDocid = -1;

                        @Override
                        public double runAsDouble() {
                            try {
                                if (!lookup.source().containsKey(field)) {
                                    return 0;
                                }
                                List<Double> titleVec = (List<Double>) lookup.source().get(field);
                                if (vector.size() != titleVec.size()) {
                                    throw new IllegalArgumentException("vector size is not equal " + field + "vector size:" + vector.size());
                                }
                                double sum1 = 0, sum2 = 0;
                                double a = 0;
                                for (int i = 0; i < vector.size(); i++) {
                                    a += vector.get(i) * titleVec.get(i);
                                    sum1 += vector.get(i) * vector.get(i);
                                    sum2 += titleVec.get(i) * titleVec.get(i);
                                }
                                return a / (Math.sqrt(sum1) * Math.sqrt(sum2));
                            } catch (Exception e) {
                                throw e;
                            }
                        }
                    };
                }

                @Override
                public boolean needs_score() {
                    return false;
                }
            };

            return context.factoryClazz.cast(factory);
        }

        throw new IllegalArgumentException("Unknown script name " + scriptSource);
    }


    @Override
    public void close() {
        // optionally close resources
    }
}
