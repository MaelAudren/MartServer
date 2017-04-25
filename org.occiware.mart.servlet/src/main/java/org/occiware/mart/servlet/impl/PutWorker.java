package org.occiware.mart.servlet.impl;

import org.occiware.mart.server.parser.OCCIRequestData;
import org.occiware.mart.server.parser.HeaderPojo;
import org.occiware.mart.server.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

/**
 * Created by cgourdin on 13/04/2017.
 */
public class PutWorker extends ServletEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(PutWorker.class);

    public PutWorker(URI serverURI, HttpServletResponse resp, HeaderPojo headers, HttpServletRequest req, String path) {
        super(serverURI, resp, headers, req, path);
    }

    public HttpServletResponse executeQuery() {

        HttpServletResponse resp = buildInputDatas();

        if (occiResponse.hasExceptions()) {
            return resp;
        }
        if (getContentType().equals(Constants.MEDIA_TYPE_TEXT_URI_LIST)) {
            return occiResponse.parseMessage("You cannot use Content-Type: text/uri-list that way, use a get collection request like http://yourhost:8080/compute/", HttpServletResponse.SC_BAD_REQUEST);
        }

        // There is content so check it.
        occiRequest.validateDataContentRequest();

        List<OCCIRequestData> OCCIRequestData = occiRequest.getOCCIRequestData();
        if (OCCIRequestData.isEmpty()) {
            return occiResponse.parseMessage("No content to put.", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (occiRequest.isInterfQuery()) {
            return occiResponse.parseMessage("you cannot use interface query on PUT method", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (occiRequest.isActionInvocationQuery()) {
            LOGGER.warn("Querying action invocation on PUT method.");
            return occiResponse.parseMessage("You cannot use an action with PUT method.", HttpServletResponse.SC_BAD_REQUEST);
        }

        if (occiRequest.isEntityLocation(getPath())) {
            occiRequest.createEntity();
            return occiResponse.getHttpResponse();
        }

        if (occiRequest.isMixinTagLocation(getPath())) {
            occiRequest.defineMixinTags();
            return occiResponse.getHttpResponse();
        }

        if (occiRequest.isCollectionQuery()) {
            occiRequest.createEntities();
            return occiResponse.getHttpResponse();
        }

        return resp;
    }


}
