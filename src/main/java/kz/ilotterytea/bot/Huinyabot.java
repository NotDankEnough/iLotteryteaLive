package kz.ilotterytea.bot;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.helix.domain.User;
import com.google.gson.Gson;
import kz.ilotterytea.bot.api.commands.CommandLoader;
import kz.ilotterytea.bot.api.delay.DelayManager;
import kz.ilotterytea.bot.handlers.MessageHandlerSamples;
import kz.ilotterytea.bot.storage.PropLoader;
import kz.ilotterytea.bot.storage.json.TargetController;
import kz.ilotterytea.bot.storage.json.UserController;
import kz.ilotterytea.bot.thirdpartythings.seventv.v1.SevenTVWebsocketClient;
import kz.ilotterytea.bot.thirdpartythings.seventv.v1.models.Message;
import kz.ilotterytea.bot.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Bot.
 * @author ilotterytea
 * @since 1.0
 */
public class Huinyabot extends Bot {
    private Properties properties;
    private TwitchClient client;
    private CommandLoader loader;
    private DelayManager delayer;
    private TargetController targets;
    private UserController users;
    private SevenTVWebsocketClient sevenTV;

    private final Logger LOGGER = LoggerFactory.getLogger(Huinyabot.class);

    public TwitchClient getClient() { return client; }
    public Properties getProperties() { return properties; }
    public CommandLoader getLoader() { return loader; }
    public DelayManager getDelayer() { return delayer; }
    public TargetController getTargetCtrl() { return targets; }
    public UserController getUserCtrl() { return users; }
    public SevenTVWebsocketClient getSevenTVWSClient() { return sevenTV; }

    private static Huinyabot instance;
    public static Huinyabot getInstance() { return instance; }
    public Huinyabot() { instance = this; }

    @Override
    public void init() {
        StorageUtils.checkIntegrity();
        targets = new TargetController(SharedConstants.TARGETS_DIR);
        users = new UserController(SharedConstants.USERS_DIR);

        properties = new PropLoader(SharedConstants.PROPERTIES_PATH);
        loader = new CommandLoader();
        delayer = new DelayManager();

        try {
            sevenTV = new SevenTVWebsocketClient();
            sevenTV.connectBlocking();
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // - - -  T W I T C H  C L I E N T  - - - :
        OAuth2Credential credential = new OAuth2Credential("twitch", properties.getProperty("OAUTH2_TOKEN"));

        client = TwitchClientBuilder.builder()
                .withChatAccount(credential)
                .withEnableTMI(true)
                .withEnableChat(true)
                .withClientId(properties.getProperty("CLIENT_ID"))
                .withEnableHelix(true)
                .build();

        client.getChat().connect();
        if (credential.getUserName() != null) {
            client.getChat().joinChannel(credential.getUserName());
        }

        List<User> userList = new ArrayList<>();
        if (client.getHelix() != null && properties.getProperty("ACCESS_TOKEN") != null && targets.getAll().keySet().size() > 0) {
            userList = client.getHelix().getUsers(
                    properties.getProperty("ACCESS_TOKEN"),
                    new ArrayList<>(targets.getAll().keySet()),
                    null
            ).execute().getUsers();

            for (User u : userList) {
                client.getChat().joinChannel(u.getLogin());
            }
        }

        for (User user : userList) {
            sevenTV.send(new Gson().toJson(new Message("join", user.getLogin())));
        }

        client.getEventManager().onEvent(IRCMessageEvent.class, MessageHandlerSamples::ircMessageEvent);
    }

    @Override
    public void dispose() {
        client.close();
        sevenTV.close();
        targets.save();
        users.save();
    }
}
