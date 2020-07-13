package com.loverian.Dmusic.command.commands;

import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import net.dv8tion.jda.api.JDA;

public class PingCommand implements Icommand {
    @Override
    public void handle(CommandContext ctx) {
        JDA jda =ctx.getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel()
                        .sendMessageFormat("Reset ping: %sms\nWS ping: %sms", ping, jda.getGatewayPing()).queue()
        );
    }

    @Override
    public String getHelp() {
        return "Shows the current ping from the bot to the discord server";
    }

    @Override
    public String getName() {
        return "ping";
    }
}
