package kz.ilotterytea.bot.models;

import kz.ilotterytea.bot.models.emotes.Emote;
import kz.ilotterytea.bot.models.emotes.Provider;
import kz.ilotterytea.bot.models.notify.NotifyListener;

import java.util.Map;

/**
 * Target (channel) model.
 * @author ilotterytea
 * @since 1.0
 */
public class TargetModel {
    /** Target's alias ID.
     * This is the channel ID from the platform the bot took it from.
     * In the case of Twitch, it could be the user ID.
     */
    private final String aliasId;
    /** Target's chat statistics. */
    private final StatsModel stats;
    /** The listening only mode. */
    private boolean listeningOnlyMode;
    /** The chat language. */
    private String language;
    /** Target's emotes. */
    private Map<Provider, Map<String, Emote>> emotes;
    /** Target's custom commands. */
    private Map<String, CustomCommand> custom;
    private final Map<String, NotifyListener> listeners;
    /** Command prefix. */
    private String prefix;

    public TargetModel(
            String aliasId,
            StatsModel stats,
            boolean listeningOnlyMode,
            String language,
            Map<Provider, Map<String, Emote>> emotes,
            Map<String, CustomCommand> customCommands,
            Map<String, NotifyListener> listeners,
            String prefix
    ) {
        this.aliasId = aliasId;
        this.stats = stats;
        this.listeningOnlyMode = listeningOnlyMode;
        this.language = language;
        this.emotes = emotes;
        this.custom = customCommands;
        this.listeners = listeners;
        this.prefix = prefix;
    }

    public StatsModel getStats() { return stats; }
    public String getAliasId() { return aliasId; }
    public Map<Provider, Map<String, Emote>> getEmotes() { return emotes; }
    public void setEmotes(Provider provider, Map<String, Emote> emotes) { this.emotes.put(provider, emotes); }
    public Map<String, CustomCommand> getCustomCommands() { return custom; }
    public void setCustomCommands(Map<String, CustomCommand> commands) { this.custom = commands; }
    public boolean getListeningMode() { return listeningOnlyMode; }
    public void setListeningMode(boolean mode) { this.listeningOnlyMode = mode; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Map<String, NotifyListener> getListeners() { return listeners; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
}
