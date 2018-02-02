/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.remoteum.sample;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.identity.claim.metadata.mgt.stub.dto.AttributeMappingDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.stub.dto.ClaimDialectDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.stub.dto.ClaimPropertyDTO;
import org.wso2.carbon.identity.claim.metadata.mgt.stub.dto.LocalClaimDTO;
import org.wso2.carbon.user.core.UserStoreException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * This demonstrates how to use remote user management API to add, delete and read users.
 */
public class RemoteUMClient {

    static Logger log = Logger.getLogger(RemoteUMClient.class);
    private static String serverUrl;
    private static String operation;
    private static String username;
    private static String password;
    private static String truststore;
    private static String truststorePassword;
    private static Properties properties = new Properties();

    private AuthenticationAdminStub authstub = null;
    private ConfigurationContext ctx;
    private String authCookie = null;
    private ClaimManager claimManager = null;

    /**
     * Initialization of environment
     *
     * @throws Exception
     */
    public RemoteUMClient() throws Exception {
        ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
        String authEPR = serverUrl + "AuthenticationAdmin";
        authstub = new AuthenticationAdminStub(ctx, authEPR);
        ServiceClient client = authstub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, authCookie);

        //set trust store properties required in SSL communication.
        System.setProperty("javax.net.ssl.trustStore", truststore);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);


        //log in as admin user and obtain the cookie
        this.login(username, password);

        //create web service client
        this.createClaimManager();
    }

    /**
     * Authenticate to carbon as admin user and obtain the authentication cookie
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public String login(String username, String password) throws Exception {
        //String cookie = null;
        boolean loggedIn = authstub.login(username, password, "localhost");
        if (loggedIn) {
            log.info("==================================================");
            log.info("The user " + username + " logged in successfully.");
            log.info("==================================================");
            authCookie = (String) authstub._getServiceClient().getServiceContext().getProperty(
                    HTTPConstants.COOKIE_STRING);
        } else {
            log.error("Error logging in " + username);
        }
        return authCookie;
    }

    public void createClaimManager() throws UserStoreException {
        claimManager = new ClaimManager(serverUrl, authCookie, ctx);
    }

    public static void main(String[] args) throws Exception {
        loadConfiguration();
        /*Create client for RemoteUserStoreManagerService and perform user management operations*/
        RemoteUMClient remoteUMClient = new RemoteUMClient();
        //create web service client
        remoteUMClient.createClaimManager();


        if (operation.equals("addClaimDialect")) {

            remoteUMClient.addClaimDialect(properties.getProperty(RemoteUMSampleConstants.CLAIM_DIALECT_URI));

        } else if (operation.equals("removeClaimDialect")) {

            remoteUMClient.removeClaimDialect(properties.getProperty(RemoteUMSampleConstants.CLAIM_DIALECT_URI));

        } else if (operation.equals("removeLocalClaim")) {

            remoteUMClient.removeLocalClaim(properties.getProperty(RemoteUMSampleConstants.CLAIM_URI));

        } else if (operation.equals("addLocalClaim")) {

            List <ClaimPropertyDTO>list = new ArrayList();

            list.add(buildClaimPropertyDTO("DisplayName", RemoteUMSampleConstants.DISPLAY_NAME));
            list.add(buildClaimPropertyDTO("Description", RemoteUMSampleConstants.CLAIM_DESCRIPTION));
            //list.add(buildClaimPropertyDTO("RegEx", RemoteUMSampleConstants.REGULAR_EXPRESSION));
            //list.add(buildClaimPropertyDTO("DisplayOrder", RemoteUMSampleConstants.DISPLAY_ORDER));
            //list.add(buildClaimPropertyDTO("SupportedByDefault", RemoteUMSampleConstants.SUPPORTED_BY_DEFAULT));
            //list.add(buildClaimPropertyDTO("Required", RemoteUMSampleConstants.REQUIRED_FOR_CLAIM));
            //list.add(buildClaimPropertyDTO("ReadOnly", RemoteUMSampleConstants.READ_ONLY));

            LocalClaimDTO localClaimDTO = new LocalClaimDTO();
            localClaimDTO.setClaimProperties(list.toArray(new ClaimPropertyDTO[list.size()]));
            localClaimDTO.setLocalClaimURI(properties.getProperty(RemoteUMSampleConstants.CLAIM_URI));

            AttributeMappingDTO attributeMappingDTO = new AttributeMappingDTO();
            attributeMappingDTO.setAttributeName(properties.getProperty(RemoteUMSampleConstants.ATTRIBUTE_NAME_1));
            attributeMappingDTO.setUserStoreDomain(properties.getProperty(RemoteUMSampleConstants.USER_STORE_DOMAIN_1));
            localClaimDTO.setAttributeMappings(new AttributeMappingDTO[]{attributeMappingDTO});

            remoteUMClient.addLocalClaim(localClaimDTO);

        } else if (operation.equals("updateLocalClaim")) {

            List <ClaimPropertyDTO>list = new ArrayList();

            list.add(buildClaimPropertyDTO("DisplayName", RemoteUMSampleConstants.DISPLAY_NAME));
            list.add(buildClaimPropertyDTO("Description", RemoteUMSampleConstants.CLAIM_DESCRIPTION));
//            list.add(buildClaimPropertyDTO("RegEx", RemoteUMSampleConstants.REGULAR_EXPRESSION));
//            list.add(buildClaimPropertyDTO("DisplayOrder", RemoteUMSampleConstants.DISPLAY_ORDER));
//            list.add(buildClaimPropertyDTO("SupportedByDefault", RemoteUMSampleConstants.SUPPORTED_BY_DEFAULT));
//            list.add(buildClaimPropertyDTO("Required", RemoteUMSampleConstants.REQUIRED_FOR_CLAIM));
//            list.add(buildClaimPropertyDTO("ReadOnly", RemoteUMSampleConstants.READ_ONLY));

            LocalClaimDTO localClaimDTO = new LocalClaimDTO();
            localClaimDTO.setClaimProperties(list.toArray(new ClaimPropertyDTO[list.size()]));
            localClaimDTO.setLocalClaimURI(properties.getProperty(RemoteUMSampleConstants.CLAIM_URI));


            AttributeMappingDTO attributeMappingDTO = new AttributeMappingDTO();
            attributeMappingDTO.setAttributeName(properties.getProperty(RemoteUMSampleConstants.ATTRIBUTE_NAME_1));
            attributeMappingDTO.setUserStoreDomain(properties.getProperty(RemoteUMSampleConstants.USER_STORE_DOMAIN_1));
            localClaimDTO.setAttributeMappings(new AttributeMappingDTO[]{attributeMappingDTO});

            remoteUMClient.updateLocalClaim(localClaimDTO);
        } else {
            System.out.println("Please enter a valid operation");
        }
    }

    public void addClaimDialect(String externalClaimDialectURI) throws Exception {

        ClaimDialectDTO externalClaimDialect = new ClaimDialectDTO();
        externalClaimDialect.setClaimDialectURI(externalClaimDialectURI);
        claimManager.addClaimDialect(externalClaimDialect);
    }

    public void removeClaimDialect(String externalClaimDialectURI) throws Exception {

        ClaimDialectDTO externalClaimDialect = new ClaimDialectDTO();
        externalClaimDialect.setClaimDialectURI(externalClaimDialectURI);
        claimManager.removeClaimDialect(externalClaimDialect);
    }

    public void removeLocalClaim(String localClaimURI) throws Exception {

        LocalClaimDTO localClaimDialect = new LocalClaimDTO();
        localClaimDialect.setLocalClaimURI(localClaimURI);
        claimManager.removeLocalClaim(localClaimURI);
    }

    public void addLocalClaim(LocalClaimDTO localClaimDTO) throws Exception {
        claimManager.addLocalClaim(localClaimDTO);
    }

    public void updateLocalClaim(LocalClaimDTO localClaimDTO) throws Exception {
        claimManager.updateLocalClaim(localClaimDTO);

    }


    private static void loadConfiguration() throws IOException {
        FileInputStream freader = new FileInputStream(RemoteUMSampleConstants.PROPERTIES_FILE_NAME);
        properties.load(freader);

        serverUrl = properties.getProperty(RemoteUMSampleConstants.REMOTE_SERVER_URL);
        username = properties.getProperty(RemoteUMSampleConstants.USER_NAME);
        password = properties.getProperty(RemoteUMSampleConstants.PASSWORD);
        operation = properties.getProperty(RemoteUMSampleConstants.CLAIM_OPERATION);

        truststore = RemoteUMSampleConstants.RESOURCE_PATH + properties.getProperty(RemoteUMSampleConstants
                .TRUST_STORE_PATH);
        truststorePassword = properties.getProperty(RemoteUMSampleConstants.TRUST_STORE_PASSWORD);
    }

    private static ClaimPropertyDTO buildClaimPropertyDTO(String propertyName, String propertyValue) {

        ClaimPropertyDTO claimPropertyDTO = new ClaimPropertyDTO();
        claimPropertyDTO.setPropertyName(propertyName);
        claimPropertyDTO.setPropertyValue(properties.getProperty(propertyValue));
        return claimPropertyDTO;
    }


}
