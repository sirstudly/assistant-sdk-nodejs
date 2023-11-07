
package com.macbackpackers.googleassistant;

import java.util.concurrent.TimeUnit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan( "com.macbackpackers" )
@PropertySource( "classpath:config.properties" )
public class Config {

    @Bean
    @Scope( "prototype" )
    public WebDriver getWebDriver( @Value( "${chromescraper.driver.options}" ) String chromeOptions,
                                   @Value( "${chromescraper.maxwait.seconds:30}" ) int maxWaitSeconds ) {
        ChromeOptions options = new ChromeOptions();
        if ( StringUtils.isNotBlank( chromeOptions ) ) {
            options.addArguments( chromeOptions.split( " " ) );
        }
        WebDriver driver = new ChromeDriver( options );

        // configure wait-time when finding elements on the page
        driver.manage().timeouts().pageLoadTimeout( maxWaitSeconds, TimeUnit.SECONDS );

        return driver;
    }

    @Bean( name = "webClient" )
    @Scope( "prototype" )
    public WebClient getWebClient() {
        WebClient webClient = new WebClient( BrowserVersion.FIREFOX );
        webClient.getOptions().setTimeout( 120000 );
        webClient.getOptions().setRedirectEnabled( true );
        webClient.getOptions().setJavaScriptEnabled( true );
        webClient.getOptions().setThrowExceptionOnFailingStatusCode( false );
        webClient.getOptions().setThrowExceptionOnScriptError( false );
        webClient.getOptions().setCssEnabled( true );
        webClient.getOptions().setUseInsecureSSL( true );
        webClient.setAjaxController( new NicelyResynchronizingAjaxController() );
        return webClient;
    }

}
