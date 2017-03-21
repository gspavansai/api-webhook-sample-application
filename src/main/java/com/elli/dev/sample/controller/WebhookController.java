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
package com.elli.dev.sample.controller;

import java.util.Map;

import java.io.IOException;

import com.elli.dev.sample.*;
import com.elli.dev.sample.data.Client;
import com.elli.dev.sample.data.WebhookNotification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
  * This is Spring Rest Controller to receive the webhook request
  * A restful class that will handle the endpoint request
  * By default when you run the app the it is hosted in http://localhost:8080
*/
@RestController
class WebhookController {

    public final static String BASE_URL_OPTION = "token_url";
    public final static String CLIENT_ID_OPTION = "client_id";
    public final static String CLIENT_SECRET_OPTION = "client_secret";
    public final static String USERNAME_OPTION = "username";
    public final static String PASSWORD_OPTION = "password";

    //fetch CLI options
    @Value("${base_url}")
    private String baseUrl;
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${payload}")
    private String payload;
    @Value("${secret}")
    private String secret;


    /*
        The path is sample path - /abccorp/callback, which is mapped to your application
     */
    @RequestMapping(path = "/abccorp/callback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> callback(Map<String, Object> model, @RequestBody String json, @RequestHeader("X-Elli-Signature") String ellieSignature) {

        //obtain access token
        Client client = new Client.Builder()
                .setBaseUrl(baseUrl)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setUsername(username)
                .setPassword(password)
                .build();

        try{
            //It is ideal to validate the X-Elli-Signature with the shared secret
           /* if(!SignatureUtility.SignMessage(ellieSignature,json.toString(),secret)){
                System.out.println("Not a valid signature");
                //return error status code
                return new ResponseEntity<String>("",HttpStatus.BAD_REQUEST);
            }*/

            Gson gson = new GsonBuilder().create();
            WebhookNotification message = gson.fromJson(json, WebhookNotification.class);

            //Loan GUID from the request payload
            String loanId = message.getmeta().getResourceId();

            //Expected Request payload Sample from the command line args
            /*
            String requestJson = " 		{ \"loanGuids\":[ \"{loanId}\" ], " +
                    " \"fields\": [ " +
                    " \"Loan.LoanFolder\", " +
                    " \"Loan.LoanNumber\", " +
                    " \"Loan.LoanRate\", " +
                    " \"Loan.LoanAmount\", " +
                    " \"Fields.4002\", " +
                    " \"Loan.LastModified\", " +
                    " \"Loan.BorrowerName\" " +
                    "  ], " +
                    "  \"sortOrder\": [{ " +
                    "  \"canonicalName\": \"Loan.LoanNumber\", " +
                    " \"order\": \"asc\" " +
                    " }, { " +
                    " \"canonicalName\": \"Fields.4000\", " +
                    " \"order\": \"desc\" " +
                    " }] 			} ";
            */

            payload = payload.replace("{loanId}", loanId);
            System.out.println("Request payload " + payload);

            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String token = client.getAccessToken();
            headers.set("Authorization", "Bearer " + token);
            System.out.println("Token " + token );

            // set empty body
            HttpEntity<String> entity = new HttpEntity<String>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();
            String loanPath = baseUrl + "/encompass/v1/loanPipeline";
            ResponseEntity<String> loanData = restTemplate
                    .exchange(loanPath, HttpMethod.POST, entity, String.class);

            if (loanData.getStatusCode() == HttpStatus.OK) {
                // Write Loan pipeline response to File
                try {
                    //The reason this call is synchronous is once you return http status code 200,
                    //this message is considered delivered and deleted from the queue. There will be
                    //no further transmission
                    new FileUtility().writeFile(loanData.getBody().getBytes());
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            } else if (loanData.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // nono... bad credentials
                System.out.println(" login response " + loanData);
            }
            return new ResponseEntity<String>("",HttpStatus.OK);
        } catch(Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}