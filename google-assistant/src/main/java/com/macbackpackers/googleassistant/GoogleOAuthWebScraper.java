package com.macbackpackers.googleassistant;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GoogleOAuthWebScraper {
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private WebClient webClient;
    private String email;
    private String password;

    public GoogleOAuthWebScraper( @Autowired WebClient webClient,
                                  @Value( "${google.login.email}" ) String email,
                                  @Value( "${google.login.password}" ) String password ) {
        this.webClient = webClient;
        this.email = email;
        this.password = password;
    }

    public void doIt( String url ) throws IOException {
        HtmlPage currentPage = webClient.getPage( url );

        List<HtmlEmailInput> emails = currentPage.getByXPath( "//input[@type='email']" );
        if ( emails.size() > 0 ) {
            emails.get( 0 ).type( email );
            HtmlButton nextBtn = currentPage.getFirstByXPath( "//button[span[normalize-space(text())='Next']]" );
            nextBtn.click();
            HtmlPasswordInput passwordField = currentPage.getFirstByXPath( "//input[@type='password']" );
            passwordField.type( password );
            nextBtn = currentPage.getFirstByXPath( "//button[span[normalize-space(text())='Next']]" );
            nextBtn.click();
            LOGGER.info( currentPage.asXml() );
        }
        LOGGER.info( currentPage.asXml() );
    }

    public void doItSelenium( String url ) {

    }
}
