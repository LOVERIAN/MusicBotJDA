package com.loverian.Dmusic.command.commands.music;

import com.loverian.Dmusic.audio.GuildMusicManager;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class LeaveCommand implements Icommand {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        AudioManager audioManager = ctx.getGuild().getAudioManager();

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx);

        GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        if(ctx.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                ctx.getMember().getRoles().stream().map(Role::getName).anyMatch("DJ"::equals)) {

            if (!audioManager.isConnected()) {
                channel.sendMessage("I'm not connected to a voice channel").queue();
                return;
            }


            if (!memberVoiceState.inVoiceChannel()) {
                channel.sendMessage("You have to be in the same voice channel as me to use this").queue();
                return;
            }
            musicManager.scheduler.clearQueue();
            musicManager.player.stopTrack();
            musicManager.player.setPaused(false);
            audioManager.closeAudioConnection();
            channel.sendMessage("Leaving...").queue();
        }else {
            ctx.getChannel().sendMessage("You don't have permission, You need a role named `DJ`").queue();
        }
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return "leaves voice channel";
    }
    @Override
    public List<String> getAliases() {
        java.util.List<java.lang.String> list = new ArrayList<>();
        //Adding elements in the List
        list.add("dc");
        return list;
    }
}
