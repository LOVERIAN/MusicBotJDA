package com.loverian.Dmusic.command.commands.music;

import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import com.loverian.Dmusic.utils.FormatUtil;
import com.loverian.Dmusic.utils.Youtube;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

public class PlayCommand implements Icommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);

    Youtube youtube = new Youtube();

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }
        final VoiceChannel memberChannel = memberVoiceState.getChannel();
        final Member self = ctx.getSelfMember();

        if (!self.hasPermission(memberChannel, Permission.VOICE_CONNECT)) {
            channel.sendMessageFormat("\uD83D\uDEAB I am missing permission to join %s", memberChannel).queue();
            return;
        }

        String input = String.join(" ", args);
        String url = null;
        String q = input;

        channel.sendMessage(String.format("%s Searching `%s` ", Config.get("LOAD"), q)).queue();
        if (!isUrl(input)) {
            url = youtube.ytSearch(input);
            input = url;

            if (url == null){
                  url = FormatUtil.extractYahoo(q);
                  input = url;
            }
            if(url == null) {
                input = "ytsearch:" + q;
                System.out.println("using ytsearch");
            }
        }


        AudioManager audioManager = ctx.getGuild().getAudioManager();
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(memberChannel);
        }

        PlayerManager manager = PlayerManager.getInstance();

        manager.loadAndPlay(input, ctx);

    }

    private boolean isUrl(String input) {
        try {
            new URL(input);

            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    @Override
    public List<String> getAliases() {
        List<String> list=new ArrayList<String>();
        //Adding elements in the List
        list.add("p");
        list.add("bajao");
        return list;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "To play music";
    }
}
