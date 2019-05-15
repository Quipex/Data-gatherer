import controller.PlayMarketController;
import controller.TrendsController;

import java.time.LocalTime;
import java.util.Arrays;

public class Main {
    private static String[] SEARCH_STRINGS = new String[] {
            "vpn",
            "free vpn",
            "download vpn"
    };

    private static String[] PLAY_MARKET_APPS = new String[] {
            "https://play.google.com/store/apps/details?id=com.free.unblock.proxy.secure.vpn",
//            "https://play.google.com/store/apps/details?id=com.xh.green",
            "https://play.google.com/store/apps/details?id=wild.vpn.network",
            "https://play.google.com/store/apps/details?id=com.xiaoming.vpn",
            "https://play.google.com/store/apps/details?id=com.fast.free.unblock.secure.vpn",
            "https://play.google.com/store/apps/details?id=free.vpn.unblock.proxy.turbovpn",
            "https://play.google.com/store/apps/details?id=free.vpn.unblock.proxy.vpnmonster",
            "https://play.google.com/store/apps/details?id=free.vpn.unblock.proxy.vpnmaster",
            "https://play.google.com/store/apps/details?id=com.open.hotspot.vpn.free",
            "https://play.google.com/store/apps/details?id=com.freevpn.unblock.proxy",
            "https://play.google.com/store/apps/details?id=com.vpn.powervpn",
            "https://play.google.com/store/apps/details?id=ufovpn.free.unblock.proxy.vpn",
            "https://play.google.com/store/apps/details?id=com.simplexsolutionsinc.vpn_unlimited"
    };

    public static void main(String[] args) {
        TrendsController trendsController = new TrendsController(LocalTime.now().plusSeconds(10),
                Arrays.asList(SEARCH_STRINGS));
        new Thread(trendsController, "Trends").start();

        PlayMarketController playMarketController = new PlayMarketController(LocalTime.now().plusSeconds(10),
                Arrays.asList(PLAY_MARKET_APPS));
        new Thread(playMarketController, "PlayMarket").start();
    }
}
