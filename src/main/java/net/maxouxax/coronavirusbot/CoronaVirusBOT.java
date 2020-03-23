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

        Integer totalCasesInt = stringToInt(totalCases);
        Integer totalDeathsInt = stringToInt(totalDeaths);
        Integer totalRecoveredInt = stringToInt(totalRecovered);
        Integer currentCasesInt = totalCasesInt-totalDeathsInt-totalRecoveredInt;

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        String currentCases = formatter.format(currentCasesInt);

        System.out.println("Data computed!");
        System.out.println(totalCases+" people got infected");
        System.out.println(currentCases+" people are currently infected");
        System.out.println(totalDeaths+" people died");
        System.out.println(totalRecovered+" people got recovered");

        System.out.println("Getting last data");
        long diffTotalCases = 0;
        long diffTotalDeaths = 0;
        long diffTotalRecovered = 0;
        long diffCurrentCases= 0;
        if(!configurationManager.isSet("totalCases")){
            System.out.println("Oops, no past data found...");
        }else {
            diffTotalCases = totalCasesInt - configurationManager.getLongValue("totalCases");
            diffTotalDeaths = totalDeathsInt - configurationManager.getLongValue("totalDeaths");
            diffTotalRecovered = totalRecoveredInt - configurationManager.getLongValue("totalRecovered");
            diffCurrentCases = currentCasesInt - configurationManager.getLongValue("currentCases");
        }

        System.out.println("Tweeting...");
        String generatedTweet = "Coronavirus Update:\n☣️️ Total cases: "+totalCases+" ("+(diffTotalCases >= 0 ? "+"+diffTotalCases : diffTotalCases)+") ☣️️\n⚠️ Current cases: "+currentCases+" ("+(diffCurrentCases >= 0 ? "+"+diffCurrentCases : diffCurrentCases)+") ⚠️\n⚰️ Deaths: "+totalDeaths+" ("+(diffTotalDeaths >= 0 ? "+"+diffTotalDeaths : diffTotalDeaths)+") ⚰️\n\uD83C\uDFE5 Recovered: "+totalRecovered+" ("+(diffTotalRecovered >= 0 ? "+"+diffTotalRecovered : diffTotalRecovered)+") \uD83C\uDFE5\n\n(+/-X) : +/-X cases since last tweet\n#Coronavirus #COVID-19\nSource: https://worldometers.info/coronavirus/";
        twitter.updateStatus(generatedTweet);

        System.out.println(generatedTweet);
        System.out.println("Saving data...");
        configurationManager.setValue("totalCases", String.valueOf(totalCasesInt));
        configurationManager.setValue("totalDeaths", String.valueOf(totalDeathsInt));
        configurationManager.setValue("totalRecovered", String.valueOf(totalRecoveredInt));
        configurationManager.setValue("currentCases", String.valueOf(currentCasesInt));
        configurationManager.saveData();

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

    public static int stringToInt(String integer){
        return Integer.parseInt(integer.replace(",", ""));
    }

}
