package fund.cyber.xchange.markets;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.anx.v2.ANXExchange;
import org.knowm.xchange.bitbay.BitbayExchange;
import org.knowm.xchange.bitcoinde.BitcoindeExchange;
import org.knowm.xchange.bitcurex.BitcurexExchange;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitmarket.BitMarketExchange;
import org.knowm.xchange.bitso.BitsoExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.bittrex.v1.BittrexExchange;
import org.knowm.xchange.bleutrade.BleutradeExchange;
import org.knowm.xchange.btc38.Btc38Exchange;
import org.knowm.xchange.btcchina.BTCChinaExchange;
import org.knowm.xchange.btce.v3.BTCEExchange;
import org.knowm.xchange.btce.v3.dto.marketdata.BTCEExchangeInfo;
import org.knowm.xchange.btce.v3.service.BTCEMarketDataServiceRaw;
import org.knowm.xchange.btctrade.BTCTradeExchange;
import org.knowm.xchange.bter.BTERExchange;
import org.knowm.xchange.campbx.CampBXExchange;
import org.knowm.xchange.ccex.CCEXExchange;
import org.knowm.xchange.cexio.CexIOExchange;
import org.knowm.xchange.coinbase.CoinbaseExchange;
import org.knowm.xchange.coinfloor.CoinfloorExchange;
import org.knowm.xchange.coinmate.CoinmateExchange;
import org.knowm.xchange.cryptofacilities.CryptoFacilitiesExchange;
import org.knowm.xchange.cryptonit.v2.CryptonitExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.empoex.EmpoExExchange;
import org.knowm.xchange.gatecoin.GatecoinExchange;
import org.knowm.xchange.gdax.GDAXExchange;
import org.knowm.xchange.gemini.v1.GeminiExchange;
import org.knowm.xchange.hitbtc.HitbtcExchange;
import org.knowm.xchange.hitbtc.dto.marketdata.HitbtcSymbols;
import org.knowm.xchange.hitbtc.service.HitbtcMarketDataService;
import org.knowm.xchange.huobi.HuobiExchange;
import org.knowm.xchange.independentreserve.IndependentReserveExchange;
import org.knowm.xchange.itbit.v1.ItBitExchange;
import org.knowm.xchange.jubi.JubiExchange;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.lakebtc.LakeBTCExchange;
import org.knowm.xchange.livecoin.LivecoinExchange;
import org.knowm.xchange.loyalbit.LoyalbitExchange;
import org.knowm.xchange.mercadobitcoin.MercadoBitcoinExchange;
import org.knowm.xchange.okcoin.OkCoinExchange;
import org.knowm.xchange.paymium.PaymiumExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.quadrigacx.QuadrigaCxExchange;
import org.knowm.xchange.quoine.QuoineExchange;
import org.knowm.xchange.ripple.RippleExchange;
import org.knowm.xchange.taurus.TaurusExchange;
import org.knowm.xchange.therock.TheRockExchange;
import org.knowm.xchange.truefx.TrueFxExchange;
import org.knowm.xchange.vaultoro.VaultoroExchange;
import org.knowm.xchange.vircurex.VircurexExchange;
import org.knowm.xchange.yobit.YoBitExchange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Markets {

    @Service
    public class BitbayMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitbayExchange.class;
        }

        public List<Trade> getTrades(CurrencyPair currencyPair) throws IOException {
            return dataService.getTrades(currencyPair, null, "desc").getTrades();
        }
    }

    @Service
    public class BitfinexMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitfinexExchange.class;
        }
    }

    @Service
    public class BitstampMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitstampExchange.class;
        }
    }

    @Service
    public class BittrexMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BittrexExchange.class;
        }
    }

    @Service
    public class BleutradeMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BleutradeExchange.class;
        }
    }

    @Service
    public class Btc38Market extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return Btc38Exchange.class;
        }
    }

    @Service
    public class BTCEMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BTCEExchange.class;
        }

        @Override
        public List<CurrencyPair> getCurrencyPairs() throws IOException {
            BTCEExchangeInfo info = ((BTCEMarketDataServiceRaw) dataService).getBTCEInfo();
            List<CurrencyPair> result = new ArrayList<>();
            info.getPairs().keySet().forEach(p -> result.add(new CurrencyPair(p.toUpperCase().replace('_','/'))));
            return result;
        }

    }

    @Service
    public class BTERMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BTERExchange.class;
        }
    }

    @Service
    public class CexIOMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CexIOExchange.class;
        }
    }

    @Service
    public class CoinbaseMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CoinbaseExchange.class;
        }
    }

    @Service
    public class CoinmateMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CoinmateExchange.class;
        }
    }

    @Service
    public class GatecoinMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return GatecoinExchange.class;
        }
    }

    @Service
    public class JubiMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return JubiExchange.class;
        }
        public boolean useCurrentDate() {
            return true;
        }
    }

    @Service
    public class KrakenMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return KrakenExchange.class;
        }
    }

    @Service
    public class LakeBTCMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return LakeBTCExchange.class;
        }
        public boolean useCurrentDate() {
            return true;
        }
    }

    @Service
    public class LoyalbitMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return LoyalbitExchange.class;
        }
    }

    @Service
    public class OkCoinMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return OkCoinExchange.class;
        }
    }

    @Service
    public class PoloniexMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return PoloniexExchange.class;
        }
    }

    @Service
    public class QuoineMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return QuoineExchange.class;
        }
    }

    /*
    @Service
    public class RippleMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return RippleExchange.class;
        }
        public boolean useCurrentDate() {
            return true;
        }
    }
*/

    @Service
    public class HitbtcMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return HitbtcExchange.class;
        }
        public List<CurrencyPair> getCurrencyPairs() throws IOException {
            HitbtcSymbols info = ((HitbtcMarketDataService) dataService).getHitbtcSymbols();
            return info.getHitbtcSymbols().stream()
                    .map(p -> new CurrencyPair(p.getCommodity(), p.getCurrency()))
                    .collect(Collectors.toList());
        }
    }

    @Service
    public class ANXMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return ANXExchange.class;
        }
    }

    @Service
    public class BitcoindeMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitcoindeExchange.class;
        }
    }

    @Service
    public class BitcurexMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitcurexExchange.class;
        }
    }

    @Service
    public class HuobiMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return HuobiExchange.class;
        }
    }

    @Service
    public class GDAXMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return GDAXExchange.class;
        }
    }

    @Service
    public class GeminiMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return GeminiExchange.class;
        }
    }

    @Service
    public class BitMarketMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitMarketExchange.class;
        }
    }

    @Service
    public class BitsoMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BitsoExchange.class;
        }
    }

    @Service
    public class BTCChinaMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BTCChinaExchange.class;
        }
    }

    @Service
    public class BTCTradeMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return BTCTradeExchange.class;
        }
    }

    @Service
    public class YoBitMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return YoBitExchange.class;
        }
    }

    @Service
    public class CCEXMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CCEXExchange.class;
        }
    }

    @Service
    public class CampBXMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CampBXExchange.class;
        }
    }

    @Service
    public class ItBitMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return ItBitExchange.class;
        }
    }

    @Service
    public class LivecoinMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return LivecoinExchange.class;
        }
    }

    @Service
    public class CoinfloorMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CoinfloorExchange.class;
        }
    }

    @Service
    public class CryptonitMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CryptonitExchange.class;
        }
    }

    @Service
    public class CryptoFacilitiesMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return CryptoFacilitiesExchange.class;
        }
    }

    @Service
    public class EmpoExMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return EmpoExExchange.class;
        }
    }

    @Service
    public class IndependentReserveMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return IndependentReserveExchange.class;
        }
    }

    @Service
    public class MercadoBitcoinMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return MercadoBitcoinExchange.class;
        }
    }

    @Service
    public class PaymiumMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return PaymiumExchange.class;
        }
    }

    @Service
    public class QuadrigaCxMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return QuadrigaCxExchange.class;
        }
    }

    @Service
    public class TaurusMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return TaurusExchange.class;
        }
    }

    @Service
    public class TheRockMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return TheRockExchange.class;
        }
    }

    @Service
    public class VaultoroMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return VaultoroExchange.class;
        }
    }

    @Service
    public class TrueFxMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return TrueFxExchange.class;
        }
    }

    @Service
    public class VircurexMarket extends Market {
        public Class<? extends Exchange> getExchangeClass() {
            return VircurexExchange.class;
        }
    }
}