package org.elasticsearch.plugins;

import java.util.Collection;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.ScriptEngine;

/**
 * @Author luorenshu(626115221 @ qq.com)
 * @date 2018/11/2 下午4:28
 **/

public class CosinSimPlugin extends Plugin implements  ScriptPlugin {

    @Override
    public ScriptEngine getScriptEngine(Settings settings, Collection<ScriptContext<?>> contexts) {
        return new CosinSimEngine();
    }


}
