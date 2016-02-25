/*
 * Copyright 2016 predic8 GmbH, www.predic8.com
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.predic8.membrane.core.interceptor.oauth2;

import com.predic8.membrane.annot.MCAttribute;
import com.predic8.membrane.annot.MCChildElement;
import com.predic8.membrane.annot.MCElement;
import com.predic8.membrane.core.Router;
import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.interceptor.AbstractInterceptor;
import com.predic8.membrane.core.interceptor.Outcome;
import com.predic8.membrane.core.interceptor.authentication.session.AccountBlocker;
import com.predic8.membrane.core.interceptor.authentication.session.CleanupThread;
import com.predic8.membrane.core.interceptor.authentication.session.SessionManager;
import com.predic8.membrane.core.interceptor.authentication.session.UserDataProvider;
import com.predic8.membrane.core.interceptor.oauth2.processors.*;
import org.springframework.beans.factory.annotation.Required;

@MCElement(name = "oauth2authserver")
public class OAuth2AuthorizationServerInterceptor extends AbstractInterceptor {

    private String issuer;
    private String location;//
    private String path;//
    private String message;//
    private boolean exposeUserCredentialsToSession;//

    private UserDataProvider userDataProvider; //
    private SessionManager sessionManager = new SessionManager();//
    private AccountBlocker accountBlocker;//
    private ClientList clientList;//
    private TokenGenerator tokenGenerator = new BearerTokenGenerator();
    private ScopeList scopeList;//
    private JwtGenerator jwtGenerator;

    private OAuth2Processors processors;

    @Override
    public void init(Router router) throws Exception {
        //uriFactory = router.getUriFactory(); TODO: use this in Processor
        if (userDataProvider == null)
            throw new Exception("No userDataProvider configured. - Cannot work without one.");
        if (getClientList() == null)
            throw new Exception("No clientList configured. - Cannot work without one.");
        if (getScopeList() == null) {
            throw new Exception("No scopeList configured. - Cannot work without one");
        }
        userDataProvider.init(router);
        getClientList().init(router);
        getScopeList().init(router);
        jwtGenerator = new JwtGenerator();
        sessionManager.init(router);
        addDefaultProcessors();
        new CleanupThread(sessionManager, accountBlocker).start();
    }

    private void addDefaultProcessors() {
        processors
                .add(new FaviconEndpointProcessor(this))
                .add(new AuthEndpointProcessor(this))
                .add(new TokenEndpointProcessor(this))
                .add(new UserinfoEndpointProcessor(this))
                .add(new RevocationEndpointProcessor(this))
                .add(new LoginDialogEndpointProcessor(this))
                .add(new EmptyEndpointProcessor(this))
                .add(new DefaultEndpointProcessor(this));
    }


    @Override
    public Outcome handleRequest(Exchange exc) throws Exception {
        return processors.runProcessors(exc);
    }

    public UserDataProvider getUserDataProvider() {
        return userDataProvider;
    }

    @Required
    @MCChildElement(order = 1)
    public void setUserDataProvider(UserDataProvider userDataProvider) {
        this.userDataProvider = userDataProvider;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @MCChildElement(order = 2)
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String getLocation() {
        return location;
    }

    @Required
    @MCAttribute
    public void setLocation(String location) {
        this.location = location;
    }

    public String getPath() {
        return path;
    }

    @Required
    @MCAttribute
    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    @MCAttribute
    public void setMessage(String message) {
        this.message = message;
    }

    public AccountBlocker getAccountBlocker() {
        return accountBlocker;
    }

    @MCChildElement(order = 3)
    public void setAccountBlocker(AccountBlocker accountBlocker) {
        this.accountBlocker = accountBlocker;
    }

    public boolean isExposeUserCredentialsToSession() {
        return exposeUserCredentialsToSession;
    }

    @MCAttribute
    public void setExposeUserCredentialsToSession(boolean exposeUserCredentialsToSession) {
        this.exposeUserCredentialsToSession = exposeUserCredentialsToSession;
    }

    public ClientList getClientList() {
        return clientList;
    }

    @Required
    @MCChildElement(order = 4)
    public void setClientList(ClientList clientList) {
        this.clientList = clientList;
    }

    public TokenGenerator getTokenGenerator() {
        return tokenGenerator;
    }

    @MCChildElement(order = 5)
    public void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public ScopeList getScopeList() {
        return scopeList;
    }

    @Required
    @MCChildElement(order = 6)
    public void setScopeList(ScopeList scopeList) {
        this.scopeList = scopeList;
    }
}
