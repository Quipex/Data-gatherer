package utils.crawler;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class PlayMarketCrawlerTest {
    private static final String ROYALPLAY_CARPLATES = "https://play.google.com/store/apps/details?id=com.royalplay.carplates";
    private static final String TELEGRAM = "https://play.google.com/store/apps/details?id=org.telegram.messenger";
    private static final String[] VPN_ADDRESSES = {
//            ROYALPLAY_CARPLATES,
//            TELEGRAM,
            "https://play.google.com/store/apps/details?id=com.free.unblock.proxy.secure.vpn",
            "https://play.google.com/store/apps/details?id=com.xh.green",
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

    @Test
    void testCrawling() {

    }
}
