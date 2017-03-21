/*
 * Copyright 2017 Ellie Mae, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.elli.dev.sample.data;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The purpose of this class is twofold: (1) obtain access token using ROPC
 * OAuth2 grant (see <code>Builder.build</code> for more detail) and (2) serve
 * as a facade for loan APIs (see <code>getLoan</code> for more detail).
 */
public class Client implements Serializable {

    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String CLIENT_SECRET_PARAM = "client_secret";
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";

    private String loanUrl;

    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Fluent builder for initializing the "client" object.
     */
    public static class Builder {

        private String baseUrl;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Client build() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            //prepare ROPC request
            MultiValueMap<String, String> params = new LinkedMultiValueMap();
            params.add(CLIENT_ID_PARAM, clientId);
            params.add(CLIENT_SECRET_PARAM, clientSecret);
            params.add(GRANT_TYPE_PARAM, "password");
            params.add(USERNAME_PARAM, username);
            params.add(PASSWORD_PARAM, password);

            System.out.println("PARAMS: " + params.toString());
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(params, headers);

            String tokenUrl = String.format("%s/oauth2/v1/token", baseUrl);
            System.out.println("tokenURL: " + tokenUrl);

            try {
                //obtain access token and return client instance
                Client client = restTemplate
                        .exchange(tokenUrl, HttpMethod.POST, entity, Client.class)
                        .getBody();
                client.init(baseUrl);
                return client;
            }
            catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
    }

    protected void init(String baseUrl) {
        this.loanUrl = String.format("%s/encompass/v1/loans/{loanId}", baseUrl);
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return accessToken;
    }
}
