package com.loverian.Dmusic.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Icommand {

    void handle(CommandContext ctx);

    String getName();


    String getHelp();

    default List<String> getAliases(){
        return Collections.emptyList();
    }
}
