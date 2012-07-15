package org.exoplatform.search.wiki;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.wiki.mow.api.Page;
import org.exoplatform.wiki.service.listener.PageWikiListener;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Implement a Wiki Page Listener to index Wiki Page content.
 */
public class SearchWikiIndexer extends PageWikiListener {
    private static final Log log = ExoLogger.getLogger(SearchWikiIndexer.class);

    private Client getNewClient() {
        Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        return client;
    }

    private XContentBuilder makeJson(Page page) throws IOException {
        return jsonBuilder().startObject()
                .field("title", page.getTitle())
                .field("content", page.getContent().getText())
                .field("url", page.getURL())
                .field("author", page.getAuthor())
                .field("created", page.getCreatedDate())
                .field("modified", page.getUpdatedDate())
                .field("name", page.getName())
                .endObject();
    }

    @Override
    public void postAddPage(String wikiType, String wikiOwner, String pageId, Page page) throws Exception {
        log.info("New Wiki Page to indexed (page id : " + pageId + ")");

        Client client = getNewClient();
        try {
            client.prepareIndex("wiki", "page", pageId).setSource(makeJson(page)).execute().actionGet();
            log.debug("New Wiki Page indexed (page id : " + pageId + ")");
        } catch (IOException e) {
            log.error("Impossible to index the new page with id " + pageId + " (silently ignoring the problem)", e);
        }
        client.close();
    }

    @Override
    public void postUpdatePage(String wikiType, String wikiOwner, String pageId, Page page) throws Exception {
        log.info("Updated Wiki Page to indexed (page id : " + pageId + ")");
        Client client = getNewClient();
        try {
            client.prepareIndex("wiki", "page", pageId).setSource(makeJson(page)).execute().actionGet();
            log.debug("Updated Wiki Page indexed (page id : " + pageId + ")");
        } catch (IOException e) {
            log.error("Impossible to index the updated page with id " + pageId + " (silently ignoring the problem)", e);
        }
        client.close();
    }

    @Override
    public void postDeletePage(String wikiType, String wikiOwner, String pageId, Page page) throws Exception {
        log.info("Deleted Wiki Page to suppress from the index (page id : " + pageId + ")");
        log.warn("NOT SUPPORTED - we currently don't remove deleted wiki page from the search index ;-( ... be patient, it is in the backlog !");
    }
}
