package dev.shiro8613.multitps;

import com.github.puregero.multilib.MultiLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import java.util.stream.DoubleStream;

import static net.kyori.adventure.text.Component.text;

public class TPS {

    private static boolean Initialized = false;
    private static final String MESSAGE_CHANNEL = "multitps:msg";
    private static final String REPLAY_CHANNEL = "multitps:replay";

    private static List<String> tpses;
    private static List<String> msptes;


    public static void Init(JavaPlugin plugin) {
        tpses =  new ArrayList<>();
        MultiLib.onString(plugin, MESSAGE_CHANNEL, (data, replay) -> {
            String sendData = getTpsData(plugin, data);
            replay.accept(REPLAY_CHANNEL, sendData);
        });

        MultiLib.onString(plugin, REPLAY_CHANNEL, TPS::setTpsData);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            setTpsData(getTpsData(plugin, "tps"));
            MultiLib.notify(MESSAGE_CHANNEL, "tps");
        }, 1);

        Initialized = true;
    }

    public static void getTPS(Player player) {
        if (!Initialized) return;
        player.sendMessage("-----------------------------------------");
        player.sendMessage("[serverName] 1m / 5m / 15m / average");
        if (tpses.size() > 0) {
            coloredTps(tpses).forEach(player::sendMessage);
        } else {
            player.sendMessage("------NoData------");
        }
        player.sendMessage("-----------------------------------------");

    }

    public static String getMSPT(Player player) {
        return "";
    }

    private static String getTpsData(JavaPlugin plugin ,String data) {
        String serverName = MultiLib.getLocalServerName();
        String sendData = "";
        switch (data) {
            case "tps": {
                double[] tps = GetTpsList(plugin);
                sendData = serverName + ":tps:" + tps[0] + "/" + tps[1] + "/" + tps[2] + "/" + tps[3];

                break;
            }
            case "mspt": {

            }
        }

        return sendData;
    }

    private static List<Component> coloredTps(List<String> strings) {
        List<Component> components = new ArrayList<>();
        strings.forEach(s -> {
            var builder = text();
            String[] data = s.split(":");
            if (data.length >= 2 ) {
                String[] tpsstr = data[1].split("/");
                if (tpsstr.length >= 4) {
                    builder.append(Component.text("[" + data[0] + "] ", NamedTextColor.WHITE));
                    builder.append(Component.text(tpsstr[0], getColor(tpsstr[0])).append(Component.text("/", NamedTextColor.WHITE)));
                    builder.append(Component.text(tpsstr[1], getColor(tpsstr[1])).append(Component.text("/", NamedTextColor.WHITE)));
                    builder.append(Component.text(tpsstr[2], getColor(tpsstr[2])).append(Component.text("/", NamedTextColor.WHITE)));
                    builder.append(Component.text(tpsstr[3], getColor(tpsstr[3])));
                }
            }
            components.add(builder.build());
        });

        return components;
    }

    private static NamedTextColor getColor(String s) {
        double d = Double.parseDouble(s);
        return d >= 18.5 ? NamedTextColor.GREEN : d > 15 ? NamedTextColor.YELLOW : NamedTextColor.RED;
    }

    private static void setTpsData(String data) {
        String[] Data = data.split(":");
        if (Data.length > 3) return;

        switch (Data[1]) {
            case "tps": {
                tpses.add(Data[0] + ":" + Data[2]);
                break;
            }
            case "mspt": {
                msptes.add("[" + Data[0] + "] " + Data[2]);
                break;
            }
        }
    }

    private static double[] GetTpsList(JavaPlugin plugin) {
        double[] tps = setScales(plugin.getServer().getTPS());

        double tps1m = tps[0];
        double tps5m = tps[1];
        double tps15m = tps[2];
        double tpsAve = Average(Arrays.stream(tps));

        return new double[] {
                tps1m,
                tps5m,
                tps15m,
                tpsAve
        };

    }

    private static double Average(DoubleStream doubleStream) {
        return BigDecimal.valueOf(doubleStream
                        .average()
                        .orElseThrow(IllegalStateException::new))
                .setScale(2,RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static double[] setScales(double[] doubles) {
        return Arrays.stream(doubles)
                .mapToObj(BigDecimal::new)
                .map(b -> b.setScale(2, RoundingMode.HALF_UP))
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();
    }
}
