package ml.docilealligator.infinityforreddit.adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.core.MarkwonTheme;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.SaveThing;
import ml.docilealligator.infinityforreddit.VoteThing;
import ml.docilealligator.infinityforreddit.activities.BaseActivity;
import ml.docilealligator.infinityforreddit.activities.CommentActivity;
import ml.docilealligator.infinityforreddit.activities.LinkResolverActivity;
import ml.docilealligator.infinityforreddit.activities.ViewPostDetailActivity;
import ml.docilealligator.infinityforreddit.activities.ViewUserDetailActivity;
import ml.docilealligator.infinityforreddit.bottomsheetfragments.CommentMoreBottomSheetFragment;
import ml.docilealligator.infinityforreddit.bottomsheetfragments.UrlMenuBottomSheetFragment;
import ml.docilealligator.infinityforreddit.comment.Comment;
import ml.docilealligator.infinityforreddit.comment.FetchComment;
import ml.docilealligator.infinityforreddit.customtheme.CustomThemeWrapper;
import ml.docilealligator.infinityforreddit.customviews.CommentIndentationView;
import ml.docilealligator.infinityforreddit.customviews.CustomMarkwonAdapter;
import ml.docilealligator.infinityforreddit.customviews.LinearLayoutManagerBugFixed;
import ml.docilealligator.infinityforreddit.customviews.MarkwonLinearLayoutManager;
import ml.docilealligator.infinityforreddit.customviews.SpoilerOnClickTextView;
import ml.docilealligator.infinityforreddit.customviews.SwipeLockScrollView;
import ml.docilealligator.infinityforreddit.fragments.ViewPostDetailFragment;
import ml.docilealligator.infinityforreddit.markdown.MarkdownUtils;
import ml.docilealligator.infinityforreddit.post.Post;
import ml.docilealligator.infinityforreddit.utils.APIUtils;
import ml.docilealligator.infinityforreddit.utils.SharedPreferencesUtils;
import ml.docilealligator.infinityforreddit.utils.Utils;
import retrofit2.Retrofit;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FIRST_LOADING = 9;
    private static final int VIEW_TYPE_FIRST_LOADING_FAILED = 10;
    private static final int VIEW_TYPE_NO_COMMENT_PLACEHOLDER = 11;
    private static final int VIEW_TYPE_COMMENT = 12;
    private static final int VIEW_TYPE_COMMENT_FULLY_COLLAPSED = 13;
    private static final int VIEW_TYPE_LOAD_MORE_CHILD_COMMENTS = 14;
    private static final int VIEW_TYPE_IS_LOADING_MORE_COMMENTS = 15;
    private static final int VIEW_TYPE_LOAD_MORE_COMMENTS_FAILED = 16;
    private static final int VIEW_TYPE_VIEW_ALL_COMMENTS = 17;

    private final AsyncListDiffer<VisibleComment> asyncListDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<VisibleComment>() {
        @Override
        public boolean areItemsTheSame(@NonNull VisibleComment oldItem, @NonNull VisibleComment newItem) {
            return oldItem.fullName.equals(newItem.fullName) && oldItem.placeholderType == newItem.placeholderType;
        }

        @Override
        public boolean areContentsTheSame(@NonNull VisibleComment oldItem, @NonNull VisibleComment newItem) {
            // todo: compare differently based on placeholderType
            if (oldItem.placeholderType == Comment.NOT_PLACEHOLDER) {
                return oldItem.equals(newItem);
            } else if (oldItem.placeholderType == Comment.PLACEHOLDER_LOAD_MORE_COMMENTS) {
                return oldItem.equals(newItem);
            } else if (oldItem.placeholderType == Comment.PLACEHOLDER_CONTINUE_THREAD) {
                return oldItem.equals(newItem);
            } else {
                throw new IllegalStateException("Illegal placeholder type");
            }
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull VisibleComment oldItem, @NonNull VisibleComment newItem) {
            if (oldItem.expanded != newItem.expanded
                    || oldItem.hasExpandedBefore != newItem.hasExpandedBefore
                    || oldItem.submitter != newItem.submitter
                    || oldItem.moderator != newItem.moderator
                    || oldItem.commentTimeMillis != newItem.commentTimeMillis
                    || oldItem.score != newItem.score
                    || oldItem.voteType != newItem.voteType
                    || oldItem.depth != newItem.depth
                    || oldItem.hasReply != newItem.hasReply
                    || oldItem.childCount != newItem.childCount
                    || oldItem.saved != newItem.saved
                    || oldItem.loadingMoreChildren != newItem.loadingMoreChildren
                    || oldItem.loadMoreChildrenFailed != newItem.loadMoreChildrenFailed
                    || !Objects.equals(oldItem.id, newItem.id)
                    || !Objects.equals(oldItem.awards, newItem.awards)
                    || !Objects.equals(oldItem.author, newItem.author)
                    || !Objects.equals(oldItem.authorFlairHTML, newItem.authorFlairHTML)
                    || !Objects.equals(oldItem.authorFlair, newItem.authorFlair)
                    || !Objects.equals(oldItem.fullName, newItem.fullName)
                    || !Objects.equals(oldItem.commentMarkdown, newItem.commentMarkdown)) {
                return null;
            }

            // todo: change to flags object
            if (!Objects.equals(oldItem.authorIconUrl, newItem.authorIconUrl)) {
                return newItem.authorIconUrl;
            }

            return super.getChangePayload(oldItem, newItem);
        }
    });

    private BaseActivity mActivity;
    private ViewPostDetailFragment mFragment;
    private Executor mExecutor;
    private Retrofit mRetrofit;
    private Retrofit mOauthRetrofit;
    private Markwon mCommentMarkwon;
    private String mAccessToken;
    private String mAccountName;
    private Post mPost;
    private ArrayList<Comment> mComments;
    private Locale mLocale;
    private RequestManager mGlide;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private String mSingleCommentId;
    private boolean mIsSingleCommentThreadMode;
    private boolean mVoteButtonsOnTheRight;
    private boolean mShowElapsedTime;
    private String mTimeFormatPattern;
    private boolean mExpandChildren;
    private boolean mCommentToolbarHidden;
    private boolean mCommentToolbarHideOnClick;
    private boolean mSwapTapAndLong;
    private boolean mShowCommentDivider;
    private boolean mShowAbsoluteNumberOfVotes;
    private boolean mFullyCollapseComment;
    private boolean mShowOnlyOneCommentLevelIndicator;
    private boolean mHideCommentAwards;
    private boolean mShowAuthorAvatar;
    private boolean mAlwaysShowChildCommentCount;
    private int mDepthThreshold;
    private CommentRecyclerViewAdapterCallback mCommentRecyclerViewAdapterCallback;
    private boolean isInitiallyLoading;
    private boolean isInitiallyLoadingFailed;
    private boolean mHasMoreComments;
    private boolean loadMoreCommentsFailed;
    private Drawable expandDrawable;
    private Drawable collapseDrawable;

    private int mColorPrimaryLightTheme;
    private int mColorAccent;
    private int mCircularProgressBarBackgroundColor;
    private int mSecondaryTextColor;
    private int mPrimaryTextColor;
    private int mCommentTextColor;
    private int mCommentBackgroundColor;
    private int mDividerColor;
    private int mUsernameColor;
    private int mSubmitterColor;
    private int mModeratorColor;
    private int mCurrentUserColor;
    private int mAuthorFlairTextColor;
    private int mUpvotedColor;
    private int mDownvotedColor;
    private int mSingleCommentThreadBackgroundColor;
    private int mVoteAndReplyUnavailableVoteButtonColor;
    private int mButtonTextColor;
    private int mPostIconAndInfoColor;
    private int mCommentIconAndInfoColor;
    private int mFullyCollapsedCommentBackgroundColor;
    private int mAwardedCommentBackgroundColor;
    private int[] verticalBlockColors;

    private int mSearchCommentIndex = -1;

    public CommentsRecyclerViewAdapter(BaseActivity activity, ViewPostDetailFragment fragment,
                                       CustomThemeWrapper customThemeWrapper,
                                       Executor executor, Retrofit retrofit, Retrofit oauthRetrofit,
                                       String accessToken, String accountName,
                                       Post post, Locale locale, String singleCommentId,
                                       boolean isSingleCommentThreadMode,
                                       SharedPreferences sharedPreferences,
                                       CommentRecyclerViewAdapterCallback commentRecyclerViewAdapterCallback) {
        mActivity = activity;
        mFragment = fragment;
        mExecutor = executor;
        mRetrofit = retrofit;
        mOauthRetrofit = oauthRetrofit;
        mGlide = Glide.with(activity);
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        mCommentTextColor = customThemeWrapper.getCommentColor();
        int commentSpoilerBackgroundColor = mCommentTextColor | 0xFF000000;
        int linkColor = customThemeWrapper.getLinkColor();
        MarkwonPlugin miscPlugin = new AbstractMarkwonPlugin() {
            @Override
            public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                if (mActivity.contentTypeface != null) {
                    textView.setTypeface(mActivity.contentTypeface);
                }
                textView.setTextColor(mCommentTextColor);
                textView.setHighlightColor(Color.TRANSPARENT);
            }

            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver((view, link) -> {
                    Intent intent = new Intent(mActivity, LinkResolverActivity.class);
                    Uri uri = Uri.parse(link);
                    intent.setData(uri);
                    intent.putExtra(LinkResolverActivity.EXTRA_IS_NSFW, mPost.isNSFW());
                    mActivity.startActivity(intent);
                });
            }

            @Override
            public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                builder.linkColor(linkColor);
            }
        };
        BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener = (textView, url) -> {
            if (!activity.isDestroyed() && !activity.isFinishing()) {
                UrlMenuBottomSheetFragment urlMenuBottomSheetFragment = UrlMenuBottomSheetFragment.newInstance(url);
                urlMenuBottomSheetFragment.show(activity.getSupportFragmentManager(), null);
            }
            return true;
        };
        mCommentMarkwon = MarkdownUtils.createFullRedditMarkwon(mActivity,
                miscPlugin, mCommentTextColor, commentSpoilerBackgroundColor, onLinkLongClickListener);
        recycledViewPool = new RecyclerView.RecycledViewPool();
        mAccessToken = accessToken;
        mAccountName = accountName;
        mPost = post;
        mComments = new ArrayList<>();
        mLocale = locale;
        mSingleCommentId = singleCommentId;
        mIsSingleCommentThreadMode = isSingleCommentThreadMode;

        mVoteButtonsOnTheRight = sharedPreferences.getBoolean(SharedPreferencesUtils.VOTE_BUTTONS_ON_THE_RIGHT_KEY, false);
        mShowElapsedTime = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ELAPSED_TIME_KEY, false);
        mTimeFormatPattern = sharedPreferences.getString(SharedPreferencesUtils.TIME_FORMAT_KEY, SharedPreferencesUtils.TIME_FORMAT_DEFAULT_VALUE);
        mExpandChildren = !sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_TOP_LEVEL_COMMENTS_FIRST, false);
        mCommentToolbarHidden = sharedPreferences.getBoolean(SharedPreferencesUtils.COMMENT_TOOLBAR_HIDDEN, false);
        mCommentToolbarHideOnClick = sharedPreferences.getBoolean(SharedPreferencesUtils.COMMENT_TOOLBAR_HIDE_ON_CLICK, true);
        mSwapTapAndLong = sharedPreferences.getBoolean(SharedPreferencesUtils.SWAP_TAP_AND_LONG_COMMENTS, false);
        mShowCommentDivider = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_COMMENT_DIVIDER, false);
        mShowAbsoluteNumberOfVotes = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ABSOLUTE_NUMBER_OF_VOTES, true);
        mFullyCollapseComment = sharedPreferences.getBoolean(SharedPreferencesUtils.FULLY_COLLAPSE_COMMENT, false);
        mShowOnlyOneCommentLevelIndicator = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ONLY_ONE_COMMENT_LEVEL_INDICATOR, false);
        mHideCommentAwards = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_COMMENT_AWARDS, false);
        mShowAuthorAvatar = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_AUTHOR_AVATAR, false);
        mAlwaysShowChildCommentCount = sharedPreferences.getBoolean(SharedPreferencesUtils.ALWAYS_SHOW_CHILD_COMMENT_COUNT, false);
        mDepthThreshold = sharedPreferences.getInt(SharedPreferencesUtils.SHOW_FEWER_TOOLBAR_OPTIONS_THRESHOLD, 5);

        mCommentRecyclerViewAdapterCallback = commentRecyclerViewAdapterCallback;
        isInitiallyLoading = true;
        isInitiallyLoadingFailed = false;
        mHasMoreComments = false;
        loadMoreCommentsFailed = false;

        expandDrawable = Utils.getTintedDrawable(activity, R.drawable.ic_expand_more_grey_24dp, customThemeWrapper.getCommentIconAndInfoColor());
        collapseDrawable = Utils.getTintedDrawable(activity, R.drawable.ic_expand_less_grey_24dp, customThemeWrapper.getCommentIconAndInfoColor());

        mColorPrimaryLightTheme = customThemeWrapper.getColorPrimaryLightTheme();
        mColorAccent = customThemeWrapper.getColorAccent();
        mCircularProgressBarBackgroundColor = customThemeWrapper.getCircularProgressBarBackground();
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mDividerColor = customThemeWrapper.getDividerColor();
        mCommentBackgroundColor = customThemeWrapper.getCommentBackgroundColor();
        mSubmitterColor = customThemeWrapper.getSubmitter();
        mModeratorColor = customThemeWrapper.getModerator();
        mCurrentUserColor = customThemeWrapper.getCurrentUser();
        mAuthorFlairTextColor = customThemeWrapper.getAuthorFlairTextColor();
        mUsernameColor = customThemeWrapper.getUsername();
        mUpvotedColor = customThemeWrapper.getUpvoted();
        mDownvotedColor = customThemeWrapper.getDownvoted();
        mSingleCommentThreadBackgroundColor = customThemeWrapper.getSingleCommentThreadBackgroundColor();
        mVoteAndReplyUnavailableVoteButtonColor = customThemeWrapper.getVoteAndReplyUnavailableButtonColor();
        mButtonTextColor = customThemeWrapper.getButtonTextColor();
        mPostIconAndInfoColor = customThemeWrapper.getPostIconAndInfoColor();
        mCommentIconAndInfoColor = customThemeWrapper.getCommentIconAndInfoColor();
        mFullyCollapsedCommentBackgroundColor = customThemeWrapper.getFullyCollapsedCommentBackgroundColor();
        mAwardedCommentBackgroundColor = customThemeWrapper.getAwardedCommentBackgroundColor();

        verticalBlockColors = new int[] {
                customThemeWrapper.getCommentVerticalBarColor1(),
                customThemeWrapper.getCommentVerticalBarColor2(),
                customThemeWrapper.getCommentVerticalBarColor3(),
                customThemeWrapper.getCommentVerticalBarColor4(),
                customThemeWrapper.getCommentVerticalBarColor5(),
                customThemeWrapper.getCommentVerticalBarColor6(),
                customThemeWrapper.getCommentVerticalBarColor7(),
        };

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Log.d("COMMENT_CHANGE", "onChanged");
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                Log.d("COMMENT_CHANGE", "onItemRangeChanged " + positionStart + " " + itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                Log.d("COMMENT_CHANGE", "onItemRangeChanged " + positionStart + " " + itemCount + " payload");
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.d("COMMENT_CHANGE", "onItemRangeInserted " + positionStart + " " + itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                Log.d("COMMENT_CHANGE", "onItemRangeRemoved " + positionStart + " " + itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                Log.d("COMMENT_CHANGE", "onItemRangeMoved " + fromPosition + " " + toPosition + " " + itemCount);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (asyncListDiffer.getCurrentList().size() == 0) {
            if (isInitiallyLoading) {
                return VIEW_TYPE_FIRST_LOADING;
            } else if (isInitiallyLoadingFailed) {
                return VIEW_TYPE_FIRST_LOADING_FAILED;
            } else {
                return VIEW_TYPE_NO_COMMENT_PLACEHOLDER;
            }
        }

        if (mIsSingleCommentThreadMode) {
            if (position == 0) {
                return VIEW_TYPE_VIEW_ALL_COMMENTS;
            }

            if (position == asyncListDiffer.getCurrentList().size() + 1) {
                if (mHasMoreComments) {
                    return VIEW_TYPE_IS_LOADING_MORE_COMMENTS;
                } else {
                    return VIEW_TYPE_LOAD_MORE_COMMENTS_FAILED;
                }
            }

            VisibleComment comment = asyncListDiffer.getCurrentList().get(position - 1);
            if (comment.getPlaceholderType() == Comment.NOT_PLACEHOLDER) {
                if (mFullyCollapseComment && !comment.isExpanded() && comment.hasExpandedBefore()) {
                    return VIEW_TYPE_COMMENT_FULLY_COLLAPSED;
                }
                return VIEW_TYPE_COMMENT;
            } else {
                return VIEW_TYPE_LOAD_MORE_CHILD_COMMENTS;
            }
        } else {
            if (position == asyncListDiffer.getCurrentList().size()) {
                if (mHasMoreComments) {
                    return VIEW_TYPE_IS_LOADING_MORE_COMMENTS;
                } else {
                    return VIEW_TYPE_LOAD_MORE_COMMENTS_FAILED;
                }
            }

            VisibleComment comment = asyncListDiffer.getCurrentList().get(position);
            if (comment.getPlaceholderType() == Comment.NOT_PLACEHOLDER) {
                if (mFullyCollapseComment && !comment.isExpanded() && comment.hasExpandedBefore()) {
                    return VIEW_TYPE_COMMENT_FULLY_COLLAPSED;
                }
                return VIEW_TYPE_COMMENT;
            } else {
                return VIEW_TYPE_LOAD_MORE_CHILD_COMMENTS;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FIRST_LOADING:
                return new LoadCommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_comments, parent, false));
            case VIEW_TYPE_FIRST_LOADING_FAILED:
                return new LoadCommentsFailedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_comments_failed_placeholder, parent, false));
            case VIEW_TYPE_NO_COMMENT_PLACEHOLDER:
                return new NoCommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_comment_placeholder, parent, false));
            case VIEW_TYPE_COMMENT:
                return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
            case VIEW_TYPE_COMMENT_FULLY_COLLAPSED:
                return new CommentFullyCollapsedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_fully_collapsed, parent, false));
            case VIEW_TYPE_LOAD_MORE_CHILD_COMMENTS:
                return new LoadMoreChildCommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more_comments_placeholder, parent, false));
            case VIEW_TYPE_IS_LOADING_MORE_COMMENTS:
                return new IsLoadingMoreCommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_footer_loading, parent, false));
            case VIEW_TYPE_LOAD_MORE_COMMENTS_FAILED:
                return new LoadMoreCommentsFailedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_footer_error, parent, false));
            default:
                return new ViewAllCommentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_all_comments, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder untypedHolder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (untypedHolder instanceof CommentViewHolder) {
                CommentViewHolder holder = (CommentViewHolder) untypedHolder;
                VisibleComment comment = getCurrentVisibleComment(position);
                mGlide.load(Objects.requireNonNull(comment).getAuthorIconUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(holder.authorIconImageView);
            } else if (untypedHolder instanceof CommentFullyCollapsedViewHolder) {
                CommentFullyCollapsedViewHolder holder = (CommentFullyCollapsedViewHolder) untypedHolder;
                VisibleComment comment = getCurrentVisibleComment(position);
                mGlide.load(Objects.requireNonNull(comment).getAuthorIconUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(holder.authorIconImageView);
            }
        } else {
            super.onBindViewHolder(untypedHolder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder untypedHolder, int position) {
        if (untypedHolder instanceof CommentViewHolder) {
            CommentViewHolder holder = (CommentViewHolder) untypedHolder;
            VisibleComment comment = getCurrentVisibleComment(position);
            if (comment != null) {
                if (mIsSingleCommentThreadMode && comment.getId().equals(mSingleCommentId)) {
                    holder.itemView.setBackgroundColor(mSingleCommentThreadBackgroundColor);
                } else if (comment.getAwards() != null && !comment.getAwards().equals("")) {
                    holder.itemView.setBackgroundColor(mAwardedCommentBackgroundColor);
                }

                String authorPrefixed = "u/" + comment.getAuthor();
                holder.authorTextView.setText(authorPrefixed);

                if (comment.getAuthorFlairHTML() != null && !comment.getAuthorFlairHTML().equals("")) {
                    holder.authorFlairTextView.setVisibility(View.VISIBLE);
                    Utils.setHTMLWithImageToTextView(holder.authorFlairTextView, comment.getAuthorFlairHTML(), true);
                } else if (comment.getAuthorFlair() != null && !comment.getAuthorFlair().equals("")) {
                    holder.authorFlairTextView.setVisibility(View.VISIBLE);
                    holder.authorFlairTextView.setText(comment.getAuthorFlair());
                }

                if (comment.isSubmitter()) {
                    holder.authorTextView.setTextColor(mSubmitterColor);
                    Drawable submitterDrawable = Utils.getTintedDrawable(mActivity, R.drawable.ic_mic_14dp, mSubmitterColor);
                    holder.authorTextView.setCompoundDrawablesWithIntrinsicBounds(
                            submitterDrawable, null, null, null);
                } else if (comment.isModerator()) {
                    holder.authorTextView.setTextColor(mModeratorColor);
                    Drawable moderatorDrawable = Utils.getTintedDrawable(mActivity, R.drawable.ic_verified_user_14dp, mModeratorColor);
                    holder.authorTextView.setCompoundDrawablesWithIntrinsicBounds(
                            moderatorDrawable, null, null, null);
                } else if (comment.getAuthor().equals(mAccountName)) {
                    holder.authorTextView.setTextColor(mCurrentUserColor);
                    Drawable currentUserDrawable = Utils.getTintedDrawable(mActivity, R.drawable.ic_current_user_14dp, mCurrentUserColor);
                    holder.authorTextView.setCompoundDrawablesWithIntrinsicBounds(
                            currentUserDrawable, null, null, null);
                }

                if (comment.getAuthorIconUrl() == null) {
                    mFragment.loadIcon(comment.getAuthor(), (authorName, iconUrl) -> {
                        if (authorName.equals(comment.getAuthor())) {
                            int currentPosition = findCommentPositionByFullname(comment.getFullName(), position);
                            if (currentPosition != -1) {
                                getCurrentComment(currentPosition).setAuthorIconUrl(iconUrl);
                                updateVisibleComments();
                            }
                        }
                    });
                } else {
                    mGlide.load(comment.getAuthorIconUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                            .into(holder.authorIconImageView);
                }

                if (mShowElapsedTime) {
                    holder.commentTimeTextView.setText(
                            Utils.getElapsedTime(mActivity, comment.getCommentTimeMillis()));
                } else {
                    holder.commentTimeTextView.setText(Utils.getFormattedTime(mLocale, comment.getCommentTimeMillis(), mTimeFormatPattern));
                }

                if (mCommentToolbarHidden) {
                    holder.bottomConstraintLayout.getLayoutParams().height = 0;
                    holder.topScoreTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.bottomConstraintLayout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    holder.topScoreTextView.setVisibility(View.GONE);
                }

                if (!mHideCommentAwards && comment.getAwards() != null && !comment.getAwards().equals("")) {
                    holder.awardsTextView.setVisibility(View.VISIBLE);
                    Utils.setHTMLWithImageToTextView(holder.awardsTextView, comment.getAwards(), true);
                }

                holder.mMarkwonAdapter.setMarkdown(mCommentMarkwon, comment.getCommentMarkdown());
                holder.mMarkwonAdapter.notifyDataSetChanged();

                String commentText = "";
                String topScoreText = "";
                if (comment.isScoreHidden()) {
                    commentText = mActivity.getString(R.string.hidden);
                } else {
                    commentText = Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                            comment.getScore() + comment.getVoteType());
                    topScoreText = mActivity.getString(R.string.top_score,
                            Utils.getNVotes(mShowAbsoluteNumberOfVotes,
                                    comment.getScore() + comment.getVoteType()));
                }
                holder.scoreTextView.setText(commentText);
                holder.topScoreTextView.setText(topScoreText);

                holder.commentIndentationView.setShowOnlyOneDivider(mShowOnlyOneCommentLevelIndicator);
                holder.commentIndentationView.setLevelAndColors(comment.getDepth(), verticalBlockColors);
                if (comment.getDepth() >= mDepthThreshold) {
                    holder.saveButton.setVisibility(View.GONE);
                    holder.replyButton.setVisibility(View.GONE);
                } else {
                    holder.saveButton.setVisibility(View.VISIBLE);
                    holder.replyButton.setVisibility(View.VISIBLE);
                }

                if (comment.hasReply()) {
                    if (comment.getChildCount() > 0 && (mAlwaysShowChildCommentCount || !comment.isExpanded())) {
                        holder.expandButton.setText("+" + comment.getChildCount());
                    }
                    if (comment.isExpanded()) {
                        holder.expandButton.setCompoundDrawablesWithIntrinsicBounds(collapseDrawable, null, null, null);
                    } else {
                        holder.expandButton.setCompoundDrawablesWithIntrinsicBounds(expandDrawable, null, null, null);
                    }
                    holder.expandButton.setVisibility(View.VISIBLE);
                }

                switch (comment.getVoteType()) {
                    case Comment.VOTE_TYPE_UPVOTE:
                        holder.upvoteButton
                                .setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                        holder.downvoteButton
                                .setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        holder.scoreTextView.setTextColor(mUpvotedColor);
                        holder.topScoreTextView.setTextColor(mUpvotedColor);
                        break;
                    case Comment.VOTE_TYPE_DOWNVOTE:
                        holder.upvoteButton
                                .setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        holder.downvoteButton
                                .setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                        holder.scoreTextView.setTextColor(mDownvotedColor);
                        holder.topScoreTextView.setTextColor(mDownvotedColor);
                        break;
                    case Comment.VOTE_TYPE_NO_VOTE:
                        holder.upvoteButton
                                .setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        holder.downvoteButton
                                .setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        holder.scoreTextView.setTextColor(mCommentIconAndInfoColor);
                        holder.topScoreTextView.setTextColor(mCommentIconAndInfoColor);

                }

                if (mPost.isArchived()) {
                    holder.replyButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor,
                                    PorterDuff.Mode.SRC_IN);
                    holder.upvoteButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor,
                                    PorterDuff.Mode.SRC_IN);
                    holder.downvoteButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor,
                                    PorterDuff.Mode.SRC_IN);
                }

                if (mPost.isLocked()) {
                    holder.replyButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor,
                                    PorterDuff.Mode.SRC_IN);
                }

                if (comment.isSaved()) {
                    holder.saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                } else {
                    holder.saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                }

                if (position == mSearchCommentIndex) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#03A9F4"));
                }
            }
        } else if (untypedHolder instanceof CommentFullyCollapsedViewHolder) {
            CommentFullyCollapsedViewHolder holder = (CommentFullyCollapsedViewHolder) untypedHolder;
            VisibleComment comment = getCurrentVisibleComment(position);
            if (comment != null) {
                String authorWithPrefix = "u/" + comment.getAuthor();
                holder.usernameTextView.setText(authorWithPrefix);

                if (comment.getAuthorIconUrl() == null) {
                    mFragment.loadIcon(comment.getAuthor(), (authorName, iconUrl) -> {
                        if (authorName.equals(comment.getAuthor())) {
                            int currentPosition = findCommentPositionByFullname(comment.getFullName(), position);
                            if (currentPosition != -1) {
                                getCurrentComment(currentPosition).setAuthorIconUrl(iconUrl);
                                updateVisibleComments();
                            }
                        }
                    });
                } else {
                    mGlide.load(comment.getAuthorIconUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                            .into(holder.authorIconImageView);
                }

                if (comment.getChildCount() > 0) {
                    holder.childCountTextView.setVisibility(View.VISIBLE);
                    holder.childCountTextView.setText("+" + comment.getChildCount());
                } else {
                    holder.childCountTextView.setVisibility(View.GONE);
                }
                if (mShowElapsedTime) {
                    holder.commentTimeTextView.setText(Utils.getElapsedTime(mActivity, comment.getCommentTimeMillis()));
                } else {
                    holder.commentTimeTextView.setText(Utils.getFormattedTime(mLocale, comment.getCommentTimeMillis(), mTimeFormatPattern));
                }
                if (!comment.isScoreHidden()) {
                    holder.scoreTextView.setText(mActivity.getString(R.string.top_score,
                            Utils.getNVotes(mShowAbsoluteNumberOfVotes, comment.getScore() + comment.getVoteType())));
                }
                holder.commentIndentationView.setShowOnlyOneDivider(mShowOnlyOneCommentLevelIndicator);
                holder.commentIndentationView.setLevelAndColors(comment.getDepth(), verticalBlockColors);
            }
        } else if (untypedHolder instanceof LoadMoreChildCommentsViewHolder) {
            LoadMoreChildCommentsViewHolder holder = (LoadMoreChildCommentsViewHolder) untypedHolder;
            VisibleComment placeholder = getCurrentVisibleComment(position);

            holder.commentIndentationView.setShowOnlyOneDivider(mShowOnlyOneCommentLevelIndicator);
            holder.commentIndentationView.setLevelAndColors(placeholder.getDepth(), verticalBlockColors);

            if (placeholder.getPlaceholderType() == Comment.PLACEHOLDER_LOAD_MORE_COMMENTS) {
                if (placeholder.isLoadingMoreChildren()) {
                    holder.placeholderTextView.setText(R.string.loading);
                } else if (placeholder.isLoadMoreChildrenFailed()) {
                    holder.placeholderTextView.setText(R.string.comment_load_more_comments_failed);
                } else {
                    holder.placeholderTextView.setText(R.string.comment_load_more_comments);
                }
            } else {
                holder.placeholderTextView.setText(R.string.comment_continue_thread);
            }

            if (placeholder.getPlaceholderType() == Comment.PLACEHOLDER_LOAD_MORE_COMMENTS) {
                holder.placeholderTextView.setOnClickListener(view -> {
                    int commentPosition = mIsSingleCommentThreadMode ? holder.getBindingAdapterPosition() - 1 : holder.getBindingAdapterPosition();
                    int parentPosition = getParentPosition(commentPosition);
                    if (parentPosition >= 0) {
                        Comment parentComment = mComments.get(parentPosition);

                        mComments.get(commentPosition).setLoadingMoreChildren(true);
                        mComments.get(commentPosition).setLoadMoreChildrenFailed(false);
                        updateVisibleComments();

                        Retrofit retrofit = mAccessToken == null ? mRetrofit : mOauthRetrofit;
                        FetchComment.fetchMoreComment(mExecutor, new Handler(), retrofit, mAccessToken,
                                parentComment.getMoreChildrenFullnames(),
                                parentComment.getMoreChildrenStartingIndex(), parentComment.getDepth() + 1,
                                mExpandChildren, new FetchComment.FetchMoreCommentListener() {
                                    @Override
                                    public void onFetchMoreCommentSuccess(ArrayList<Comment> expandedComments,
                                                                          int childrenStartingIndex) {
                                        int parentCurrentPosition = findCommentPositionByFullname(parentComment.getFullName(), parentPosition);
                                        if (parentCurrentPosition == -1) {
                                            return;
                                        }

                                        Comment parentCurrentComment = mComments.get(parentCurrentPosition);
                                        if (parentCurrentComment.isExpanded()) {
                                            int placeholderPosition = findLoadMorePlaceholderPositionByFullname(parentComment.getFullName(), commentPosition);

                                            if (parentCurrentComment.getChildren().size() > childrenStartingIndex) {
                                                parentCurrentComment.setMoreChildrenStartingIndex(childrenStartingIndex);
                                                parentCurrentComment.getChildren().get(parentCurrentComment.getChildren().size() - 1)
                                                        .setLoadingMoreChildren(false);
                                                parentCurrentComment.getChildren().get(parentCurrentComment.getChildren().size() - 1)
                                                        .setLoadMoreChildrenFailed(false);

                                                mComments.get(placeholderPosition).setLoadingMoreChildren(false);
                                                mComments.get(placeholderPosition).setLoadMoreChildrenFailed(false);
                                            } else {
                                                parentCurrentComment.getChildren()
                                                        .remove(parentCurrentComment.getChildren().size() - 1);
                                                parentCurrentComment.removeMoreChildrenFullnames();

                                                mComments.remove(placeholderPosition);
                                            }

                                            mComments.addAll(placeholderPosition, expandedComments);
                                        } else {
                                            if (parentCurrentComment.hasReply() && parentCurrentComment.getChildren().size() <= childrenStartingIndex) {
                                                parentCurrentComment.getChildren()
                                                        .remove(parentCurrentComment.getChildren().size() - 1);
                                                parentCurrentComment.removeMoreChildrenFullnames();
                                            }
                                        }

                                        parentCurrentComment.addChildren(expandedComments);
                                        updateVisibleComments();
                                    }

                                    @Override
                                    public void onFetchMoreCommentFailed() {
                                        int parentCurrentPosition = findCommentPositionByFullname(parentComment.getFullName(), parentPosition);
                                        if (parentCurrentPosition != -1) {
                                            Comment parentCurrentComment = mComments.get(parentPosition);
                                            if (parentCurrentComment.isExpanded()) {
                                                int placeholderPositionHint = parentCurrentPosition + parentCurrentComment.getChildren().size();
                                                int placeholderPosition = findLoadMorePlaceholderPositionByFullname(parentComment.getFullName(), placeholderPositionHint);

                                                if (placeholderPosition != -1) {
                                                    mComments.get(placeholderPosition).setLoadingMoreChildren(false);
                                                    mComments.get(placeholderPosition).setLoadMoreChildrenFailed(true);
                                                }
                                                holder.placeholderTextView.setText(R.string.comment_load_more_comments_failed);
                                            }

                                            parentCurrentComment.getChildren().get(parentCurrentComment.getChildren().size() - 1)
                                                    .setLoadingMoreChildren(false);
                                            parentCurrentComment.getChildren().get(parentCurrentComment.getChildren().size() - 1)
                                                    .setLoadMoreChildrenFailed(true);
                                        }
                                    }
                                });
                    }
                });
            } else {
                holder.placeholderTextView.setOnClickListener(view -> {
                    Comment comment = getCurrentComment(position);
                    if (comment != null) {
                        Intent intent = new Intent(mActivity, ViewPostDetailActivity.class);
                        intent.putExtra(ViewPostDetailActivity.EXTRA_POST_DATA, mPost);
                        intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_ID, comment.getParentId());
                        intent.putExtra(ViewPostDetailActivity.EXTRA_CONTEXT_NUMBER, "0");
                        mActivity.startActivity(intent);
                    }
                });
            }
        }
    }

    private int getParentPosition(int position) {
        if (position >= 0 && position < mComments.size()) {
            int childDepth = mComments.get(position).getDepth();
            for (int i = position; i >= 0; i--) {
                if (mComments.get(i).getDepth() < childDepth) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * Find position of comment that is not a placeholder
     */
    private int findCommentPositionByFullname(@NonNull String fullname, int positionHint) {
        return findCommentPositionByFullname(fullname, positionHint, Comment.NOT_PLACEHOLDER);
    }

    private int findLoadMorePlaceholderPositionByFullname(@NonNull String fullname, int positionHint) {
        return findCommentPositionByFullname(fullname, positionHint, Comment.PLACEHOLDER_LOAD_MORE_COMMENTS);
    }

    private int findCommentPositionByFullname(@NonNull String fullname, int positionHint, int placeholderType) {
        if (positionHint >= 0 && positionHint < mComments.size()
                && fullname.equals(mComments.get(positionHint).getFullName())
                && mComments.get(positionHint).getPlaceholderType() == placeholderType) {
            return positionHint;
        }

        for (int i = 0; i < mComments.size(); i++) {
            Comment comment = mComments.get(i);
            if (fullname.equals(comment.getFullName())
                    && comment.getPlaceholderType() == placeholderType) {
                return i;
            }
        }

        return -1;
    }

    private void expandChildren(ArrayList<Comment> comments, ArrayList<Comment> newList) {
        if (comments != null && comments.size() > 0) {
            for (Comment comment : comments) {
                newList.add(comment);
                expandChildren(comment.getChildren(), newList);
                comment.setExpanded(true);
            }
        }
    }

    private void collapseChildren(int position) {
        mComments.get(position).setExpanded(false);
        int depth = mComments.get(position).getDepth();
        int allChildrenSize = 0;
        for (int i = position + 1; i < mComments.size(); i++) {
            if (mComments.get(i).getDepth() > depth) {
                allChildrenSize++;
            } else {
                break;
            }
        }

        if (allChildrenSize > 0) {
            mComments.subList(position + 1, position + 1 + allChildrenSize).clear();
        }
        updateVisibleComments();
    }

    public void addComments(@NonNull ArrayList<Comment> comments, boolean hasMoreComments) {
        if (mComments.size() == 0) {
            isInitiallyLoading = false;
            isInitiallyLoadingFailed = false;
        }

        mComments.addAll(comments);

        mHasMoreComments = hasMoreComments;
        updateVisibleComments();
    }

    public void addComment(Comment comment) {
        mComments.add(0, comment);
        updateVisibleComments();
    }

    public void addChildComment(Comment comment, String parentFullname, int parentPosition) {
        if (!parentFullname.equals(mComments.get(parentPosition).getFullName())) {
            for (int i = 0; i < mComments.size(); i++) {
                if (parentFullname.equals(mComments.get(i).getFullName())) {
                    parentPosition = i;
                    break;
                }
            }
        }

        mComments.get(parentPosition).addChild(comment);
        mComments.get(parentPosition).setHasReply(true);
        if (!mComments.get(parentPosition).isExpanded()) {
            mComments.get(parentPosition).setExpanded(true);
            ArrayList<Comment> newList = new ArrayList<>();
            expandChildren(mComments.get(parentPosition).getChildren(), newList);
            mComments.addAll(parentPosition + 1, newList);
        } else {
            mComments.add(parentPosition + 1, comment);
        }
        updateVisibleComments();
    }

    public void setSingleComment(String singleCommentId, boolean isSingleCommentThreadMode) {
        mSingleCommentId = singleCommentId;
        mIsSingleCommentThreadMode = isSingleCommentThreadMode;
    }

    public ArrayList<Comment> getComments() {
        return mComments;
    }

    private void updateVisibleComments() {
        List<VisibleComment> visibleComments = new ArrayList<>();
        for (Comment comment: mComments) {
            if (comment.getDepth() == 0) {
                collectVisibleComments(comment, visibleComments);
            }
        }
        if (visibleComments.size() != mComments.size()) {
            Log.e("COMMENT", "Wrong size");
//            throw new IllegalStateException("Mismatch in comments size");
        }
        asyncListDiffer.submitList(visibleComments);
    }

    private void collectVisibleComments(Comment comment, List<VisibleComment> visibleComments) {
        visibleComments.add(new VisibleComment(comment));
        if (comment.isExpanded() && comment.getChildren() != null) {
            for (Comment child: comment.getChildren()) {
                collectVisibleComments(child, visibleComments);
            }
        }
    }

    public void initiallyLoading() {
        resetCommentSearchIndex();
        mComments.clear();
        isInitiallyLoading = true;
        isInitiallyLoadingFailed = false;
        updateVisibleComments();
    }

    public void initiallyLoadCommentsFailed() {
        isInitiallyLoading = false;
        isInitiallyLoadingFailed = true;
        updateVisibleComments();
    }

    public void loadMoreCommentsFailed() {
        loadMoreCommentsFailed = true;
        updateVisibleComments();
    }

    public void editComment(String commentAuthor, String commentContentMarkdown, int position) {
        if (commentAuthor != null) {
            mComments.get(position).setAuthor(commentAuthor);
        }

        mComments.get(position).setSubmittedByAuthor(mComments.get(position).isSubmitter());

        mComments.get(position).setCommentMarkdown(commentContentMarkdown);
        updateVisibleComments();
    }

    public void editComment(Comment fetchedComment, Comment originalComment, int position) {
        if (position >= mComments.size() || !mComments.get(position).equals(originalComment)) {
            position = mComments.indexOf(originalComment);
            if (position < 0) {
                Toast.makeText(mActivity, R.string.show_removed_comment_failed, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mComments.get(position).setSubmittedByAuthor(originalComment.isSubmitter());
        mComments.get(position).setCommentMarkdown(fetchedComment.getCommentMarkdown());

        if (mIsSingleCommentThreadMode) {
            notifyItemChanged(position + 1);
        } else {
            notifyItemChanged(position);
        }
    }

    public void deleteComment(int position) {
        if (mComments != null && position >= 0 && position < mComments.size()) {
            if (mComments.get(position).hasReply()) {
                mComments.get(position).setAuthor("[deleted]");
                mComments.get(position).setCommentMarkdown("[deleted]");
                updateVisibleComments();
            } else {
                mComments.remove(position);
                updateVisibleComments();
            }
        }
    }

    public int getNextParentCommentPosition(int currentPosition) {
        if (mComments != null && !mComments.isEmpty()) {
            if (mIsSingleCommentThreadMode) {
                for (int i = currentPosition + 1; i - 1 < mComments.size() && i - 1 >= 0; i++) {
                    if (mComments.get(i - 1).getDepth() == 0) {
                        return i;
                    }
                }
            } else {
                for (int i = currentPosition + 1; i < mComments.size(); i++) {
                    if (mComments.get(i).getDepth() == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public int getPreviousParentCommentPosition(int currentPosition) {
        if (mComments != null && !mComments.isEmpty()) {
            if (mIsSingleCommentThreadMode) {
                for (int i = currentPosition - 1; i - 1 >= 0; i--) {
                    if (mComments.get(i - 1).getDepth() == 0) {
                        return i;
                    }
                }
            } else {
                for (int i = currentPosition - 1; i >= 0; i--) {
                    if (mComments.get(i).getDepth() == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction, int swipeLeftAction, int swipeRightAction) {
        if (viewHolder instanceof CommentViewHolder) {
            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.START) {
                if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((CommentViewHolder) viewHolder).upvoteButton.performClick();
                } else if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((CommentViewHolder) viewHolder).downvoteButton.performClick();
                }
            } else {
                if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((CommentViewHolder) viewHolder).upvoteButton.performClick();
                } else if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((CommentViewHolder) viewHolder).downvoteButton.performClick();
                }
            }
        }
    }

    public void giveAward(String awardsHTML, int awardCount, int position) {
        position = mIsSingleCommentThreadMode ? position + 1 : position;
        Comment comment = getCurrentComment(position);
        if (comment != null) {
            comment.addAwards(awardsHTML);
            updateVisibleComments();
        }
    }

    public void setSaveComment(int position, boolean isSaved) {
        Comment comment = getCurrentComment(position);
        if (comment != null) {
            comment.setSaved(isSaved);
            updateVisibleComments();
        }
    }

    public int getSearchCommentIndex() {
        return mSearchCommentIndex;
    }

    public void highlightSearchResult(int searchCommentIndex) {
        mSearchCommentIndex = searchCommentIndex;
        updateVisibleComments();
    }

    public void resetCommentSearchIndex() {
        mSearchCommentIndex = -1;
        updateVisibleComments();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof CommentViewHolder) {
            holder.itemView.setBackgroundColor(mCommentBackgroundColor);
            ((CommentViewHolder) holder).authorTextView.setTextColor(mUsernameColor);
            ((CommentViewHolder) holder).authorFlairTextView.setVisibility(View.GONE);
            ((CommentViewHolder) holder).authorTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mGlide.clear(((CommentViewHolder) holder).authorIconImageView);
            ((CommentViewHolder) holder).topScoreTextView.setTextColor(mSecondaryTextColor);
            ((CommentViewHolder) holder).awardsTextView.setText("");
            ((CommentViewHolder) holder).awardsTextView.setVisibility(View.GONE);
            ((CommentViewHolder) holder).expandButton.setVisibility(View.GONE);
            ((CommentViewHolder) holder).upvoteButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            ((CommentViewHolder) holder).scoreTextView.setTextColor(mCommentIconAndInfoColor);
            ((CommentViewHolder) holder).downvoteButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            ((CommentViewHolder) holder).expandButton.setText("");
            ((CommentViewHolder) holder).replyButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        if (isInitiallyLoading || isInitiallyLoadingFailed || asyncListDiffer.getCurrentList().size() == 0) {
            return 1;
        }

        if (mHasMoreComments || loadMoreCommentsFailed) {
            if (mIsSingleCommentThreadMode) {
                return asyncListDiffer.getCurrentList().size() + 2;
            } else {
                return asyncListDiffer.getCurrentList().size() + 1;
            }
        }

        if (mIsSingleCommentThreadMode) {
            return asyncListDiffer.getCurrentList().size() + 1;
        } else {
            return asyncListDiffer.getCurrentList().size();
        }
    }

    public interface CommentRecyclerViewAdapterCallback {
        void retryFetchingComments();

        void retryFetchingMoreComments();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.linear_layout_item_comment)
        LinearLayout linearLayout;
        @BindView(R.id.author_icon_image_view_item_post_comment)
        ImageView authorIconImageView;
        @BindView(R.id.author_text_view_item_post_comment)
        TextView authorTextView;
        @BindView(R.id.author_flair_text_view_item_post_comment)
        TextView authorFlairTextView;
        @BindView(R.id.comment_time_text_view_item_post_comment)
        TextView commentTimeTextView;
        @BindView(R.id.top_score_text_view_item_post_comment)
        TextView topScoreTextView;
        @BindView(R.id.awards_text_view_item_comment)
        TextView awardsTextView;
        @BindView(R.id.comment_markdown_view_item_post_comment)
        RecyclerView commentMarkdownView;
        @BindView(R.id.bottom_constraint_layout_item_post_comment)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.up_vote_button_item_post_comment)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_comment)
        TextView scoreTextView;
        @BindView(R.id.down_vote_button_item_post_comment)
        ImageView downvoteButton;
        @BindView(R.id.placeholder_item_post_comment)
        View placeholder;
        @BindView(R.id.more_button_item_post_comment)
        ImageView moreButton;
        @BindView(R.id.save_button_item_post_comment)
        ImageView saveButton;
        @BindView(R.id.expand_button_item_post_comment)
        TextView expandButton;
        @BindView(R.id.reply_button_item_post_comment)
        ImageView replyButton;
        @BindView(R.id.vertical_block_indentation_item_comment)
        CommentIndentationView commentIndentationView;
        @BindView(R.id.divider_item_comment)
        View commentDivider;
        CustomMarkwonAdapter mMarkwonAdapter;

        CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (mVoteButtonsOnTheRight) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(bottomConstraintLayout);
                constraintSet.clear(upvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(upvoteButton.getId(), ConstraintSet.END);
                constraintSet.clear(scoreTextView.getId(), ConstraintSet.START);
                constraintSet.clear(scoreTextView.getId(), ConstraintSet.END);
                constraintSet.clear(downvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(downvoteButton.getId(), ConstraintSet.END);
                constraintSet.clear(expandButton.getId(), ConstraintSet.START);
                constraintSet.clear(expandButton.getId(), ConstraintSet.END);
                constraintSet.clear(saveButton.getId(), ConstraintSet.START);
                constraintSet.clear(saveButton.getId(), ConstraintSet.END);
                constraintSet.clear(replyButton.getId(), ConstraintSet.START);
                constraintSet.clear(replyButton.getId(), ConstraintSet.END);
                constraintSet.clear(moreButton.getId(), ConstraintSet.START);
                constraintSet.clear(moreButton.getId(), ConstraintSet.END);
                constraintSet.connect(upvoteButton.getId(), ConstraintSet.END, scoreTextView.getId(), ConstraintSet.START);
                constraintSet.connect(upvoteButton.getId(), ConstraintSet.START, placeholder.getId(), ConstraintSet.END);
                constraintSet.connect(scoreTextView.getId(), ConstraintSet.END, downvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(scoreTextView.getId(), ConstraintSet.START, upvoteButton.getId(), ConstraintSet.END);
                constraintSet.connect(downvoteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.connect(downvoteButton.getId(), ConstraintSet.START, scoreTextView.getId(), ConstraintSet.END);
                constraintSet.connect(placeholder.getId(), ConstraintSet.END, upvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(placeholder.getId(), ConstraintSet.START, moreButton.getId(), ConstraintSet.END);
                constraintSet.connect(moreButton.getId(), ConstraintSet.START, expandButton.getId(), ConstraintSet.END);
                constraintSet.connect(moreButton.getId(), ConstraintSet.END, placeholder.getId(), ConstraintSet.START);
                constraintSet.connect(expandButton.getId(), ConstraintSet.START, saveButton.getId(), ConstraintSet.END);
                constraintSet.connect(expandButton.getId(), ConstraintSet.END, moreButton.getId(), ConstraintSet.START);
                constraintSet.connect(saveButton.getId(), ConstraintSet.START, replyButton.getId(), ConstraintSet.END);
                constraintSet.connect(saveButton.getId(), ConstraintSet.END, expandButton.getId(), ConstraintSet.START);
                constraintSet.connect(replyButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(replyButton.getId(), ConstraintSet.END, saveButton.getId(), ConstraintSet.START);
                constraintSet.applyTo(bottomConstraintLayout);
            }

            if (linearLayout.getLayoutTransition() != null) {
                linearLayout.getLayoutTransition().setAnimateParentHierarchy(false);
            }

            if (mShowCommentDivider) {
                commentDivider.setBackgroundColor(mDividerColor);
                commentDivider.setVisibility(View.VISIBLE);
            }

            if (mActivity.typeface != null) {
                authorTextView.setTypeface(mActivity.typeface);
                commentTimeTextView.setTypeface(mActivity.typeface);
                authorFlairTextView.setTypeface(mActivity.typeface);
                topScoreTextView.setTypeface(mActivity.typeface);
                awardsTextView.setTypeface(mActivity.typeface);
                scoreTextView.setTypeface(mActivity.typeface);
                expandButton.setTypeface(mActivity.typeface);
            }

            if (mShowAuthorAvatar) {
                authorIconImageView.setVisibility(View.VISIBLE);
            } else {
                ((ConstraintLayout.LayoutParams) authorTextView.getLayoutParams()).leftMargin = 0;
                ((ConstraintLayout.LayoutParams) authorFlairTextView.getLayoutParams()).leftMargin = 0;
            }

            commentMarkdownView.setRecycledViewPool(recycledViewPool);
            LinearLayoutManagerBugFixed linearLayoutManager = new MarkwonLinearLayoutManager(mActivity, new SwipeLockScrollView.SwipeLockInterface() {
                @Override
                public void lockSwipe() {
                    ((ViewPostDetailActivity) mActivity).lockSwipeRightToGoBack();
                }

                @Override
                public void unlockSwipe() {
                    ((ViewPostDetailActivity) mActivity).unlockSwipeRightToGoBack();
                }
            });
            commentMarkdownView.setLayoutManager(linearLayoutManager);
            mMarkwonAdapter = MarkdownUtils.createCustomTablesAdapter();
            commentMarkdownView.setAdapter(mMarkwonAdapter);

            itemView.setBackgroundColor(mCommentBackgroundColor);
            authorTextView.setTextColor(mUsernameColor);
            commentTimeTextView.setTextColor(mSecondaryTextColor);
            authorFlairTextView.setTextColor(mAuthorFlairTextColor);
            topScoreTextView.setTextColor(mSecondaryTextColor);
            awardsTextView.setTextColor(mSecondaryTextColor);
            commentDivider.setBackgroundColor(mDividerColor);
            upvoteButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            scoreTextView.setTextColor(mCommentIconAndInfoColor);
            downvoteButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            moreButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            expandButton.setTextColor(mCommentIconAndInfoColor);
            saveButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            replyButton.setColorFilter(mCommentIconAndInfoColor, PorterDuff.Mode.SRC_IN);

            authorFlairTextView.setOnClickListener(view -> authorTextView.performClick());

            moreButton.setOnClickListener(view -> {
                getItemCount();
                Comment comment = getCurrentComment(this);
                if (comment != null) {
                    Bundle bundle = new Bundle();
                    if (!mPost.isArchived() && !mPost.isLocked() && comment.getAuthor().equals(mAccountName)) {
                        bundle.putBoolean(CommentMoreBottomSheetFragment.EXTRA_EDIT_AND_DELETE_AVAILABLE, true);
                    }
                    bundle.putString(CommentMoreBottomSheetFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                    bundle.putParcelable(CommentMoreBottomSheetFragment.EXTRA_COMMENT, comment);
                    if (mIsSingleCommentThreadMode) {
                        bundle.putInt(CommentMoreBottomSheetFragment.EXTRA_POSITION, getBindingAdapterPosition() - 1);
                    } else {
                        bundle.putInt(CommentMoreBottomSheetFragment.EXTRA_POSITION, getBindingAdapterPosition());
                    }
                    bundle.putBoolean(CommentMoreBottomSheetFragment.EXTRA_IS_NSFW, mPost.isNSFW());
                    if (comment.getDepth() >= mDepthThreshold) {
                        bundle.putBoolean(CommentMoreBottomSheetFragment.EXTRA_SHOW_REPLY_AND_SAVE_OPTION, true);
                    }
                    CommentMoreBottomSheetFragment commentMoreBottomSheetFragment = new CommentMoreBottomSheetFragment();
                    commentMoreBottomSheetFragment.setArguments(bundle);
                    commentMoreBottomSheetFragment.show(mActivity.getSupportFragmentManager(), commentMoreBottomSheetFragment.getTag());
                }
            });

            replyButton.setOnClickListener(view -> {
                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPost.isArchived()) {
                    Toast.makeText(mActivity, R.string.archived_post_reply_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPost.isLocked()) {
                    Toast.makeText(mActivity, R.string.locked_post_reply_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                Comment comment = getCurrentComment(this);
                if (comment != null) {
                    Intent intent = new Intent(mActivity, CommentActivity.class);
                    intent.putExtra(CommentActivity.EXTRA_PARENT_DEPTH_KEY, comment.getDepth() + 1);
                    intent.putExtra(CommentActivity.EXTRA_COMMENT_PARENT_BODY_MARKDOWN_KEY, comment.getCommentMarkdown());
                    intent.putExtra(CommentActivity.EXTRA_COMMENT_PARENT_BODY_KEY, comment.getCommentRawText());
                    intent.putExtra(CommentActivity.EXTRA_PARENT_FULLNAME_KEY, comment.getFullName());
                    intent.putExtra(CommentActivity.EXTRA_IS_REPLYING_KEY, true);

                    int parentPosition = mIsSingleCommentThreadMode ? getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                    intent.putExtra(CommentActivity.EXTRA_PARENT_POSITION_KEY, parentPosition);
                    mFragment.startActivityForResult(intent, CommentActivity.WRITE_COMMENT_REQUEST_CODE);
                }
            });

            upvoteButton.setOnClickListener(view -> {
                if (mPost.isArchived()) {
                    Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                Comment comment = getCurrentComment(this);
                if (comment != null) {
                    int previousVoteType = comment.getVoteType();
                    String newVoteDir;
                    int newVoteType;

                    if (previousVoteType != Comment.VOTE_TYPE_UPVOTE) {
                        //Not upvoted before
                        newVoteType = Comment.VOTE_TYPE_UPVOTE;
                        newVoteDir = APIUtils.DIR_UPVOTE;
                    } else {
                        //Upvoted before
                        newVoteType = Comment.VOTE_TYPE_NO_VOTE;
                        newVoteDir = APIUtils.DIR_UNVOTE;
                    }
                    comment.setVoteType(newVoteType);
                    updateVisibleComments();

                    VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingListener() {
                        @Override
                        public void onVoteThingSuccess(int position) {
                            comment.setVoteType(newVoteType);

                            int positionHint = mIsSingleCommentThreadMode ? getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                            int currentPosition = findCommentPositionByFullname(comment.getFullName(), positionHint);
                            if (currentPosition != -1) {
                                updateVisibleComments();
                            }
                        }

                        @Override
                        public void onVoteThingFail(int position) {
                        }
                    }, comment.getFullName(), newVoteDir, getBindingAdapterPosition());
                }
            });

            downvoteButton.setOnClickListener(view -> {
                if (mPost.isArchived()) {
                    Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                Comment comment = getCurrentComment(this);
                if (comment != null) {
                    int previousVoteType = comment.getVoteType();
                    String newVoteDir;
                    int newVoteType;

                    if (previousVoteType != Comment.VOTE_TYPE_DOWNVOTE) {
                        //Not downvoted before
                        newVoteDir = APIUtils.DIR_DOWNVOTE;
                        newVoteType = Comment.VOTE_TYPE_DOWNVOTE;
                    } else {
                        //Downvoted before
                        newVoteDir = APIUtils.DIR_UNVOTE;
                        newVoteType = Comment.VOTE_TYPE_NO_VOTE;
                    }
                    comment.setVoteType(newVoteType);
                    updateVisibleComments();

                    VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingListener() {
                        @Override
                        public void onVoteThingSuccess(int position1) {
                            comment.setVoteType(newVoteType);

                            int positionHint = mIsSingleCommentThreadMode ? getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                            int currentPosition = findCommentPositionByFullname(comment.getFullName(), positionHint);
                            if (currentPosition != -1) {
                                updateVisibleComments();
                            }
                        }

                        @Override
                        public void onVoteThingFail(int position1) {
                        }
                    }, comment.getFullName(), newVoteDir, getBindingAdapterPosition());
                }
            });

            saveButton.setOnClickListener(view -> {
                Comment comment = getCurrentComment(this);
                if (comment != null) {
                    if (comment.isSaved()) {
                        comment.setSaved(false);
                        SaveThing.unsaveThing(mOauthRetrofit, mAccessToken, comment.getFullName(), new SaveThing.SaveThingListener() {
                            @Override
                            public void success() {
                                comment.setSaved(false);
                                updateVisibleComments();
                                Toast.makeText(mActivity, R.string.comment_unsaved_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failed() {
                                comment.setSaved(true);
                                updateVisibleComments();
                                Toast.makeText(mActivity, R.string.comment_unsaved_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        comment.setSaved(true);
                        SaveThing.saveThing(mOauthRetrofit, mAccessToken, comment.getFullName(), new SaveThing.SaveThingListener() {
                            @Override
                            public void success() {
                                comment.setSaved(true);
                                updateVisibleComments();
                                Toast.makeText(mActivity, R.string.comment_saved_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failed() {
                                comment.setSaved(false);
                                updateVisibleComments();
                                Toast.makeText(mActivity, R.string.comment_saved_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            authorTextView.setOnClickListener(view -> {
                Comment comment = getCurrentComment(this);
                if (comment != null) {
                    Intent intent = new Intent(mActivity, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, comment.getAuthor());
                    mActivity.startActivity(intent);
                }
            });

            authorIconImageView.setOnClickListener(view -> {
                authorTextView.performClick();
            });

            expandButton.setOnClickListener(view -> {
                if (expandButton.getVisibility() == View.VISIBLE) {
                    int commentPosition = mIsSingleCommentThreadMode ? getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                    Comment comment = getCurrentComment(this);
                    if (comment != null) {
                        if (mComments.get(commentPosition).isExpanded()) {
                            collapseChildren(commentPosition);
                            updateVisibleComments();
                        } else {
                            comment.setExpanded(true);
                            ArrayList<Comment> newList = new ArrayList<>();
                            expandChildren(mComments.get(commentPosition).getChildren(), newList);
                            mComments.get(commentPosition).setExpanded(true);
                            mComments.addAll(commentPosition + 1, newList);
                            updateVisibleComments();
                        }
                    }
                } else if (mFullyCollapseComment) {
                    int commentPosition = mIsSingleCommentThreadMode ? getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                    if (commentPosition >= 0 && commentPosition < mComments.size()) {
                        collapseChildren(commentPosition);
                    }
                }
            });

            if (mSwapTapAndLong) {
                if (mCommentToolbarHideOnClick) {
                    View.OnLongClickListener hideToolbarOnLongClickListener = view -> hideToolbar();
                    itemView.setOnLongClickListener(hideToolbarOnLongClickListener);
                    commentTimeTextView.setOnLongClickListener(hideToolbarOnLongClickListener);
                    mMarkwonAdapter.setOnLongClickListener(v -> {
                        if (v instanceof TextView) {
                            if (((TextView) v).getSelectionStart() == -1 && ((TextView) v).getSelectionEnd() == -1) {
                                hideToolbar();
                            }
                        }
                        return true;
                    });
                }
                mMarkwonAdapter.setOnClickListener(v -> {
                    if (v instanceof SpoilerOnClickTextView) {
                        if (((SpoilerOnClickTextView) v).isSpoilerOnClick()) {
                            ((SpoilerOnClickTextView) v).setSpoilerOnClick(false);
                            return;
                        }
                    }
                    expandComments();
                });
                itemView.setOnClickListener(view -> expandComments());
            } else {
                if (mCommentToolbarHideOnClick) {
                    mMarkwonAdapter.setOnClickListener(view -> {
                        if (view instanceof SpoilerOnClickTextView) {
                            if (((SpoilerOnClickTextView) view).isSpoilerOnClick()) {
                                ((SpoilerOnClickTextView) view).setSpoilerOnClick(false);
                                return;
                            }
                        }
                        hideToolbar();
                    });
                    View.OnClickListener hideToolbarOnClickListener = view -> hideToolbar();
                    itemView.setOnClickListener(hideToolbarOnClickListener);
                    commentTimeTextView.setOnClickListener(hideToolbarOnClickListener);
                }
                mMarkwonAdapter.setOnLongClickListener(view -> {
                    if (view instanceof TextView) {
                        if (((TextView) view).getSelectionStart() == -1 && ((TextView) view).getSelectionEnd() == -1) {
                            expandComments();
                        }
                    }
                    return true;
                });
                itemView.setOnLongClickListener(view -> {
                    expandComments();
                    return true;
                });
            }
        }

        private boolean expandComments() {
            expandButton.performClick();
            return true;
        }

        private boolean hideToolbar() {
            if (bottomConstraintLayout.getLayoutParams().height == 0) {
                bottomConstraintLayout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                topScoreTextView.setVisibility(View.GONE);
                mFragment.delayTransition();
            } else {
                mFragment.delayTransition();
                bottomConstraintLayout.getLayoutParams().height = 0;
                topScoreTextView.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }

    @Nullable
    private Comment getCurrentComment(RecyclerView.ViewHolder holder) {
        return getCurrentComment(holder.getBindingAdapterPosition());
    }

    @Nullable
    private Comment getCurrentComment(int position) {
        if (mIsSingleCommentThreadMode) {
            if (position - 1 >= 0 && position - 1 < mComments.size()) {
                return mComments.get(position - 1);
            }
        } else {
            if (position >= 0 && position < mComments.size()) {
                return mComments.get(position);
            }
        }

        return null;
    }

    @Nullable
    private VisibleComment getCurrentVisibleComment(int bindingAdapterPosition) {
        if (mIsSingleCommentThreadMode) {
            if (bindingAdapterPosition - 1 >= 0 && bindingAdapterPosition - 1 < mComments.size()) {
                return asyncListDiffer.getCurrentList().get(bindingAdapterPosition - 1);
            }
        } else {
            if (bindingAdapterPosition >= 0 && bindingAdapterPosition < mComments.size()) {
                return asyncListDiffer.getCurrentList().get(bindingAdapterPosition);
            }
        }

        return null;
    }

    class CommentFullyCollapsedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.vertical_block_indentation_item_comment_fully_collapsed)
        CommentIndentationView commentIndentationView;
        @BindView(R.id.author_icon_image_view_item_comment_fully_collapsed)
        ImageView authorIconImageView;
        @BindView(R.id.user_name_text_view_item_comment_fully_collapsed)
        TextView usernameTextView;
        @BindView(R.id.child_count_text_view_item_comment_fully_collapsed)
        TextView childCountTextView;
        @BindView(R.id.score_text_view_item_comment_fully_collapsed)
        TextView scoreTextView;
        @BindView(R.id.time_text_view_item_comment_fully_collapsed)
        TextView commentTimeTextView;
        @BindView(R.id.divider_item_comment_fully_collapsed)
        View commentDivider;

        public CommentFullyCollapsedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (mActivity.typeface != null) {
                usernameTextView.setTypeface(mActivity.typeface);
                childCountTextView.setTypeface(mActivity.typeface);
                scoreTextView.setTypeface(mActivity.typeface);
                commentTimeTextView.setTypeface(mActivity.typeface);
            }
            itemView.setBackgroundColor(mFullyCollapsedCommentBackgroundColor);
            usernameTextView.setTextColor(mUsernameColor);
            childCountTextView.setTextColor(mSecondaryTextColor);
            scoreTextView.setTextColor(mSecondaryTextColor);
            commentTimeTextView.setTextColor(mSecondaryTextColor);

            if (mShowCommentDivider) {
                commentDivider.setBackgroundColor(mDividerColor);
                commentDivider.setVisibility(View.VISIBLE);
            }

            if (mShowAuthorAvatar) {
                authorIconImageView.setVisibility(View.VISIBLE);
            } else {
                usernameTextView.setPaddingRelative(0, usernameTextView.getPaddingTop(), usernameTextView.getPaddingEnd(), usernameTextView.getPaddingBottom());
            }

            itemView.setOnClickListener(view -> {
                int commentPosition = mIsSingleCommentThreadMode ? getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                if (commentPosition >= 0 && commentPosition < mComments.size()) {
                    Comment comment = getCurrentComment(this);
                    if (comment != null) {
                        comment.setExpanded(true);
                        ArrayList<Comment> newList = new ArrayList<>();
                        expandChildren(mComments.get(commentPosition).getChildren(), newList);
                        mComments.get(commentPosition).setExpanded(true);
                        mComments.addAll(commentPosition + 1, newList);

                        updateVisibleComments();
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                itemView.performClick();
                return true;
            });
        }
    }

    class LoadMoreChildCommentsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.vertical_block_indentation_item_load_more_comments_placeholder)
        CommentIndentationView commentIndentationView;
        @BindView(R.id.placeholder_text_view_item_load_more_comments)
        TextView placeholderTextView;
        @BindView(R.id.divider_item_load_more_comments_placeholder)
        View commentDivider;

        LoadMoreChildCommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (mShowCommentDivider) {
                commentDivider.setVisibility(View.VISIBLE);
            }

            if (mActivity.typeface != null) {
                placeholderTextView.setTypeface(mActivity.typeface);
            }
            itemView.setBackgroundColor(mCommentBackgroundColor);
            placeholderTextView.setTextColor(mPrimaryTextColor);
            commentDivider.setBackgroundColor(mDividerColor);
        }
    }

    class LoadCommentsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_progress_bar_item_load_comments)
        CircleProgressBar circleProgressBar;

        LoadCommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            circleProgressBar.setBackgroundTintList(ColorStateList.valueOf(mCircularProgressBarBackgroundColor));
            circleProgressBar.setColorSchemeColors(mColorAccent);
        }
    }

    class LoadCommentsFailedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.error_text_view_item_load_comments_failed_placeholder)
        TextView errorTextView;

        LoadCommentsFailedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> mCommentRecyclerViewAdapterCallback.retryFetchingComments());
            if (mActivity.typeface != null) {
                errorTextView.setTypeface(mActivity.typeface);
            }
            errorTextView.setTextColor(mSecondaryTextColor);
        }
    }

    class NoCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.error_text_view_item_no_comment_placeholder)
        TextView errorTextView;

        NoCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                errorTextView.setTypeface(mActivity.typeface);
            }
            errorTextView.setTextColor(mSecondaryTextColor);
        }
    }

    class IsLoadingMoreCommentsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_bar_item_comment_footer_loading)
        ProgressBar progressbar;

        IsLoadingMoreCommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            progressbar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
        }
    }

    class LoadMoreCommentsFailedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.error_text_view_item_comment_footer_error)
        TextView errorTextView;
        @BindView(R.id.retry_button_item_comment_footer_error)
        Button retryButton;

        LoadMoreCommentsFailedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                errorTextView.setTypeface(mActivity.typeface);
                retryButton.setTypeface(mActivity.typeface);
            }
            errorTextView.setText(R.string.load_comments_failed);
            retryButton.setOnClickListener(view -> mCommentRecyclerViewAdapterCallback.retryFetchingMoreComments());
            errorTextView.setTextColor(mSecondaryTextColor);
            retryButton.setBackgroundTintList(ColorStateList.valueOf(mColorPrimaryLightTheme));
            retryButton.setTextColor(mButtonTextColor);
        }
    }

    class ViewAllCommentsViewHolder extends RecyclerView.ViewHolder {

        ViewAllCommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(view -> {
                if (mActivity != null && mActivity instanceof ViewPostDetailActivity) {
                    mIsSingleCommentThreadMode = false;
                    mSingleCommentId = null;
                    updateVisibleComments();
                    mFragment.changeToNomalThreadMode();
                }
            });

            if (mActivity.typeface != null) {
                ((TextView) itemView).setTypeface(mActivity.typeface);
            }
            itemView.setBackgroundTintList(ColorStateList.valueOf(mCommentBackgroundColor));
            ((TextView) itemView).setTextColor(mColorAccent);
        }
    }

    private static class VisibleComment {
        private final int placeholderType;
        private final boolean expanded;
        private final boolean hasExpandedBefore;
        private final String id;
        private final String awards;
        private final String author;
        private final String authorFlairHTML;
        private final String authorFlair;
        private final boolean submitter;
        private final boolean moderator;
        private final String authorIconUrl;
        private final String fullName;
        private final long commentTimeMillis;
        private final String commentMarkdown;
        private final int score;
        private final int voteType;
        private final int depth;
        private final boolean hasReply;
        private final int childCount;
        private final boolean saved;
        private final boolean loadingMoreChildren;
        private final boolean loadMoreChildrenFailed;
        private final boolean scoreHidden;

        VisibleComment(Comment comment) {
            placeholderType = comment.getPlaceholderType();
            expanded = comment.isExpanded();
            hasExpandedBefore = comment.hasExpandedBefore();
            id = comment.getId();
            awards = comment.getAwards();
            author = comment.getAuthor();
            authorFlairHTML = comment.getAuthorFlairHTML();
            authorFlair = comment.getAuthorFlair();
            submitter = comment.isSubmitter();
            moderator = comment.isModerator();
            authorIconUrl = comment.getAuthorIconUrl();
            fullName = comment.getFullName();
            commentTimeMillis = comment.getCommentTimeMillis();
            commentMarkdown = comment.getCommentMarkdown();
            score = comment.getScore();
            voteType = comment.getVoteType();
            depth = comment.getDepth();
            hasReply = comment.hasReply();
            childCount = comment.getChildCount();
            saved = comment.isSaved();
            loadingMoreChildren = comment.isLoadingMoreChildren();
            loadMoreChildrenFailed = comment.isLoadMoreChildrenFailed();
            scoreHidden = comment.isScoreHidden();
        }

        public int getPlaceholderType() {
            return placeholderType;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public boolean hasExpandedBefore() {
            return hasExpandedBefore;
        }

        public String getId() {
            return id;
        }

        public String getAwards() {
            return awards;
        }

        public String getAuthor() {
            return author;
        }

        public String getAuthorFlairHTML() {
            return authorFlairHTML;
        }

        public String getAuthorFlair() {
            return authorFlair;
        }

        public boolean isSubmitter() {
            return submitter;
        }

        public boolean isModerator() {
            return moderator;
        }

        public String getAuthorIconUrl() {
            return authorIconUrl;
        }

        public String getFullName() {
            return fullName;
        }

        public long getCommentTimeMillis() {
            return commentTimeMillis;
        }

        public String getCommentMarkdown() {
            return commentMarkdown;
        }

        public int getScore() {
            return score;
        }

        public int getVoteType() {
            return voteType;
        }

        public int getDepth() {
            return depth;
        }

        public boolean hasReply() {
            return hasReply;
        }

        public int getChildCount() {
            return childCount;
        }

        public boolean isSaved() {
            return saved;
        }

        public boolean isLoadingMoreChildren() {
            return loadingMoreChildren;
        }

        public boolean isLoadMoreChildrenFailed() {
            return loadMoreChildrenFailed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VisibleComment that = (VisibleComment) o;
            return placeholderType == that.placeholderType && expanded == that.expanded && hasExpandedBefore == that.hasExpandedBefore && submitter == that.submitter && moderator == that.moderator && commentTimeMillis == that.commentTimeMillis && score == that.score && voteType == that.voteType && depth == that.depth && hasReply == that.hasReply && childCount == that.childCount && saved == that.saved && loadingMoreChildren == that.loadingMoreChildren && loadMoreChildrenFailed == that.loadMoreChildrenFailed && Objects.equals(id, that.id) && Objects.equals(awards, that.awards) && Objects.equals(author, that.author) && Objects.equals(authorFlairHTML, that.authorFlairHTML) && Objects.equals(authorFlair, that.authorFlair) && Objects.equals(authorIconUrl, that.authorIconUrl) && Objects.equals(fullName, that.fullName) && Objects.equals(commentMarkdown, that.commentMarkdown) && scoreHidden == that.scoreHidden;
        }

        @Override
        public int hashCode() {
            return Objects.hash(placeholderType, expanded, hasExpandedBefore, id, awards, author, authorFlairHTML, authorFlair, submitter, moderator, authorIconUrl, fullName, commentTimeMillis, commentMarkdown, score, voteType, depth, hasReply, childCount, saved, loadingMoreChildren, loadMoreChildrenFailed, scoreHidden);
        }

        public boolean isScoreHidden() {
            return scoreHidden;
        }
    }
}
