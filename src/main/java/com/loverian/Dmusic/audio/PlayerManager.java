package com.loverian.Dmusic.audio;

import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.commands.music.NpCommand;
import com.loverian.Dmusic.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class PlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;


    private PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(CommandContext ctx) {
        long guildId = ctx.getGuild().getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, ctx);
            musicManagers.put(guildId, musicManager);
        }

        ctx.getGuild().getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void loadAndPlay(String trackUrl, CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        GuildMusicManager musicManager = getGuildMusicManager(ctx);
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                ctx.getMember().getId();
                track.setUserData(ctx.getMember().getUser().getIdLong());
                play(musicManager, track, channel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().remove(0);
                }
                firstTrack.setUserData(ctx.getMember().getUser().getIdLong());
                play(musicManager, firstTrack, channel);

            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });

    }

    private void play(GuildMusicManager musicManager, AudioTrack track, TextChannel channel) {
         musicManager.scheduler.queue(track, channel);
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}