package org.exoplatform.search.content;

import org.exoplatform.services.cms.CmsService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;

/**
 * Implement a Content Listener to index Content.
 */
public class ContentSearchIndexer extends Listener<CmsService, Node> {
    private static final Log log = ExoLogger.getLogger(ContentSearchIndexer.class);

    @Override
    public void onEvent(Event<CmsService, Node> cmsServiceNodeEvent) throws Exception {
        log.info("WCM INDEXER : event name = "+cmsServiceNodeEvent.getEventName());
        log.info("WCM INDEXER : node UUID = "+cmsServiceNodeEvent.getData().getUUID());
        log.info("WCM INDEXER : node name = "+cmsServiceNodeEvent.getData().getName());
    }
}
