import controller.PlayMarketController;
import controller.QueueController;
import controller.TrendsController;
import exceptions.ApplicationException;
import lombok.extern.log4j.Log4j2;

import java.time.LocalTime;
import java.util.Arrays;

@Log4j2
public class Main {

    private static String[] SEARCH_STRINGS = new String[]{
            "vpn",
            "android vpn",
            "mobile vpn",
    };

    private static String[] PLAY_MARKET_APPS = new String[]{
            "https://play.google.com/store/apps/details?id=com.free.unblock.proxy.secure.vpn",
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

    public static void main(String[] args) throws ApplicationException {
        log.debug("Main thread started");

        new Thread(new QueueController(), "Queue").start();
        log.debug("Started Queue thread");

        TrendsController trendsController = new TrendsController(LocalTime.of(20, 0),
//        TrendsController trendsController = new TrendsController(LocalTime.now().plusSeconds(3),
                Arrays.asList(SEARCH_STRINGS));
        new Thread(trendsController, "Trends").start();
        log.debug("Started Trends thread");

        PlayMarketController playMarketController = new PlayMarketController(LocalTime.of(20, 0),
//        PlayMarketController playMarketController = new PlayMarketController(LocalTime.now().plusSeconds(3),
                Arrays.asList(PLAY_MARKET_APPS));
        new Thread(playMarketController, "Market").start();
        log.debug("Started Market thread");

        log.debug("Main thread finished");
    }
}
