package org.exoplatform.search.forum;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.exoplatform.forum.service.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 * Implement a Forum Listener to index Post content.
 *
 * TODO : missing delete Post
 */
public class SearchForumIndexer extends ForumEventListener {
    private static final Log log = ExoLogger.getLogger(SearchForumIndexer.class);

    @Override
    public void saveCategory(Category category) {
    }

    @Override
    public void saveForum(Forum forum) {
    }

    @Override
    public void addTopic(Topic topic, String categoryId, String forumId) {
    }

    @Override
    public void updateTopic(Topic topic, String categoryId, String forumId) {
    }

    @Override
    public void addPost(Post post, String categoryId, String forumId, String topicId) {
        Client client = getNewClient();
        try {
            client.prepareIndex("forum", "post", post.getId())
                    .setSource(jsonBuilder().startObject()
                            .field("author", post.getOwner())
                            .field("content", post.getMessage())
                            .field("created", post.getCreatedDate())
                            .field("modified", post.getModifiedDate())
                            .endObject()).execute().actionGet();
            log.debug("New Post indexed (post id : " + post.getId() + ")");
        } catch (IOException e) {
            log.error("Impossible to index the new post with id " + post.getId() + " (silently ignoring the problem)", e);
        }
        client.close();
    }

    @Override
    public void updatePost(Post post, String categoryId, String forumId, String topicId) {
        Client client = getNewClient();
        try {
            client.prepareIndex("forum", "post", post.getId())
                    .setSource(jsonBuilder().startObject()
                            .field("author", post.getOwner())
                            .field("content", post.getMessage())
                            .field("created", post.getCreatedDate())
                            .field("modified", post.getModifiedDate())
                            .endObject()).execute().actionGet();
            log.debug("Updated Post indexed (post id : " + post.getId() + ")");
        } catch (IOException e) {
            log.error("Impossible to update the index for the updated post with id " + post.getId() + " (silently ignoring the problem)", e);
        }
        client.close();
    }

    private Client getNewClient() {
        Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        return client;
    }
}
