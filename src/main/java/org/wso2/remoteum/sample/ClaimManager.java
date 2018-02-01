package org.wso2.remoteum.sample;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.claim.metadata.mgt.stub.ClaimMetadataManagementServiceStub;
import org.wso2.carbon.identity.claim.metadata.mgt.stub.dto.LocalClaimDTO;
import org.wso2.carbon.user.core.UserStoreException;

import org.wso2.carbon.identity.claim.metadata.mgt.stub.dto.ClaimDialectDTO;

import java.rmi.RemoteException;

public class ClaimManager {

    private static final Log log = LogFactory.getLog(ClaimManager.class);

    private ClaimMetadataManagementServiceStub stub = null;

    public ClaimManager(String serverUrl, String cookie, ConfigurationContext configCtxt) throws UserStoreException {
        try {
            this.stub = new ClaimMetadataManagementServiceStub(configCtxt, serverUrl + "ClaimMetadataManagementService");
            ServiceClient client = this.stub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty("Cookie", cookie);
        } catch (AxisFault e) {
            this.handleException(e.getMessage(), e);
        }
    }

    private String[] handleException(String msg, Exception e) throws UserStoreException {
        log.error(e.getMessage(), e);
        throw new UserStoreException(msg, e);
    }

    /*===========================================================================================*/


    public void addClaimDialect(ClaimDialectDTO claimDialectDTO) throws RemoteException, Exception {
        try {
            stub.addClaimDialect(claimDialectDTO);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to add new claim dialect", e);
        }
    }

    public void removeClaimDialect(ClaimDialectDTO claimDialectDTO) throws RemoteException, Exception {
        try {
            stub.removeClaimDialect(claimDialectDTO);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to remove claim dialect", e);
        }
    }

    public void removeLocalClaim(String claimURI) throws RemoteException, Exception {
        try {
            stub.removeLocalClaim(claimURI);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to remove claim Mapping ", e);
        }
    }

    public void addLocalClaim(LocalClaimDTO localClaimDTO) throws RemoteException, Exception {
        try {
            stub.addLocalClaim(localClaimDTO);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to add new claim Mapping", e);
        }
    }

    public void updateLocalClaim(LocalClaimDTO localClaimDTO) throws RemoteException, Exception {
        try {
            stub.updateLocalClaim(localClaimDTO);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to update claim Mapping", e);
        }
    }
}
