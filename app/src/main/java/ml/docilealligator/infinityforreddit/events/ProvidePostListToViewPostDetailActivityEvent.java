package ml.docilealligator.infinityforreddit.events;

import java.util.ArrayList;

import ml.docilealligator.infinityforreddit.SortType;
import ml.docilealligator.infinityforreddit.post.Post;
import ml.docilealligator.infinityforreddit.postfilter.PostFilter;

public class ProvidePostListToViewPostDetailActivityEvent {
    public final long postFragmentId;
    public final ArrayList<Post> posts;
    public final int postType;
    public final String subredditName;
    public final String username;
    public final String userWhere;
    public final String multiPath;
    public final String query;
    public final String trendingSource;
    public final PostFilter postFilter;
    public final SortType sortType;
    public final ArrayList<String> readPostList;

    public ProvidePostListToViewPostDetailActivityEvent(long postFragmentId, ArrayList<Post> posts, int postType,
                                                        String subredditName, String username, String userWhere,
                                                        String multiPath, String query, String trendingSource,
                                                        PostFilter postFilter, SortType sortType, ArrayList<String> readPostList) {
        this.postFragmentId = postFragmentId;
        this.posts = posts;
        this.postType = postType;
        this.subredditName = subredditName;
        this.username = username;
        this.userWhere = userWhere;
        this.multiPath = multiPath;
        this.query = query;
        this.trendingSource = trendingSource;
        this.postFilter = postFilter;
        this.sortType = sortType;
        this.readPostList = readPostList;
    }
}
