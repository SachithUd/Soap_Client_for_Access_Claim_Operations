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
import org.wso2.carbon.um.ws.api.WSAuthorizationManager;
import org.wso2.carbon.um.ws.api.WSUserStoreManager;
import org.wso2.carbon.user.core.UserStoreException;

import java.io.FileInputStream;
import java.io.IOException;
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
    private WSUserStoreManager remoteUserStoreManager = null;
    private ClaimManager claimManager = null;
    private WSAuthorizationManager remoteAuthorizationManager = null;

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

        /*============================================================================================================*/

        if (operation.equals("addClaimDialect")) {

            //remoteUMClient.addClaimDialect("http://mydialectnew123.com");
            remoteUMClient.addClaimDialect(properties.getProperty(RemoteUMSampleConstants.CLAIM_DIALECT_URI));

        } else if (operation.equals ("removeClaimDialect")) {

            remoteUMClient.removeClaimDialect(properties.getProperty(RemoteUMSampleConstants.CLAIM_DIALECT_URI));

        } else if (operation.equals("removeLocalClaim")) {

            remoteUMClient.removeLocalClaim(properties.getProperty(RemoteUMSampleConstants.CLAIM_URI));

        } else if (operation.equals("addLocalClaim")) {
            LocalClaimDTO localClaimDTO = new LocalClaimDTO();

            ClaimPropertyDTO claimPropertyDTO1 = new ClaimPropertyDTO();
            claimPropertyDTO1.setPropertyName("DisplayName");
            claimPropertyDTO1.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.DISPLAY_NAME));

            ClaimPropertyDTO claimPropertyDTO2 = new ClaimPropertyDTO();
            claimPropertyDTO2.setPropertyName("Description");
            claimPropertyDTO2.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.CLAIM_DESCRIPTION));

            ClaimPropertyDTO claimPropertyDTO3 = new ClaimPropertyDTO();
            claimPropertyDTO3.setPropertyName("RegEx");
            claimPropertyDTO3.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.REGULAR_EXPRESSION));

            ClaimPropertyDTO claimPropertyDTO4 = new ClaimPropertyDTO();
            claimPropertyDTO4.setPropertyName("DisplayOrder");
            claimPropertyDTO4.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.DISPLAY_ORDER));

            ClaimPropertyDTO claimPropertyDTO5 = new ClaimPropertyDTO();
            claimPropertyDTO5.setPropertyName("SupportedByDefault");
            claimPropertyDTO5.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.SUPPORTED_BY_DEFAULT));

            ClaimPropertyDTO claimPropertyDTO6 = new ClaimPropertyDTO();
            claimPropertyDTO6.setPropertyName("Required");
            claimPropertyDTO6.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.REQUIRED_FOR_CLAIM));

            ClaimPropertyDTO claimPropertyDTO7 = new ClaimPropertyDTO();
            claimPropertyDTO7.setPropertyName("ReadOnly");
            claimPropertyDTO7.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.READ_ONLY));

            localClaimDTO.setClaimProperties(new ClaimPropertyDTO[]{claimPropertyDTO1, claimPropertyDTO2, claimPropertyDTO3, claimPropertyDTO4, claimPropertyDTO5, claimPropertyDTO6, claimPropertyDTO7});
            localClaimDTO.setLocalClaimURI(properties.getProperty(RemoteUMSampleConstants.CLAIM_URI));


            AttributeMappingDTO attributeMappingDTO = new AttributeMappingDTO();
            attributeMappingDTO.setAttributeName(properties.getProperty(RemoteUMSampleConstants.ATTRIBUTE_NAME_1));
            attributeMappingDTO.setUserStoreDomain(properties.getProperty(RemoteUMSampleConstants.USER_STORE_DOMAIN_1));
            localClaimDTO.setAttributeMappings(new AttributeMappingDTO[]{attributeMappingDTO});

            remoteUMClient.addLocalClaim(localClaimDTO);

        } else if (operation.equals("updateLocalClaim")) {

            LocalClaimDTO localClaimDTO = new LocalClaimDTO();

            ClaimPropertyDTO claimPropertyDTO1 = new ClaimPropertyDTO();
            claimPropertyDTO1.setPropertyName("DisplayName");
            claimPropertyDTO1.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.DISPLAY_NAME));

            ClaimPropertyDTO claimPropertyDTO2 = new ClaimPropertyDTO();
            claimPropertyDTO2.setPropertyName("Description");
            claimPropertyDTO2.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.CLAIM_DESCRIPTION));

            ClaimPropertyDTO claimPropertyDTO3 = new ClaimPropertyDTO();
            claimPropertyDTO3.setPropertyName("RegEx");
            claimPropertyDTO3.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.REGULAR_EXPRESSION));

            ClaimPropertyDTO claimPropertyDTO4 = new ClaimPropertyDTO();
            claimPropertyDTO4.setPropertyName("DisplayOrder");
            claimPropertyDTO4.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.DISPLAY_ORDER));

            ClaimPropertyDTO claimPropertyDTO5 = new ClaimPropertyDTO();
            claimPropertyDTO5.setPropertyName("SupportedByDefault");
            claimPropertyDTO5.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.SUPPORTED_BY_DEFAULT));

            ClaimPropertyDTO claimPropertyDTO6 = new ClaimPropertyDTO();
            claimPropertyDTO6.setPropertyName("Required");
            claimPropertyDTO6.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.REQUIRED_FOR_CLAIM));

            ClaimPropertyDTO claimPropertyDTO7 = new ClaimPropertyDTO();
            claimPropertyDTO7.setPropertyName("ReadOnly");
            claimPropertyDTO7.setPropertyValue(properties.getProperty(RemoteUMSampleConstants.READ_ONLY));

            localClaimDTO.setClaimProperties(new ClaimPropertyDTO[]{claimPropertyDTO1, claimPropertyDTO2, claimPropertyDTO3, claimPropertyDTO4, claimPropertyDTO5, claimPropertyDTO6, claimPropertyDTO7});
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

    public void removeLocalClaim(String localClaimURI) throws Exception{

        LocalClaimDTO localClaimDialect = new LocalClaimDTO();
        localClaimDialect.setLocalClaimURI(localClaimURI);
        claimManager.removeLocalClaim(localClaimURI);
    }

    public void addLocalClaim(LocalClaimDTO localClaimDTO) throws Exception{
        claimManager.addLocalClaim(localClaimDTO);
    }

    public void updateLocalClaim(LocalClaimDTO localClaimDTO) throws Exception{
        claimManager.updateLocalClaim(localClaimDTO);

    }


/*===================================================================================================*/



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

}
