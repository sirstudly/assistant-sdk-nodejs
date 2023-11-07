package com.macbackpackers.googleassistant;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

@Component
@Scope( "prototype" )
public class GoogleOAuthSeleniumScraper implements Closeable {
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private WebDriver webDriver;
    private String email;
    private String password;
    @Value( "${chromescraper.maxwait.seconds:30}" )
    private int maxWaitSeconds;

    public GoogleOAuthSeleniumScraper( @Autowired WebDriver webDriver,
                                       @Value( "${google.login.email}" ) String email,
                                       @Value( "${google.login.password}" ) String password ) {
        this.webDriver = webDriver;
        this.email = email;
        this.password = password;
    }

    public void doIt( String url ) throws IOException {
        WebDriverWait wait = new WebDriverWait( webDriver, maxWaitSeconds );
        webDriver.get( url );
        List<WebElement> emails = webDriver.findElements( By.xpath( "//input[@type='email']" ) );
        if ( emails.size() > 0 ) {
            emails.get( 0 ).sendKeys( email );
            WebElement nextBtn = webDriver.findElement( By.xpath( "//button[span[normalize-space(text())='Next']]" ) );
            nextBtn.click();
            WebElement passwordField = webDriver.findElement( By.xpath( "//input[@type='password']" ) );
            passwordField.sendKeys( password );
            nextBtn = webDriver.findElement( By.xpath( "//button[span[normalize-space(text())='Next']]" ) );
            nextBtn.click();
//            LOGGER.info(currentPage.asXml());
        }
//        LOGGER.info(currentPage.asXml());
    }

    @Override
    public void close() throws IOException {
        webDriver.close();
    }
}
