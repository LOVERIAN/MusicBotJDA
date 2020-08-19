package com.loverian.Dmusic.command.commands.music;

import com.loverian.Dmusic.audio.GuildMusicManager;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import com.loverian.Dmusic.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class QueueCommand implements Icommand {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx);
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        if (queue.isEmpty()) {
            channel.sendMessage("The queue is empty").queue();

            return;
        }

        int trackCount = Math.min(queue.size(), 10);
        List<AudioTrack> tracks = new ArrayList<>(queue);
        AudioPlayer player = musicManager.player;
        String user;
        AudioTrackInfo info = player.getPlayingTrack().getInfo();
        user = Objects.requireNonNull(ctx.getJDA().getUserById((long) player.getPlayingTrack().getUserData())).getName();
        EmbedBuilder builder = EmbedUtils.defaultEmbed()
                .setTitle("Current Queue (Total: " + queue.size() + ")")
                .setDescription(String.format("__Now Playing:__\n" +
                        "[%s](%s) | `%s` `Requested by: %s`\n\n" +
                                "__Up Next:__\n", info.title, info.uri,
                        FormatUtil.formatTime(info.length), user))
                .setColor(Color.orange)
                .setFooter("Page 1/1");

        for (int i = 0; i < trackCount; i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo inf = track.getInfo();
            user = Objects.requireNonNull(ctx.getJDA().getUserById((long) player.getPlayingTrack().getUserData())).getName();
            builder.appendDescription(String.format(
                    "`%s` [%s](%s) | `%s` `Requested by: %s`\n\n",
                    i+1,
                    inf.title,
                    inf.uri,
                    FormatUtil.formatTime(inf.length), user
            ));
        }

        channel.sendMessage(builder.build()).queue();
    }

    @Override
    public List<String> getAliases() {
        List<String> list=new ArrayList<String>();
        //Adding elements in the List
        list.add("q");
        list.add("list");
        return list;
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "shows tracks in queue";
    }
}
