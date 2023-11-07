
package com.macbackpackers.googleassistant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger( Main.class );

    public static void main( String argv[] ) throws Exception {
        LOGGER.info( "Starting... " + Main.class + " on " + new Date() );
        System.setProperty( "webdriver.chrome.driver", Main.class.getClassLoader().getResource( "chromedriver" ).toURI().getPath() );
        AbstractApplicationContext context = new AnnotationConfigApplicationContext( Config.class );

        // make sure there is only ever one process running
        GoogleOAuthWebScraper scraper = context.getBean( GoogleOAuthWebScraper.class );
//        try (GoogleOAuthSeleniumScraper scraper = context.getBean(GoogleOAuthSeleniumScraper.class)) {
        scraper.doIt( "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=612774080827-dnba0kr8e5nlcqp2g2p5asulnf55s0vp.apps.googleusercontent.com&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fassistant-sdk-prototype+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fgcm&state=W0DKyyKjTLjLI1y0TWEmyYJ0C6AhWU&prompt=consent&access_type=offline" );
//        }
    }

    public static void main__OLD( String args[] ) throws Exception {
        System.getProperties().load( Main.class.getClassLoader().getResourceAsStream( "config.properties" ) );
        LOGGER.info( System.getProperty( "chromescraper.driver.options" ) );
        LOGGER.info( System.getProperty( "chromescraper.maxwait.seconds" ) );

        Config config = new Config();
        WebDriver driver = null; // config.createWebDriver();
        WebDriverWait wait = null; // new WebDriverWait( driver, config.getMaxWaitSeconds() );

        try {
            driver.get( "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=612774080827-dnba0kr8e5nlcqp2g2p5asulnf55s0vp.apps.googleusercontent.com&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fassistant-sdk-prototype+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fgcm&state=kSQd7RBrCSS5XIi5AKvKxgFvvnuxNe&prompt=consent&access_type=offline" );
            WebElement email = driver.findElement( By.xpath( "//input[@type='email']" ) );
            email.sendKeys( "castlerockedinburgh@gmail.com" );
            driver.findElement( By.xpath( "//span[text()='Next']" ) ).click();
        } finally {
            driver.close();
        }
    }

    private static WebElement findNextUnmatchedTvShow( WebDriver driver, WebDriverWait wait, List<String> visitedLinks ) {
        final String TV_LIBRARY = "http://app.plex.tv/desktop?secure=0#!/media/be30d250a3913b42c8bb6eb11dfbe19972e0030b/com.plexapp.plugins.library?key=%2Flibrary%2Fsections%2F2%2Fall%3Ftype%3D2&pageType=list&context=content.library&source=2";
        driver.get( TV_LIBRARY );
        List<WebElement> links = findElements( driver, wait, By.xpath( "//div[div/div/div/div[contains(@class, 'MetadataPosterCardIcon-placeholderIcon-2P76zo')]]/div/a" ) );
        return links.stream().filter( a -> false == visitedLinks.contains( a.getAttribute( "href" ) ) ).findFirst().get();
    }

    private static List<WebElement> findElements( WebDriver driver, WebDriverWait wait, By by ) {
        wait.until( ExpectedConditions.visibilityOfElementLocated( by ) );
        return driver.findElements( by );
    }

    private static List<WebElement> findOptionalElements( WebDriver driver, WebDriverWait wait, By by ) {
        try {
            wait.until( ExpectedConditions.visibilityOfElementLocated( by ) );
            return driver.findElements( by );
        }
        catch ( TimeoutException ex ) {
            LOGGER.info( "Element " + by.toString() + " not found... continuing.." );
            return new ArrayList<>();
        }
    }

    private static WebElement findElement( WebDriver driver, WebDriverWait wait, By by ) {
        wait.until( ExpectedConditions.visibilityOfElementLocated( by ) );
        return driver.findElement( by );
    }

    public static void main3( String args[] ) throws Exception {
        //printResults(exec(new String[] {"cmd", "/c", "dir"},new File(".")));

        Process process = exec( new String[]{
                "google-oauthlib-tool", "--client-secrets", "credentials.json",
                "--credentials", "devicecredentials.json",
                "--scope", "https://www.googleapis.com/auth/assistant-sdk-prototype",
                "--scope", "https://www.googleapis.com/auth/gcm",
                "--save", "--headless"}, new File( "/home/ec2-user/google-assistant-grpc" ) );

        BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        String line = "";
        while ( ( line = reader.readLine() ) != null ) {
            Pattern p = Pattern.compile( "(https.*)" );
            Matcher m = p.matcher( line );
            if ( m.find() ) {
                System.out.println( m.group( 1 ) );
            }
        }
        //printResults();
    }

    private static void writeLine( Process p, String value ) throws IOException {
        BufferedWriter bufferedwriter = new BufferedWriter( new OutputStreamWriter( p.getOutputStream() ) );
        bufferedwriter.write( value );
        bufferedwriter.flush();
    }

    private static Process exec( String[] cmdarray, File dir ) throws IOException {
        return new ProcessBuilder( cmdarray )
                .directory( dir )
                .start();
    }

    /**
     * Reads next line from process if available. Non-blocking.
     *
     * @param process
     * @return next output line or null if nothing to read
     * @throws IOException
     */
    private static String parseOutput( Process process ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        String line = "";
        if ( reader.ready() ) {
            return reader.readLine();
        }
        return null;
    }

    private static void printResults( Process process ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        String line = "";
        while ( ( line = reader.readLine() ) != null ) {
            System.out.println( line );
        }
    }
}
