package net.maxouxax.coronavirusbot;

import net.maxouxax.coronavirusbot.utils.ConfigurationManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class CoronaVirusBOT  {

    private static ConfigurationManager configurationManager;

    public static void main(String[] args) throws IOException, TwitterException {
        loadConfig();
        System.out.println("Collecting data...");
        Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
        Elements webData = doc.select(".maincounter-number");
        System.out.println("Connecting to Twitter API...");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configurationManager.getStringValue("oauthConsumerKey"))
                .setOAuthConsumerSecret(configurationManager.getStringValue("oauthConsumerSecret"))
                .setOAuthAccessToken(configurationManager.getStringValue("oauthAccessToken"))
                .setOAuthAccessTokenSecret(configurationManager.getStringValue("oauthAccessTokenSecret"));
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        System.out.println("Connected to Twitter API succesfully !");
        System.out.println("Computing data for Twitter...");
        String totalCases = webData.get(0).text();
        String totalDeaths = webData.get(1).text();
        String totalRecovered = webData.get(2).text();
        int currentCasesInt = Integer.parseInt(totalCases.replace(",",""))-Integer.parseInt(totalDeaths.replace(",",""))-Integer.parseInt(totalRecovered.replace(",",""));
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        String currentCases = formatter.format(currentCasesInt);
        System.out.println("Data computed!");
        System.out.println(totalCases+" people got infected");
        System.out.println(currentCases+" people are currently infected");
        System.out.println(totalDeaths+" people died");
        System.out.println(totalRecovered+" people got recovered");
        System.out.println("Tweeting...");
        String generatedTweet = "CoronaVirus Update:\n☢️ Total cases: "+totalCases+" ☢️\n⚠️ Current cases: "+currentCases+" ⚠️\n⚰️ Deaths: "+totalDeaths+" ⚰️\n\uD83C\uDFE5 Recovered: "+totalRecovered+" \uD83C\uDFE5\n\n#CoronaVirus #Covid_19\nSource: https://worldometers.info/coronavirus/";
        twitter.updateStatus(generatedTweet);
        System.out.println("Done!");
    }

    private static void loadConfig() {
        try {
            configurationManager = new ConfigurationManager("config.json");
            configurationManager.loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
