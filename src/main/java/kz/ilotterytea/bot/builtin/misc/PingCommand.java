package kz.ilotterytea.bot.builtin.misc;

import kz.ilotterytea.bot.Huinyabot;
import kz.ilotterytea.bot.SharedConstants;
import kz.ilotterytea.bot.api.commands.Command;
import kz.ilotterytea.bot.api.commands.Request;
import kz.ilotterytea.bot.api.commands.Response;
import kz.ilotterytea.bot.i18n.LineIds;
import kz.ilotterytea.bot.utils.StringUtils;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Ping command.
 *
 * @author ilotterytea
 * @since 1.0
 */
public class PingCommand implements Command {
    @Override
    public String getNameId() {
        return "ping";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pong", "пинг", "понг");
    }

    @Override
    public Response run(Request request) throws Exception {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

        String ut = StringUtils.formatTimestamp(uptime / 1000);

        Runtime rt = Runtime.getRuntime();
        double usedMemMb = ((rt.totalMemory() - rt.freeMemory()) / 1024.0) / 1024.0;
        double totalMemMb = (rt.totalMemory() / 1024.0) / 1024.0;
        double percentMemUsage = Math.round((usedMemMb / totalMemMb) * 100.0);

        OkHttpClient client = new OkHttpClient.Builder().build();

        // Getting info about Stats:
        String statsStatus;

        try (okhttp3.Response response = client.newCall(new okhttp3.Request.Builder()
                .get()
                .url(SharedConstants.STATS_URL + "/api/v1/health")
                .build()
        ).execute()) {
            if (response.code() != 200) {
                statsStatus = "NOT OK (" + response.code() + ")";
            } else {
                statsStatus = "OK (" + (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + "ms)";
            }
        } catch (IOException e) {
            e.printStackTrace();
            statsStatus = "N/A";
        }

        return Response.ofSingle(Huinyabot.getInstance().getLocale().formattedText(
                request.getChannel().getPreferences().getLanguage(),
                LineIds.C_PING_SUCCESS,
                System.getProperty("java.version"),
                ut,
                String.valueOf(Math.round(percentMemUsage)),
                String.valueOf(Math.round(usedMemMb)),
                String.valueOf(Math.round(totalMemMb)),
                String.valueOf(Huinyabot.getInstance().getClient().getChat().getLatency()),
                (Huinyabot.getInstance().getSevenTVEventClient().getClient().isClosed()) ?
                        Huinyabot.getInstance().getLocale().literalText(
                                request.getChannel().getPreferences().getLanguage(),
                                LineIds.DISCON
                        ) : Huinyabot.getInstance().getLocale().literalText(
                        request.getChannel().getPreferences().getLanguage(),
                        LineIds.CON
                ),
                statsStatus,
                SharedConstants.getVersion()
        ));
    }
}
